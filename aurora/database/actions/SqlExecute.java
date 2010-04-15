/*
 * Created on 2008-6-17
 */
package aurora.database.actions;

import java.util.Collection;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.database.SqlRunner;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.RawSqlService;
import aurora.database.service.SqlServiceContext;

public class SqlExecute extends AbstractEntry {
    
    public static final String MODE_BATCH = "batch";
    
    DatabaseServiceFactory  svcFactory;
    
    String                  service;
    
    String                  sourcePath;
    
    String                  mode = "single";
    
    boolean                 trace;

    public SqlExecute( DatabaseServiceFactory svcFactory ) {
        this.svcFactory = svcFactory;
    }

    public void run(ProcedureRunner runner) throws Exception {
        if( service==null ) throw new ConfigurationError("Must set 'service' property");
        SqlServiceContext context = SqlServiceContext.createSqlServiceContext(runner.getContext());
        String parsed_service = TextParser.parse(service, runner.getContext());        
        RawSqlService svc = svcFactory.getSqlService(parsed_service, context);
        svc.setTrace(getTrace());
        if(MODE_BATCH.equalsIgnoreCase(mode)){
            CompositeMap map = context.getCurrentParameter();
            Collection params = SqlRunner.getSourceParameter(map, sourcePath);
            if(params!=null)
                svc.updateBatch(context, params);
        }else
            svc.update(context);
    }

    /**
     * @return the service
     */
    public String getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * @return the mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * @return the source
     */
    public String getSourcePath() {
        return sourcePath;
    }

    /**
     * @param source the source to set
     */
    public void setSourcePath(String source) {
        this.sourcePath = source;
    }

    /**
     * @return the trace
     */
    public boolean getTrace() {
        return trace;
    }

    /**
     * @param trace the trace to set
     */
    public void setTrace(boolean trace) {
        this.trace = trace;
    }

}
