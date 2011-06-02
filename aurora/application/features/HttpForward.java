package aurora.application.features;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Enumeration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpForward extends HttpServlet{
	public static String KEY_ADDRESS="address"; 
	protected void service(HttpServletRequest request,
			HttpServletResponse response){
		try {
			writeResponse(response,getHttpUrl(request));
		} catch (Exception e) {			
			e.printStackTrace();
		}		
	}
		
	public String getHttpUrl(HttpServletRequest request){
		String address=super.getInitParameter(KEY_ADDRESS),paramName;		
		String[] paramValues;
		StringBuffer params=new StringBuffer();
		int i,length;
		boolean isFirst=true;
		Enumeration enumn=request.getParameterNames();
		while (enumn.hasMoreElements()) {
			paramName = (String) enumn.nextElement();
			paramValues=request.getParameterValues(paramName);
			for(i=0,length=paramValues.length;i<length;i++){
				if(isFirst){
					params.append("?");
					isFirst=false;
				}else{
					params.append("&");
				}
				params.append(paramName);
				params.append("=");
				params.append(paramValues[i].replace(' ', '+'));					
			}
		}		
		return address+params;
	}
		
	public void	writeResponse(HttpServletResponse response,String url) throws Exception{
		OutputStream os = null;
		InputStream is = null;		
		HttpURLConnection connection=null;
		int Buffer_size = 50 * 1024;
		try{	
			URL postUrl = new URL(url);
			connection = (HttpURLConnection)postUrl.openConnection();
			connection.setReadTimeout(0);
			connection.connect();			
			response.setContentType(connection.getContentType());			
			response.setContentLength(connection.getContentLength());
			response.addHeader("Content-Disposition",connection.getHeaderField("Content-Disposition"));			
			os = response.getOutputStream();
			try{
				is = connection.getInputStream();
			}catch(Exception e){
				is= connection.getErrorStream();
			}
			byte buf[]=new byte[Buffer_size];
			int len;
			while((len=is.read(buf))>0)
				os.write(buf,0,len);			
		}finally{			
			if (is != null){
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if (os != null){
				try {
					os.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if(connection!=null){
				connection.disconnect();
			}
		}
	}
}
