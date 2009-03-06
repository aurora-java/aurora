/*
 * Created on 2008-1-24
 */
package aurora.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import uncertain.datatype.DataType;
import uncertain.datatype.DataTypeRegistry;
import aurora.bm.BusinessModel;
import aurora.bm.Field;

public class ResultSetLoader {
    
    String              rootName = "records";
    String              elementName = "record";
    DataTypeRegistry    datatypeRegistry = DataTypeRegistry.getInstance();
    byte                fieldNameCase = Character.UNASSIGNED;  
   
    
    public ResultSetLoader(){
        
    }
    
    public ResultSetLoader(DataTypeRegistry registry){
        datatypeRegistry = registry;
    }
    
    String getFieldName( String name ){
        String key = null;
        if(name!=null){
            if( fieldNameCase==Character.UPPERCASE_LETTER )
                key = name.toUpperCase();
            else if( fieldNameCase==Character.LOWERCASE_LETTER )
                key = name.toLowerCase();
            else
                key = name;            
        }
        return key;
    }
    
    void fetchRowByMetaData( ResultSet rs, ResultSetMetaData meta, IResultSetConsumer consumer)
        throws SQLException
    {
        consumer.newRow(elementName);
        for(int i=1; i<=meta.getColumnCount(); i++){
            String name = meta.getColumnName(i);
            name = getFieldName(name);
            Object value = rs.getObject(i);
            consumer.loadField(name, value);
        }  
        consumer.endRow();
    }
    
    void fetchRowByStructure( ResultSet rs, BusinessModel struct, IResultSetConsumer consumer)
        throws SQLException
    {
        consumer.newRow(elementName);
        Field[] fields = struct.getFields();
        DataType[] types = struct.getFieldTypeArray(datatypeRegistry);
        if(fields==null) throw new IllegalArgumentException("Can't get fields from model");
        for(int i=0; i<fields.length; i++){
            String name = getFieldName(fields[i].getName());
            if(name==null) throw new IllegalArgumentException("must specify name property in field config: "+fields[i].getObjectContext().toXML());
            String physical_name = fields[i].getPhysicalName();   
            DataType type = types[i];
            Object value = null;
            try{
                if(type!=null)
                    value = type.getObject(rs, rs.findColumn(physical_name));
                else
                    value = rs.getObject(physical_name);
            }catch(Exception ex){
                throw new RuntimeException("can't load value for field No. "+(i+1)+", named '"+fields[i].getName()+"'", ex);
            }
            consumer.loadField(name, value);
        }  
        consumer.endRow();
    }
    
    public void loadByResultSet( ResultSet rs, FetchDescriptor desc, IResultSetConsumer consumer)
        throws SQLException
    {
        ResultSetMetaData   meta = rs.getMetaData();
        consumer.begin(rootName);        
        if(desc.fetchAll){
            while(rs.next()){
                fetchRowByMetaData(rs, meta, consumer);
            }            
        }else{
            //System.out.println("fetch begin");
            if(!desc.locate(rs)) return;
            for(int i=0; i<desc.getPageSize(); i++){
                //System.out.println("fetch No."+i);
                fetchRowByMetaData(rs, meta, consumer);
                if(!rs.next()) break;
            }
        }
        consumer.end();
    }
    
    public void loadByConfig( ResultSet rs, FetchDescriptor desc, BusinessModel meta, IResultSetConsumer consumer)
        throws SQLException
    {
        consumer.begin(rootName);        
        if(desc.fetchAll){
            while(rs.next()){
                fetchRowByStructure(rs, meta, consumer);
            }            
        }else{
            if(!desc.locate(rs)) return;
            for(int i=0; i<desc.getPageSize(); i++){                
                fetchRowByStructure(rs, meta, consumer);                                
                if(!rs.next()) break;
            }
        }
        consumer.end();        
        
    }

}
