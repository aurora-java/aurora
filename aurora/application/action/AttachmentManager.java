package aurora.application.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.sql.BLOB;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;

import uncertain.composite.CompositeMap;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.database.DBUtil;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.datasource.DataSourceConfig;
import aurora.presentation.component.std.IDGenerator;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

@SuppressWarnings("unchecked")
public class AttachmentManager extends AbstractEntry{
	
	public static final String VERSION = "$Revision$";
	
	public static final String PROPERTITY_ACTION_TYPE = "actiontype";
	public static final String PROPERTITY_SAVE_TYPE = "savetype";
	public static final String PROPERTITY_SAVE_PATH = "savepath";
	public static final String PROPERTITY_URL = "url";
	public static final String PROPERTITY_RANDOM_NAME = "random_name";
	
	private static final String SAVE_TYPE_DATABASE = "db";
	private static final String SAVE_TYPE_FILE = "file";
	
	
	public int Buffer_size = 500 * 1024;
	
	
	private static final String FND_UPLOAD_FILE_TYPE = "fnd.fnd_upload_file_type";
	
	private String fileType = "*.*";
	private String fileSize = "";
	private String saveType;
	private String savePath;
	private String actionType;
	private String useSubFolder = null;
	private String randomName = "true";
	private String dataSourcename = null;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");

	private DatabaseServiceFactory databasefactory;
	IObjectRegistry registry;

	public AttachmentManager(IObjectRegistry registry) {
		this.registry=registry;
		databasefactory = (DatabaseServiceFactory) registry.getInstanceOfType(DatabaseServiceFactory.class);
	}
	
