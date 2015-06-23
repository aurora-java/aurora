/*
 * Created on 2008-3-28
 */
package aurora.database.sql;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ComplexExpression extends RawSqlExpression {
    
    static class FieldDefine {
        
        String  name;   
        
        SelectField   field;
        
        public FieldDefine(String name, SelectField field){
            this.name = name;
            this.field = field;
        }
        
        public String getName(){
            return name;
        }
        
        public SelectField getField(){
            return field;
        }
        
        public boolean equals(Object another){
            if(another instanceof String){
                return name.equals(another);
            }else if(another instanceof SelectField){
                return field==another;
            }else
                return super.equals(another);
        }
        
        public String getRegexString(){
            return "\\{" + name + "\\}";
        }
    }
    
    List fields;

    /**
     * @param type
     * @param expressionText
     */
    public ComplexExpression(String expressionText) {
        super(expressionText);
        fields = new LinkedList();
    }
    
    public void defineField( String name, SelectField field){
        FieldDefine fd = new FieldDefine(name, field);
        fields.add(fd);
    }
    
    public void defineField(SelectField field){
        defineField(Integer.toString(fields.size()+1), field);
    }
    
    public void removeField(String field){
        fields.remove(fields.indexOf(field));
    }
    
    public void removeField(SelectField field){
        fields.remove(fields.indexOf(field));
    }
    
    public String getTranslatedExpression(){
        String text = getExpressionText();
        Iterator it = fields.iterator();
        while(it.hasNext()){
            FieldDefine fd = (FieldDefine)it.next();
            text = text.replaceAll(fd.getRegexString(), fd.getField().getNameForOperate());
        }
        return text;
    }
    
    public String toSql(){
        return getTranslatedExpression();
    }

}
