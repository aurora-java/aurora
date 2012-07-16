package aurora.application.task;

import uncertain.composite.CompositeMap;
import uncertain.event.EventModel;
import aurora.service.ServiceContext;

public class TaskServiceInterpret {

	public final String KEY_GENERATE_STATE = "_generate_state_task";
	
	public int preCreateSuccessResponse(ServiceContext context)
			throws Exception {
		CompositeMap parameter = context.getParameter();
		if (!parameter.getBoolean(KEY_GENERATE_STATE, false))
			return EventModel.HANDLE_NORMAL;
		return EventModel.HANDLE_STOP;
	}
}
