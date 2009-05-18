/*
 * Created on 2007-8-22
 */
package aurora.presentation.features;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ocm.ISingleton;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.markup.HTMLContent;

/**
 * 
 * onclick="alert()";
 * @author Zhou Fan
 *
 */
public class EventAware implements ISingleton {
    
    Properties events;
    
    public EventAware() 
        throws IOException
    {
        events = new Properties();
        InputStream is = EventAware.class.getClassLoader().getResourceAsStream("aurora/resource/script/event.config");
        events.load(is);
        is.close();
    }
    
    public void onCreateViewContent( BuildSession session, ViewContext context ){
        CompositeMap view = context.getView();
        HTMLContent  content = new HTMLContent(context);
        
        Iterator it = view.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry)it.next();
            if( entry.getKey()!=null && entry.getValue()!=null && events.containsKey(entry.getKey())){
                String scripts = entry.getValue().toString();
                scripts = TextParser.parse(scripts, context.getModel());
                content.addEventHandle(entry.getKey().toString(), scripts);
            }
        }
        
    }

}
