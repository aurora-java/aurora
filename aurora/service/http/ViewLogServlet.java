package aurora.service.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.core.UncertainEngine;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;
import aurora.application.features.ServiceLogging;

public class ViewLogServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9084871702386808386L;
	
	String logPath;
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext  context = config.getServletContext();
		UncertainEngine uncertainEngine = WebContextInit.getUncertainEngine(context);
		if (uncertainEngine == null)
			throw new ServletException("Uncertain engine not initialized");

		// get global service config
		IObjectRegistry reg = uncertainEngine.getObjectRegistry();
		if (reg == null)
			throw new ServletException("IObjectRegistry not initialized");
		ServiceLogging serviceLogging = (ServiceLogging)reg.getInstanceOfType(ServiceLogging.class);
		if(serviceLogging == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(null, ServiceLogging.class, "aurora.service.http.ViewLogServlet");
		logPath=serviceLogging.getLogPath();
		
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
		PrintWriter out = response.getWriter();
		response.setContentType("text/html;charset=UTF-8");// 设置响应的MIME类型。
		out.println("<HTML>");
		out.println("<BODY>");
		File f = new File(fileName);
		BufferedReader bufferin = null;
		try {
			FileReader in = new FileReader(f);
			bufferin = new BufferedReader(in);
			String str = null;
			while ((str = bufferin.readLine()) != null) {
				out.print("<BR>" + str);
			}
			bufferin.close();
			in.close();
			out.println("</BODY>");
			out.println("</HTML>");
		}finally{
			if(bufferin != null){
				try {
					bufferin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(out != null){
				out.close();
			}
		}
	}
}
