/*
 * Created on 2007-8-22
 */
package aurora.presentation.component;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.html.HTMLContent;
import aurora.util.template.Attributes;

public class TextAreaRenderer   implements ISingleton {
    
    public static final String KEY_ROWS = "rows";
    public static final String KEY_COLS = "cols";

    public void onCreateViewContent( BuildSession session, ViewContext context ){
        CompositeMap view = context.getView();
        HTMLContent content = new HTMLContent(context);
        Attributes attribs =  content.getHtmlAttributes();
        
        String rows = view.getString(KEY_ROWS);
        if(rows!=null) attribs.put(KEY_ROWS, rows);
        
        String cols = view.getString(KEY_COLS);
        if(cols!=null) attribs.put(KEY_ROWS, rows);    
    }
    

}
