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
        Integer sid = (Integer)session.getAttribute("sid");
        if(sid==null){
            sid = new Integer(getId());
            session.setAttribute("sid", sid);
            System.out.println("creating new session:"+sid);
        }else{
            System.out.println("existing session id:"+sid);
        }
    }

}
