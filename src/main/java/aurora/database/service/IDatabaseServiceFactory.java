/*
 * Created on 2011-9-30 下午01:04:24
 * $Id$
 */
package aurora.database.service;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import uncertain.composite.CompositeMap;
import aurora.bm.BusinessModel;
import aurora.bm.IModelFactory;
import aurora.database.profile.IDatabaseFactory;

public interface IDatabaseServiceFactory {

    /**
     * @return the dataSource
     */
    public DataSource getDataSource();

    /**
     * @param dataSource
     *            the dataSource to set
     */
    public void setDataSource(DataSource dataSource);

    /**
     * @return ModelFactory instance to create BusinessModel from xml config
     */
    public IModelFactory getModelFactory();

    /**
     * @param metadataFactory
     *            the ModelFactory to set
     */
    public void setModelFactory(IModelFactory factory);

    public BusinessModelService getModelService(String name) throws IOException;

    public BusinessModelService getModelService(BusinessModel model,
            CompositeMap context_map) throws IOException;

    public BusinessModelService getModelService(CompositeMap bm_config,
            CompositeMap context_map) throws IOException;

    public BusinessModelService getModelService(String name,
            CompositeMap context_map) throws IOException;

    public IDatabaseFactory getDatabaseFactory();

    public void setDatabaseFactory(IDatabaseFactory databaseFactory);
    
    public SqlServiceContext createContextWithConnection() throws SQLException;

}