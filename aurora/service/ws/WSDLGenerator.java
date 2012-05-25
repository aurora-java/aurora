package aurora.service.ws;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.ocm.OCManager;
import uncertain.schema.Namespace;
import aurora.bm.BusinessModel;
import aurora.bm.Field;
import aurora.bm.Operation;
import aurora.service.validation.Parameter;

public class WSDLGenerator {

	private BusinessModel model;
	private String location;
	private boolean queryMutiRecords = true;
	private static HashMap<String, String> xsdMap = new HashMap<String, String>();
	private String[] operations;
	private static final String TARGET_NAMESPACE = "http://www.aurora-framework.org/schema";
	private static final String TARGET_PREFIX = "tns";
	private static final Namespace xsd = new Namespace("xsd", "http://www.w3.org/2001/XMLSchema");
	private static final Namespace soap = new Namespace("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
	private static final Namespace wsdl = new Namespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
	private static final Namespace tns = new Namespace("tns", "http://www.aurora-framework.org/schema");

	enum WSDL_TYPES {
		type, message, portType, binding, service, port
	}

	static {
		xsdMap.put(String.class.getCanonicalName(), xsd.getPrefix() + ":string");
		xsdMap.put(byte.class.getCanonicalName(), xsd.getPrefix() + ":byte");
		xsdMap.put(short.class.getCanonicalName(), xsd.getPrefix() + ":short");
		xsdMap.put(int.class.getCanonicalName(), xsd.getPrefix() + ":int");
		xsdMap.put(long.class.getCanonicalName(), xsd.getPrefix() + ":long");
		xsdMap.put(Long.class.getCanonicalName(), xsd.getPrefix() + ":long");
		xsdMap.put(float.class.getCanonicalName(), xsd.getPrefix() + ":float");
		xsdMap.put(double.class.getCanonicalName(), xsd.getPrefix() + ":double");
		xsdMap.put(Double.class.getCanonicalName(), xsd.getPrefix() + ":double");
		xsdMap.put(BigInteger.class.getCanonicalName(), xsd.getPrefix() + ":integer");
		xsdMap.put(BigDecimal.class.getCanonicalName(), xsd.getPrefix() + ":decimal");
		xsdMap.put(Calendar.class.getCanonicalName(), xsd.getPrefix() + ":dateTime");
		xsdMap.put(Date.class.getCanonicalName(), xsd.getPrefix() + ":dateTime");
	}

	public WSDLGenerator(BusinessModel model, String location) {
		this.model = model;
		this.location = location;
		operations = new String[] { Operation.QUERY, Operation.DELETE,
				Operation.EXECUTE, Operation.INSERT, Operation.UPDATE };
	}
	public WSDLGenerator(BusinessModel model, String location, String operation) {
		this.model = model;
		this.location = location;
		operations = new String[] {operation};
	}
	public WSDLGenerator(BusinessModel model, String location, boolean queryMutiRecords) {
		this.model = model;
		this.location = location;
		operations = new String[] {Operation.QUERY};
		this.queryMutiRecords = queryMutiRecords;
	}

	public CompositeMap run(){
		CompositeMap wsdlRoot = getWSDLTemplate(model.getName(), location);
		for (int i = 0; i < operations.length; i++) {
			Orient orient = new Orient();
			createTypes(wsdlRoot, operations[i], orient);
			if (orient.hasRequestType) {
				createMessage(wsdlRoot, operations[i], true);
			}
			if (orient.hasResponseType) {
				createMessage(wsdlRoot, operations[i], false);
			}
			createOpertaion(wsdlRoot, operations[i], orient);
			createBindingOpertaion(wsdlRoot, operations[i], orient);
		}
		return wsdlRoot;
	}

	private void createOpertaion(CompositeMap wsdlRoot, String operation, Orient orient) {
		CompositeMap oper = getWSDLNode("operation");
		oper.put("name", operation);
		if (orient.hasRequestType) {
			CompositeMap input = getWSDLNode("input");
			input.put("message", TARGET_PREFIX + ":" + createName(operation, true, WSDL_TYPES.message));
			oper.addChild(input);
		}
		if (orient.hasResponseType) {
			CompositeMap output = getWSDLNode("output");
			output.put("message", TARGET_PREFIX + ":" + createName(operation, false, WSDL_TYPES.message));
			oper.addChild(output);
		}
		if (oper.getChilds() != null)
			wsdlRoot.getChild("portType").addChild(oper);
	}

	private void createBindingOpertaion(CompositeMap wsdlRoot, String operation, Orient orient) {
		if (!orient.hasRequestType && !orient.hasResponseType)
			return;
		CompositeMap oper = getWSDLNode("operation");
		CompositeMap soap = getSOAPNode("operation");
		soap.put("soapAction", operation);
		oper.addChild(soap);
		oper.put("name", operation);
		if (orient.hasRequestType) {
			CompositeMap input = getWSDLNode("input");
			CompositeMap body = getSOAPNode("body");
			body.put("use", "literal");
			input.addChild(body);
			oper.addChild(input);
		}
		if (orient.hasResponseType) {
			CompositeMap output = getWSDLNode("output");
			CompositeMap body = getSOAPNode("body");
			body.put("use", "literal");
			output.addChild(body);
			oper.addChild(output);
		}
		wsdlRoot.getChild("binding").addChild(oper);
	}

	private void createMessage(CompositeMap wsdlRoot, String operation, boolean isRequest) {
		CompositeMap message = getWSDLNode("message");
		message.put("name", createName(operation, isRequest, WSDL_TYPES.message));
		CompositeMap part = getWSDLNode("part");
		part.put("name", createName(operation, isRequest, WSDL_TYPES.port));
		if ("query".equals(operation)&&!isRequest) {
			if (queryMutiRecords)
				part.put("element", TARGET_PREFIX + ":" + "records");
			else {
				part.put("element", TARGET_PREFIX + ":" + "record");
			}
		} else
			part.put("element", TARGET_PREFIX + ":" + createName(operation, isRequest, WSDL_TYPES.type));
		message.addChild(part);
		int index = wsdlRoot.getChilds().indexOf(wsdlRoot.getChild("types"));
		wsdlRoot.addChild(index + 1, message);
	}

	private CompositeMap getWSDLTemplate(String modelName, String location) {
		CompositeMap wsdlRoot = new CompositeMap(wsdl.getPrefix(), wsdl.getUrl(), "definitions");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(tns.getUrl(), tns.getPrefix());
		wsdlRoot.setNamespaceMapping(map);
		wsdlRoot.put("name", modelName);
		wsdlRoot.put("targetNamespace", TARGET_NAMESPACE);
		CompositeMap types = new CompositeMap(wsdl.getPrefix(), wsdl.getUrl(), "types");
		CompositeMap schema = new CompositeMap(xsd.getPrefix(), xsd.getUrl(), "schema");
		schema.put("targetNamespace", TARGET_NAMESPACE);
		types.addChild(schema);
		wsdlRoot.addChild(types);
		CompositeMap portType = new CompositeMap(wsdl.getPrefix(), wsdl.getUrl(), "portType");
		portType.put("name", modelName + "_" + WSDL_TYPES.portType);
		wsdlRoot.addChild(portType);
		CompositeMap binding = new CompositeMap(wsdl.getPrefix(), wsdl.getUrl(), "binding");
		binding.put("name", modelName + "_" + WSDL_TYPES.binding);
		binding.put("type", TARGET_PREFIX + ":" + modelName + "_" + WSDL_TYPES.portType);
		CompositeMap transport = new CompositeMap(soap.getPrefix(), soap.getUrl(), "binding");
		transport.put("style", "document");
		transport.put("transport", "http://schemas.xmlsoap.org/soap/http");
		binding.addChild(transport);
		wsdlRoot.addChild(binding);
		CompositeMap service = new CompositeMap(wsdl.getPrefix(), wsdl.getUrl(), "service");
		service.put("name", modelName + "_" + WSDL_TYPES.service);
		CompositeMap port = new CompositeMap(wsdl.getPrefix(), wsdl.getUrl(), "port");
		port.put("name", modelName + "_" + WSDL_TYPES.port);
		port.put("binding", TARGET_PREFIX + ":" + modelName + "_" + WSDL_TYPES.binding);
		service.addChild(port);
		CompositeMap address = new CompositeMap(soap.getPrefix(), soap.getUrl(), "address");
		address.put("location", location);
		port.addChild(address);
		wsdlRoot.addChild(service);
		return wsdlRoot;
	}

	private void createTypes(CompositeMap wsdlRoot, String operation, Orient orient){
		List<Parameter> parameters = model.getParameterForOperationInList(operation);
		CompositeMap request = getXSDCtElement("element");
		request.put("name", createName(operation, true, WSDL_TYPES.type));
		CompositeMap response;
		if ("query".equals(operation)) {
			if (queryMutiRecords)
				response = getMutiRecord();
			else {
				response = getXSDCtElement("element");
				response.put("name", "record");
			}
			Field[] fields = model.getFields();
			if (parameters == null && fields == null)
				return;
			if (parameters == null)
				parameters = new LinkedList<Parameter>();
			if (fields != null) {
				OCManager mOcManager = OCManager.getInstance();
				Parameter param = null;
				for (int i = 0; i < fields.length; i++) {
					Field field = fields[i];
					param = new Parameter();
					CompositeMap m = ((DynamicObject) field).getObjectContext();
					mOcManager.populateObject(m, param);
					param.setOutput(true);
					param.setInput(false);
					parameters.add(param);
				}
			}
			interpretParameters(wsdlRoot, orient, parameters, request, response);
			return;
		}
		if (parameters == null)
			return;
		response = getXSDCtElement("element");
		response.put("name", createName(operation, false, WSDL_TYPES.type));
		interpretParameters(wsdlRoot, orient, parameters, request, response);
	}

	private void interpretParameters(CompositeMap wsdlRoot, Orient orient, List<Parameter> parameters,
			CompositeMap request, CompositeMap response){
		Iterator<Parameter> paraIts = parameters.iterator();
		Set<String> inputParameters = new HashSet<String>();
		Set<String> outputParameters = new HashSet<String>();
		while (paraIts.hasNext()) {
			Parameter para = paraIts.next();
			if (para.getInput()) {
				orient.hasRequestType = true;
				if (inputParameters.contains(para.getName()))
					continue;
				inputParameters.add(para.getName());
				addXSDAttribute(request, para.getName(), para.getDataType());
			}
			if (para.getOutput()) {
				orient.hasResponseType = true;
				if (outputParameters.contains(para.getName()))
					continue;
				outputParameters.add(para.getName());
				addXSDAttribute(response, para.getName(), para.getDataType());
			}
		}
		CompositeMap schema = (CompositeMap) wsdlRoot.getObject("/types/schema");
		if (orient.hasRequestType) {
			schema.addChild(request);
		}
		if (orient.hasResponseType) {
			schema.addChild(response.getRoot());
		}
	}

	private String createName(String operation, boolean isRequest, WSDL_TYPES types) {
		return operation + (isRequest ? "Request" : "Response") + types;
	}

	private CompositeMap getMutiRecord() {
		CompositeMap records = getXSDNode("element");
		records.put("name", "records");
		CompositeMap complexType = getXSDNode("complexType");
		records.addChild(complexType);
		CompositeMap sequence = getXSDNode("sequence");
		complexType.addChild(sequence);
		CompositeMap record = getXSDNode("element");
		sequence.addChild(record);
		record.put("maxOccurs", "unbounded");
		record.put("name", "record");
		CompositeMap type = getXSDNode("complexType");
		record.addChild(type);
		return record;
	}

	private CompositeMap getXSDNode(String name) {
		return new CompositeMap(xsd.getPrefix(), xsd.getUrl(), name);
	}

	private CompositeMap getWSDLNode(String name) {
		return new CompositeMap(wsdl.getPrefix(), wsdl.getUrl(), name);
	}

	private CompositeMap getSOAPNode(String name) {
		return new CompositeMap(soap.getPrefix(), soap.getUrl(), name);
	}

	private CompositeMap getXSDCtElement(String name) {
		CompositeMap element = getXSDNode("element");
		CompositeMap complexType = getXSDNode("complexType");
		element.addChild(complexType);
		return element;
	}

	private void addXSDAttribute(CompositeMap element, String name, String javaType){
		CompositeMap complexType = element.getChild("complexType");
		CompositeMap atttribute = getXSDNode("attribute");
		atttribute.put("name", name);
		String type = xsdMap.get(javaType);
		if (type == null)
			throw new RuntimeException(javaType + " is not defined the map type");
		atttribute.put("type", type);
		complexType.addChild(atttribute);
	}

	class Orient {
		public boolean hasRequestType;
		public boolean hasResponseType;
	}
}
