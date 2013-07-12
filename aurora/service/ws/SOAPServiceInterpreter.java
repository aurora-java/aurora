/*
 * Created on 2007-11-4
 */
package aurora.service.ws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.composite.TextParser;
import uncertain.composite.XMLOutputter;
import uncertain.event.EventModel;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.ProcedureRunner;
import uncertain.util.LoggingUtil;
import aurora.database.FetchDescriptor;
import aurora.database.service.BusinessModelService;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.ServiceOutputConfig;
import aurora.service.http.HttpServiceInstance;

import com.sun.xml.internal.messaging.saaj.util.Base64;

public class SOAPServiceInterpreter {

	public static final String DEFAULT_SOAP_CONTENT_TYPE = "text/xml;charset=utf-8";

	public static final String HEAD_SOAP_PARAMETER = "soapaction";

	public static final QualifiedName ENVELOPE = new QualifiedName("soapenv", "http://schemas.xmlsoap.org/soap/envelope/", "Envelope");
	public static final QualifiedName BODY = new QualifiedName("soapenv", "http://schemas.xmlsoap.org/soap/envelope/", "Body");

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private IObjectRegistry mRegistry;

	public SOAPServiceInterpreter(IObjectRegistry registry) {
		this.mRegistry = registry;
	}

	public int preParseParameter(ServiceContext service_context) throws Exception {
		if (!isSOAPRequest(service_context)) {
			return EventModel.HANDLE_NORMAL;
		}
		service_context.setRequestType(HEAD_SOAP_PARAMETER);
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance.getInstance(service_context.getObjectContext());
		HttpServletRequest request = svc.getRequest();
		String soapActionParam = getSOAPAction(request);
		service_context.getObjectContext().putObject("/request/@soapaction", soapActionParam, true);

		String soapContent = inputStream2String(svc.getRequest().getInputStream());
		ILogger logger = LoggingContext.getLogger(service_context.getObjectContext(), this.getClass().getCanonicalName());
		logger.config("request:" + LINE_SEPARATOR + soapContent);
		if (soapContent == null || "".equals(soapContent))
			return EventModel.HANDLE_NORMAL;
		CompositeLoader cl = new CompositeLoader();
		CompositeMap soap = cl.loadFromString(soapContent, "UTF-8");
		CompositeMap parameter = (CompositeMap) soap.getChild(BODY.getLocalName()).getChilds().get(0);
		service_context.setParameter(parameter);
		parseAuthorization(service_context);
		logger.config("context:" + LINE_SEPARATOR + service_context.getObjectContext().toXML());
		return EventModel.HANDLE_NORMAL;
	}

	private void parseAuthorization(ServiceContext service_context) {
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance.getInstance(service_context.getObjectContext());
		String authorization = svc.getRequest().getHeader("Authorization");
		if (authorization != null) {
			String encodeAuth = authorization.substring("Basic ".length());
			String decode = Base64.base64Decode(encodeAuth);
			String[] strs = decode.split(":");
			CompositeMap record = new CompositeMap("Authorization");
			record.put("user", strs[0]);
			record.put("password", strs[1]);
			service_context.getObjectContext().addChild(record);
		}
	}

	public String inputStream2String(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		while ((i = is.read()) != -1) {
			baos.write(i);
		}
		String result = new String(baos.toByteArray(), "UTF-8");
		return result;
	}

	void prepareResponse(HttpServletResponse response)

	{
		response.setContentType(DEFAULT_SOAP_CONTENT_TYPE);
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
		response.setHeader("Server", "Simple-Server/1.1");
		response.setHeader("Transfer-Encoding", "chunked");
		response.setCharacterEncoding("UTF-8");
	}

