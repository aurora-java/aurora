/*
 * Created on 2008-1-23
 */
package aurora.database.rsconsumer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.AbstractLocatableObject;
import aurora.database.IResultSetConsumer;

public class CompositeMapCreator extends AbstractLocatableObject implements IResultSetConsumer, IRootMapAcceptable {
    
    protected CompositeMap        rootMap;
    protected CompositeMap        currentRecord;
    boolean             hasRootMap = false;
    String prefix;
    String nameSpace;
    String localName;
    boolean attribAsCdata = false;
    boolean fetchOneRecord = false;
    String attribAsCdataList;
    

    AttribMapping[] attribMappings;
    Map attributeMappings = new HashMap();
    List attribAsCdatas;
    public CompositeMapCreator(){

    }
    
    public CompositeMapCreator( CompositeMap root ){
        setRoot(root);
    }

    public void endRow() {
        if(!fetchOneRecord){
            rootMap.addChild(currentRecord);
        }
    }

    public void begin( String root_name ) {
        if(!hasRootMap||rootMap==null)
            rootMap = new CompositeMap(root_name);
    }

    public void end() {

    }

    public void loadField(String name, Object value) {
        String newName = (String)attributeMappings.get(name);
        if(newName == null)
            newName = name;
        currentRecord.put(newName, value);
        if(attribAsCdata&&attribAsCdatas!=null){
            if(attribAsCdatas.contains(name)){
                String orginal = currentRecord.getText();
                currentRecord.setText(orginal!=null?orginal:""+value);
            }
        }
    }

    public void newRow( String row_name ) {
        if(fetchOneRecord){
            currentRecord = rootMap;
            return;
        }
        currentRecord = new CompositeMap(row_name);
        if(nameSpace != null)
            currentRecord.setNameSpace(prefix, nameSpace);
        if(localName!=null)
            currentRecord.setName(localName);
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
    public AttribMapping[] getAttribMappings() {
        return attribMappings;
    }

    public void setAttribMappings(AttribMapping[] attribMappings) {
        this.attribMappings = attribMappings;
        if(attribMappings != null){
            for(int i= 0;i<attribMappings.length;i++){
                AttribMapping map = attribMappings[i];
                attributeMappings.put(map.from, map.to);
            }
        }
    }
    
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public boolean getAttribAsCdata() {
        return attribAsCdata;
    }

    public void setAttribAsCdata(boolean attribAsCdata) {
        this.attribAsCdata = attribAsCdata;
    }

    public String getAttribAsCdataList() {
        return attribAsCdataList;
    }

    public void setAttribAsCdataList(String attribAsCdataList) {
        this.attribAsCdataList = attribAsCdataList;
        attribAsCdatas = new LinkedList();
        if(attribAsCdata&&attribAsCdataList!= null){
            String[] atts = attribAsCdataList.split(",");
            if(atts != null){
                for(int i=0;i<atts.length;i++){
                    attribAsCdatas.add(atts[i]);
                }
            }
        }
    }

    public boolean getFetchOneRecord() {
        return fetchOneRecord;
    }

    public void setFetchOneRecord(boolean fetchOnRecord) {
        this.fetchOneRecord = fetchOnRecord;
    }
}
