/*
 * Created on 2008-3-1
 */
package aurora.bm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.ocm.OCManager;

public class ModelFactory implements IModelFactory {

    // public static final String PROC_GENERATE_SQL =
    // "aurora.database.sql.GenerateSqlStatement";

    public static final String DEFAULT_MODEL_EXTENSION = "bm";

    // UncertainEngine mUncertainEngine;
    OCManager mOcManager;

    CompositeLoader mCompositeLoader;

    // name -> BusinessModel
    Map mModelCache;

    public ModelFactory(OCManager ocm) {
        // mUncertainEngine = engine;
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
    public BusinessModel getModelForRead(String name, String ext)
            throws IOException {
        // assert name!=null;
        String full_name = name + '.' + ext;
        BusinessModel model = (BusinessModel) mModelCache.get(full_name);
        if (model == null) {
            model = getNewModelInstance(name, ext);
            mModelCache.put(full_name, model);
        }
        return model;
    }

    public BusinessModel getModelForRead(String name) throws IOException {
        return getModelForRead(name, mCompositeLoader.getDefaultExt());
    }

    public BusinessModel getModel(CompositeMap config) {
        BusinessModel model = new BusinessModel();
        model.setModelFactory(this);
        model.setOcManager(mOcManager);
        model.initialize(config);
        /*
         * String base = model.getExtend(); String mode = model.getExtendMode();
         * if (mode == null) mode = BusinessModel.VALUE_OVERRIDE; boolean
         * is_override = BusinessModel.VALUE_OVERRIDE .equalsIgnoreCase(mode);
         * if (base != null) { CompositeMap base_config = null; try {
         * base_config = getModelConfig(base); } catch (IOException ex) { throw
         * new RuntimeException("Error when loading base model " + base, ex); }
         * CompositeMap final_config = mergeConfig(config, base_config,
         * is_override); model.initialize(final_config); }
         */
        model.makeReady();
        mModelCache.put(model.getName(), model);
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

    public CompositeMap getModelConfig(String name, String ext)
            throws IOException {
        try {
            CompositeMap config = mCompositeLoader.loadFromClassPath(name, ext);
            if (config == null)
                throw new IOException("Can't load resource " + name);
            String base = config.getString(BusinessModel.KEY_EXTEND);
            String mode = config.getString(BusinessModel.KEY_EXTEND_MODE);
            if (mode == null)
                mode = BusinessModel.VALUE_OVERRIDE;
            boolean is_override = BusinessModel.VALUE_OVERRIDE
                    .equalsIgnoreCase(mode);
            if (base != null) {
                CompositeMap base_config = null;
                try {
                    base_config = getModelConfig(base);
                } catch (IOException ex) {
                    throw new RuntimeException("Error when loading base model "
                            + base, ex);
                }
                CompositeMap final_config = mergeConfig(config, base_config,
                        is_override);
                return final_config;
            } else
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

    public CompositeLoader getCompositeLoader() {
        return mCompositeLoader;
    }

}
