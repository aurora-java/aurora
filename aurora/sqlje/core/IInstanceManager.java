/*
 * Created on 2014-7-30 下午4:06:13
 * $Id$
 */
package aurora.sqlje.core;

public interface IInstanceManager {
    
    public ISqlCallEnabled createInstance( Class clazz );
    
    public ISqlCallEnabled createInstance( Class clazz, ISqlCallEnabled caller );

}
