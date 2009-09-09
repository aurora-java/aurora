/*
 * Created on 2009-5-14
 */
package aurora.application.features;

import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.event.EventModel;
import uncertain.event.RuntimeContext;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.ProcedureRunner;
import aurora.database.service.DatabaseServiceFactory;
import aurora.presentation.BuildSession;
import aurora.presentation.PresentationManager;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

public class ScreenRenderer {
    
    public static final String PAGE = "page";
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
    HttpServiceInstance                 mService;
    CompositeMap                mContext;
    CompositeMap                mScreen;
    DatabaseServiceFactory      mServiceFactory;
    
    public int onCreateView( ProcedureRunner runner ){
        mContext = runner.getContext(); 
        mService = (HttpServiceInstance)ServiceInstance.getInstance(mContext);        
        mScreen = mService.getServiceConfigData().getChild(PAGE);
        if( mScreen != null ){
            mScreen.setName(HTML_PAGE);
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
        response.setContentType("text/html;charset=utf-8");
        Writer out = response.getWriter();
        BuildSession session = mPrtManager.createSession(out);
        ILogger logger = LoggingContext.getLogger(runner.getContext(), BuildSession.LOGGING_TOPIC);
        session.setLogger(logger);
        /*
        RuntimeContext  rtc = RuntimeContext.getInstance(mContext);
        ILogger logger = (ILogger)rtc.getInstanceOfType(ILogger.class);
        session.setLogger(logger);
        */
        session.buildView(mService.getServiceContext().getModel(), mScreen);
        out.flush();
        
        return EventModel.HANDLE_NO_SAME_SEQUENCE;
        
    }

}
