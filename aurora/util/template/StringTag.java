/*
 * Created on 2007-8-5 обнГ05:17:04
 */
package aurora.util.template;

/**
 * A wrapper to hold string tag
 * @author Zhou Fan
 *
 */
public class StringTag implements IDynamicContent {
    
    String  tag;
    
    public StringTag(String tag){
        this.tag = tag;
    }
    
    public StringTag(StringBuffer tag_buf){
        this.tag = tag_buf.toString();
    }
    
    public String toString(){
        return tag;
    }

}
