/*
 * Created on 2007-8-4
 */
package aurora.presentation;

import java.io.File;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;

import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.FeatureAttach;
import uncertain.ocm.OCManager;
import uncertain.proc.ParticipantRegistry;
import aurora.util.template.ITemplateFactory;

/**
 * Manage all aspects of aurora presentation framework
 * @author Zhou Fan
 */
public class PresentationManager {
    
    static final TemplateBasedView TEMPLATE_BASED_VIEW = new TemplateBasedView();
    
    OCManager               ocManager;
    ParticipantRegistry     registry;
    ITemplateFactory        template_factory;
    UncertainEngine         uncertainEngine;
    
    // ElementID -> Component
    HashMap                 component_id_map = new HashMap();
    // name -> ComponentPackage
    HashMap                 package_name_map = new HashMap();
    
    // mappable properties
    String                  resource_url;
    
    public PresentationManager(){
        ocManager = OCManager.getInstance();
        registry = ParticipantRegistry.defaultInstance();
    }

    public PresentationManager( OCManager manager ){
        this.ocManager = manager;
        registry = ParticipantRegistry.defaultInstance();
    }
    
    public PresentationManager( UncertainEngine engine){
        this.uncertainEngine = engine;
        this.ocManager = engine.getOcManager();
        this.registry = engine.getParticipantRegistry();
    }
    
    /**
     * @return the template_factory
     */
    public ITemplateFactory getTemplateFactory() {
        return template_factory;
    }

    /**
     * @param template_factory the template_factory to set
     */
    public void setTemplateFactory(ITemplateFactory template_factory) {
        this.template_factory = template_factory;
    }

    
    public BuildSession createSession( Writer writer ){
        BuildSession session = new BuildSession(this );
        session.setWriter(writer);
        return session;
    }
    
    public Configuration createConfiguration(){
        if(uncertainEngine==null)
            return new Configuration(registry, ocManager);
        else
            return uncertainEngine.createConfig();
    }
    
    protected Component getComponent( CompositeMap view ){
        return (Component)component_id_map.get(view.getIdentifier());
    }
    
    /**
     * Get IViewBuilder instance associated with view config, to perform
     * actual building.
     * @param view_config
     * @return
     */
    public IViewBuilder getViewBuilder( CompositeMap view_config ){
        Component component = getComponent(view_config);
        if(component==null){
            return new TemplateBasedView();
        }
        else{
            Class type = component.getBuilder();
            if(type==null) return null;
            try{
                return (IViewBuilder)ocManager.getObjectCreator().createInstance(type);
            } catch(Exception ex){
                throw new RuntimeException("can't create instance of "+type.getName()+" when getting IViewBuilder from view config");
            }
        }
    }
    
    public ComponentPackage getPackage( CompositeMap view ){
        Component component = getComponent(view);
        if(component==null) return null;
        return component.getOwner();
    }
    
    public ComponentPackage getPackage( String name){
        return (ComponentPackage)package_name_map.get(name);
    }
    
    public void addPackage( ComponentPackage p ){
        component_id_map.putAll(p.component_id_map);
        package_name_map.put(p.getName(), p);
        
        // Add all attached features
        ClassRegistry cr = ocManager.getClassRegistry();
        Iterator it = p.getComponents().iterator();
        while(it.hasNext()){
            Component c = (Component)it.next();
            Class[] types = c.getFeatureClassArray();
            for(int i=0; i<types.length; i++){
                FeatureAttach f = new FeatureAttach(c.getNameSpace(), c.getName(), types[i].getName());
                try{
                    cr.addFeatureAttach(f);
                }catch(ClassNotFoundException ex){
                    throw new RuntimeException(ex);
                }
            }                
        }
    }
    
    public ComponentPackage loadPackage( File base_path, CompositeMap config ){
        ComponentPackage pkg = new ComponentPackage();
        //pkg.base_path = base_path;        
        ocManager.populateObject(config, pkg);
        addPackage(pkg);
        return pkg;
    }

    /**
     * @return the resource_url
     */
    public String getResourceURL() {
        return resource_url;
    }

    /**
     * @param resource_url the resource_url to set
     */
    public void getResourceURL(String resource_url) {
        this.resource_url = resource_url;
    }


}
