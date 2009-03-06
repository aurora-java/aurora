/*
 * Created on 2007-8-16 ÏÂÎç03:18:22
 */
package aurora.presentation.component;

import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.html.HTMLContent;

public class TextEditRenderer   implements ISingleton {
    
    public static final String KEY_HTML_TEXTEDIT_SIZE = "html.textedit.size";
    public static final String KEY_UI_INPUT_TEXTEDIT = "ui.input.textedit";

    public void onCreateViewContent( BuildSession session, ViewContext context ){
        CompositeMap view = context.getView();
        Map map = context.getMap();
        HTMLContent content = new HTMLContent(context);

        content.setHtmlClass(KEY_UI_INPUT_TEXTEDIT);
        
        String size = view.getString("size");
        if(size==null) size="20";        
        map.put(KEY_HTML_TEXTEDIT_SIZE, size);
        
        String type = view.getString("type", "text");
        map.put("html.textedit.type", type);
        
        
    }

}
