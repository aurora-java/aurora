/*
 * Created on 2014-9-17 下午10:35:27
 * $Id$
 */
package aurora.service.http;

import uncertain.event.EventModel;

public class NoResponse {
    
    public int preCreateSuccessResponse(){
        return EventModel.HANDLE_STOP;
    }
    
    public int preCreateFailResponse(){
        return EventModel.HANDLE_STOP;
    }
    

}
