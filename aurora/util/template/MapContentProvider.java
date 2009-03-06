/*
 * Created on 2007-8-5 ÏÂÎç05:18:43
 */
package aurora.util.template;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import uncertain.composite.CompositeMap;

public class MapContentProvider implements IContentProvider {
    
    Map                 simple_map;
    CompositeMap        composite_map;
    
    public MapContentProvider(Map map){
        if(simple_map instanceof CompositeMap)
            composite_map = (CompositeMap)map;
        else
            simple_map = map;
    }
    
    public boolean accepts( IDynamicContent tag ){
        return tag instanceof StringTag;
    }

    public Object createContent(IDynamicContent content) {
        Object o = composite_map==null?simple_map.get(content.toString()): composite_map.getObject(content.toString());
        return o;
        /*
        if(o==null) 
            return null;
        if(object_formater!=null)
            return object_formater.toString(o);        
        return o.toString();
        */
    }

    /*
    public void write(Writer writer, IDynamicContent content)
            throws IOException 
    {
        String s = getContent(content);
        if(s!=null)
            writer.write(s);
    }
    */

/*    
    IObjectFormater     object_formater;
    
    public void setObjectFormater( IObjectFormater formater ) {
        this.object_formater = formater;
    }
*/
}
