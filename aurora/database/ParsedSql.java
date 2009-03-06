/*
 * Created on 2007-10-30
 */
package aurora.database;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uncertain.util.QuickTagParser;
import uncertain.util.TagParseHandle;
import aurora.service.validation.Parameter;

public class ParsedSql {
    
    public static final char DYNAMIC_SQL_INDICATOR = ':';
    
    class SQLParseHandle implements TagParseHandle {

        public  SQLParseHandle(){

        }
          
        public String ProcessTag(int index, String tag) {
          if(tag.length()==0) return "";
          BindParameter param = new BindParameter(tag);
          param.position = parsed_sql.length();
          String        replacement = null;
          if( tag.charAt(0) == DYNAMIC_SQL_INDICATOR){
              tag = tag.substring(1);
              param.input_path = tag;
              param.is_sql_statement = true;
              static_statement = false;
              replacement =  "";
          } else {                      
              replacement =  "?";
          }
          
          addParameter(param);
                    
          parsed_sql.append(replacement);
          return replacement;
        }
         
         public int ProcessCharacter( int index, char ch){
            parsed_sql.append(ch);
            return (int)ch;
         }

    };

    StringBuffer        parsed_sql;
    LinkedList          parameter_list;
    // access_path -> BindParameter
    HashMap             parameter_map;
    SQLParseHandle      handle;
    boolean             static_statement = true;
    boolean             has_output_parameter = false;
    
    public ParsedSql(){
        parsed_sql = new StringBuffer();
        handle = new SQLParseHandle();
        parameter_list = new LinkedList();
        parameter_map = new HashMap();
    }
    
    public ParsedSql(String sql_with_tag ){
        this();
        parse(sql_with_tag);
    }
    
    public void defineParameter(Parameter param){
        parameter_map.put(param.getInputPath(), param);
    }
    
    public void defineParameters(Collection param_list){
        Iterator it = param_list.iterator();
        while(it.hasNext()){
            Parameter param = (Parameter)it.next();
            defineParameter( param );
        }
    }
    
    
    void addParameter(BindParameter param){
        Parameter p = (Parameter)parameter_map.get(param.input_path);
        if(p!=null){
            param.copyFrom(p);
        }
        if(param.is_output) has_output_parameter = true;
        parameter_list.add(param);
    }
    
    public void parse( String sql ){
        parsed_sql.setLength(0);
        parameter_list.clear();
        QuickTagParser parser = new QuickTagParser();
        //AdaptiveTagParser       parser   = AdaptiveTagParser.newUnixShellParser();
        parser.parse(sql, handle );
        parser.clear();
        
    }
    
    public List getBindParameters(){
        return parameter_list;
    }
    
    public String getParsedSQL(){
        return parsed_sql.toString();
    }

    /**
     * @return the static_statement
     */
    public boolean isStaticStatement() {
        return static_statement;
    }
    
    public boolean hasOutputParameter(){
        return has_output_parameter;
    }

    /**
     * @param static_statement the static_statement to set
     */
    public void setStaticStatement(boolean static_statement) {
        this.static_statement = static_statement;
    }

}
