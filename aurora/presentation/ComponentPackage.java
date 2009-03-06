/*
 * Created on 2007-9-12
 */
package aurora.presentation;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import uncertain.composite.CompositeMap;

public class ComponentPackage {
    
    String      resource_base_url;
    File        resource_base_path;
    File        template_base_path;
    String      name;
    String      template_path;
    
    // ElementIdentifier -> Component
    HashMap     component_id_map;
    
    // Component name -> Component
    HashMap     component_name_map; 
    
    public ComponentPackage(){
        component_id_map = new HashMap();
    }
    
    public String   getResourceURL(String theme, String resource_name ){
        if( theme==null)
            return resource_base_url+'/'+resource_name;
        else
            return resource_base_url + '/' + theme + '/' + resource_name;
    }
    
    
    public void setComponents( Collection clist ){
        Iterator it = clist.iterator();
        while(it.hasNext()){
            Component c = (Component)it.next();
            addComponent(c);
        }
    }
    
    public Collection getComponents(){
        return component_id_map.values();
    }
    
    public void addComponent( Component component ){
        component_id_map.put( component.getElementIdentifier(), component);
        component.setOwner(this);      
    }
    
    public Component getComponent( CompositeMap view ){
        return (Component)component_id_map.get(view.getIdentifier());
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    public File getTemplateFile( String theme, String template_name ){
        return null;
    }

}
