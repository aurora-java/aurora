package aurora.service.http;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.composite.XMLOutputter;
import uncertain.core.UncertainEngine;
import uncertain.ocm.IObjectRegistry;
import aurora.bm.BusinessModel;
import aurora.bm.Operation;
import aurora.database.service.DatabaseServiceFactory;
import aurora.service.ws.ISOAPConfiguration;
import aurora.service.ws.WSDLGenerator;

public class WSDLServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String SERVLET_NAME = "wsdl";
	DatabaseServiceFactory mDatabaseServiceFactory;
	private boolean enableDefaultResponse = true;
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext context = config.getServletContext();
		UncertainEngine uncertainEngine = WebContextInit.getUncertainEngine(context);
		if (uncertainEngine == null)
			throw new ServletException("Uncertain engine not initialized");

		// get global service config
		IObjectRegistry reg = uncertainEngine.getObjectRegistry();
		if (reg == null)
			throw new ServletException("IObjectRegistry not initialized");
		mDatabaseServiceFactory = (DatabaseServiceFactory) reg.getInstanceOfType(DatabaseServiceFactory.class);
		ISOAPConfiguration soapConfiguration = (ISOAPConfiguration)reg.getInstanceOfType(ISOAPConfiguration.class);
		if(soapConfiguration != null)
			enableDefaultResponse = soapConfiguration.isEnableDefaultResponse();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		String[] args = uri.split("/");
		if (args.length < 4) {
			throw new ServletException("Invalid request format");
		}
		int start_index = 0;
		for (int i = 0; i < args.length; i++) {
			String tmp = args[i];
			if (SERVLET_NAME.equals(tmp)) {
				start_index = i;
				break;
			}
		}
		String object_name = args[(start_index + 1)];
		String operation_name = null;
		if (start_index + 2 < args.length)
			operation_name = args[(start_index + 2)];

		BusinessModel bm = mDatabaseServiceFactory.getModelFactory().getModelForRead(object_name);
		if (bm == null)
			throw new ServletException("Can't load model:" + object_name);
		WSDLGenerator wsdlGenerator;
		String fullUrl = getFullUrl(request);
		if (operation_name == null) {
			wsdlGenerator = new WSDLGenerator(bm, fullUrl);
		} else if (Operation.QUERY.equalsIgnoreCase(operation_name)) {
			String multi = request.getParameter("multi_flag");
			if ("Y".equalsIgnoreCase(multi)) {
				wsdlGenerator = new WSDLGenerator(bm, fullUrl, true);
			} else {
				wsdlGenerator = new WSDLGenerator(bm, fullUrl, false);
			}
		} else {
			wsdlGenerator = new WSDLGenerator(bm, fullUrl, operation_name);
		}
		wsdlGenerator.setEnableDefaultResponse(enableDefaultResponse);
		response.setContentType("text/plain;charset=UTF-8");// 设置响应的MIME类型。
		PrintWriter out = response.getWriter();
		try {
			String content = XMLOutputter.defaultInstance().toXML(wsdlGenerator.run(), true);
			out.print(content);
			out.flush();
		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

	private String getFullUrl(HttpServletRequest request) {
		StringBuffer url = request.getRequestURL();
		if (request.getQueryString() != null) {
			url.append("?");
			url.append(request.getQueryString());
		}
		String request_url = url.toString();
		String autocrud = request_url.replace("wsdl", "autocrud");
		return autocrud;
	}
}
