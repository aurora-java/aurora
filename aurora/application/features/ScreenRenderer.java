/*
 * Created on 2009-5-14
 */
package aurora.application.features;

import java.io.Writer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.composite.CompositeMap;
import uncertain.event.EventModel;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.ProcedureRunner;
import aurora.application.config.ScreenConfig;
import aurora.database.service.DatabaseServiceFactory;
import aurora.presentation.BuildSession;
import aurora.presentation.PresentationManager;
import aurora.service.IService;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

public class ScreenRenderer {

    public static final String HTML_PAGE = "html-page";

    /**
     * @param prtManager
     */
    public ScreenRenderer(PresentationManager prtManager, DatabaseServiceFactory fact ) {
        super();
        mPrtManager = prtManager;
        mServiceFactory = fact;
    }

    PresentationManager         mPrtManager;
    HttpServiceInstance         mService;
    CompositeMap                mContext;
    CompositeMap                mScreen;
    DatabaseServiceFactory      mServiceFactory;
    
    public int onCreateView( ProcedureRunner runner ){
        mContext = runner.getContext(); 
        mService = (HttpServiceInstance)ServiceInstance.getInstance(mContext); 
        ScreenConfig cfg = ScreenConfig.createScreenConfig(mService.getServiceConfigData());
        mScreen = cfg.getViewConfig();
        if( mScreen != null ){
            mScreen.setName(HTML_PAGE);
            mScreen.setNameSpaceURI(null);
            mContext.addChild(mScreen);
            mContext.putBoolean("output", true);
        }
        return EventModel.HANDLE_NORMAL;
    }
    
    public int onBuildOutputContent( ProcedureRunner runner )
        throws Exception
    {
        if( mScreen==null ) return EventModel.HANDLE_NORMAL;
        HttpServletResponse response = mService.getResponse();
        HttpServletRequest request = mService.getRequest();
        response.setContentType("text/html;charset=utf-8");
        Writer out = response.getWriter();
        BuildSession session = mPrtManager.createSession(out);
        Cookie[] cookies = request.getCookies();
        String appTheme = "default";
	    if(cookies!=null) {
		    for(int i=0; i<cookies.length; i++){
		    	Cookie cookie = cookies[i];
		    	String cname = cookie.getName();
		    	if("app_theme".equals(cname)){	    		
		    		appTheme = cookie.getValue();
		    	}
		    }      
    	}
    	session.setTheme(appTheme);
        session.setBaseConfig(mService.getServiceConfig());
        session.setInstanceOfType(IService.class, mService);
        ILogger logger = LoggingContext.getLogger(runner.getContext(), BuildSession.LOGGING_TOPIC);
        //System.out.println("session "+logger);
        //logger.info("start build session");
        session.setLogger(logger);
        session.buildView(mService.getServiceContext().getModel(), mScreen);
        out.flush();
        
        return EventModel.HANDLE_NO_SAME_SEQUENCE;
        
    }

}
