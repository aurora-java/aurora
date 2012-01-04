/*
 * Created on 2010-5-11 下午04:51:02
 * $Id$
 */
package aurora.application;

import uncertain.composite.CompositeMap;
import uncertain.exception.MessageFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;

/**
 * Provides static constants and util methods
 */
public class AuroraApplication {
    /*
    static {
        MessageFactory.loadResource("resources.aurora_validation_exceptions");
    }
    */
    public static final String AURORA_FRAMEWORK_NAMESPACE = "http://www.aurora-framework.org/application";
    public static final String AURORA_BUSINESS_MODEL_NAMESPACE = "http://www.aurora-framework.org/schema/bm";
    public static final String AURORA_DATABASE_NAMESPACE = "http://www.aurora-framework.org/schema/database";
    
    public static final String AURORA_APP_LOGGING_TOPIC = "aurora.application";
    public static final String AURORA_APP_SESSION_CHECK_LOGGING_TOPIC = "aurora.application.session_check";
    
    public static ILogger getLogger(CompositeMap context){
        return LoggingContext.getLogger(context, AURORA_APP_LOGGING_TOPIC);
    }

}
