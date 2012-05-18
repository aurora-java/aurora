/*
 * Created on 2008-3-1
 */
package aurora.bm;

import java.io.IOException;
import java.util.Iterator;

import org.xml.sax.SAXException;


import uncertain.cache.CacheFactoryConfig;
import uncertain.cache.ICache;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.exception.MessageFactory;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;

public class ModelFactory implements IModelFactory {
    
    static {
        MessageFactory.loadResource("resources/aurora_bm_exceptions");
    }

    // public static final String PROC_GENERATE_SQL =
    // "aurora.database.sql.GenerateSqlStatement";

    public static final String DEFAULT_MODEL_EXTENSION = "bm";

    // UncertainEngine mUncertainEngine;
    OCManager mOcManager;

    CompositeLoader mCompositeLoader;
    
    IObjectRegistry  mObjRegistry;
    
    boolean mUseCache = false;

    // name -> BusinessModel
    ICache mModelCache;
    
    public void onInitialize(){
        if(mObjRegistry!=null){
            ICache cache = CacheFactoryConfig.getNamedCache(mObjRegistry, "BusinessModel");
            if(cache!=null){
                setUseCache(true);
                setCache(cache);
            }
        }
    }

    public boolean getUseCache() {
        return mUseCache;
    }

    public void setUseCache(boolean useCache) {
        this.mUseCache = useCache;
        if(!mUseCache)
            if(mModelCache!=null)
                mModelCache.clear();
    }
    
    public ICache getCache() {
        return mModelCache;
    }

    public void setCache(ICache mModelCache) {
        this.mModelCache = mModelCache;
    }
    
    private String getCacheKey( String name, String ext ){
        return name+'['+ext+']';
    }

    public ModelFactory(OCManager ocm) {
        // mUncertainEngine = engine;
        mOcManager = ocm;
        mCompositeLoader = CompositeLoader.createInstanceForOCM();
        mCompositeLoader.setDefaultExt(DEFAULT_MODEL_EXTENSION);
    }
    
    public ModelFactory(OCManager ocm, IObjectRegistry reg) {
        this(ocm);
        this.mObjRegistry = reg;
    }
    
    private void saveCachedModel( String name, BusinessModel model ){
        if(mUseCache && mModelCache!=null)
            mModelCache.setValue(name, model);
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
    public BusinessModel getModelForRead(String name, String ext)
            throws IOException {
        if(!mUseCache)
            return getNewModelInstance(name, ext);
        String full_name = getCacheKey(name,ext);
        BusinessModel model = (BusinessModel) mModelCache.getValue(full_name);
        if (model == null) {
            model = getNewModelInstance(name, ext);
            saveCachedModel(full_name, model);
        }
        return model;
    }

    public BusinessModel getModelForRead(String name) throws IOException {
        return getModelForRead(name, mCompositeLoader.getDefaultExt());
    }

    public BusinessModel getModel(CompositeMap config) {
        BusinessModel model = createBusinessModelInternal(config);
        return model;
    }

    /**
     * @todo check this
     * @param config
     * @param base_config
     * @return
     */
    private CompositeMap mergeConfig(CompositeMap config,
            CompositeMap base_config, boolean is_override) {
        CompositeMap merged_map = (CompositeMap) config.clone();
        CompositeUtil.copyAttributes(base_config, merged_map);
        Iterator it = base_config.getChildIterator();
        while (it.hasNext()) {
            CompositeMap origin_child = (CompositeMap) it.next();
            String name = origin_child.getName();
            CompositeMap new_child = merged_map.getChild(name);
            if (new_child != null) {
                if (is_override)
                    CompositeUtil.mergeChildsByOverride(origin_child,
                            new_child, "name");
                else
                    CompositeUtil.mergeChildsByReference(origin_child,
                            new_child, "name");
            } else
                merged_map.addChild((CompositeMap) origin_child.clone());
        }
        return merged_map;
    }

    protected BusinessModel createBusinessModelInternal(CompositeMap config) {
        BusinessModel model = new BusinessModel();
        model.setModelFactory(this);
        model.setOcManager(mOcManager);
        model.initialize(config);

        String base = model.getExtend();
        if (base != null) {
            String mode = model.getExtendMode();
            if (mode == null)
                mode = BusinessModel.VALUE_OVERRIDE;
            boolean is_override = BusinessModel.VALUE_OVERRIDE
                    .equalsIgnoreCase(mode);
            try {
                BusinessModel parent_model = getModelForRead(base);
                CompositeMap final_config = mergeConfig(config, parent_model
                        .getObjectContext(), is_override);
                model.initialize(final_config);
                model.setParent(parent_model);
            } catch (IOException ex) {
                  throw BmBuiltinExceptionFactory.createParentBMLoadException(base, config, ex );
//                throw new RuntimeException("Error when loading base model "
//                        + base, ex);
            }
        }
        model.makeReady();
        return model;
    }

    protected BusinessModel getNewModelInstance( String name, String ext )
            throws IOException {
        if (name == null)
            throw new IllegalArgumentException("model name is null");
            //throw BuiltinExceptionFactory.createAttributeMissing(source, "name");
        try {
            CompositeMap config = mCompositeLoader.loadFromClassPath(name, ext);
            if (config == null)
                throw new IOException("Can't load resource " + name);
            BusinessModel model = createBusinessModelInternal(config);
            model.setName(name);
            //saveCachedModel(name, model);
            return model;
        } catch (SAXException ex) {
            throw new RuntimeException("Error when parsing " + name, ex);
        }
    }

    public CompositeMap getModelConfig(String name, String ext)
            throws IOException {
        BusinessModel model = getNewModelInstance(name, ext);
        return model.getObjectContext();
    }

    public CompositeMap getModelConfig(String name) throws IOException {
        return getModelConfig(name, mCompositeLoader.getDefaultExt());
    }

    public BusinessModel getModel(String name, String ext) throws IOException {
        return getNewModelInstance(name, ext);
    }

    public BusinessModel getModel(String name) throws IOException {
        return getModel(name, mCompositeLoader.getDefaultExt());
    }

    public CompositeLoader getCompositeLoader() {
        return mCompositeLoader;
    }


}
