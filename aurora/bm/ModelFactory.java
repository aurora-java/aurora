/*
 * Created on 2008-3-1
 */
package aurora.bm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;

public class ModelFactory {
    
    //public static final String PROC_GENERATE_SQL = "aurora.database.sql.GenerateSqlStatement"; 
    
    UncertainEngine         uncertainEngine;
    
    CompositeLoader         compositeLoader; 
    
    // name -> BusinessModel
    Map                     modelCache;
    
    public ModelFactory(UncertainEngine  engine){
        this.uncertainEngine = engine;
        this.compositeLoader = engine.getCompositeLoader();
        modelCache = new HashMap();
    }
    
    /**
     * Get a model instance. The returned model is for read only.
     * Invoker shall not make any modification to returned model.
     * @param name name of model
     * @return A read-only BusinessModel instance
     * @throws IOException
     */
    public BusinessModel getModelForRead( String name )
        throws IOException
    {
        BusinessModel model = (BusinessModel)modelCache.get(name);
        if(model==null){
            model = getNewModelInstance( name );
            modelCache.put(name, model);
        }
        return model;
    }
    
    /**
     * 
     * @param config
     * @return
     */
    public BusinessModel getModel( CompositeMap config ){
        BusinessModel model = new BusinessModel();
        model.setModelFactory(this);
        model.initialize(config);
        model.makeReady();
        modelCache.put(model.getName(), model);
        return model;        
    }

    public CompositeMap getModelConfig( String name )
        throws IOException
    {
        CompositeMap config = uncertainEngine.loadCompositeMap(name);
        if(config==null) throw new IOException("Can't load resource "+name);
        return config;        
    }
    
    protected BusinessModel getNewModelInstance( String name )
        throws IOException
    {
        if(name==null)
            throw new IllegalArgumentException("model name is null");
        CompositeMap config = getModelConfig(name);
        BusinessModel model = getModel( config );
        model.setName(name);
        return model;
    }
    
    
    public BusinessModel getModel( String name )
        throws IOException
    {
        return getNewModelInstance(name);
    }    


}
