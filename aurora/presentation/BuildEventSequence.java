/*
 * Created on 2007-8-17 ����06:17:33
 * $Id$
 */
package aurora.presentation;

import java.io.IOException;

import uncertain.util.StringSplitter;

public class BuildEventSequence  {
    
    String[]    events;
    String      name;
    
    /**
     * @return the name of this event sequence
     */
    public String getName() {
        return name;
    }

    /**
     * @param name set name of this event sequence, which will be associated
     * with a view name
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setEvents(String event_list){
        events = StringSplitter.splitToArray(event_list,',', false);
    }
    
    public String getEvents(){
        return StringSplitter.concatenate(events, ",");
    }

    public void buildView(BuildSession session, ViewContext view_context)
            throws IOException, ViewCreationException {
        // TODO Auto-generated method stub

    }

}
