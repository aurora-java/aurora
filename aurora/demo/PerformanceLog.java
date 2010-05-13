/*
 * Created on 2010-5-13 下午03:02:55
 * $Id$
 */
package aurora.demo;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.ProcedureRunner;

public class PerformanceLog {
    
    public void doLog( CompositeMap context, String msg ){
        ILogger logger = LoggingContext.getLogger(context, "aurora.application");
        logger.info(msg);
    }
    
    public void preCheckSession( ProcedureRunner runner ){
        doLog(runner.getContext(), "Service begin");
    }
    
    public void preCreateSuccessResponse( ProcedureRunner runner ){
        doLog(runner.getContext(), "Internal logic finish");
    }
    
    public void postServiceEnd( ProcedureRunner runner ){
        doLog(runner.getContext(), "Service end");
    }
}
