package aurora.application.action;

import java.awt.Composite;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aurora.application.util.MD5Util;
import aurora.database.service.BusinessModelService;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;
import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.ProcedureRunner;
import uncertain.util.LoggingUtil;

public class AttachmentResumable extends AttachmentManager {
	
	private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String UPLOAD_RESUMABLE = "resumable";
    
    private CompositeMap context;
    
    
	public AttachmentResumable(IObjectRegistry registry) {
		super(registry);
	}
	
	public void run(ProcedureRunner runner) throws Exception {
		context = runner.getContext();
		HttpServiceInstance serviceInstance = (HttpServiceInstance) ServiceInstance.getInstance(context);		
		HttpServletRequest request = serviceInstance.getRequest();
		HttpServletResponse response = serviceInstance.getResponse();
		String method = request.getMethod();
		String xupload = request.getHeader("x-upload");
		if(UPLOAD_RESUMABLE.equals(xupload)){
			if (method.equals(METHOD_GET)) {
				doGet(request, response);
			}else if(method.equals(METHOD_POST)) {
				doPost(request, response);
			}			
		}else{
			super.run(runner);		
		}
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int resumableChunkNumber        = getResumableChunkNumber(request);

        ResumableInfo info = getResumableInfo(request);
        
        RandomAccessFile raf = new RandomAccessFile(info.resumableFilePath, "rw");

        //Seek to position
        raf.seek((resumableChunkNumber - 1) * (long)info.resumableChunkSize);

        //Save to file
        InputStream is = request.getInputStream();
        long readed = 0;
        long content_length = request.getContentLength();
        byte[] bytes = new byte[1024 * 100];
        while(readed < content_length) {
            int r = is.read(bytes);
            if (r < 0)  {
                break;
            }
            raf.write(bytes, 0, r);
            readed += r;
        }
        raf.close();

        //Mark as uploaded.
        info.uploadedChunks.add(new ResumableInfo.ResumableChunkNumber(resumableChunkNumber));
        if (info.checkIfUploadFinished()) { //Check if all chunks uploaded, and change filename
            ResumableInfoStorage.getInstance().remove(info);
            String id = uploadFinished(info);
            response.getWriter().print("Finished-"+id);
        } else {
            response.getWriter().print("Upload");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int resumableChunkNumber        = getResumableChunkNumber(request);
        ResumableInfo info = getResumableInfo(request);

        if (info.uploadedChunks.contains(new ResumableInfo.ResumableChunkNumber(resumableChunkNumber))) {
            response.getWriter().print("Uploaded"); //This Chunk has been Uploaded.
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private int getResumableChunkNumber(HttpServletRequest request) {
        return HttpUtils.toInt(request.getParameter("resumableChunkNumber"), -1);
    }

    private ResumableInfo getResumableInfo(HttpServletRequest request) throws ServletException {
        String base_dir = getSavePath();

        int resumableChunkSize          = HttpUtils.toInt(request.getParameter("resumableChunkSize"), -1);
        long resumableTotalSize         = HttpUtils.toLong(request.getParameter("resumableTotalSize"), -1);
        String resumableIdentifier      = request.getParameter("resumableIdentifier");
        String userId 					= request.getParameter("user_id");
        String resumableFilename        = request.getParameter("resumableFilename");
//        String resumableRelativePath    = request.getParameter("resumableRelativePath");
        String resumableHashFileName    = MD5Util.md5Hex(userId+"-"+resumableFilename);
        //Here we add a ".temp" to every upload file to indicate NON-FINISHED
        File path = new File(base_dir,"temp");
        path.mkdir();
        String resumableFilePath  = new File(path, resumableHashFileName).getAbsolutePath();
        ResumableInfoStorage storage = ResumableInfoStorage.getInstance();

        ResumableInfo info = storage.get(this,resumableChunkSize, resumableTotalSize,resumableIdentifier, resumableFilename, resumableHashFileName, resumableFilePath);
       
        if (!info.vaild()) {
            storage.remove(info);
            throw new ServletException("Invalid request params.");
        }
        return info;
    }
    
    
    public String uploadFinished(ResumableInfo info) throws IOException{
    	String attach_id = null;
    	InputStream in = null;
    	ServiceContext service = ServiceContext.createServiceContext(context);
    	CompositeMap params = service.getParameter();
    	params.put("file_name", info.resumableFilename);
		params.put("file_size", new Long(info.resumableTotalSize));
		try {
	    	BusinessModelService modelService = databasefactory.getModelService(FND_UPLOAD_FILE_TYPE, context);
			modelService.execute(null);
			Object aid = service.getModel().getObject("/parameter/@attachment_id");
			Connection conn = getContextConnection(context);
			in = new FileInputStream(info.resumableFilePath);
	        attach_id = aid.toString();
        	if(SAVE_TYPE_DATABASE.equalsIgnoreCase(getSaveType())){
            	writeBLOB(conn, in, attach_id);	            	
            }else if(SAVE_TYPE_FILE.equalsIgnoreCase(getSaveType())){
            	writeFile(context,conn, in, attach_id, info.resumableFilename);
            }
        } catch (Exception e) {
        	ILogger logger = LoggingContext.getLogger(context,ServiceInstance.LOGGING_TOPIC);
			LoggingUtil.logException(e, logger);
			throw new IOException(e);
		}finally{
			if(in != null ) in.close();
        	File file = new File(info.resumableFilePath);        	
        	if(file.exists()) file.delete();
        }
        return attach_id;
    	
    }

}
