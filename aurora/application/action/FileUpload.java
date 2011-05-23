package aurora.application.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import oracle.sql.BLOB;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import uncertain.composite.CompositeMap;
import uncertain.event.EventModel;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.ProcedureRunner;
import aurora.database.DBUtil;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.events.E_DetectProcedure;
import aurora.presentation.component.std.IDGenerator;
import aurora.service.IService;
import aurora.service.ServiceContext;
import aurora.service.ServiceController;
import aurora.service.ServiceInstance;
import aurora.service.controller.ControllerProcedures;
import aurora.service.http.HttpServiceInstance;

public class FileUpload implements E_DetectProcedure {
	public static final String PROPERTITY_SAVE_TYPE = "savetype";
	public static final String PROPERTITY_SAVE_PATH = "savepath";
	public static final String PROPERTITY_URL = "_url";
	
	private static final String SAVE_TYPE_DATABASE = "db";
	private static final String SAVE_TYPE_FILE = "file";
	
	
	private static final String FND_UPLOAD_FILE_TYPE = "fnd.fnd_upload_file_type";
	private CompositeMap params;
	
	private String saveType;
	private String savePath;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");

	private DatabaseServiceFactory databasefactory;

	public FileUpload(IObjectRegistry registry) {
		databasefactory = (DatabaseServiceFactory) registry.getInstanceOfType(DatabaseServiceFactory.class);
	}

	public void onDoUpload(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		ServiceContext service = ServiceContext.createServiceContext(runner.getContext());
		HttpServiceInstance serviceInstance = (HttpServiceInstance) ServiceInstance.getInstance(context);
		
		params = service.getParameter();
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload up = new ServletFileUpload(factory);
		List items = null;
		List files = new ArrayList();
		Connection conn = null;
		String url = null;
		try {
			items = up.parseRequest(serviceInstance.getRequest());
			Iterator i = items.iterator();
			while (i.hasNext()) {
				FileItem fileItem = (FileItem) i.next();
				if (fileItem.isFormField()) {
					String name = fileItem.getFieldName();
					String value = fileItem.getString("UTF-8");
					if("_url".equalsIgnoreCase(name)){
						url = value;
					}else{
						params.put(name, value);
					}
				} else {
					files.add(fileItem);
				}
			}
			Iterator it = files.iterator();
			while (it.hasNext()) {
				FileItem fileItem = (FileItem) it.next();
				File file = new File(fileItem.getName());
				String file_name = file.getName();
				params.put("file_name", file_name);
				params.put("file_size", new Long(fileItem.getSize()));
				BusinessModelService modelService = databasefactory.getModelService(FND_UPLOAD_FILE_TYPE, context);
				modelService.execute(null);
				Object aid = service.getModel().getObject("/parameter/@attachment_id");
				SqlServiceContext sqlServiceContext = SqlServiceContext.createSqlServiceContext(context);
				conn = sqlServiceContext.getConnection();
	            InputStream in = fileItem.getInputStream();
	            String attach_id = aid.toString();
	            if(SAVE_TYPE_DATABASE.equalsIgnoreCase(getSaveType())){
	            	writeBLOB(conn, in, attach_id);	            	
	            }else if(SAVE_TYPE_FILE.equalsIgnoreCase(getSaveType())){
	            	writeFile(conn,in, attach_id);
	            }
	            
	            fileItem.delete();
	            params.put("success", "true");
	            
	            if(url==null){
		            PrintWriter out = serviceInstance.getResponse().getWriter();
		            out.write(aid.toString());
		            out.close();
	            }
			}
			if(url!=null){
				serviceInstance.getResponse().sendRedirect(url);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
            if (conn != null)
                conn.close();
        }
	}
	
	public void writeFile(Connection conn,InputStream instream, String aid) throws Exception    {
		String fileName = IDGenerator.getInstance().generate();
    	String datePath = sdf.format(new Date());	
    	String path = getSavePath();
    	if(path.lastIndexOf("/") == -1) path += "/";
    	path += datePath;
    	FileUtils.forceMkdir(new File(path));
        Statement stmt = null;
        try{
            long size = 0;
            int b;
            //Write file to disk
            FileOutputStream fos ;
            File file = new File(path,fileName);
            fos = new FileOutputStream(file);
            while(( b = instream.read())>=0){
                fos.write(b);
                size++;
            }
            fos.close();
            // Update attachment record
            stmt = conn.createStatement();
            stmt.executeUpdate("update fnd_atm_attachment a set a.file_path = '"+file.getPath()+"' where a.attachment_id = "+aid);
            conn.commit();
        }finally{
            DBUtil.closeStatement(stmt);
        }
	}

	public long writeBLOB(Connection conn, InputStream instream, String aid) throws Exception {
		conn.setAutoCommit(false);
		long size = 0;
		Statement st = null;
		ResultSet rs = null;
		OutputStream outstream = null;
		try {
			st = conn.createStatement();
			st.executeUpdate("update fnd_atm_attachment t set t.content = empty_blob() where t.attachment_id=" + aid);
			st.execute("commit");

			rs = st.executeQuery("select content from fnd_atm_attachment t where t.attachment_id = " + aid + " for update");
			if (!rs.next())
				throw new IllegalArgumentException("attachment_id not set");
			BLOB blob = ((oracle.jdbc.driver.OracleResultSet) rs).getBLOB(1);
			rs.close();

			if (blob == null) {
				System.out.println("Warning: can't update fnd_atm_attachment.content for recrd " + aid);
				return 0;
			}
			outstream = blob.getBinaryOutputStream(0);
			int chunk = blob.getChunkSize();
			byte[] buff = new byte[chunk];
			int le;
			while ((le = instream.read(buff)) != -1) {
				outstream.write(buff, 0, le);
				size += le;
			}
			outstream.close();
			st.execute("commit");
			st.close();
			instream.close();
			conn.commit();
			return size;
		} finally {
			DBUtil.closeResultSet(rs);
			DBUtil.closeStatement(st);
		}
	}

	public int onDetectProcedure(IService service) throws Exception {
		ServiceController controller = ServiceController
				.createServiceController(service.getServiceContext()
						.getObjectContext());
		controller.setProcedureName(ControllerProcedures.UPLOAD_SERVICE);
		return EventModel.HANDLE_NORMAL;
	}

	public String getSaveType() {
		return saveType == null ? SAVE_TYPE_DATABASE : saveType;
	}

	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}

	public String getSavePath() {
		return savePath == null ? "." : savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

}
