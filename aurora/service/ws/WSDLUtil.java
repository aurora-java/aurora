package aurora.service.ws;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.schema.Namespace;
import aurora.service.ws.BMWSDLGenerator.WSDL_TYPES;

public class WSDLUtil {

	public static final Namespace xsd = new Namespace("xsd", "http://www.w3.org/2001/XMLSchema");
	public static final Namespace soap = new Namespace("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
	public static final Namespace wsdl = new Namespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
	public static final String TARGET_NAMESPACE = "http://www.aurora-framework.org/schema";
	public static final String NODE_NAME_PREFIX = "autoName";
	public static final String TARGET_PREFIX = "tns";
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");


	public static void createOrientNode(String soapAction, CompositeMap xmlSample, String target_prefix, CompositeMap wsdlRoot,
			boolean isRequest) {
		CompositeMap schema = (CompositeMap) wsdlRoot.getObject("/types/schema");
		CompositeMap elementType = WSDLUtil.createType(xmlSample);
		schema.addChild(elementType);
	
		String elementTypeName = elementType.getString("name");
		CompositeMap message = WSDLUtil.createMessage(elementTypeName, isRequest);
		int index = wsdlRoot.getChilds().indexOf(wsdlRoot.getChild("types"));
		wsdlRoot.addChild(index + 1, message);
	}

	public static CompositeMap createType(CompositeMap xmlSample) {
		CompositeMap elementType = null;
		boolean isComplexTypeElement = WSDLUtil.isComplexTypeElement(xmlSample);
		if (!isComplexTypeElement) {
			elementType = WSDLUtil.getXSDNode("element");
			elementType.put("type", "xsd:string");
		} else {
			elementType = WSDLUtil.getXSDNode("element");
			CompositeMap complexType = WSDLUtil.getXSDNode("complexType");
	
			List<CompositeMap> childList = xmlSample.getChilds();
			if (childList != null && childList.size() != 0) {
				CompositeMap sequence = WSDLUtil.getXSDNode("sequence");
				List<CompositeMap> elementTypeList = new LinkedList<CompositeMap>();
				for (CompositeMap child : childList) {
					CompositeMap childElementType = createType(child);
					if (!elementTypeList.contains(childElementType)) {
						elementTypeList.add(childElementType);
						sequence.addChild(childElementType);
					}else{
						CompositeMap existsElementType = sequence.getChild(childElementType);
						existsElementType.put("maxOccurs", "unbounded");
					}
	
				}
				complexType.addChild(sequence);
			}
			if (!xmlSample.isEmpty()) {
				Iterator iterator = xmlSample.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry entry = (Map.Entry) iterator.next();
					CompositeMap attributeType = WSDLUtil.getXSDNode("attribute");
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

	public static CompositeMap createMessage(String elementName, boolean isRequest) {
		CompositeMap message = getWSDLNode("message");
		message.put("name", createMessageName(isRequest, WSDL_TYPES.message));
		CompositeMap part = getWSDLNode("part");
		part.put("name", createName("", isRequest, WSDL_TYPES.part));
	
		part.put("element", WSDLUtil.TARGET_PREFIX + ":" + elementName);
		message.addChild(part);
		return message;
	}

	public static String createMessageName(boolean isRequest, WSDL_TYPES types) {
		return WSDLUtil.createName("", isRequest, types);
	}

	public static String createName(String elementName, boolean isRequest, WSDL_TYPES types) {
		return elementName + (isRequest ? "request" : "response") + "_" + types;
	}

	public static boolean isComplexTypeElement(CompositeMap element) {
		if (element == null)
			return false;
		if (!element.isEmpty() || element.getChilds() != null)
			return true;
		return false;
	}

	public static CompositeMap getWSDLTemplate(String location, String name_prefix, String soapAction, String TARGET_NAMESPACE){
		Namespace tns = new Namespace("tns", TARGET_NAMESPACE);
	
		CompositeMap wsdlRoot = new CompositeMap(WSDLUtil.wsdl.getPrefix(), WSDLUtil.wsdl.getUrl(), "definitions");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(tns.getUrl(), tns.getPrefix());
		wsdlRoot.setNamespaceMapping(map);
		wsdlRoot.put("name", name_prefix);
		wsdlRoot.put("targetNamespace", TARGET_NAMESPACE);
		CompositeMap types = new CompositeMap(WSDLUtil.wsdl.getPrefix(), WSDLUtil.wsdl.getUrl(), "types");
		CompositeMap schema = new CompositeMap(WSDLUtil.xsd.getPrefix(), WSDLUtil.xsd.getUrl(), "schema");
		schema.put("targetNamespace", TARGET_NAMESPACE);
		schema.put("elementFormDefault", "qualified");
		types.addChild(schema);
		wsdlRoot.addChild(types);
		CompositeMap portType = new CompositeMap(WSDLUtil.wsdl.getPrefix(), WSDLUtil.wsdl.getUrl(), "portType");
		portType.put("name", name_prefix + "_" + WSDL_TYPES.portType);
		CompositeMap operation = createOpertaion(soapAction);
		portType.addChild(operation);
		wsdlRoot.addChild(portType);
		CompositeMap binding = new CompositeMap(WSDLUtil.wsdl.getPrefix(), WSDLUtil.wsdl.getUrl(), "binding");
		String bindingName = getBindingName(location);
		binding.put("name", bindingName);
		binding.put("type", WSDLUtil.TARGET_PREFIX + ":" + name_prefix + "_" + WSDL_TYPES.portType);
		CompositeMap transport = new CompositeMap(WSDLUtil.soap.getPrefix(), WSDLUtil.soap.getUrl(), "binding");
		transport.put("style", "document");
		transport.put("transport", "http://schemas.xmlsoap.org/soap/http");
		binding.addChild(transport);
		CompositeMap bindingOpertaion = createBindingOpertaion(soapAction);
		binding.addChild(bindingOpertaion);
		wsdlRoot.addChild(binding);
		CompositeMap service = new CompositeMap(WSDLUtil.wsdl.getPrefix(), WSDLUtil.wsdl.getUrl(), "service");
		service.put("name", name_prefix + "_" + WSDL_TYPES.service);
		CompositeMap port = new CompositeMap(WSDLUtil.wsdl.getPrefix(), WSDLUtil.wsdl.getUrl(), "port");
		port.put("name", name_prefix + "_" + WSDL_TYPES.port);
		port.put("binding", WSDLUtil.TARGET_PREFIX + ":" + bindingName);
		service.addChild(port);
		CompositeMap address = new CompositeMap(WSDLUtil.soap.getPrefix(), WSDLUtil.soap.getUrl(), "address");
		address.put("location", location);
		port.addChild(address);
		wsdlRoot.addChild(service);
		return wsdlRoot;
	}
	private static String getBindingName(String location){
		String[] parts = location.split("/");
		int length = parts.length;
		return parts[length-2]+"."+parts[length-1]+"_"+WSDL_TYPES.binding;
	}

	public static CompositeMap createOpertaion(String soapAction) {
		CompositeMap operation = WSDLUtil.getWSDLNode("operation");
		operation.put("name", soapAction);
		CompositeMap inputElement = WSDLUtil.getWSDLNode("input");
		inputElement.put("message", WSDLUtil.TARGET_PREFIX + ":" + createMessageName(true, WSDL_TYPES.message));
		CompositeMap outputElement = WSDLUtil.getWSDLNode("output");
		outputElement.put("message", WSDLUtil.TARGET_PREFIX + ":" + createMessageName(false, WSDL_TYPES.message));
		operation.addChild(inputElement);
		operation.addChild(outputElement);
		return operation;
	}

	public static CompositeMap createBindingOpertaion(String soapAction) {
		CompositeMap oper = WSDLUtil.getWSDLNode("operation");
		CompositeMap soap = WSDLUtil.getSOAPNode("operation");
		soap.put("soapAction", soapAction);
		oper.addChild(soap);
		oper.put("name", soapAction);
		CompositeMap input = WSDLUtil.getWSDLNode("input");
		CompositeMap inputBody = WSDLUtil.getSOAPNode("body");
		inputBody.put("use", "literal");
		input.addChild(inputBody);
		oper.addChild(input);
		CompositeMap output = WSDLUtil.getWSDLNode("output");
		CompositeMap outputBody = WSDLUtil.getSOAPNode("body");
		outputBody.put("use", "literal");
		output.addChild(outputBody);
		oper.addChild(output);
		return oper;
	}

	public static CompositeMap getXSDNode(String name) {
		return new CompositeMap(WSDLUtil.xsd.getPrefix(), WSDLUtil.xsd.getUrl(), name);
	}

	public static CompositeMap getWSDLNode(String name) {
		return new CompositeMap(WSDLUtil.wsdl.getPrefix(), WSDLUtil.wsdl.getUrl(), name);
	}

	public static CompositeMap getSOAPNode(String name) {
		return new CompositeMap(WSDLUtil.soap.getPrefix(), WSDLUtil.soap.getUrl(), name);
	}
}
