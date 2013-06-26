package aurora.service.ws;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;
import uncertain.schema.Namespace;
import aurora.service.ws.WSDLGenerator.WSDL_TYPES;

public class XMLSampleToWSDL {

	private static final String NODE_NAME_PREFIX = "auto";
	private static final String TARGET_PREFIX = "tns";
	private static final Namespace xsd = new Namespace("xsd", "http://www.w3.org/2001/XMLSchema");
	private static final Namespace soap = new Namespace("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
	private static final Namespace wsdl = new Namespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");;

	public static void main(String[] args) {

		String webserviceUrl = "http://oracle-base.com/webservices/server.php";

		String soapAction = "http://oracle-base.com/webservices/server.php/ws_add";

		StringBuffer request_sb = new StringBuffer();
		request_sb
				.append(" <web:ws_add xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://www.oracle-base.com/webservices/\">");
		request_sb.append("		<int1 >a</int1>");
		request_sb.append("		<int2 >b</int2>");
		request_sb.append("	</web:ws_add>");

		StringBuffer response_sb = new StringBuffer();
		response_sb
				.append("<SOAP-ENV:Body xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >");
		response_sb.append("	<ns1:ws_addResponse xmlns:ns1=\"http://www.oracle-base.com/webservices/\">");
		response_sb.append("		<return >123</return>");
		response_sb.append("	</ns1:ws_addResponse>");
		response_sb.append("</SOAP-ENV:Body>");

		try {
			String wsdl = convertXMLSampleToWSDL(webserviceUrl, soapAction, request_sb.toString(), response_sb.toString());
			System.out.println(wsdl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String convertXMLSampleToWSDL(String webserviceUrl, String soapAction, String requestXML, String responseXML)
			throws Exception {
		CompositeLoader loader = new CompositeLoader();
		CompositeMap request = null;
		if (requestXML != null && !"".equals(requestXML))
			request = loader.loadFromString(requestXML, "UTF-8");
		CompositeMap response = null;
		if (responseXML != null && !"".equals(responseXML))
			response = loader.loadFromString(responseXML, "UTF-8");
		return XMLOutputter.defaultInstance().toXML(convertXMLSampleToWSDL(webserviceUrl, soapAction, request, response), true);
	}

	public static CompositeMap convertXMLSampleToWSDL(String webserviceUrl, String soapAction, CompositeMap request, CompositeMap response)
			throws Exception {
		String target_namespace = request.getNamespaceURI();
		CompositeMap wsdlRoot = getWSDLTemplate(webserviceUrl, NODE_NAME_PREFIX, soapAction, target_namespace);
		if (request != null) {
			createOrientNode(soapAction, request, TARGET_PREFIX, wsdlRoot, true);
		}
		if (response != null) {
			createOrientNode(soapAction, response, TARGET_PREFIX, wsdlRoot, false);
		}
		return wsdlRoot;
	}

	private static void createOrientNode(String soapAction, CompositeMap xmlSample, String target_prefix, CompositeMap wsdlRoot,
			boolean isRequest) {
		CompositeMap schema = (CompositeMap) wsdlRoot.getObject("/types/schema");
		CompositeMap elementType = createType(xmlSample);
		schema.addChild(elementType);

		String elementTypeName = elementType.getString("name");
		CompositeMap message = createMessage(elementTypeName, isRequest);
		int index = wsdlRoot.getChilds().indexOf(wsdlRoot.getChild("types"));
		wsdlRoot.addChild(index + 1, message);
	}

	private static CompositeMap createType(CompositeMap xmlSample) {
		CompositeMap elementType = null;
		boolean isComplexTypeElement = isComplexTypeElement(xmlSample);
		if (!isComplexTypeElement) {
			elementType = getXSDNode("element");
			elementType.put("type", "xsd:string");
		} else {
			elementType = getXSDNode("element");
			CompositeMap complexType = getXSDNode("complexType");

			List<CompositeMap> childList = xmlSample.getChilds();
			if (childList != null && childList.size() != 0) {
				CompositeMap sequence = getXSDNode("sequence");
				List<CompositeMap> elementTypeList = new LinkedList<CompositeMap>();
				for (CompositeMap child : childList) {
					CompositeMap childElementType = createType(child);
					if (!elementTypeList.contains(childElementType)) {
						elementTypeList.add(childElementType);
						sequence.addChild(childElementType);
					}

				}
				complexType.addChild(sequence);
			}
			if (!xmlSample.isEmpty()) {
				Iterator iterator = xmlSample.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry entry = (Map.Entry) iterator.next();
					CompositeMap attributeType = getXSDNode("attribute");
					attributeType.put("name", entry.getKey());
					attributeType.put("type", "xsd:string");
					complexType.addChild(attributeType);
				}
			}
			elementType.addChild(complexType);
		}
		elementType.put("name", xmlSample.getName());
		return elementType.getRoot();
	}

	private static CompositeMap createMessage(String elementName, boolean isRequest) {
		CompositeMap message = getWSDLNode("message");
		message.put("name", createMessageName(isRequest, WSDL_TYPES.message));
		CompositeMap part = getWSDLNode("part");
		part.put("name", createName("", isRequest, WSDL_TYPES.part));

		part.put("element", TARGET_PREFIX + ":" + elementName);
		message.addChild(part);
		return message;
	}

	private static String createMessageName(boolean isRequest, WSDL_TYPES types) {
		return createName("", isRequest, types);
	}

	private static String createName(String elementName, boolean isRequest, WSDL_TYPES types) {
		return elementName + (isRequest ? "request" : "response") + "_" + types;
	}

	private static boolean isComplexTypeElement(CompositeMap element) {
		if (element == null)
			return false;
		if (!element.isEmpty() || element.getChilds() != null)
			return true;
		return false;
	}

	private static CompositeMap getWSDLTemplate(String webserviceUrl, String name_prefix, String soapAction, String TARGET_NAMESPACE)
			throws Exception {
		Namespace tns = new Namespace("tns", TARGET_NAMESPACE);

		CompositeMap wsdlRoot = new CompositeMap(wsdl.getPrefix(), wsdl.getUrl(), "definitions");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(tns.getUrl(), tns.getPrefix());
		wsdlRoot.setNamespaceMapping(map);
		wsdlRoot.put("name", name_prefix);
		wsdlRoot.put("targetNamespace", TARGET_NAMESPACE);
		CompositeMap types = new CompositeMap(wsdl.getPrefix(), wsdl.getUrl(), "types");
		CompositeMap schema = new CompositeMap(xsd.getPrefix(), xsd.getUrl(), "schema");
		schema.put("targetNamespace", TARGET_NAMESPACE);
		schema.put("elementFormDefault", "qualified");
		types.addChild(schema);
		wsdlRoot.addChild(types);
		CompositeMap portType = new CompositeMap(wsdl.getPrefix(), wsdl.getUrl(), "portType");
		portType.put("name", name_prefix + "_" + WSDL_TYPES.portType);
		CompositeMap operation = createOpertaion(soapAction);
		portType.addChild(operation);
		wsdlRoot.addChild(portType);
		CompositeMap binding = new CompositeMap(wsdl.getPrefix(), wsdl.getUrl(), "binding");
		binding.put("name", name_prefix + "_" + WSDL_TYPES.binding);
		binding.put("type", TARGET_PREFIX + ":" + name_prefix + "_" + WSDL_TYPES.portType);
		CompositeMap transport = new CompositeMap(soap.getPrefix(), soap.getUrl(), "binding");
		transport.put("style", "document");
		transport.put("transport", "http://schemas.xmlsoap.org/soap/http");
		binding.addChild(transport);
		CompositeMap bindingOpertaion = createBindingOpertaion(soapAction);
		binding.addChild(bindingOpertaion);
		wsdlRoot.addChild(binding);
		CompositeMap service = new CompositeMap(wsdl.getPrefix(), wsdl.getUrl(), "service");
		service.put("name", name_prefix + "_" + WSDL_TYPES.service);
		CompositeMap port = new CompositeMap(wsdl.getPrefix(), wsdl.getUrl(), "port");
		port.put("name", name_prefix + "_" + WSDL_TYPES.port);
		port.put("binding", TARGET_PREFIX + ":" + name_prefix + "_" + WSDL_TYPES.binding);
		service.addChild(port);
		CompositeMap address = new CompositeMap(soap.getPrefix(), soap.getUrl(), "address");
		address.put("location", webserviceUrl);
		port.addChild(address);
		wsdlRoot.addChild(service);
		return wsdlRoot;
	}

	private static CompositeMap createOpertaion(String soapAction) {
		CompositeMap operation = getWSDLNode("operation");
		operation.put("name", soapAction);
		CompositeMap inputElement = getWSDLNode("input");
		inputElement.put("message", TARGET_PREFIX + ":" + createMessageName(true, WSDL_TYPES.message));
		CompositeMap outputElement = getWSDLNode("output");
		outputElement.put("message", TARGET_PREFIX + ":" + createMessageName(false, WSDL_TYPES.message));
		operation.addChild(inputElement);
		operation.addChild(outputElement);
		return operation;
	}

	private static CompositeMap createBindingOpertaion(String soapAction) {
		CompositeMap oper = getWSDLNode("operation");
		CompositeMap soap = getSOAPNode("operation");
		soap.put("soapAction", soapAction);
		oper.addChild(soap);
		oper.put("name", soapAction);
		CompositeMap input = getWSDLNode("input");
		CompositeMap inputBody = getSOAPNode("body");
		inputBody.put("use", "literal");
		input.addChild(inputBody);
		oper.addChild(input);
		CompositeMap output = getWSDLNode("output");
		CompositeMap outputBody = getSOAPNode("body");
		outputBody.put("use", "literal");
		output.addChild(outputBody);
		oper.addChild(output);
		return oper;
	}

	private static CompositeMap getXSDNode(String name) {
		return new CompositeMap(xsd.getPrefix(), xsd.getUrl(), name);
	}

	private static CompositeMap getWSDLNode(String name) {
		return new CompositeMap(wsdl.getPrefix(), wsdl.getUrl(), name);
	}

	private static CompositeMap getSOAPNode(String name) {
		return new CompositeMap(soap.getPrefix(), soap.getUrl(), name);
	}
}
