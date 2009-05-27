/*
 * Created on 2009-5-22
 */
package aurora.presentation.markup;

import uncertain.composite.DynamicObject;

public class HtmlPage extends DynamicObject {
    
    public static final String getScriptTag( String script_name ){
        StringBuffer buf = new StringBuffer("<script src=\"");
        buf.append(script_name);
        buf.append("\"></script>");
        return buf.toString();
    }
    
    public static final String getStyleSheetTag( String css_name ){
        StringBuffer buf = new StringBuffer("<link href=\"");
        buf.append(css_name);
        buf.append("\" rel=\"stylesheet\" type=\"text/css\">");
        return buf.toString();
    }
    
    public void addScript( String script_url ){
        TagList     list = (TagList)get("html.script");
        if(list==null){
            list = new TagList();
            put("html.script", list);
        }
    }

}
