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

    
    public BusinessModel getNewModelInstance( String name )
        throws IOException
    {
        if(name==null)
            throw new IllegalArgumentException("model name is null");
        CompositeMap config = uncertainEngine.loadCompositeMap(name);
        if(config==null) throw new IOException("Can't load resource "+name);
        BusinessModel model = new BusinessModel();
        model.setModelFactory(this);
        model.initialize(config);
        model.setName(name);
        model.makeReady();
        return model;
    }
    
    
    public BusinessModel getModel( String name )
        throws IOException
    {
        return getNewModelInstance(name);
    }    
    /*
    public void invoke( Model model, String procedure_name, CompositeMap context ) 
        throws Throwable 
    {
        ProcedureRunner runner = uncertainEngine.createProcedureRunner(procedure_name);
        if(runner==null) throw new IllegalArgumentException("can't load procedure "+procedure_name);
        runner.run();
        if(runner.getException()!=null)
            throw runner.getException();
    }
    */
    
    /*
    public StringBuffer generateSql( BusinessModel meta, String sql_type){
        ProcedureRunner runner = uncertainEngine.createProcedureRunner(PROC_GENERATE_SQL);
        if(runner==null) throw new IllegalStateException("Can't load "+PROC_GENERATE_SQL);
        Configuration config = uncertainEngine.createConfig(meta.getObjectContext());
        runner.setConfiguration(config);
        StringBuffer sql = (StringBuffer)runner.getContextField("GeneratedSql");        
        return sql;
    }
    */

}
