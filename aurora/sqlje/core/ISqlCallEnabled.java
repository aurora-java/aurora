/*
 * Created on 2014-7-30 下午4:07:23
 * $Id$
 */
package aurora.sqlje.core;

public interface ISqlCallEnabled {
    
    public IInstanceManager _$getInstanceManager();
    
    public void _$setInstanceManager( IInstanceManager inst_mgr);
    
    public ISqlCallStack _$getSqlCallStack();
    
    public void _$setSqlCallStack( ISqlCallStack stack );

}
