package aurora.service.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;
import uncertain.core.UncertainEngine;
import uncertain.event.RuntimeContext;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import aurora.application.features.ServiceLogging;
import aurora.bm.BusinessModel;
import aurora.bm.Operation;
import aurora.database.FetchDescriptor;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.service.ws.ISOAPConfiguration;
import aurora.service.ws.BMWSDLGenerator;

public class WSDLServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String SERVLET_NAME = "wsdl";
	private IObjectRegistry mRegistry;
	private DatabaseServiceFactory mDatabaseServiceFactory;
	private ISOAPConfiguration soapConfiguration;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext context = config.getServletContext();
		UncertainEngine uncertainEngine = WebContextInit.getUncertainEngine(context);
		if (uncertainEngine == null)
			throw new ServletException("Uncertain engine not initialized");

		// get global service config
		mRegistry = uncertainEngine.getObjectRegistry();
		if (mRegistry == null)
			throw new ServletException("IObjectRegistry not initialized");
		mDatabaseServiceFactory = (DatabaseServiceFactory) mRegistry.getInstanceOfType(DatabaseServiceFactory.class);
		soapConfiguration = (ISOAPConfiguration) mRegistry.getInstanceOfType(ISOAPConfiguration.class);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String wsdl = "";
		CompositeMap dbConfig = null;
		ILogger appLogger = LoggingContext.getLogger(this.getClass().getCanonicalName(), mRegistry);
		CompositeMap context = new CompositeMap("context");
		try {
			HttpServiceInstance svc = createServiceInstance(request, response);
			context = svc.getContextMap();
		} catch (Exception e) {
			appLogger.log(Level.SEVERE, "", e);
		}
		ILogger pageLogger = LoggingContext.getLogger(context, this.getClass().getCanonicalName());
		try {
			dbConfig = queryDBConfig(soapConfiguration, request, pageLogger);
		} catch (Exception e) {
			pageLogger.log(Level.SEVERE, "", e);
		}
		if (dbConfig != null && dbConfig.getChilds() != null) {
			CompositeMap record = (CompositeMap) dbConfig.getChilds().get(0);
			wsdl = record.getString("wsdl");
		} else {
			boolean isBMRequest = isBMRequest(request);
			if (!isBMRequest) {
				wsdl = "<error>Svc'wsdl must define first!</error>";
			} else {
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
				BMWSDLGenerator wsdlGenerator;
				String fullUrl = getFullUrl(request);
				if (operation_name == null) 
					throw new ServletException("pelease enter operation");
				if (Operation.QUERY.equalsIgnoreCase(operation_name)) {
					String multi = request.getParameter("multi");
					if ("Y".equalsIgnoreCase(multi)) {
						wsdlGenerator = new BMWSDLGenerator(bm, fullUrl, true);
					} else {
						wsdlGenerator = new BMWSDLGenerator(bm, fullUrl, false);
					}
				} else {
					wsdlGenerator = new BMWSDLGenerator(bm, fullUrl, operation_name);
				}
				wsdlGenerator.setDefaultResponse(soapConfiguration.getDefaultResponse());
				wsdl = XMLOutputter.defaultInstance().toXML(wsdlGenerator.run(), true);
			}
		}
		response.setContentType("text/plain;charset=UTF-8");// 设置响应的MIME类型。
		PrintWriter out = response.getWriter();
		try {
			out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			out.print(wsdl);
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

	private CompositeMap queryDBConfig(ISOAPConfiguration soapConfiguration, HttpServletRequest request, ILogger appLogger) throws Exception {
		String model = soapConfiguration.getModel();
		if (model == null)
			return null;
		String url = request.getRequestURL().toString();
		appLogger.config("request Url:" + url);
		String accessUrl = "";
		boolean isBMRequest = isBMRequest(request);
		if(isBMRequest){
			accessUrl = url.replace("wsdl", "autocrud");
		}else{
			accessUrl = url.replace("wsdl/", "");
		}
		appLogger.config("accessUrl :" + accessUrl);
		CompositeMap parameter = new CompositeMap("parameter");
		parameter.put("url", accessUrl);
		parameter.put("enabled_flag","Y");
		return queryBM(model, parameter);
	}

	private CompositeMap queryBM(String bm_name, CompositeMap parameter) throws Exception {
		IDatabaseServiceFactory databaseServiceFactory = (IDatabaseServiceFactory) mRegistry.getInstanceOfType(IDatabaseServiceFactory.class);
		if (databaseServiceFactory == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(null, IDatabaseServiceFactory.class, this.getClass().getName());
		SqlServiceContext sqlContext = databaseServiceFactory.createContextWithConnection();
		try {
			CompositeMap context = sqlContext.getObjectContext();
			if (context == null)
				context = new CompositeMap();
			BusinessModelService service = databaseServiceFactory.getModelService(bm_name, context);
			CompositeMap resultMap = service.queryAsMap(parameter, FetchDescriptor.fetchAll());
			return resultMap;
		} finally {
			if (sqlContext != null)
				sqlContext.freeConnection();
		}
	}

	private boolean isBMRequest(HttpServletRequest request) {
		if (request.getRequestURI().endsWith(".svc"))
			return false;
		return true;
	}

	private HttpServiceInstance createServiceInstance(HttpServletRequest request, HttpServletResponse response) throws Exception {
		final String name = FacadeServlet.getServiceName(request);
		HttpServiceFactory serviceFactory = (HttpServiceFactory) mRegistry.getInstanceOfType(HttpServiceFactory.class);
		if (serviceFactory == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(null, HttpServiceFactory.class, this.getClass().getName());
		final HttpServiceInstance svc = serviceFactory.createHttpService(name, request, response, this);

		ServiceLogging serviceLogging = (ServiceLogging) mRegistry.getInstanceOfType(ServiceLogging.class);
		if (serviceLogging == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(null, ServiceLogging.class, this.getClass().getName());
		CompositeMap context = svc.getContextMap();
		serviceLogging.onContextCreate(RuntimeContext.getInstance(context));
		return svc;
	}
}
