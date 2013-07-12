package aurora.service.ws;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
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

public class BMWSDLGenerator {

	private BusinessModel model;
	private String location;
	private boolean isQueryMutiRecords = false;
	private static HashMap<String, String> xsdMap = new HashMap<String, String>();
	private String soapAction;
	
	private static final Namespace xsd = new Namespace("xsd", "http://www.w3.org/2001/XMLSchema");

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");;
	private CompositeMap defaultResponse;

	enum WSDL_TYPES {
		type, message, portType, binding, service, part, port
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
		xsdMap.put(Clob.class.getCanonicalName(), xsd.getPrefix() + ":string");
		xsdMap.put(java.sql.Date.class.getCanonicalName(), xsd.getPrefix() + ":date");
		xsdMap.put(java.sql.Time.class.getCanonicalName(), xsd.getPrefix() + ":time");
		xsdMap.put(java.sql.Timestamp.class.getCanonicalName(), xsd.getPrefix() + ":datetime");
	}

	public BMWSDLGenerator(BusinessModel model, String location, String operation) {
		this.model = model;
		this.location = location;
		this.soapAction = operation;
	}

	public BMWSDLGenerator(BusinessModel model, String location, boolean queryMutiRecords) {
		this.model = model;
		this.location = location;
		this.soapAction = Operation.QUERY;
		this.isQueryMutiRecords = queryMutiRecords;
	}

	public CompositeMap run() {
		CompositeMap wsdlRoot = WSDLUtil.getWSDLTemplate(location, WSDLUtil.NODE_NAME_PREFIX, soapAction, WSDLUtil.TARGET_NAMESPACE);
		createRequest(wsdlRoot);
		createResponse(wsdlRoot);
		return wsdlRoot;
	}

	public void createRequest(CompositeMap wsdlRoot) {
		boolean isRequest = true;
		CompositeMap schema = (CompositeMap) wsdlRoot.getObject("/types/schema");
		CompositeMap elementType = createType(soapAction, isRequest);
		schema.addChild(elementType);
		String elementTypeName = elementType.getString("name");
		CompositeMap message = WSDLUtil.createMessage(elementTypeName, isRequest);
		int index = wsdlRoot.getChilds().indexOf(wsdlRoot.getChild("types"));
		wsdlRoot.addChild(index + 1, message);
	}

	public void createResponse(CompositeMap wsdlRoot) {
		boolean isRequest = false;
		CompositeMap schema = (CompositeMap) wsdlRoot.getObject("/types/schema");
		if (isQueryMutiRecords) {
			CompositeMap elementType = createMutiResponseType(soapAction);
			String elementTypeName = elementType.getString("name");
			schema.addChild(elementType);
			CompositeMap message = WSDLUtil.createMessage(elementTypeName, isRequest);
			int index = wsdlRoot.getChilds().indexOf(wsdlRoot.getChild("types"));
			wsdlRoot.addChild(index + 1, message);
		} else {
			WSDLUtil.createOrientNode(soapAction, defaultResponse, WSDLUtil.TARGET_PREFIX, wsdlRoot, isRequest);
		}

	}

	private CompositeMap createMutiResponseType(String operation) {
		boolean isRequest = false;
		List<Parameter> parameters = model.getParameterForOperationInList(operation);
		CompositeMap recordType = getMutiResponseType();
		Field[] fields = model.getFields();
		if (parameters == null && fields == null)
			return recordType.getRoot();
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
		interpretParameter(parameters, recordType, isRequest);
		return recordType.getRoot();

	}

	private CompositeMap createType(String operation, boolean isRequest) {
		List<Parameter> parameters = model.getParameterForOperationInList(operation);
		CompositeMap elementType = getXSDCtElement("element");
		elementType.put("name", createName("", isRequest, WSDL_TYPES.type));
		interpretParameter(parameters, elementType, isRequest);
		return elementType;
	}

	private void interpretParameter(List<Parameter> parameters, CompositeMap elementType, boolean isRequest) {
		Iterator<Parameter> paraIts = parameters.iterator();
		Set<String> exists = new HashSet<String>();
		while (paraIts.hasNext()) {
			Parameter para = paraIts.next();
			if (isRequest && para.getInput()) {
				if (exists.contains(para.getName()))
					continue;
				exists.add(para.getName());
				addXSDAttribute(elementType, para.getName(), para.getDataType());
			}
			if (!isRequest && para.getOutput()) {
				if (exists.contains(para.getName()))
					continue;
				exists.add(para.getName());
				addXSDAttribute(elementType, para.getName(), para.getDataType());
			}
		}
	}

	private String createName(String operation, boolean isRequest, WSDL_TYPES types) {
		return operation + (isRequest ? "Request" : "Response") + types;
	}

	private CompositeMap getMutiResponseType() {
		CompositeMap records = getXSDNode("element");
		records.put("name", model.getName());
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

	private CompositeMap getXSDCtElement(String name) {
		CompositeMap element = getXSDNode("element");
		CompositeMap complexType = getXSDNode("complexType");
		element.addChild(complexType);
		return element;
	}

	private void addXSDAttribute(CompositeMap element, String name, String javaType) {
		CompositeMap complexType = element.getChild("complexType");
		CompositeMap atttribute = getXSDNode("attribute");
		atttribute.put("name", name);
		String type = xsdMap.get(javaType);
		if (type == null)
			throw new RuntimeException(javaType + " is not defined the map type");
		atttribute.put("type", type);
		complexType.addChild(atttribute);
	}

	public CompositeMap getDefaultResponse() {
		return defaultResponse;
	}

	public void setDefaultResponse(CompositeMap defaultResponse) {
		this.defaultResponse = defaultResponse;
	}
}
