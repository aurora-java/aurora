/*
 * Created on 2010-5-13 上午11:36:33
 * $Id$
 */
package aurora.database.sql;

import java.util.Collection;

import aurora.service.validation.Parameter;

/**
 * For statement that has binding parameter 
 */
public interface IStatementWithParameter {
    
    /**
     * @return a Collection containing aurora.service.validation.Parameter
     */
    public Collection getParameters();
    
    public void setParameters( Collection params );
    
    public void addParameter( Parameter param );

}
