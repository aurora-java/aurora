/*
 * Created on 2009-8-6
 */
package aurora.presentation.component;

import java.io.IOException;
import java.io.Writer;

import uncertain.composite.CompositeMap;
import uncertain.composite.JSONAdaptor;
import uncertain.core.ConfigurationError;
import aurora.bm.IModelFactory;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;

/**
 * Transport model meta data as JSON object ModelMetaData
 * 
 * @author Zhou Fan
 * 
 */
public class ModelMetaData implements IViewBuilder {

    IModelFactory mFactory;

    public ModelMetaData(IModelFactory factory) {
        this.mFactory = factory;
    }

    static String getLastPart(String name) {
        int id = name.lastIndexOf('.');
        if (id > 0)
            return name.substring(id + 1, name.length());
        else
            return name;
    }

    public void buildView(BuildSession session, ViewContext view_context)
            throws IOException, ViewCreationException {
        CompositeMap view = view_context.getView();
        String base_model = view.getString("ref");
        if (base_model != null) {

            CompositeMap bm = mFactory.getModelConfig(base_model);
            if (bm == null)
                throw new ConfigurationError("Can't load BusinessModel "
                        + base_model);
            String name = view.getString("name");
            if (name == null)
                name = getLastPart(bm.getName());

            Writer out = session.getWriter();
            out.write("<script>\r\n var ");
            out.write(name);
            out.write(" = ");
            out.write(JSONAdaptor.toJSONObject(bm).toString());
            out.write("; \r\n</script>");
        }
    }

    public String[] getBuildSteps(ViewContext context) {
        return null;
    }

}
