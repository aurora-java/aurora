/*
 * Created on 2011-7-13 下午01:41:06
 * $Id$
 */
package aurora.database.rsconsumer;

import uncertain.composite.CompositeMap;

public interface IRootMapAcceptable {
    
    public void setRoot( CompositeMap root );
    
    public CompositeMap getRoot();

}
