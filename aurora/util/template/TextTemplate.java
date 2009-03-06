/*
 * Created on 2007-8-5
 */
package aurora.util.template;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Template based text content. Sequence of static content and dynamic content. 
 */
public class TextTemplate implements Serializable, Cloneable {
    
    LinkedList      content_list;
    
    public TextTemplate(){
        content_list = new LinkedList();
    }
    
    /*
    public IStaticContent getContent( String tag ){
        return null;
    }
    */
    
    public void addContent(IStaticContent content){
        content_list.add(content);        
    }
    
    public void addContent(String content){
        content_list.add(content);
    }
    
    public void addContent(StringBuffer content){
        content_list.add(content);
    }
    
    public void addContent(IDynamicContent content){
        content_list.add(content);
    }

    public List getContents(){
        return null;
    }
    
    /**
     * Create text output
     * @param writer
     * @param provider
     * @throws IOException
     */
    public void createOutput(Writer writer, IContentProvider provider)
        throws IOException
    {
        Iterator it = content_list.iterator();
        while(it.hasNext()){
            Object obj = it.next();
            if(obj instanceof String || obj instanceof StringBuffer){
                writer.write(obj.toString());
            }
            else if( obj instanceof IDynamicContent){
                Object o = provider.createContent((IDynamicContent)obj);
                //provider.write(writer, (IDynamicContent)obj);
                if(o!=null)
                    writer.write(o.toString());                    
            }
            else if( obj instanceof IStaticContent){
                ((IStaticContent)obj).write(writer);
            }
        }
    }

}
