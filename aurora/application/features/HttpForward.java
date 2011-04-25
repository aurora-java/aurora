package aurora.application.features;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
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
		Enumeration enum=request.getParameterNames();
		while (enum.hasMoreElements()) {
			paramName = (String) enum.nextElement();
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
		ReadableByteChannel rbc = null;
		WritableByteChannel wbc = null;
		HttpURLConnection connection=null;
		int Buffer_size = 500 * 1024;
		try{	
			URL postUrl = new URL(url);
			connection = (HttpURLConnection)postUrl.openConnection();
			connection.connect();
			response.setContentType(connection.getContentType());			
			response.setContentLength(connection.getContentLength());
			response.addHeader("Content-Disposition",connection.getHeaderField("Content-Disposition"));
			
			os = response.getOutputStream();
			is = connection.getInputStream();
			rbc = Channels.newChannel(is);
			wbc = Channels.newChannel(os);
			ByteBuffer buf = ByteBuffer.allocate(Buffer_size);
			while ((rbc.read(buf)) > 0) {
				buf.position(0);
				wbc.write(buf);
				buf.clear();
				os.flush();
			}
		}finally{
			if (rbc != null){
				try {
					rbc.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
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
