/*
 * Created on 2011-7-6 下午05:09:58
 * $Id$
 */
package aurora.bm;

import uncertain.composite.CompositeMap;
import uncertain.exception.ConfigurationFileException;

public class BmBuiltinExceptionFactory {
    
    public static ConfigurationFileException createNamedFieldNotFound( String ref_field_name, CompositeMap source_config ){
        return new ConfigurationFileException("aurora.bm.named_field_not_found", new Object[]{ref_field_name}, null, source_config );
    }
    
    public static ConfigurationFileException createParentBMLoadException(String parent_name, CompositeMap config, Throwable cause ){
        return  new ConfigurationFileException("aurora.bm.error_loading_parent_bm", new Object[]{parent_name}, cause, config);
    }
    
    public static ConfigurationFileException createUnknownDatabaseType(String database_type_name, CompositeMap config ){
        return  new ConfigurationFileException("aurora.database.unknown_database_type", new Object[]{database_type_name}, null, config);
    }

}
