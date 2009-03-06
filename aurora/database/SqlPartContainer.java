/*
 * Created on 2008-6-30
 */
package aurora.database;

import java.util.HashMap;
import java.util.Map;

import uncertain.util.StringSplitHandle;
import uncertain.util.TagParseHandle;

public class SqlPartContainer implements TagParseHandle {
    
    Map     mPartMap;
    
    public SqlPartContainer(){
        
    }
    
    public String ProcessTag(int index,String tag){
        return "";
    }

    public int ProcessCharacter( int index, char ch){
        return ch;            
    }    


}
