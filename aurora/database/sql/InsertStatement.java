/*
 * Created on 2009-11-16 下午03:20:52
 * Author: Zhou Fan
 */
package aurora.database.sql;

import java.util.LinkedList;
import java.util.List;

import aurora.database.Constant;

public class InsertStatement extends AbstractCompsiteStatement {
    
    List                insertFields;
    ISqlStatement       insertTable;
    SelectStatement     selectStatement;    
    
    public InsertStatement( InsertStatement another ){
        super(Constant.TYPE_INSERT);
        copy(another);
    }
    
    public InsertStatement(  String table_name ){
        super(Constant.TYPE_INSERT);
        insertFields = new LinkedList();
        setInsertTable(new RawSqlExpression(table_name));
    }
    
    public List getInsertFields(){
        return  insertFields;
    }
    
    public void addInsertField(InsertField field){
        insertFields.add(field);
    }
    
    public InsertField addInsertField( String name, String source ){
        InsertField field = new InsertField(this, name, source);
        addInsertField(field);
        return field;
    }

    public ISqlStatement getInsertTable() {
        return insertTable;
    }

    public void setInsertTable(ISqlStatement insertTable) {
        this.insertTable = insertTable;
    }
    
    public void copy(InsertStatement another){
        insertFields = new LinkedList();
        insertFields.addAll(another.insertFields);
        insertTable = another.insertTable;
        selectStatement = another.selectStatement;
    }
    

}
