/*
 * Created on 2011-7-14 下午03:43:23
 * $Id$
 */
package aurora.application.action;

import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.application.AuroraApplication;
import aurora.application.ISessionInfoProvider;
import aurora.application.util.LanguageUtil;
import aurora.bm.IBusinessModelAccessChecker;
import aurora.bm.IBusinessModelAccessCheckerFactory;
import aurora.i18n.ILocalizedMessageProvider;
import aurora.security.IResourceAccessChecker;
import aurora.service.ServiceContext;
import aurora.service.http.AutoCrudServiceContext;
import aurora.service.validation.ErrorMessage;

public class BmAccessCheck extends AbstractEntry {
    
    public static final String DEFAULT_ACCESS_CHECK_ERROR_MESSAGE = "aurora.bm.access_check_fail";
    
    IObjectRegistry                         mRegistry;
    IBusinessModelAccessCheckerFactory      mBmAccessChecker;
    ISessionInfoProvider                    mSessionProvider;


    String                                  mResultPath = "@success";
    String                                  mErrorMessage = DEFAULT_ACCESS_CHECK_ERROR_MESSAGE;
   
    public BmAccessCheck(IObjectRegistry reg, IBusinessModelAccessCheckerFactory accessChecker, ISessionInfoProvider provider) {
        super();
        this.mBmAccessChecker = accessChecker;
        this.mSessionProvider = provider;
        this.mRegistry = reg;
        //this.mModelFactory = factory;
    }

    public void run(ProcedureRunner runner) throws Exception {
        boolean result = true;
        CompositeMap context_map = runner.getContext();
        ILogger logger = LoggingContext.getLogger(context_map, AuroraApplication.AURORA_APP_SESSION_CHECK_LOGGING_TOPIC);
        ServiceContext sc = ServiceContext.createServiceContext(context_map);

        AutoCrudServiceContext acsc = AutoCrudServiceContext.createAutoCrudServiceContext(context_map);
        String operation = acsc.getRequestedOperation();
        String bm = acsc.getRequestedBM();
        
        logger.log(Level.CONFIG, "Checking BM access {0} for operation {1}", new Object[]{bm, operation});
        if(!"batch_update".equals(operation)){
            IBusinessModelAccessChecker checker = mBmAccessChecker.getChecker(bm, context_map);
            if(checker==null){
                logger.config("No access checker found. Maybe there is no such combination defined in system. Access check failed");
                result = false;
            }
            else{
                result = checker.canPerformOperation(operation);
                logger.log(Level.CONFIG, "Result from {0} is {1}", new Object[]{checker, new Boolean(result)} );
            }
        }else{
            logger.config("for batch_update, access check will be performed on each record from parameter");
        }
        if(mResultPath!=null){
            context_map.putObject(mResultPath, new Boolean(result), true);
            //logger.log(Level.CONFIG, "Writting result to {0}", new Object[]{mResultPath});
        }
        else{
            sc.setSuccess(result);
        }
        if(!result){
            String code = IResourceAccessChecker.RESULT_UNAUTHORIZED;
            if(!mSessionProvider.isLoggedin(context_map))
                code = IResourceAccessChecker.RESULT_LOGIN_REQUIRED;
            String msg = mErrorMessage==null?code:mErrorMessage;
            msg = LanguageUtil.getTranslatedMessage(mRegistry, msg, context_map);
            ErrorMessage m = new ErrorMessage( code, msg, null);
            sc.setError(m.getObjectContext());
            return;
        }
            
    }

    public String getResultPath() {
        return mResultPath;
    }

    public void setResultPath(String resultPath) {
        this.mResultPath = resultPath;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        mErrorMessage = errorMessage;
    }

}