	public Connection getContextConnection(CompositeMap context) throws SQLException {
		if (context == null)
			throw new IllegalStateException("Can not get context from ServiceThreadLocal!");
		SqlServiceContext sqlServiceContext = SqlServiceContext.createSqlServiceContext(context);
		Connection conn = sqlServiceContext.getNamedConnection(null);
		if (conn == null) {
			sqlServiceContext.initConnection(registry, null);
			conn = sqlServiceContext.getNamedConnection(null);
		}
		return conn;
	}
	
	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		String actionType = getActionType();
		if("upload".equalsIgnoreCase(actionType)){
			doUpload(context);
//			runner.stop();
			ProcedureRunner preRunner=runner;
			while(preRunner.getCaller()!=null){
				preRunner=preRunner.getCaller();
				preRunner.stop();
			}
		}else if("update".equalsIgnoreCase(actionType)){
			doUpdate(context);	
			ProcedureRunner preRunner=runner;
			while(preRunner.getCaller()!=null){
				preRunner=preRunner.getCaller();
				preRunner.stop();
			}
		}else if("delete".equalsIgnoreCase(actionType)){
			doDelete(context);
		}else if("download".equalsIgnoreCase(actionType)){
			doDownload(context);
//			runner.stop();
			ProcedureRunner preRunner=runner;
			while(preRunner.getCaller()!=null){
				preRunner=preRunner.getCaller();
				preRunner.stop();
			}				
		}
	}
	
	private void doDownload(CompositeMap context) throws Exception{
		ServiceContext service = ServiceContext.createServiceContext(context);
		HttpServiceInstance serviceInstance = (HttpServiceInstance) ServiceInstance.getInstance(context);
		CompositeMap params = service.getParameter();
		Object aid = params.getObject("@attachment_id");
		if(aid!=null){
			Connection conn = getContextConnection(context);
			PreparedStatement pst = null;
			ResultSet rs = null;
			InputStream is = null;
			OutputStream os = null;
			ReadableByteChannel rbc = null;
	        WritableByteChannel wbc = null;
			try {
				pst = conn.prepareStatement("select file_name,file_size,mime_type, file_path, content from fnd_atm_attachment t where t.attachment_id = ?");
				pst.setObject(1,aid);
				rs = pst.executeQuery(); 
				if (!rs.next()) throw new IllegalArgumentException("attachment_id not set");
				String path = rs.getString(4);
				String fileName = rs.getString(1);
				int fileSize = rs.getInt(2);
				String mimeType = rs.getString(3);
				HttpServletResponse response = serviceInstance.getResponse();
				response.setHeader("cache-control", "must-revalidate");
				response.setHeader("pragma", "public");	
				response.setHeader("Content-Type", mimeType);
				response.setHeader("Content-disposition", "attachment;" + processFileName(serviceInstance.getRequest(),fileName));
				
				 try{                	
                	Class.forName("org.apache.catalina.startup.Bootstrap");
                	if (fileSize > 0)
                		response.setContentLength(fileSize);    
                }catch(ClassNotFoundException e){}
				if(path!=null){
					File file = new File(path);
					if(file.exists()){
				        os = response.getOutputStream();
				        is = new FileInputStream(path);
		                rbc = Channels.newChannel(is);
		                wbc = Channels.newChannel(os);
		                ByteBuffer buf = ByteBuffer.allocate(Buffer_size);
		                int size=-1;
		                while( (size = rbc.read(buf))>0){
		                	buf.flip();
		                    wbc.write(buf);
		                    buf.compact();
		                    os.flush();
		                }
					}
				}else{
					Blob content = rs.getBlob(5);
		            if (content != null) { 
		                
		                os = response.getOutputStream();
		                is = content.getBinaryStream();
		                rbc = Channels.newChannel(is);
		                wbc = Channels.newChannel(os);
		                ByteBuffer buf = ByteBuffer.allocate(Buffer_size);
		                int size = -1;
		                while ((size = rbc.read(buf)) > 0) {
		                	buf.flip();
		                    wbc.write(buf);
		                    buf.compact();
		                    os.flush();
		                }
		            }
				}
				response.setHeader("Connection", "close");
			} finally{
				DBUtil.closeResultSet(rs);
				DBUtil.closeStatement(pst);
				try{if(is!=null) is.close();
                }catch(Exception ex){}
                try{if(os!=null) os.close();
                }catch(Exception ex){}
			}
		}
	}
	
	private void doDelete(CompositeMap context) throws Exception{
		ServiceContext service = ServiceContext.createServiceContext(context);
		HttpServiceInstance serviceInstance = (HttpServiceInstance) ServiceInstance.getInstance(context);
		
		CompositeMap params = service.getParameter();
		Object aid = serviceInstance.getRequest().getAttribute("attachment_id");
		if(aid ==null) aid = (Object)params.getObject("/parameter/record/@attachment_id");
		if(aid!=null && !"".equals(aid)){
			Connection conn = getContextConnection(context);
			PreparedStatement pst = null;
			ResultSet rs = null;
			try {
				pst = conn.prepareStatement("select file_path from fnd_atm_attachment t where t.attachment_id = ?");
				pst.setObject(1,aid);
				rs = pst.executeQuery(); 
				if (!rs.next()) throw new IllegalArgumentException("attachment_id not set");
				String path = rs.getString(1);
				if(path!=null){
					File file = new File(path);
					if(file.exists()){
						file.delete();
					}
				}
				pst = conn.prepareStatement("delete from fnd_atm_attachment at where at.attachment_id = ?");
				pst.setObject(1,aid);
				pst.execute();
				pst = conn.prepareStatement("delete from fnd_atm_attachment_multi atm where atm.attachment_id = ?");
				pst.setObject(1,aid);
				pst.execute();
//				st.execute("delete from fnd_atm_attachment at where at.attachment_id = " + aid);
//				st.execute("delete from fnd_atm_attachment_multi atm where atm.attachment_id = " + aid);
			} finally{
				DBUtil.closeResultSet(rs);
				DBUtil.closeStatement(pst);
			}
		}
	}
	
	
	
	
	private void doUpdate(CompositeMap context) throws Exception{
		ServiceContext service = ServiceContext.createServiceContext(context);
		HttpServiceInstance serviceInstance = (HttpServiceInstance) ServiceInstance.getInstance(context);
		CompositeMap params = service.getParameter();
		Object aid = (Object)params.getObject("@attachment_id");
		
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload up = new ServletFileUpload(factory);
		List items = up.parseRequest(serviceInstance.getRequest());
		FileItem fileItem = null;
		Iterator i = items.iterator();
		while (i.hasNext()) {
			FileItem fi = (FileItem) i.next();
			if (!fi.isFormField()) {
				fileItem = fi;
			}
		}
		
		if(aid!=null && !"".equals(aid)){
			Connection conn = getContextConnection(context);
			PreparedStatement pst = null;
			ResultSet rs = null;String path = null;
			try {
				pst = conn.prepareStatement("select file_path from fnd_atm_attachment t where t.attachment_id = ?");
				pst.setObject(1,aid);
				rs = pst.executeQuery();
				if (!rs.next()) throw new IllegalArgumentException("attachment_id not set");
				path = rs.getString(1);
			} finally{
				DBUtil.closeResultSet(rs);
				DBUtil.closeStatement(pst);
			}
			if(path!=null){
				File delFile = new File(path);
				if(delFile.exists()){
					delFile.delete();
				}
				if(fileItem != null){
					long size = 0;int b;
					FileOutputStream fos = null;
					InputStream ins = null;
					try {
			            fos = new FileOutputStream(delFile);
			            ins = fileItem.getInputStream();
			            while(( b = ins.read())>=0){
			                fos.write(b);
			                size++;
			            }
			            pst = conn.prepareStatement("update fnd_atm_attachment a set a.file_size = ? where a.attachment_id = ?");
						pst.setObject(1,size);
						pst.setObject(2,aid);
						pst.executeUpdate();
					}finally {
			            if(ins!=null)ins.close();
			            if(fos!=null)fos.close();
			           DBUtil.closeStatement(pst);
					}
				}
			}else {
				DataSourceConfig dataSourceConfig=(DataSourceConfig)this.registry.getInstanceOfType(DataSourceConfig.class);
				Connection nativeConn=dataSourceConfig.getNativeJdbcExtractor(conn);
				long size = 0;
				InputStream instream = fileItem.getInputStream();
				OutputStream outstream = null;
				try {
					pst = nativeConn.prepareStatement("update fnd_atm_attachment t set t.content = empty_blob() where t.attachment_id= ?");
					pst.setObject(1,aid);
					pst.executeUpdate();
					pst = nativeConn.prepareStatement("select content from fnd_atm_attachment t where t.attachment_id = ? for update");
					pst.setObject(1,aid);
					rs = pst.executeQuery();
					
//					st = nativeConn.createStatement();
//					st.executeUpdate("update fnd_atm_attachment t set t.content = empty_blob() where t.attachment_id=" + aid);
//					rs = st.executeQuery("select content from fnd_atm_attachment t where t.attachment_id = " + aid + " for update");
					if (!rs.next())
						throw new IllegalArgumentException("attachment_id not set");
					BLOB blob = ((oracle.jdbc.OracleResultSet) rs).getBLOB(1);
					rs.close();
					if (blob == null) {
						throw new IllegalArgumentException("Warning: can't update fnd_atm_attachment.content for recrd " + aid);
					}
					outstream = blob.getBinaryOutputStream(0);
					int chunk = blob.getChunkSize();
					byte[] buff = new byte[chunk];
					int le;
					while ((le = instream.read(buff)) != -1) {
						outstream.write(buff, 0, le);
						size += le;
					}
					pst = nativeConn.prepareStatement("update fnd_atm_attachment a set a.file_size = ? where a.attachment_id = ?");
					pst.setObject(1,size);
					pst.setObject(2,aid);
					pst.executeUpdate();
					
//		            st.executeUpdate("update fnd_atm_attachment a set a.file_size = "+size+" where a.attachment_id = "+aid);
				} finally {
					if(outstream!=null)outstream.close();
					if(instream!=null)instream.close();
					DBUtil.closeResultSet(rs);
					DBUtil.closeStatement(pst);
				}
			}
		}
	}
	
	
	private void doUpload(CompositeMap context) throws Exception{
		ServiceContext service = ServiceContext.createServiceContext(context);
		HttpServiceInstance serviceInstance = (HttpServiceInstance) ServiceInstance.getInstance(context);
		
		CompositeMap params = service.getParameter();
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload up = new ServletFileUpload(factory);
//		int ms = getFileSize();
//		if(ms > 0)up.setSizeMax(ms);
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
					if(PROPERTITY_URL.equalsIgnoreCase(name)){
						url = value;
					}else if(PROPERTITY_ACTION_TYPE.equalsIgnoreCase(name)){
						actionType = value;
					}else{
						params.put(name, value);
						if("attachment_id".equalsIgnoreCase(name))
						serviceInstance.getRequest().setAttribute("attachment_id", value);
					}
				} else {
					List fts = Arrays.asList(getFileType().split(";"));
					List fsz = Arrays.asList(getFileSize().split(";"));
					String name = fileItem.getName().toLowerCase(); 
					String ft = name.substring(name.lastIndexOf(".")+1,name.length());
					int index = fts.indexOf("*."+ft);
					if("*.*".equals(getFileType()) || index !=-1){
						String fl = "";
						if(fsz.size()!=fts.size()){
							fl = (String)fsz.get(0);
						}else if(index!=-1){
							fl = (String)fsz.get(index);
						}
						if(!"".equals(fl) && fileItem.getSize() > 1024*Integer.valueOf(fl)){
							throw new Exception("上传文件超出大小限制!");
						}
						files.add(fileItem);							
					}else {
						throw new Exception("文件类型不匹配!只允许 " + fts);	
					}
				}
			}
			Iterator it = files.iterator();
			while (it.hasNext()) {
				FileItem fileItem = (FileItem) it.next();
				File file = new File(fileItem.getName());
				String file_name = file.getName();
				if("".equals(file_name)) continue;
				params.put("file_name", file_name);
				params.put("file_size", new Long(fileItem.getSize()));
				BusinessModelService modelService = databasefactory.getModelService(FND_UPLOAD_FILE_TYPE, context);
				modelService.execute(null);
				Object aid = service.getModel().getObject("/parameter/@attachment_id");
				conn = getContextConnection(context);
	            InputStream in = fileItem.getInputStream();
	            String attach_id = aid.toString();
	            if(SAVE_TYPE_DATABASE.equalsIgnoreCase(getSaveType())){
	            	writeBLOB(conn, in, attach_id);	            	
	            }else if(SAVE_TYPE_FILE.equalsIgnoreCase(getSaveType())){
	            	writeFile(context,conn, in, attach_id, file_name);
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
			LoggingContext.getLogger(context,AttachmentManager.class.getCanonicalName()).log(ex.getMessage());
			throw ex;
		} 
	}
	
	
	private void writeFile(CompositeMap context,Connection conn,InputStream instream, String aid,String fileName) throws Exception {
		if("".equals(fileName)) return;
		if("true".equals(getRandomName())) {
			fileName = IDGenerator.getInstance().generate();
		}
    	String datePath = sdf.format(new Date());
    	String path = getSavePath(ServiceContext.createServiceContext(context).getModel()).replaceAll("\\\\", "/");
    	if(path.charAt(path.length()-1)!='/') path += "/";
    	if("true".equalsIgnoreCase(getUseSubFolder())) path += datePath;
    	FileUtils.forceMkdir(new File(path));
    	PreparedStatement pst = null;
        try{
            long size = 0;
            int len;
            byte[] b = new byte[1024 * 8];
            //Write file to disk
            FileOutputStream fos ;
            File file = new File(path,fileName);
            fos = new FileOutputStream(file);
            while(( len = instream.read(b))>=0){
                fos.write(b, 0, len);
                size++;
            }
            fos.close();
            // Update attachment record
            pst = conn.prepareStatement("update fnd_atm_attachment a set a.file_path = ? where a.attachment_id = ?");
            pst.setObject(1,file.getPath());
			pst.setObject(2,aid);
			pst.executeUpdate();
        }finally{
            DBUtil.closeStatement(pst);
        }
	}

	private long writeBLOB(Connection conn, InputStream instream, String aid) throws Exception {
		if(conn.getAutoCommit()){
			conn.setAutoCommit(false);
		}
		DataSourceConfig dataSourceConfig=(DataSourceConfig)this.registry.getInstanceOfType(DataSourceConfig.class);
		Connection nativeConn=dataSourceConfig.getNativeJdbcExtractor(conn);
		long size = 0;
		PreparedStatement pst = null;
//		Statement st = null;
		ResultSet rs = null;
		OutputStream outstream = null;
		try {
			pst = nativeConn.prepareStatement("update fnd_atm_attachment t set t.content = empty_blob() where t.attachment_id=?");
			pst.setObject(1, aid);
			pst.executeUpdate();			
			
//			st = nativeConn.createStatement();
//			st.executeUpdate("update fnd_atm_attachment t set t.content = empty_blob() where t.attachment_id=" + aid);
			
			pst = nativeConn.prepareStatement("select content from fnd_atm_attachment t where t.attachment_id = ? for update");
			pst.setObject(1, aid);
			rs = pst.executeQuery();

//			rs = st.executeQuery("select content from fnd_atm_attachment t where t.attachment_id = " + aid + " for update");
			if (!rs.next())
				throw new IllegalArgumentException("attachment_id not set");
			
			
			BLOB blob = ((oracle.jdbc.OracleResultSet) rs).getBLOB(1);
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
//			st.execute("commit");
//			st.close();
			instream.close();
//			conn.commit();
			return size;
		} finally {
			DBUtil.closeResultSet(rs);
			DBUtil.closeStatement(pst);
		}
	}

	public String getSaveType() {
		return saveType == null ? SAVE_TYPE_DATABASE : saveType;
	}

	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getSavePath(CompositeMap model) {
		String sp = getSavePath();
		return sp == null ? "." : uncertain.composite.TextParser.parse(sp, model);
	}
	
	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public String getActionType() {
		return actionType == null ? "upload" : actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	
	public String processFileName(HttpServletRequest request, String filename) throws UnsupportedEncodingException {
		String userAgent = request.getHeader("User-Agent");
		String new_filename = URLEncoder.encode(filename, "UTF8");
		// 如果没有UA，则默认使用IE的方式进行编码，因为毕竟IE还是占多数的
		String rtn = "filename=\"" + new_filename + "\"";
		if (userAgent != null) {
			userAgent = userAgent.toLowerCase();
			// IE浏览器，只能采用URLEncoder编码
			if (userAgent.indexOf("msie") != -1) {
				rtn = "filename=\"" + new String(filename.getBytes("gb2312"),"iso-8859-1") + "\"";
			}
//			if (userAgent.indexOf("msie 6") != -1 || userAgent.indexOf("msie 7") != -1) {
//				rtn = "filename=\"" + new String(filename.getBytes("gb2312"),"iso-8859-1") + "\"";
//			}
//			else if (userAgent.indexOf("msie") != -1) {
//				rtn = "filename=\"" + new_filename + "\"";
//			}
			// Opera浏览器只能采用filename*
			else if (userAgent.indexOf("opera") != -1) {
				rtn = "filename*=UTF-8''" + new_filename;
			}
			// Safari浏览器，只能采用ISO编码的中文输出
			else if (userAgent.indexOf("safari") != -1) {
				rtn = "filename=\""
						+ new String(filename.getBytes("UTF-8"), "ISO8859-1")
						+ "\"";
			}
			// Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
			else if (userAgent.indexOf("applewebkit") != -1) {
				new_filename = MimeUtility.encodeText(filename, "UTF8", "B");
				rtn = "filename=\"" + new_filename + "\"";
			}
			// FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
			else if (userAgent.indexOf("mozilla") != -1) {
				rtn = "filename*=UTF-8''" + new_filename;
			}
		}
		return rtn;
	}
	
//	public static String toUtf8String(String s) {
//		StringBuffer sb = new StringBuffer();
//		for (int i = 0; i < s.length(); i++) {
//			char c = s.charAt(i);
//			if ((c >= 0) && (c <= 255)) {
//				sb.append(c);
//			} else {
//				byte[] b;
//				try {
//					b = Character.toString(c).getBytes("utf-8");
//				} catch (Exception ex) {
//					System.out.println(ex);
//					b = new byte[0];
//				}
//				for (int j = 0; j < b.length; j++) {
//					int k = b[j];
//					if (k < 0) {
//						k += 256;
//					}
//					sb.append("%" + Integer.toHexString(k).toUpperCase());
//				}
//			}
//		}
//		return sb.toString();
//	}

	public String getUseSubFolder() {
		return useSubFolder == null ? "true" : useSubFolder;
	}

	public void setUseSubFolder(String useSubFolder) {
		this.useSubFolder = useSubFolder;
	}

	public String getRandomName() {
		return randomName;
	}

	public void setRandomName(String randomName) {
		this.randomName = randomName;
	}
	
	public String getDataSourceName() {
		return dataSourcename;
	}

	public void setDataSourceName(String name) {
		this.dataSourcename = name;
	}


}
