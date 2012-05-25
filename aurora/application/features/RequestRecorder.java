/*
 * Created on 2011-9-22 下午05:17:17
 * $Id$
 */
package aurora.application.features;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import uncertain.composite.CompositeMap;
import uncertain.core.IGlobalInstance;
import uncertain.event.EventModel;
import uncertain.event.RuntimeContext;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.mbean.IMBeanNameProvider;
import uncertain.mbean.IMBeanRegister;
import uncertain.mbean.IMBeanRegistrable;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.proc.trace.StackTraceManager;
import uncertain.proc.trace.TraceElement;
import uncertain.util.StackTraceUtil;
import aurora.database.DBUtil;
import aurora.database.actions.ModelBatchUpdate;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.events.E_ServiceFinish;
import aurora.service.IService;
import aurora.service.ServiceContext;
import aurora.service.http.HttpServiceInstance;
import aurora.service.http.UserAgentTools;

public class RequestRecorder extends AbstractLocatableObject implements
        E_ServiceFinish, IGlobalInstance, IMBeanRegistrable,
        RequestRecorderMBean {

    public static final String KEY_CONTEXT_DUMP = "context_dump";

    static int sequence = 1;

    // settable attributes
    int checkInterval = 1000;
    long connectionIdleTime = 20 * 60 * 1000;
    String[] nodeToSaveArray = { "session" };
    String requestSaveBm = "sys.monitor.sys_runtime_request_record";
    boolean saveDetail = true;
    int batchSize = 100;
    int threadCount = 1;

    // init created instances
    IObjectRegistry mObjectReg;
    OCManager mOcManager;
    DataSource mDataSource;
    DatabaseServiceFactory mDbFactory;
    ILogger mLogger;

    // internal state
    long requestCount = 0;
    long processedCount = 0;
    long processTime = 0;
    boolean isRunning = true;
    int maxQueueSize = 0;
    Date maxQueueTime = new Date();

    //Queue<CompositeMap> recordQueue = new LinkedBlockingQueue<CompositeMap>();
    Queue<CompositeMap> recordQueue = new ConcurrentLinkedQueue<CompositeMap>();
    //RequestProcessor processorThread;
    RequestProcessor[]  threads;

    public RequestRecorder(IObjectRegistry reg) {
        this.mObjectReg = reg;
    }

     public class RequestProcessor extends Thread {

        public static final String KEY_INSERT = "insert";
        public static final String KEY_STATUS = "_status";
        public static final String KEY_DETAIL = "detail";
        long idleTime = 0;
        Connection conn = null;
        ModelBatchUpdate batchSaveService;

        public RequestProcessor(String name) {
            super(name);
            batchSaveService = new ModelBatchUpdate(mDbFactory, mOcManager, mObjectReg);
            batchSaveService.setModel(requestSaveBm);
        }
        
        private void processRequestClientInfo( CompositeMap data ){
            String agent = data.getString("user-agent");
            if (agent != null) {
                String[] browsers = UserAgentTools.getBrowser(agent);
                data.put("client_browser_family", browsers[0]);
                data.put("client_browser", browsers[1]);
                data.put("client_browser_version", browsers[2]);

                String[] os = UserAgentTools.getOS(agent);
                data.put("client_os_family", os[0]);
                data.put("client_os", os[1]);
                data.put("client_os_version", os[2]);

                String p = UserAgentTools.getUserPlatform(agent);
                if (p != null)
                    data.put("client_platform", p);
            }


        }
        
        private Throwable getRootCause(Throwable exception) {
            Throwable cause = exception.getCause();
            if (cause == null)
                return exception;
            return getRootCause(cause);
        }        
        
        private void processException(Throwable thr, CompositeMap data){
            CompositeMap item = new CompositeMap("exception");
            item.put(KEY_STATUS, KEY_INSERT);
            item.put("exception_type", thr.getClass().getCanonicalName());
            item.put("exception_message", thr.getMessage());
            item.put("full_stack_trace", StackTraceUtil.toString(thr));
            item.put(KEY_CONTEXT_DUMP, data.get(KEY_CONTEXT_DUMP));
            data.remove(KEY_CONTEXT_DUMP);
            Throwable cause = getRootCause(thr);
            if(cause!=null && cause!=thr){
                item.put("root_stack_trace", StackTraceUtil.toString(cause));
            }
            data.createChild("exceptions").addChild(item);
        }

        private void processRequestData(CompositeMap data) {
            data.put(KEY_STATUS, KEY_INSERT);
            // process request data
            CompositeMap detail = data.getChild(KEY_DETAIL);
            if (detail != null){
                int id=1;
                for (Iterator it = detail.getChildIterator(); it != null
                        && it.hasNext(); id++) {
                    CompositeMap item = (CompositeMap) it.next();
                    item.put(KEY_STATUS, KEY_INSERT);
                    item.put("sequence_num", id);
                }
            }
            
            // process exception
            Throwable thr = (Throwable)data.get(RuntimeContext.KEY_EXCEPTION);
            if(thr==null)
                thr=(Throwable)data.get(RuntimeContext.KEY_LAST_HANDLED_EXCEPTION);
            if(thr!=null)
                processException(thr,data);
            
            // process client info
            processRequestClientInfo(data);

        }
        
        private void commitConnection( Connection conn, ILogger logger ){
            try {
                if (conn != null & !conn.isClosed())
                    conn.commit();
            } catch (SQLException ex) {
                logger.log(Level.WARNING,
                        "Error when commiting connection", ex);
            }            
        }

        void createConnection()
            throws SQLException
        {
            conn = mDataSource.getConnection();
            conn.setAutoCommit(false);
        }

        void checkConnection() throws SQLException {
            if (conn == null){
                createConnection();
            }
            if (conn != null)
                if (conn.isClosed()) {
                    DBUtil.closeConnection(conn);
                    createConnection();
                }
        }
        
        private int pollData( CompositeMap[] data_array){
            int i;
            for(i=0; i<batchSize; i++){
                CompositeMap data = recordQueue.poll();
                if(data==null)
                    break;
                processRequestData(data);
                data_array[i] = data;
            }
            return i;
        }

        public void run() {
            mLogger.config("Request record thread start");
            try {
                while (isRunning) {
                    //CompositeMap data = recordQueue.poll();
                    CompositeMap[] datas = new CompositeMap[batchSize];
                    int num = pollData(datas);
                    if (num == 0) {
                        try {
                            sleep(checkInterval);
                            idleTime += checkInterval;
                        } catch (InterruptedException ex) {
                        }
                        if (conn!=null&&idleTime > connectionIdleTime) {
                            DBUtil.closeConnection(conn);
                            conn = null;
                        }
                        mLogger.config("No data to save");
                    } else {
                        long time = System.currentTimeMillis();
                        idleTime = 0;
                        int size = getCurrentQueueSize() + num;
                        if (size > maxQueueSize) {
                            maxQueueSize = size;
                            maxQueueTime = new Date();
                        }

                        try {
                            checkConnection();
                        } catch (SQLException ex) {
                            mLogger.log(
                                    Level.WARNING,
                                    "Error when getting connection for request log",
                                    ex);
                            mLogger.log(
                                    Level.WARNING,
                                    "Total "+num+"Request info will be discarded:");
                            continue;
                        }
                        mLogger.config("Prepqre to save");
                        CompositeMap context = new CompositeMap("context");
                        SqlServiceContext sqlctx = SqlServiceContext
                                .createSqlServiceContext(context);
                        sqlctx.setConnection(conn);
                        CompositeMap params = sqlctx.getParameter();
                        for(int i=0; i<num; i++)
                            params.addChild(datas[i]);
                        
                        try {
                            //batchSaveService.run(runner);
                            batchSaveService.doBatchUpdate( params.getChilds(), context);
                            commitConnection(conn,mLogger);
                        } catch (Exception ex) {
                            mLogger.log(Level.WARNING,
                                    "Can't save request data", ex);
                        }
                        context.clear();
                        time = System.currentTimeMillis() - time;
                        processTime += time;
                        processedCount += num;
                        mLogger.config("save finish");
                    }

                }
            }catch(Throwable thr){
                mLogger.log(Level.SEVERE, "Error in request record thread", thr);
            }
            finally {
                if (conn != null)
                    DBUtil.closeConnection(conn);
            }
        }

    };

    public void onInitialize() throws IOException {
        if (requestSaveBm == null)
            throw BuiltinExceptionFactory.createAttributeMissing(this,
                    "requestSaveService");
        this.mDbFactory = (DatabaseServiceFactory) mObjectReg
                .getInstanceOfType(DatabaseServiceFactory.class);
        if (mDbFactory == null)
            throw BuiltinExceptionFactory.createInstanceNotFoundException(this,
                    DatabaseServiceFactory.class);
        mOcManager = (OCManager)mObjectReg.getInstanceOfType(OCManager.class);
        
        mLogger = LoggingContext.getLogger("aurora.monitor", mObjectReg);
        mLogger.info("request recorder initialize");
        mDataSource = mDbFactory.getDataSource();

        if (mDataSource == null)
            throw BuiltinExceptionFactory.createInstanceNotFoundException(this,
                    DataSource.class);

        threads = new RequestProcessor[threadCount];
        for(int i=0; i<threadCount; i++){
            threads[i] = new RequestProcessor(RequestProcessor.class.getName() +"."+ sequence++);
            threads[i].start();
        }
    }
    
    private void copyChild(CompositeMap data, String name,
            CompositeMap to_copy_from) {
        CompositeMap child = to_copy_from.getChild(name);
        if (child != null) {
            data.addChild((CompositeMap) child.clone());
        }
    }

    public int onServiceFinish(IService service) throws Exception {

        mLogger.info("Prepare request for save:"+((HttpServiceInstance)service).getName());

        requestCount++;
        ServiceContext ctx = service.getServiceContext();
        CompositeMap context = ctx.getObjectContext();

        // create request data by copying service context
        if (context.getChild("request") == null)
            return EventModel.HANDLE_NORMAL;
        CompositeMap request_data = (CompositeMap) (context.getChild("request")
                .clone());
        boolean is_success = ctx.isSuccess() && (!ctx.hasError());
        request_data.put("success", is_success);
        for (int i = 0; i < nodeToSaveArray.length; i++) {
            copyChild(request_data, nodeToSaveArray[i], context);
        }
        HttpServletRequest request = ((HttpServiceInstance) service)
                .getRequest();
        request_data.put("query_string", request.getQueryString());
        // copy request process detail
        StackTraceManager stm = ctx.getStackTraceManager();
        if (stm != null) {
            TraceElement trace = stm.getRootNode();
            if (trace != null) {
                if (saveDetail) {
                    CompositeMap trace_data = trace.asCompositeMap(true, true);
                    if (trace_data != null && trace_data.getChilds() != null
                            && trace_data.getChilds().size() > 0) {
                        trace_data.setName("detail");
                        request_data.addChild(trace_data);
                    }
                }

            }
            request_data.put("enter_time",
                    new java.sql.Timestamp(trace.getEnterTime()));
            request_data.put("exit_time",
                    new java.sql.Timestamp(trace.getExitTime()));
            request_data.put("duration", trace.getDuration());
        }
        
        // set exception if exception is thrown
        Throwable thr = ctx.getException();
        if(thr==null)
            thr=ctx.getLastHandledException();
        if(thr!=null){
            String context_dump = context.toXML();
            request_data.put(KEY_CONTEXT_DUMP, context_dump);
            RuntimeContext.getInstance(request_data).setException(thr);
        }        
        recordQueue.offer(request_data);
        mLogger.info("Request saved:"+((HttpServiceInstance)service).getName());

        // System.out.println("put data into queue " + request_data.toXML());
        return EventModel.HANDLE_NORMAL;
    }

    public void shutdown() {
        isRunning = false;
        for(int i=0; i<threads.length; i++){
            if(threads[i]!=null&&threads[i].isAlive())
                threads[i].interrupt();
        }
    }

    public void onShutdown() {
        shutdown();
    }

    public int getCurrentQueueSize() {
        return recordQueue.size();
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public Date getMaxQueueSizeTime() {
        return maxQueueTime;
    }

    public long getRequestCount() {
        return requestCount;
    }
    
    public long getProcessedCount() {
        return processedCount;
    }
    
    public long getTotalProcessTime(){
        return processTime;
    }
    
    public double getAverageProcessTime(){
        return (double)processTime/processedCount;
    }

    public void registerMBean(IMBeanRegister register,
            IMBeanNameProvider name_provider)
            throws MalformedObjectNameException,
            InstanceAlreadyExistsException, MBeanRegistrationException,
            NotCompliantMBeanException {
        String name = name_provider.getMBeanName("Application",
                "name=RequestRecorder");
        register.register(name, this);
    }

    public int getCheckInterval() {
        return checkInterval;
    }

    public void setCheckInterval(int checkInterval) {
        this.checkInterval = checkInterval;
    }

    public long getConnectionIdleTime() {
        return connectionIdleTime;
    }

    public void setConnectionIdleTime(long connectionIdleTime) {
        this.connectionIdleTime = connectionIdleTime;
    }

    public String getRequestSaveBm() {
        return requestSaveBm;
    }

    public void setRequestSaveBm(String requestSaveBm) {
        this.requestSaveBm = requestSaveBm;
    }

    public boolean isSaveDetail() {
        return saveDetail;
    }

    public void setSaveDetail(boolean saveDetail) {
        this.saveDetail = saveDetail;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

}
