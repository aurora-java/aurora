/*
 * Created on 2008-3-11
 */
package aurora.database;

import java.sql.SQLException;
import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.bm.BusinessModel;
import aurora.database.service.SqlServiceContext;

public class DatabaseService {
    
    BusinessModel                model;
    SqlServiceContext       serviceContext;
    
    public void insert( CompositeMap parameters ) throws SQLException {
        
    }
    
    public void update( CompositeMap parameters ) throws SQLException {
        
    }
    
    public void delete( CompositeMap parameters ) throws SQLException {
        
    }
    
    public List query(  CompositeMap parameters ) throws SQLException {
        return null;
    }
    
    public List query(  CompositeMap parameters, int start, int page_size ) throws SQLException {
        return null;
    }

    public CompositeMap queryForMap( CompositeMap parameters ) throws SQLException {
        return null;
    }

}
