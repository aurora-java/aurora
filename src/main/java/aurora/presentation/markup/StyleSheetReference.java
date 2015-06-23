/*
 * Created on 2009-7-21
 */
package aurora.presentation.markup;

public class StyleSheetReference {
    
    /**
     * @param css_url
     */
    public StyleSheetReference(String css_url) {
        super();
        this.css_url = css_url;
    }
    
    String css_url;
    
    public String toString(){
        StringBuffer buf = new StringBuffer("<link href=\"");
        buf.append(css_url);
        buf.append("\" rel=\"stylesheet\" type=\"text/css\"/>");
        return buf.toString();        
    }

}
