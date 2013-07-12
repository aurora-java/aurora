package aurora.service.ws;


import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;

public class XMLSampleToWSDL {

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
		CompositeMap wsdlRoot = WSDLUtil.getWSDLTemplate(webserviceUrl, WSDLUtil.NODE_NAME_PREFIX, soapAction, target_namespace);
		if (request != null) {
			WSDLUtil.createOrientNode(soapAction, request, WSDLUtil.TARGET_PREFIX, wsdlRoot, true);
		}
		if (response != null) {
			WSDLUtil.createOrientNode(soapAction, response, WSDLUtil.TARGET_PREFIX, wsdlRoot, false);
		}
		return wsdlRoot;
	}
}
