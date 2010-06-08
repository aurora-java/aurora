/*
 * Created on 2008-3-1
 */
package aurora.bm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ocm.OCManager;

public class ModelFactory implements IModelFactory {

    // public static final String PROC_GENERATE_SQL =
    // "aurora.database.sql.GenerateSqlStatement";

    public static final String DEFAULT_MODEL_EXTENSION = "bm";

    //UncertainEngine mUncertainEngine;
    OCManager       mOcManager;

    CompositeLoader mCompositeLoader;

    // name -> BusinessModel
    Map mModelCache;

    public ModelFactory(OCManager   ocm) {
        //mUncertainEngine = engine;
        mOcManager = ocm;
        mCompositeLoader = CompositeLoader.createInstanceForOCM();
        mCompositeLoader.setDefaultExt(DEFAULT_MODEL_EXTENSION);
        mModelCache = new HashMap();
    }

    /**
     * Get a model instance. The returned model is for read only. Invoker shall
     * not make any modification to returned model.
     * 
     * @param name
     *            name of model
     * @return A read-only BusinessModel instance
     * @throws IOException
     */
    public BusinessModel getModelForRead(String name, String ext ) throws IOException {
//        assert name!=null;
        String full_name = name + '.' + ext;
        BusinessModel model = (BusinessModel) mModelCache.get(full_name);
        if (model == null) {
            model = getNewModelInstance(name, ext);
            mModelCache.put(full_name, model);
        }
        return model;
    }
    
    public BusinessModel getModelForRead(String name ) throws IOException {
        return getModelForRead(name, mCompositeLoader.getDefaultExt());
    }

    public BusinessModel getModel(CompositeMap config) {
        BusinessModel model = new BusinessModel();
        model.setModelFactory(this);
        model.setOcManager(mOcManager);
        model.initialize(config);
        model.makeReady();
        mModelCache.put(model.getName(), model);
        return model;
    }

    public CompositeMap getModelConfig(String name, String ext)
            throws IOException {
        try {
            CompositeMap config = mCompositeLoader.loadFromClassPath(name, ext);
            if (config == null)
                throw new IOException("Can't load resource " + name);
            return config;
        } catch (SAXException ex) {
            throw new RuntimeException("Error when parsing " + name, ex);
        }
    }

    public CompositeMap getModelConfig(String name) throws IOException {
        return getModelConfig(name, mCompositeLoader.getDefaultExt());
    }

    protected BusinessModel getNewModelInstance(String name, String ext)
            throws IOException {
        if (name == null)
            throw new IllegalArgumentException("model name is null");
        CompositeMap config = getModelConfig(name, ext);
        BusinessModel model = getModel(config);
        model.setName(name);
        return model;
    }

    public BusinessModel getModel(String name, String ext) throws IOException {
        return getNewModelInstance(name, ext);
    }
    
    public BusinessModel getModel(String name) throws IOException {
        return getNewModelInstance(name, mCompositeLoader.getDefaultExt());
    }
    
    public CompositeLoader  getCompositeLoader(){
        return mCompositeLoader;
    }

}
