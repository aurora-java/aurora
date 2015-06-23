/*
 * Created on 2010-4-29 上午01:32:17
 * $Id$
 */
package aurora.demo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import uncertain.ocm.ISingleton;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

public class SessionChecker implements ISingleton {
    
    public static final String USER_ID = "user_id";
    static int sequence = 1000;
    
    static synchronized int getId(){
        return sequence++;
    }
    
    public void onCheckSession( ServiceContext context)
        throws Exception
    {
        HttpServiceInstance svc = (HttpServiceInstance)ServiceInstance.getInstance(context.getObjectContext());
        HttpServletRequest request = svc.getRequest();
        HttpSession session = request.getSession(true);
        Integer sid = (Integer)session.getAttribute(USER_ID);
        if(sid==null){
            sid = new Integer(getId());
            session.setAttribute(USER_ID, sid);
        }
        context.getObjectContext().putObject("/session/@user_id", sid,true);
    }

}
