/*
 * Created on 2007-11-4
 */
package aurora.service.ws;

import java.io.ByteArrayOutputStream;
import com.sun.xml.internal.messaging.saaj.util.Base64;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xml.sax.SAXException;

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
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.ServiceOutputConfig;
import aurora.service.http.HttpServiceInstance;

public class SOAPServiceInterpreter {

	public static final String DEFAULT_SOAP_CONTENT_TYPE = "text/xml;charset=utf-8";

	public static final String HEAD_SOAP_PARAMETER = "soapaction";

	public static final QualifiedName ENVELOPE = new QualifiedName("soapenv", "http://schemas.xmlsoap.org/soap/envelope/", "Envelope");
	public static final QualifiedName BODY = new QualifiedName("soapenv", "http://schemas.xmlsoap.org/soap/envelope/", "Body");

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");;

	private IObjectRegistry mRegistry;

	/*
	 * static {
	 * MessageFactory.loadResource("resources.aurora_validation_exceptions"); }
	 */
	public SOAPServiceInterpreter(IObjectRegistry registry) {
		this.mRegistry = registry;
	}

	public int preParseParameter(ServiceContext service_context) throws Exception {
		if (!isSOAPRequest(service_context)) {
			return EventModel.HANDLE_NORMAL;
		}
		service_context.setRequestType(HEAD_SOAP_PARAMETER);
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance.getInstance(service_context.getObjectContext());
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
		return baos.toString();
	}

	void prepareResponse(HttpServletResponse response)

	{
		response.setContentType(DEFAULT_SOAP_CONTENT_TYPE);
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
		response.setHeader("Server", "Simple-Server/1.1");
		response.setHeader("Transfer-Encoding", "chunked");
		response.setCharacterEncoding("UTF-8");
	}

	public void writeResponse(ServiceContext service_context) throws IOException {
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance.getInstance(service_context.getObjectContext());
		if (!isSOAPRequest(svc.getRequest()))
			return;
		String output = null;
		ServiceOutputConfig cfg = svc.getServiceOutputConfig();
		if (cfg != null)
			output = cfg.getOutput();
		CompositeMap body = createSOAPBody();
		boolean write_result = service_context.getBoolean("write_result", true);
		if (write_result) {
			// CompositeMap result = context_map.getChild("result");
			CompositeMap result = null;
			if (output != null) {
				Object obj = service_context.getObjectContext().getObject(output);
				if (obj != null) {
					if (!(obj instanceof CompositeMap))
						throw new IllegalArgumentException("Target for SOAP output is not instance of CompositeMap: " + obj);
					result = (CompositeMap) obj;
				} else
					result = new CompositeMap("result");
			} else
				result = service_context.getModel();
			if (result != null) {
				result.put("success", service_context.isSuccess());
				body.addChild(result);
			}
		}
		prepareResponse(svc.getResponse());
		PrintWriter out = svc.getResponse().getWriter();
		out.append("<?xml version='1.0' encoding='UTF-8'?>");
		String content = XMLOutputter.defaultInstance().toXML(body.getRoot());
		LoggingContext.getLogger(service_context.getObjectContext(), this.getClass().getCanonicalName()).config(
				"response content:" + LINE_SEPARATOR + content);
		out.print(content);
		out.flush();

	}

	public void onCreateSuccessResponse(ServiceContext service_context) throws IOException {
		if (isSOAPRequest(service_context))
			writeResponse(service_context);
	}

	public void onCreateFailResponse(ProcedureRunner runner) throws IOException {
		ServiceContext context = ServiceContext.createServiceContext(runner.getContext());
		if (!isSOAPRequest(context))
			return;
		context.setRequestType(HEAD_SOAP_PARAMETER);
		// log exception
		ILogger logger = LoggingContext.getLogger(context.getObjectContext(), ServiceInstance.LOGGING_TOPIC);
		Throwable thr = runner.getException();
		if (thr != null) {
			LoggingUtil.logException(thr, logger);
		}
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance.getInstance(context.getObjectContext());
		HttpServletResponse response = svc.getResponse();
		prepareResponse(response);
		PrintWriter out = response.getWriter();
		out.append("<?xml version='1.0' encoding='UTF-8'?>");
		CompositeMap body = createSOAPBody();
		CompositeMap soap_error = context.getObjectContext().getChild("result");
		if (soap_error != null) {
			body.addChild(soap_error);
		}else{
			ISOAPConfiguration config = (ISOAPConfiguration)mRegistry.getInstanceOfType(ISOAPConfiguration.class);
			if(config != null){
				CompositeMap errorResponseTemplate = config.getErrorResponseTemplate();
				if(errorResponseTemplate == null)
					throw BuiltinExceptionFactory.createNodeMissing(null, "errorResponseTemplate");
				String ert_text = errorResponseTemplate.getText();
				if (ert_text == null || "".equals(ert_text)) {
					throw BuiltinExceptionFactory.createCDATAMissing(null, "errorResponseTemplate");
				}
				String template = TextParser.parse(ert_text, context.getObjectContext());
				CompositeLoader cl = new CompositeLoader();
				try {
					soap_error = cl.loadFromString(template, "UTF-8");
					body.addChild(soap_error);
				} catch (SAXException e) {
					LoggingUtil.logException(thr, logger);
				}
			}else{
				CompositeMap result = new CompositeMap("result");
				result.put("success", "false");
				body.addChild(result);
				CompositeMap error = context.getError();
				if (error != null) {
					result.addChild(error);
				}
			}
		}
		out.println(body.getRoot().toXML());
		out.flush();
	}

	private boolean isSOAPRequest(ServiceContext service_context) {
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance.getInstance(service_context.getObjectContext());
		return isSOAPRequest(svc.getRequest());
	}

	private boolean isSOAPRequest(HttpServletRequest svc) {
		String soapParam = svc.getHeader(HEAD_SOAP_PARAMETER);
		if (soapParam != null)
			return true;
		soapParam = svc.getParameter(HEAD_SOAP_PARAMETER);
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
