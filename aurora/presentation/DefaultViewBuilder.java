/*
 * Created on 2009-7-23
 */
package aurora.presentation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import uncertain.util.template.CompositeMapTagCreator;
import uncertain.util.template.ITagCreatorRegistry;
import uncertain.util.template.TagCreatorRegistry;
import uncertain.util.template.TagTemplateParser;
import uncertain.util.template.TextTemplate;

/**
 * Directly output tag content in view config. This builder will be set as
 * default IViewBuilder for unknown view config
 * 
 * @author Zhou Fan
 * 
 */
public class DefaultViewBuilder implements IViewBuilder, ISingleton {
    
    static CompositeMapTagCreator DEFAULT_CREATOR = new CompositeMapTagCreator();
    /*
    static TagCreatorRegistry DEFAULT_TAG_CREATOR_REGISTRY = new TagCreatorRegistry();
    static {
        DEFAULT_TAG_CREATOR_REGISTRY.registerTagCreator(null, new CompositeMapTagCreator() );
    } 
    */   
/*
 */
    String getParsedContent(BuildSession session, ITagCreatorRegistry reg, String text, CompositeMap model) 
        throws IOException
    {
    	text = prepareText(text);
        if (text.indexOf("$") >= 0)
            return getParsedContent(session, text, model, reg);
        else
            return text;
    }
    
    private static String prepareText( String text ){
        if(text.indexOf("$(") !=-1){
            text = text.replaceAll("\\$\\(", "\\$\\$\\(");
        }
        return text;
    }
    
    private static TagCreatorRegistry createTagRegistryFromSession(BuildSession session){
        ITagCreatorRegistry parent = session.getTagCreatorRegistry();
        TagCreatorRegistry  reg = new TagCreatorRegistry();
        reg.setDefaultCreator(DEFAULT_CREATOR);
        reg.setParent(parent);
        return reg;
    }
    
    private static String getParsedContent(BuildSession session, String text, CompositeMap model, ITagCreatorRegistry reg )
        throws IOException {
        TagTemplateParser parser = session.getPresentationManager().getTemplateParser();
        StringReader reader = new StringReader(text);
        TextTemplate tplt = parser.buildTemplate(reader, reg);        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos);
        tplt.createOutput(writer, model);
        writer.flush();
        baos.flush();
        String result = baos.toString();
        baos.close();
        return result;        
    }

    public void buildView(BuildSession session, ViewContext view_context)
            throws IOException, ViewCreationException {
        TagCreatorRegistry reg = createTagRegistryFromSession(session);
        
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
                        out.write(getParsedContent(session, reg, value.toString(),model));
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
                    out.write(getParsedContent(session, reg, text,model));
                    out.write(close_tag);
                }
            }
            out.flush();
    }

    public String[] getBuildSteps(ViewContext context) {
        return null;
    }

}
