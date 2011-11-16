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
import uncertain.composite.XMLOutputter;
import uncertain.event.EventModel;
import uncertain.exception.MessageFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.ProcedureRunner;
import uncertain.util.LoggingUtil;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.ServiceOutputConfig;
import aurora.service.http.HttpServiceInstance;

public class SOAPServiceInterpreter {

	public static final String DEFAULT_SOAP_CONTENT_TYPE = "text/xml;charset=utf-8";

	public static final String HEAD_SOAP_PARAMETER = "soapaction";

	public static final QualifiedName ENVELOPE = new QualifiedName("soapenv",
			"http://schemas.xmlsoap.org/soap/envelope/", "Envelope");
	public static final QualifiedName BODY = new QualifiedName("soapenv",
			"http://schemas.xmlsoap.org/soap/envelope/", "Body");
	static {
		MessageFactory.loadResource("resources.aurora_validation_exceptions");
	}

	public SOAPServiceInterpreter() {
	}

	public int preParseParameter(ServiceContext service_context)
			throws Exception {
		if (!isSOAPRequest(service_context))
			return EventModel.HANDLE_NORMAL;
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance
				.getInstance(service_context.getObjectContext());
		String soapContent = inputStream2String(svc.getRequest()
				.getInputStream());
		LoggingContext.getLogger(service_context.getObjectContext(),
				this.getClass().getCanonicalName()).config(
				"request:\r\n" + soapContent);
		if (soapContent == null || "".equals(soapContent))
			return EventModel.HANDLE_NORMAL;
		CompositeLoader cl = new CompositeLoader();
		CompositeMap soap = cl.loadFromString(soapContent, "UTF-8");
		CompositeMap parameter = (CompositeMap) soap.getChild(
				BODY.getLocalName()).getChilds().get(0);
		service_context.setParameter(parameter);
		LoggingContext.getLogger(service_context.getObjectContext(),
				this.getClass().getCanonicalName()).config(
				"context:\r\n" + service_context.getObjectContext().toXML());
		return EventModel.HANDLE_NORMAL;
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

	public void writeResponse(ServiceContext service_context)
			throws IOException{
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance
				.getInstance(service_context.getObjectContext());
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
				Object obj = service_context.getObjectContext().getObject(
						output);
				if (obj != null) {
					if (!(obj instanceof CompositeMap))
						throw new IllegalArgumentException(
								"Target for SOAP output is not instance of CompositeMap: "
										+ obj);
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
		LoggingContext.getLogger(service_context.getObjectContext(),
				this.getClass().getCanonicalName()).config(
				"response content:\r\n" + content);
		out.print(content);
		out.flush();

	}

	public void onCreateSuccessResponse(ServiceContext service_context)
			throws IOException{
		if (isSOAPRequest(service_context))
			writeResponse(service_context);
	}

	public void onCreateFailResponse(ProcedureRunner runner)
			throws IOException{
		ServiceContext context = ServiceContext.createServiceContext(runner
				.getContext());
		if (!isSOAPRequest(context))
			return;
		// log exception
		ILogger logger = LoggingContext.getLogger(context.getObjectContext(),
				ServiceInstance.LOGGING_TOPIC);
		Throwable thr = runner.getException();
		if (thr != null) {
			LoggingUtil.logException(thr, logger);
		}
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance
				.getInstance(context.getObjectContext());
		HttpServletResponse response = svc.getResponse();
		prepareResponse(response);
		PrintWriter out = response.getWriter();
		out.append("<?xml version='1.0' encoding='UTF-8'?>");
		CompositeMap body = createSOAPBody();
		CompositeMap result = new CompositeMap();
		result.put("success", "false");
		body.addChild(result);
		CompositeMap error = context.getError();
		if (error != null) {
			result.addChild(error);
		}
		out.println(body.getRoot().toXML());
		out.flush();
	}

	private boolean isSOAPRequest(ServiceContext service_context) {
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance
				.getInstance(service_context.getObjectContext());
		return isSOAPRequest(svc.getRequest());
	}

	private boolean isSOAPRequest(HttpServletRequest svc) {
		String soapParam = svc.getHeader(HEAD_SOAP_PARAMETER);
		if (soapParam != null)
			return true;
		return false;
	}

	private CompositeMap createSOAPBody() {
		CompositeMap env = new CompositeMap(ENVELOPE.getPrefix(), ENVELOPE
				.getNameSpace(), ENVELOPE.getLocalName());
		CompositeMap body = new CompositeMap(BODY.getPrefix(), BODY
				.getNameSpace(), BODY.getLocalName());
		env.addChild(body);
		return body;
	}

}
