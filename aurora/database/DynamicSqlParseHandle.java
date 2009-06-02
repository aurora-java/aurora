/*
 * Created on 2009-6-2
 */
package aurora.database;

import uncertain.composite.CompositeMap;
import uncertain.util.QuickTagParser;
import uncertain.util.TagParseHandle;

public class DynamicSqlParseHandle implements TagParseHandle {
    
    boolean         mContainsDynamicSql = false;
    CompositeMap    mParameter;

    public  DynamicSqlParseHandle( CompositeMap param ){
        this.mParameter = param;
    }
      
    public String ProcessTag(int index, String tag) {
      if(tag.length()==0) return "";
      String str = null;
      if( tag.charAt(0) == ParsedSql.DYNAMIC_SQL_INDICATOR){
          tag = tag.substring(1);
          Object obj = mParameter.getObject(tag);
          if( obj==null )
              str = "";
          else
              str = obj.toString();
      } else {                      
          str = "${" + tag + "}";
      }
      if(str.indexOf("${:")>=0)
          mContainsDynamicSql = true;
     return str;
    }
     
     public int ProcessCharacter( int index, char ch){        
        return (int)ch;
     }
     
     public boolean containsDynamicSql(){
         return mContainsDynamicSql;
     }
     
     public static String processSql( String sql, CompositeMap param ){
         DynamicSqlParseHandle handle = new DynamicSqlParseHandle(param);
         QuickTagParser parser = new QuickTagParser();
         String result = parser.parse(sql , handle);
         if( handle.containsDynamicSql() )
             result = processSql( result, param );
         return result;
     }

};