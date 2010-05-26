/*
 * Created on 2008-3-28
 */
package aurora.database.sql;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import aurora.database.DatabaseConstant;

public class SelectStatement extends AbstractStatementWithWhere {
    // List<SelectSource>
    List                selectSourceList;
    // table name -> SelectSource
    Map                 selectSourceMap;
    // List<SelectField>
    List                selectFieldList;
    // List<Join>
    List                joinList;

    public SelectStatement(){
        super(DatabaseConstant.TYPE_SELECT);
        selectSourceList    = getPartsNotNull(DatabaseConstant.PART_FROM);
        selectFieldList   = getPartsNotNull(DatabaseConstant.PART_FIELDS);
        joinList    = getPartsNotNull(DatabaseConstant.PART_JOINS);
        selectSourceMap = new HashMap();
    }
    
    public List getFromListForRead(){
        return Collections.unmodifiableList(selectSourceList);
    }
    
    public List getFieldsForRead(){
        return Collections.unmodifiableList(selectFieldList);
    }
    
    public void addSelectSource( SelectSource part ){
        part.setParent(this);
        selectSourceList.add(part);
        selectSourceMap.put(part.getTableName(), part);        
    }
    
    public SelectSource addSelectSource( String tableName){
        SelectSource part = new SelectSource(tableName);
        addSelectSource(part);
        return part;
    }
    
    public SelectSource addSelectSource( SelectStatement subQuery ){
        SelectSource part = new SelectSource(subQuery);
        addSelectSource(part);
        return part;        
    }
    
    public SelectSource getSelectFrom(String tableName){
        return (SelectSource)selectSourceMap.get(tableName);
    }
    
    void checkField( SelectField field ){
        if(field.isSubQuery())
            field.setParent(this);
    }
    
    public void addSelectField( SelectField field ){
        selectFieldList.add(field);
        checkField(field);
    }
    
    public void addSelectField( int index, SelectField field){
        selectFieldList.add(index, field);
        checkField(field);
    }
    
    public void addSelectFieldFirst(SelectField field){
        selectFieldList.add(0,field);
        checkField(field);
    }
    
    public int getSelectFieldIndex(SelectField field){
        return selectFieldList.indexOf(field);
    }
    /*
    public int getFieldIndex(String field_name){
        return 0;
    }
    */
    public SelectField getField( String field_name){
        Iterator it = selectFieldList.iterator();
        while(it.hasNext()){
            Object fld = it.next();
            if(fld instanceof SelectField)
                if( ((SelectField)fld).getFieldName().equals(field_name))
                    return (SelectField)fld;            
        }
        return null;
    }

    public void removeField( SelectField field ){
        selectFieldList.remove(field);
    }
    
    public boolean containsTable( String tableName){
        return selectSourceMap.containsKey(tableName);
    }
    
    public void addJoin( Join join ){
        if(join.getLeftPart().getParent()!=this)
            addSelectSource(join.getLeftPart());
        if(join.getRightPart().getParent()!=this)
            addSelectSource(join.getRightPart());
        join.setParent(this);
        joinList.add(join);
        
    }
    
    public Join createJoin( String type, SelectSource s1, SelectSource s2 ){
        if(!selectSourceList.contains(s1))
            addSelectSource(s1);
        if(!selectSourceList.contains(s2))
            addSelectSource(s2);
        Join join = new Join(type, s1, s2);
        addJoin(join);
        return join;        
    }
    
    public Join createJoin( String type, String table1, String table2 ){
        SelectSource s1 = getSelectFrom(table1);
        if(s1==null) s1 = addSelectSource(table1);
        SelectSource s2 = getSelectFrom(table2);
        if(s2==null) s2 = addSelectSource(table2);
        Join join = new Join(type, s1, s2);
        addJoin(join);
        return join;
    }
    
    public List getJoins(){
        return this.joinList;
    }
    
    public void addGroupByField(SelectField field){
        getGroupByFields().add(field);
    }
    
    public void addGroupByField(int index, SelectField field){
        getGroupByFields().add(index, field);        
    }
    
    public List getGroupByFields(){
        return super.getPartsNotNull(DatabaseConstant.PART_GROUP_BY);
    }
    
    public void addOrderByField( ISqlStatement field, String order){
        OrderByField    f = new OrderByField(field, order);
        f.setParent(this);
        getOrderByFields().add(f);
    }
    
    public void addOrderByField( ISqlStatement field ){
        addOrderByField(field,null);
    }
    
    public void addOrderByField( OrderByField field ){
        getOrderByFields().add(field);
    }
    
    public List getOrderByFields(){
        return super.getPartsNotNull(DatabaseConstant.PART_ORDER_BY);
    }
    
    public void createDefaultAlias( String prefix ){
        int id=1;
        Iterator it = selectSourceList.iterator();
        while(it.hasNext()){
            SelectSource source = (SelectSource)it.next();
            if(source.getAlias()!=null) continue;
            String alias = prefix + id;
            source.setAlias(alias);
            id++;
        }
    }
    
    public ConditionList getJoinConditions(){        
        ConditionList list = new ConditionList();
        Iterator it = joinList.iterator();
        while(it.hasNext()){
            Join join = (Join)it.next();
            list.addCondition(join.getJoinConditions());
        }
        return list;
    }


}
