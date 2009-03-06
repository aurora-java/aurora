/*
 * Created on 2007-8-23
 */
package aurora.util.template;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class CompositeContentProvider implements IContentProvider {
    
    LinkedList  provider_list;
    //HashMap     type_map;
    
    public CompositeContentProvider(){
       provider_list = new LinkedList();
       //type_map = new HashMap();
    }
    
    public void register(  IContentProvider provider ){
        provider_list.add(provider);
        //type_map.put(content_type, provider);
    }
    
    public boolean accepts(IDynamicContent content){
        Iterator it = provider_list.iterator();
        while(it.hasNext()){
            IContentProvider provider = (IContentProvider)it.next();
            if(provider.accepts(content))
                return true;
        }
        return false;
    }

    public Object createContent(IDynamicContent content) {
       Iterator it = provider_list.iterator();
       IContentProvider provider = null;
       while(it.hasNext()){
           IContentProvider p = (IContentProvider)it.next();
           if(p.accepts(content)){
               provider = p;
               break;
           }               
       }
       return provider == null? null : provider.createContent(content);
    }

}
