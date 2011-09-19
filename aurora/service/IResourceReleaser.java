/*
 * Created on 2011-5-25 下午12:50:19
 * $Id$
 */
package aurora.service;


public interface IResourceReleaser {
    
    /** Release resource generated in service context */
    public void doRelease( ServiceContext context );

}
