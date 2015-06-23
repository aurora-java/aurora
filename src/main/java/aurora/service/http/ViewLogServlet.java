package aurora.service.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.core.DirectoryConfig;
import uncertain.core.UncertainEngine;
import uncertain.ocm.IObjectRegistry;
import aurora.application.features.ServiceLogging;

public class ViewLogServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9084871702386808386L;
	
	String logPath;
	
	DirectoryConfig mDirConfig;
	IObjectRegistry reg;
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext  context = config.getServletContext();
		UncertainEngine uncertainEngine = WebContextInit.getUncertainEngine(context);
		if (uncertainEngine == null)
			throw new ServletException("Uncertain engine not initialized");

		reg = uncertainEngine.getObjectRegistry();
		if (reg == null)
			throw new ServletException("IObjectRegistry not initialized");

		 mDirConfig = uncertainEngine.getDirectoryConfig();
		logPath = getLogPath();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{
		String fileName = request.getParameter("file");
		if(fileName == null || !fileName.endsWith(".log"))
			return;
		File logBasePaht = new File(logPath);
		File logFile = new File(fileName);
		if(!logFile.getCanonicalPath().startsWith(logBasePaht.getCanonicalPath())){
			return;
		}
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = response.getWriter();
		File f = new File(fileName);
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
			String line = null;
			while ((line=bufferedReader.readLine())!= null) {
				out.println(line);
			}
			
		}finally{
			if(bufferedReader != null){
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(out != null){
				out.flush();
				out.close();
			}
		}
	}
    public String getLogPath() {
    	String path = null;
		ServiceLogging serviceLogging = (ServiceLogging)reg.getInstanceOfType(ServiceLogging.class);
		if(serviceLogging != null)
			path = serviceLogging.getLogPath();
        if(path==null)
            return mDirConfig.getLogDirectory();
        else
            return mDirConfig.translateRealPath(path);
    }
}
