/*
 * Created on 2008-8-5
 */
package aurora.application.action;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.application.AuroraApplication;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

/**
 * <pre>
 * <session-clear /> destroy a existing HttpSession
 * <session-create /> simply create a HttpSession
 * <session-write /> -> write all fields from default session path "/session" to HttpSession
 * <session-write source="/model/session" /> -> write all fields from source to HttpSession
 * <session-write source="/model/session/@value" target="value" /> -> write one field from source to HttpSession
 * </pre>
 */
public class HttpSessionOperate extends AbstractEntry  {
    
    public static final String SESSION_WRITE = "session-write";
    public static final String SESSION_CLEAR = "session-clear";
    public static final String SESSION_CREATE = "session-create";
    
    String  mOperationName;
    String  mSource;
    String  mTarget;

    /**
     * @param logger
     */
    public HttpSessionOperate() {
        super();
    }

    /**
     * @return source path for session attribute to write
     */
    public String getSource() {
        return mSource;
    }

    /**
     * @param source the mSource to set
     */
    public void setSource(String source) {
        mSource = source;
    }

    /**
     * @return the mTarget
     */
    public String getTarget() {
        return mTarget;
    }

    /**
     * @param target the mTarget to set
     */
    public void setTarget(String target) {
        mTarget = target;
    }

    /**
     * Get name of CompositeMap
     */
    public void beginConfigure(CompositeMap config) {
        super.beginConfigure(config);
        mOperationName = config.getName();
        
    }
    
    void writeAllFields( CompositeMap map, HttpSession session ){
        Iterator it = map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry)it.next();
            Object key = entry.getKey();
            if(key!=null)
                session.setAttribute(key.toString(), entry.getValue());
        }
    }
    
    public void writeSession(HttpServletRequest request, CompositeMap context ){
        ILogger mLogger = LoggingContext.getLogger(context, AuroraApplication.AURORA_APP_SESSION_CHECK_LOGGING_TOPIC);
        CompositeMap session_map = null;
        // Write all session fields
        if(mSource==null){
            session_map = (CompositeMap)context.getObject("/session");
            if(session_map==null){
                mLogger.warning("Can't get /session section in service context");
                return;
            }else{
                HttpSession session = request.getSession(true);
                writeAllFields( session_map, session );
                return;
            }
        }
        
        // Write one field
        if(mTarget!=null){
            HttpSession session = request.getSession(true);
            Object value = context.getObject(mSource);
            mTarget = uncertain.composite.TextParser.parse(mTarget, context);
            session.setAttribute(mTarget, value);
        }else{
            Object value = context.getObject(mSource);
            if( value == null ){
                mLogger.warning("source element not found from path '"+mSource+"'");
                return;
            }
            if(value instanceof CompositeMap){
                HttpSession session = request.getSession(true);
                session_map = (CompositeMap)value;
                writeAllFields(session_map, session);
            }else
                throw new IllegalArgumentException("Object from '"+mSource+"' is not instance of CompositeMap, but " + value.getClass().getName());
        }
            
    }
    
    public void run(ProcedureRunner runner) throws Exception {
        CompositeMap context = runner.getContext();
        HttpServiceInstance svc = (HttpServiceInstance)ServiceInstance.getInstance(context);
        HttpServletRequest request = svc.getRequest();
        if( SESSION_WRITE.equalsIgnoreCase(mOperationName)){
            writeSession( request, context );
        }else if ( SESSION_CLEAR.equalsIgnoreCase(mOperationName)){
            HttpSession session = request.getSession(false);
            if(session!=null){
                session.invalidate();
            }
        }else if ( SESSION_CREATE.equalsIgnoreCase(mOperationName)){
            request.getSession(true);
        }
    }

}
