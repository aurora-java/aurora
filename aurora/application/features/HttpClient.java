package aurora.application.features;

import java.io.InputStream;
import java.io.OutputStream;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureRunner;
import aurora.bm.BusinessModel;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;
import aurora.service.http.WebContextInit;

public class HttpClient extends HttpServlet {
	private static final long serialVersionUID = -3144250964634670506L;
	String KEY_ADDRESS = "address";
	String KEY_PROCEDURE="procedure";
	String KEY_OUTPUT="output";	
	
	String procedure;
	String returnPath;
	String address;
	IProcedureManager mProcManager;
	ProcedureRunner mRunner;
	UncertainEngine mUncertainEngine;
	DatabaseServiceFactory svcFactory;
	
	protected void service(HttpServletRequest request,
			HttpServletResponse response) {	
		
		boolean is_check=false;		
		try {
			if(!mUncertainEngine.isRunning()){
		        StringBuffer msg = new StringBuffer("Application failed to initialize");
		        Throwable thr = mUncertainEngine.getInitializeException();
		        if(thr!=null)
		            msg.append(":").append(thr.getMessage());
		        response.sendError(500, msg.toString());
		        return;
		    }		    
			String param=getParam(request);
			if(procedure!=null){
				mProcManager=mUncertainEngine.getProcedureManager();
				Procedure proc = mProcManager.loadProcedure(procedure);				
				
				CompositeMap context=new CompositeMap("context");
				HttpSession httpSession=request.getSession();
				Enumeration<String> enume=httpSession.getAttributeNames();
				CompositeMap session=context.createChild("session");
				while(enume.hasMoreElements()){
					String key=enume.nextElement();
					session.put(key, httpSession.getAttribute(key));
				}			
			
				CompositeMap parameter=context.createChild("parameter");				
				parameter.putString("param", param);				
				
				mRunner= new ProcedureRunner();
				mRunner.setProcedure(proc);
				mRunner.setContext(context);
				mRunner.run();						
				Object msg=context.getObject(returnPath);
				if(msg!=null){				
					  response.sendError(500, msg.toString());
				}else{
					is_check=true;
				}	
				
				BusinessModel bm=(BusinessModel)context.get("BusinessModel");
				BusinessModelService service = svcFactory.getModelService(bm, context);
				service.getServiceContext().freeConnection();				
			}else{
				is_check=true;
			}	
			if(is_check)
				writeResponse(response, address+"?"+param);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public String getParam(HttpServletRequest request) {
		String paramName;
		String[] paramValues;
		StringBuffer params = new StringBuffer();
		int i, length;
		boolean isFirst = true;
		Enumeration<String> enumn = request.getParameterNames();
		while (enumn.hasMoreElements()) {
			paramName = enumn.nextElement();
			paramValues = request.getParameterValues(paramName);
			for (i = 0, length = paramValues.length; i < length; i++) {
				if (isFirst) {
					// params.append("?");
					isFirst = false;
				} else {
					params.append("&");
				}
				params.append(paramName);
				params.append("=");
				params.append(paramValues[i].replace(' ', '+'));
			}
		}
		return params.toString();
	}

	public void writeResponse(HttpServletResponse httpResponse, String url)
			throws Exception {
		// httpClient4.1.2
		DefaultHttpClient httpclient = new DefaultHttpClient();
		OutputStream os = null;
		InputStream is = null;
		try {
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				os = httpResponse.getOutputStream();
				is = entity.getContent();
				int Buffer_size = 50 * 1024;
				byte buf[] = new byte[Buffer_size];
				int len;
				while ((len = is.read(buf)) > 0)
					os.write(buf, 0, len);
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception ignore) {
				}
			}

			if (os != null) {
				try {
					os.close();
				} catch (Exception ignore) {
				}
			}
			httpclient.getConnectionManager().shutdown();
		}
	}	
	
	public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mUncertainEngine= (UncertainEngine) this.getServletContext().getAttribute(WebContextInit.KEY_UNCERTAIN_ENGINE);
        svcFactory=(DatabaseServiceFactory) mUncertainEngine.getObjectRegistry().getInstanceOfType(DatabaseServiceFactory.class);
		procedure=super.getInitParameter(KEY_PROCEDURE);
		returnPath=super.getInitParameter(KEY_OUTPUT);
		address=super.getInitParameter(KEY_ADDRESS);
    }
}
