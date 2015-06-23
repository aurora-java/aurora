/*
 * Created on 2009-7-23
 */
package aurora.presentation;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;

/**
 * Directly output tag content in view config. This builder will be set as
 * default IViewBuilder for unknown view config
 * 
 * @author Zhou Fan
 * 
 */
public class DefaultViewBuilder implements IViewBuilder, ISingleton {
    
    //static CompositeMapTagCreator DEFAULT_CREATOR = new CompositeMapTagCreator();

    private static String getParsedContent(BuildSession session, String text, CompositeMap model) 
        throws IOException
    {
        /*
    	if(text.indexOf("$(") !=-1){
            text = text.replaceAll("\\$\\(", "\\$\\$\\(");
        }
        */
        return session.parseString(text, model);
    }

    /*
    private static TagCreatorRegistry createTagRegistryFromSession(BuildSession session){
        ITagCreatorRegistry parent = session.getTagCreatorRegistry();
        TagCreatorRegistry  reg = new TagCreatorRegistry();
        reg.setDefaultCreator(DEFAULT_CREATOR);
        reg.setParent(parent);
        return reg;
    }
    
    public static String parseString( BuildSession session, String text, CompositeMap model )
        throws IOException {
        ITagCreatorRegistry reg = createTagRegistryFromSession(session);
        return BuildSession.getParsedContent(session.getPresentationManager().getTemplateParser(), text, model, reg);
    }
    */

    public void buildView(BuildSession session, ViewContext view_context)
            throws IOException, ViewCreationException {
        //TagCreatorRegistry reg = createTagRegistryFromSession(session);
        
        CompositeMap view = view_context.getView();
        CompositeMap model = view_context.getModel();
        String close_tag = "</" + view.getName() + ">";
        Writer out = session.getWriter();
            out.write('<');
            out.write(view.getName());
            if(view.size()>0){          // print attributes
                for(Iterator it = view.entrySet().iterator(); it.hasNext();){
                    Map.Entry entry = (Map.Entry)it.next();
                    Object key = entry.getKey();
                    if(key==null) continue;
                    out.write(' ');
                    out.write(key.toString());
                    out.write("=\"");
                    Object value = entry.getValue();
                    if(value!=null)
                        out.write(getParsedContent(session, value.toString(), model));
                        //out.write(getParsedContent(session, reg, value.toString(),model));
                    out.write('\"');                            
                }
            }
            out.write('>');
            Collection childs = view.getChilds();
            if(childs!=null){           // print childs
                try{
                    session.buildViews(model, childs);
                }catch(Exception ex){
                    throw new ViewCreationException(ex);
                }
                out.write(close_tag);
            }else{
                String text = view.getText();
                if(text!=null){
                    out.write(getParsedContent(session, text, model));
                    //out.write(getParsedContent(session, reg, text,model));
                    out.write(close_tag);
                }else{
                	out.write(close_tag);
                }
            }
            out.flush();
    }

    public String[] getBuildSteps(ViewContext context) {
        return null;
    }

}
