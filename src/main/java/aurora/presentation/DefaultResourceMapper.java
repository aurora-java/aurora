/*
 * Created on 2009-5-12
 */
package aurora.presentation;

public class DefaultResourceMapper implements IResourceUrlMapper {
    
    static final DefaultResourceMapper DEFAULT_INSTANCE = new DefaultResourceMapper();
    
    public static DefaultResourceMapper getInstance(){
        return DEFAULT_INSTANCE;
    }
    
    String mBaseName = "resource";
    
    public DefaultResourceMapper(){
        
    }
    
    public DefaultResourceMapper( String base_name ){
        mBaseName = base_name;
    }

    public String getResourceUrl(String package_name, String theme,
            String resource_path) {        
        StringBuffer buf = new StringBuffer();
        buf.append(mBaseName);
        buf.append("/");
        buf.append(package_name);
        buf.append("/");
        buf.append(theme);
        buf.append("/");
        buf.append(resource_path);
        return buf.toString();
    }

}
