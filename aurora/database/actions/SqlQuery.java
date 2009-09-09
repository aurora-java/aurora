/*
 * Created on 2008-6-17
 */
package aurora.database.actions;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.ocm.OCManager;
import uncertain.proc.ProcedureRunner;
import aurora.database.FetchDescriptor;
import aurora.database.IResultSetConsumer;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.RawSqlService;
import aurora.database.service.SqlServiceContext;

public class SqlQuery extends AbstractQueryAction {

    // public static final String MODE_BATCH = "batch";
    // String mode = "single";
  
    DatabaseServiceFactory svcFactory;

    String service;
    String sourcePath;

    SqlServiceContext context;
    RawSqlService sqlService;

    public SqlQuery(DatabaseServiceFactory svcFactory, OCManager manager) {
        super(manager);
        this.svcFactory = svcFactory;
    }

    protected void prepare( CompositeMap context_map ) throws Exception {
        context = SqlServiceContext
                .createSqlServiceContext(context_map);
        if (service == null)
            throw new ConfigurationError("Must set 'service' property");
        sqlService = svcFactory.getSqlService(service, context);
    }

    protected void doQuery(CompositeMap param, IResultSetConsumer consumer,
            FetchDescriptor desc) throws Exception {
        sqlService.query(context, consumer, desc);
    }

    protected void cleanUp(CompositeMap context_map ) {

    }

    /**
     * @return the service
     */
    public String getService() {
        return service;
    }

    /**
     * @param service
     *            the service to set
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * @return the source
     */
    public String getSourcePath() {
        return sourcePath;
    }

    /**
     * @param source
     *            the source to set
     */
    public void setSourcePath(String source) {
        this.sourcePath = source;
    }

}
