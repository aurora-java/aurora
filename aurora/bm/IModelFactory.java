/*
 * Created on 2009-9-14 下午02:22:48
 * Author: Zhou Fan
 */
package aurora.bm;

import java.io.IOException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;

public interface IModelFactory {
    
    /**
     * Get model config in CompositeMap
     * @param name Name of config file in java full class name style, such as myproj.model.OrderLine
     * @return CompositeMap instance that contains config, or null if can't load by specified name
     */
    public CompositeLoader  getCompositeLoader();
    
    public CompositeMap getModelConfig( String name ) throws IOException;
    
    public CompositeMap getModelConfig( String name, String ext ) throws IOException;
    
    /**
     * Get a new model instance, the returned instance can be modified
     * @param name Name of model
     * @return Created BusinessModel instance
     */
    public BusinessModel getModel( String name ) throws IOException;
    
    public BusinessModel getModel( String name, String ext ) throws IOException;
    
    /**
     * Get a model instance. The returned model is for read only.
     * Invoker shall not make any modification to returned model.
     * @param name name of model
     * @return A read-only BusinessModel instance
     * @throws IOException
     */
    public BusinessModel getModelForRead( String name ) throws IOException;
    
    public BusinessModel getModelForRead( String name, String ext ) throws IOException;    
    
    /**
     * Create a BusinessModel instance from previously loaded CompositeMap
     * @param config CompositeMap containing BusinessModel config
     * @return Created BusinessModel instance
     */
    public BusinessModel getModel( CompositeMap config );    

}
