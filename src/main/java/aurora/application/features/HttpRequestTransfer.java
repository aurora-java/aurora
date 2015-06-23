/*
 * Created on 2009-9-8 下午01:27:55
 * Author: Zhou Fan
 */
package aurora.application.features;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

public class HttpRequestTransfer implements ISingleton {
	private static final String KEY_REQUEST = "request";
	private static final String KEY_ADDRESS = "address";

	public static void copyParameter(HttpServletRequest request,
			ServiceInstance svc) {
		Enumeration ep = request.getParameterNames();

		CompositeMap params = svc.getServiceContext().getParameter();
		while (ep.hasMoreElements()) {
			String name = (String) ep.nextElement();
			String[] values = request.getParameterValues(name);
			if (values == null)
				params.put(name, null);
			else {
				if (values.length == 1)
					params.put(name, values[0]);
				else if (values.length > 1)
					params.put(name, values);
			}
		}
	}

	public static void copyHeader(HttpServletRequest request,
			ServiceInstance svc) {
		CompositeMap req_map = svc.getContextMap().getChild("request");
		if (req_map == null)
			req_map = svc.getContextMap().createChild("request");
		Enumeration head_enum = request.getHeaderNames();
		while (head_enum.hasMoreElements()) {
			String head = (String) head_enum.nextElement();
			String head_value = request.getHeader(head);
			req_map.put(head, head_value);
		}
		String ip = request.getHeader("x-forwarded-for");	
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");			
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");		
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if(ip!=null){
			String[] ips=ip.split(",");
			ip=ips[0].trim();
		}
		req_map.put(KEY_ADDRESS, ip);
	}

	public static void copyRequest(HttpServiceInstance svc) {
		HttpServletRequest request = svc.getRequest();
		// JSONObject dm = getParameterString(svc.getRequest());
		CompositeMap r = svc.getContextMap().createChild(KEY_REQUEST);
		String ip = request.getHeader("x-forwarded-for");	
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");			
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");		
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}		
		r.put(KEY_ADDRESS, ip);
		// r.put("url", svc.getName());
		r.put("url", request.getRequestURI());
		/*
		 * if (dm.length() != 0) { r.put("params", dm); }
		 */
		r.put("server_name", request.getServerName());
		r.put("context_path", request.getContextPath());
		r.put("server_port", new Integer(request.getServerPort()));
		r.put("request_id", UUID.randomUUID());
		CompositeMap cookie = svc.getContextMap().createChild("cookie");
		populateCookieMap(request, cookie);
		copyParameter(svc.getRequest(), svc);
		copyHeader(svc.getRequest(), svc);
	}

	/*
	 * public static JSONObject getParameterString(HttpServletRequest request) {
	 * Map params = request.getParameterMap(); JSONObject ps = new JSONObject();
	 * if (params.size() > 0) { Iterator it = params.entrySet().iterator();
	 * String[] valueHolder = new String[1]; while (it.hasNext()) { Map.Entry
	 * entry = (Map.Entry) it.next(); String name = entry.getKey().toString();
	 * Object value = entry.getValue(); String[] values; if (value instanceof
	 * String[]) { values = (String[]) value; } else { valueHolder[0] =
	 * value.toString(); values = valueHolder; } try { ArrayList al = new
	 * ArrayList(); for (int i = 0; i < values.length; i++) { if (values[i] !=
	 * null) { al.add(values[i]); } } ps.put(name, al); } catch (Exception e) {
	 * throw new RuntimeException(e); } } } return ps; }
	 */
	public static void populateCookieMap(HttpServletRequest request,
			CompositeMap target) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (int i = 0; i < cookies.length; i++) {
				CompositeMap m = createCookieMap(cookies[i]);
				target.put(cookies[i].getName(), m);
			}
	}

	public static CompositeMap createCookieMap(Cookie cookie) {
		CompositeMap m = new CompositeMap("cookie");
		m.put("name", cookie.getName());
		m.put("value", cookie.getValue());
		m.put("domain", cookie.getDomain());
		m.put("path", cookie.getPath());
		m.putInt("maxage", cookie.getMaxAge());
		m.putBoolean("secure", cookie.getSecure());
		return m;
	}
}
