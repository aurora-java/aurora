package aurora.application.action;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oracle.sql.BLOB;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import uncertain.composite.CompositeMap;
import uncertain.event.EventModel;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.ProcedureRunner;
import aurora.database.DBUtil;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.events.E_DetectProcedure;
import aurora.service.IService;
import aurora.service.ServiceContext;
import aurora.service.ServiceController;
import aurora.service.ServiceInstance;
import aurora.service.controller.ControllerProcedures;
import aurora.service.http.HttpServiceInstance;

public class FileUpload implements E_DetectProcedure {

	private static final String FND_UPLOAD_FILE_TYPE = "fnd.fnd_upload_file_type";
	private CompositeMap params;

	private DatabaseServiceFactory databasefactory;

	public FileUpload(IObjectRegistry registry) {
		databasefactory = (DatabaseServiceFactory) registry.getInstanceOfType(DatabaseServiceFactory.class);
	}

	public void onDoUpload(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		ServiceContext service = ServiceContext.createServiceContext(runner
				.getContext());
		HttpServiceInstance serviceInstance = (HttpServiceInstance) ServiceInstance
				.getInstance(context);
		params = service.getParameter();
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload up = new ServletFileUpload(factory);
		List items = null;
		List files = new ArrayList();
		Connection conn = null;
		try {
			items = up.parseRequest(serviceInstance.getRequest());
			Iterator i = items.iterator();
			while (i.hasNext()) {
				FileItem fileItem = (FileItem) i.next();
				if (fileItem.isFormField()) {
					String name = fileItem.getFieldName();
					String value = fileItem.getString("UTF-8");
					params.put(name, value);
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
	            writeBLOB(conn, in, attach_id);
	            fileItem.delete();
	            params.put("success", "true");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
            if (conn != null)
                conn.close();
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

}
