/*
 * Project: Aurora
 * 
 * Copyright(c) 2009 www.hand-china.com
 * All rights reserved.
 */
package aurora.database;

import java.util.HashMap;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.service.ServiceContext;
import aurora.service.validation.ErrorMessage;

/**
 * DataBaseExceptionDescriptor
 * 
 * @version $Id$
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
public class DataBaseExceptionDescriptor extends SQLExceptionDescriptor {

	private static final String SERVICE_NAME = "sys.sys_error_message";

	private UncertainEngine engine;

	
	public DataBaseExceptionDescriptor(UncertainEngine engine) {
		this.engine = engine;
	}

	
	public CompositeMap process(ServiceContext context, Throwable exception) {
		return parseErrorMessage(context, exception);
	}

	
	private CompositeMap parseErrorMessage(ServiceContext context, Throwable exception) {
	    // Added by Zhoufan 2012-12-21
	    // If error part is already set in service context, then previous component may  have already 
	    // translated SQL exception message
	    CompositeMap err = context.getObjectContext().getChild(ServiceContext.KEY_ERROR);
	    if(err!=null){
	        if(err.size()==0) err = null;
	    }
	    if(err!=null)
	        return null;
	    // end modify
		CompositeMap error = new CompositeMap(ErrorMessage.ERROR_MESSAGE);
		String message = null,code = null;
		try {
			String errMsg = exception.getMessage();
			int endIndex = errMsg.indexOf("\n");
			int startIndex = errMsg.indexOf(": ") + 2;
			Integer errLineId = new Integer(-1);
			try{
				errLineId = Integer.valueOf(errMsg.substring(startIndex, endIndex));
			}catch(Exception e){}
			
			if(errLineId.intValue()!=-1){
//				SqlServiceContext sqlServiceContext = SqlServiceContext.createSqlServiceContext(context.getObjectContext());
//				sqlServiceContext.getParameter().put("lineId", errLineId);
				Map map  = new HashMap();
				map.put("lineId", errLineId);
	            
				DatabaseServiceFactory svcFactory = (DatabaseServiceFactory) engine.getObjectRegistry().getInstanceOfType(DatabaseServiceFactory.class);
				BusinessModelService sqlService = svcFactory.getModelService(SERVICE_NAME,context.getObjectContext());//(name, context_map)getSqlService
				CompositeMap resultMap = sqlService.queryAsMap(map, FetchDescriptor.getDefaultInstance());
	
				CompositeMap msg = (CompositeMap) resultMap.getChilds().get(0);
				message = (String) msg.getObject("@MESSAGE");
				code = (String) msg.getObject("@MESSAGE_CODE");
				error.put(ErrorMessage.KEY_MESSAGE, message);
				error.put(ErrorMessage.KEY_CODE, code);
			}else{
				error.put(ErrorMessage.KEY_MESSAGE, errMsg);				
			}
			return error;
		} catch (Exception e) {
			e.printStackTrace();
			return super.getParsedError(context, exception);
		}
	}
}
