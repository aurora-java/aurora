/*
 * Created on 2008-1-23
 */
package aurora.database.rsconsumer;

import aurora.database.IResultSetConsumer;
import uncertain.composite.CompositeMap;
import uncertain.ocm.AbstractLocatableObject;

public class CompositeMapCreator extends AbstractLocatableObject implements IResultSetConsumer, IRootMapAcceptable {
    
    protected CompositeMap        rootMap;
    protected CompositeMap        currentRecord;
    boolean             hasRootMap = false;
    
    public CompositeMapCreator(){
        
    }
    
    public CompositeMapCreator( CompositeMap root ){
        setRoot(root);
    }

    public void endRow() {
        rootMap.addChild(currentRecord);
    }

    public void begin( String root_name ) {
        if(!hasRootMap)
            rootMap = new CompositeMap(root_name);
    }

    public void end() {

    }

    public void loadField(String name, Object value) {
        currentRecord.put(name, value);
    }

    public void newRow( String row_name ) {
        currentRecord = new CompositeMap(row_name);
    }
    
    public CompositeMap getCompositeMap(){
        return rootMap;
    }
    
    public Object getResult(){
        return getCompositeMap();
    }
    
    public void setRecordCount( long count ){
        rootMap.put("totalCount", new Long(count));
    }
    
    public void setRoot( CompositeMap root ){
        this.rootMap = root;
        this.hasRootMap = true;        
    }
    
    public CompositeMap getRoot(){
        return rootMap;
    }

}