	public void writeResponse(ServiceContext service_context) throws Exception {
		CompositeMap context = service_context.getObjectContext();
		ILogger logger = LoggingContext.getLogger(context, this.getClass().getCanonicalName());
		logger.config("context:" + context.toString());
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance.getInstance(context);
		HttpServletRequest request = svc.getRequest();
		if (!isSOAPRequest(request))
			return;
		boolean isBMRequest = isBMRequest(request);
		ISOAPConfiguration soapConfiguration = (ISOAPConfiguration) mRegistry.getInstanceOfType(ISOAPConfiguration.class);
		if (soapConfiguration == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(null, ISOAPConfiguration.class, this.getClass().getName());
		CompositeMap dbConfig = queryDBConfig(soapConfiguration, request);

		CompositeMap body = createSOAPBody();
		if (dbConfig != null && dbConfig.getChilds() != null) {
			CompositeMap record = (CompositeMap) dbConfig.getChilds().get(0);
			String response_xml = TextParser.parse(record.getString("response_xml"), context);
			CompositeMap response_content = (new CompositeLoader()).loadFromString(response_xml, "UTF-8");
			body.addChild(response_content);
		} else {
			String multi = request.getParameter("multi");
			if (isBMRequest && !"Y".equalsIgnoreCase(multi)) {
				CompositeMap autoResponse = soapConfiguration.getDefaultResponse();
				String response_xml = TextParser.parse(XMLOutputter.defaultInstance().toXML(autoResponse), context);
				CompositeMap response_content = (new CompositeLoader()).loadFromString(response_xml, "UTF-8");
				body.addChild(response_content);
			} else {
				String output = null;
				ServiceOutputConfig cfg = svc.getServiceOutputConfig();
				if (cfg != null)
					output = cfg.getOutput();

				boolean write_result = service_context.getBoolean("write_result", true);
				if (write_result) {
					CompositeMap result = null;
					if (output != null) {
						Object obj = service_context.getObjectContext().getObject(output);
						if (obj != null) {
							if (!(obj instanceof CompositeMap))
								throw new IllegalArgumentException("Target for SOAP output is not instance of CompositeMap: " + obj);
							result = (CompositeMap) obj;
						} else
							result = new CompositeMap("result");
					} else {
						result = service_context.getModel();
					}
					result.put("success", service_context.isSuccess());
					if(result.getNamespaceURI() == null){
						result.setNameSpaceURI(WSDLUtil.TARGET_NAMESPACE);
					}
					body.addChild(result);
				}
			}
		}
		prepareResponse(svc.getResponse());
		PrintWriter out = svc.getResponse().getWriter();
		out.append("<?xml version='1.0' encoding='UTF-8'?>").append(LINE_SEPARATOR);
		String content = XMLOutputter.defaultInstance().toXML(body.getRoot());
		logger.config("response content:" + LINE_SEPARATOR + content);
		out.print(content);
		out.flush();
	}

	public void onCreateSuccessResponse(ServiceContext service_context) throws Exception {
		if (isSOAPRequest(service_context))
			writeResponse(service_context);
	}

	public void onCreateFailResponse(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		ServiceContext service_context = ServiceContext.createServiceContext(context);
		if (!isSOAPRequest(service_context))
			return;
		service_context.setRequestType(HEAD_SOAP_PARAMETER);
		// log exception
		ILogger logger = LoggingContext.getLogger(context, ServiceInstance.LOGGING_TOPIC);
		Throwable thr = runner.getException();
		if (thr != null) {
			LoggingUtil.logException(thr, logger);
		}
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance.getInstance(context);
		HttpServletResponse response = svc.getResponse();
		prepareResponse(response);
		PrintWriter out = response.getWriter();
		out.append("<?xml version='1.0' encoding='UTF-8'?>");
		CompositeMap body = createSOAPBody();
		CompositeMap soap_error = service_context.getObjectContext().getChild("result");
		if (soap_error != null) {
			body.addChild(soap_error);
		} else {
			ISOAPConfiguration soapConfiguration = (ISOAPConfiguration) mRegistry.getInstanceOfType(ISOAPConfiguration.class);
			if (soapConfiguration == null)
				throw BuiltinExceptionFactory.createInstanceNotFoundException(null, ISOAPConfiguration.class, this.getClass().getName());
			CompositeMap dbConfig = queryDBConfig(soapConfiguration, svc.getRequest());
			if (dbConfig != null && dbConfig.getChilds() != null) {
				CompositeMap record = (CompositeMap) dbConfig.getChilds().get(0);
				String response_xml = TextParser.parse(record.getString("response_xml"), context);
				CompositeMap response_content = (new CompositeLoader()).loadFromString(response_xml, "UTF-8");
				body.addChild(response_content);
			} else {
				CompositeMap defaultResponse = soapConfiguration.getDefaultResponse();
				String response_xml = TextParser.parse(XMLOutputter.defaultInstance().toXML(defaultResponse), context);
				CompositeMap response_content = (new CompositeLoader()).loadFromString(response_xml, "UTF-8");
				body.addChild(response_content);
			}
		}
		out.println(body.getRoot().toXML());
		out.flush();
	}

	private boolean isBMRequest(HttpServletRequest request) {
		if ("/autocrud".equalsIgnoreCase(request.getServletPath()))
			return true;
		return false;
	}

	private CompositeMap queryDBConfig(ISOAPConfiguration soapConfiguration, HttpServletRequest request) throws Exception {
		String model = soapConfiguration.getModel();
		if (model == null)
			return null;
		String url = request.getRequestURL().toString();
		CompositeMap parameter = new CompositeMap("parameter");
		parameter.put("url", url);
		parameter.put("response_format_fixed", "Y");
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

	private boolean isSOAPRequest(ServiceContext service_context) {
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance.getInstance(service_context.getObjectContext());
		return isSOAPRequest(svc.getRequest());
	}

	private String getSOAPAction(HttpServletRequest svc) {
		String soapParam = svc.getHeader(HEAD_SOAP_PARAMETER);
		if (soapParam != null)
			return soapParam;
		soapParam = svc.getParameter(HEAD_SOAP_PARAMETER);
		if (soapParam != null)
			return soapParam;
		return null;
	}

	private boolean isSOAPRequest(HttpServletRequest svc) {
		String soapParam = getSOAPAction(svc);
		if (soapParam != null)
			return true;
		return false;
	}

	private CompositeMap createSOAPBody() {
		CompositeMap env = new CompositeMap(ENVELOPE.getPrefix(), ENVELOPE.getNameSpace(), ENVELOPE.getLocalName());
		CompositeMap body = new CompositeMap(BODY.getPrefix(), BODY.getNameSpace(), BODY.getLocalName());
		env.addChild(body);
		return body;
	}
}
