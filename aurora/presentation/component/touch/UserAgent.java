package aurora.presentation.component.touch;

import aurora.service.ServiceInstance;
import aurora.service.ServiceThreadLocal;
import aurora.service.http.HttpServiceInstance;
import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;

public class UserAgent {

	private static final int IS_MOBILE_CLIENT = 1;

	private static final String[] MOBILE_SPECIFIC_SUBSTRING = {"hrms", "iPad",
			"iPhone", "Android", "MIDP", "Opera Mobi", "Opera Mini",
			"BlackBerry", "HP iPAQ", "IEMobile", "MSIEMobile", "Windows Phone",
			"HTC", "LG", "MOT", "Nokia", "Symbian", "Fennec", "Maemo", "Tear",
			"Midori", "armv", "Windows CE", "WindowsCE", "Smartphone",
			"240x320", "176x220", "320x320", "160x160", "webOS", "Palm",
			"Sagem", "Samsung", "SGH", "SIE", "SonyEricsson", "MMP", "UCWEB" };

	public static CompositeMap detectUserAgent(IObjectRegistry registry) {
		CompositeMap result = new CompositeMap();
		CompositeMap context = ServiceThreadLocal.getCurrentThreadContext();
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance
				.getInstance(context);
		String agent = svc.getRequest().getHeader("User-Agent");
		if(agent != null) result.put("text", agent.toLowerCase());
		result.put("touch_client", touchClient(agent));
		return result;
	}

	private static int touchClient(String userAgent) {
		if (null == userAgent) {
			return 0;
		}
		for (String mobile : MOBILE_SPECIFIC_SUBSTRING) {
			if (userAgent.contains(mobile)
					|| userAgent.contains(mobile.toUpperCase())
					|| userAgent.contains(mobile.toLowerCase())) {
				return IS_MOBILE_CLIENT;
			}
		}
		return 0;
	}
}
