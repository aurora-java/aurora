/**
 * config/
 *        package.xml
 *        class-registry.xml
 *        components.xml
 * /theme/default/resource/
 * /theme/default/template/
 * /theme/theme1/resource/
 * /theme/theme1/template/
 * /theme/theme2/resource/
 * /theme/theme2/template/
 * 
 * Created on 2009-5-1
 */
package aurora.presentation;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ocm.ClassRegistry;
import uncertain.pkg.ComponentPackage;
import uncertain.pkg.PackageConfigurationError;

public class ViewComponentPackage extends ComponentPackage {
    
    public static final String ELEMENT_VIEW_COMPONENT = "view-component";
    public static final String THEME_PATH = "theme";
    public static final String TEMPLATE_PATH = "template";
    public static final String RESOURCE_PATH = "resource";
    public static final String DEFAULT_THEME = "default";
    
    public static final String FILE_COMPONENTS_CONFIG = "components.xml";
    
    public static void loadBuiltInRegistry( ClassRegistry reg ){
        reg.addClassMapping( ELEMENT_VIEW_COMPONENT, ViewComponent.class );
    }
    
    File        mBaseThemePath;
    File        mDefaultSchemePath;
    // ElementIdentifier -> Component
    Map         mComponentMap = new HashMap();
    
    public ViewComponentPackage(){
        super();
    }
    
    protected void loadComponents(){
        File component_file = new File( getConfigPath(), FILE_COMPONENTS_CONFIG);
        if(!component_file.exists())
            throw new PackageConfigurationError(FILE_COMPONENTS_CONFIG+" not exists in config directory");
        CompositeLoader loader = super.getPackageManager().getCompositeLoader();
        try{
            CompositeMap components = loader.loadByFullFilePath(component_file.getPath());
            //System.out.println(components.toXML());
            super.getPackageManager().getOCManager().populateObject(components, this);
        }catch(Exception ex){
            throw new PackageConfigurationError("Error when loading "+FILE_COMPONENTS_CONFIG, ex);
        }        
    }
    
    public void load( String base_path )
        throws IOException
    {
        super.load(base_path);
        // check theme directory
        mBaseThemePath = new File( super.mBasePathFile, THEME_PATH);
        if(!mBaseThemePath.exists())
            throw new PackageConfigurationError("package directory doesn't contains theme directory");
        mDefaultSchemePath = new File( mBaseThemePath, DEFAULT_THEME);
        if(!mDefaultSchemePath.exists())
            throw new PackageConfigurationError("package directory doesn't contains theme/default/ directory");
        // deal with components.xml
        loadComponents();
    }
    
    public ViewComponentPackage( String base_path )
        throws IOException
    {
        this();
        load( base_path );
    }
    
    /**
     * Determine whether a physical resource file exists in specified theme directory 
     * @param theme name of theme
     * @param resource_name relative path of resource file
     * @return true if resource file exists in specified theme
     */
    public boolean isResourceExist( String theme, String resource_name ){
        File theme_dir = new File( mBaseThemePath, theme);
        File resource_base_path = new File( theme_dir, RESOURCE_PATH );            
        File resource_file = new File( resource_base_path, resource_name);
        return resource_file.exists();            
    }
    
    protected File getFileByTheme( String theme, String base_path, String file_name ){
        if( theme==null ) theme = DEFAULT_THEME;
        File theme_dir = new File( mBaseThemePath, theme);
        if(theme_dir.exists()){
            File resource_base_path = new File( theme_dir, base_path);            
            File resource_file = new File( resource_base_path, file_name);
            if( resource_file.exists())
                return resource_file;
            else
                theme_dir = mDefaultSchemePath;
        }else
            theme_dir = mDefaultSchemePath;
        File resource_base_path = new File( theme_dir, base_path);
        File resource_file = new File( resource_base_path, file_name);
        if( resource_file.exists() ) return resource_file;
        else return null;
    }    
    
    /**
     * Get resource file under specified theme name
     * @param theme name of theme
     * @param file_name name of resource file
     * @return if there exists such file under specified theme, a File object responding to resource
     * will be returned; if there is not such resource file in specified theme, but the default theme
     * contains this resource, then resource in default theme will be returned instead. Otherwise 
     * return null. 
     */
    public File getResourceFile( String theme, String file_name ){
        return getFileByTheme(theme, RESOURCE_PATH, file_name );
    }
    
    public File getResourceFile( String file_name ){
        return getResourceFile(null, file_name);
    }
    
    public File getTemplateFile( String theme, String file_name ){
        return getFileByTheme(theme, TEMPLATE_PATH, file_name );
    }
    
    public File getTemplateFile( String file_name ){
        return getTemplateFile(null, file_name);
    }
    
    public void addComponents( ViewComponent[] components ){
        for(int i=0; i<components.length; i++)
            addComponent( components[i]);
    }

    public void addComponent( ViewComponent component ){
        mComponentMap.put( component.getElementIdentifier(), component);
        component.setOwner(this);      
    }
    
    public ViewComponent getComponent( CompositeMap view ){
        return (ViewComponent)mComponentMap.get(view.getQName());
    }
    
    protected Map getComponentMap(){
        return mComponentMap;
    }
    
    public Collection getAllComponents(){
        return mComponentMap.values();
    }
}
