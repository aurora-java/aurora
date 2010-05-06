/*
 * Created on 2010-4-29 上午01:32:17
 * $Id$
 */
package aurora.demo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ocm.ISingleton;
import uncertain.proc.CheckCookie;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

public class SessionChecker implements ISingleton {

	private String url;
	private String value;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void onCheckSession(CheckCookie checkcookie, CompositeMap context)
			throws Exception {
		// String s =
		// context.getObjectContext().getObject(checkcookie.getField()).toString();
		// checkcookie.setField(context.getObjectContext().getObject(checkcookie.getField()).toString());
		if (context.getObject(checkcookie.getField()).toString().equals(
				checkcookie.getValue().toString())) {
			HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance
					.getInstance(context);
			svc.getResponse().sendRedirect(checkcookie.getUrl());
		}
		
	}

}