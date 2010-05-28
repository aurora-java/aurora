/*
 * Project: Aurora
 * 
 * Copyright(c) 2009 www.hand-china.com
 * All rights reserved.
 */
package aurora.database;

import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.RawSqlService;
import aurora.database.service.SqlServiceContext;
import aurora.service.ServiceContext;
import aurora.service.exception.BaseExceptionDescriptor;
import aurora.service.validation.ErrorMessage;

/**
 * DataBaseExceptionDescriptor
 * 
 * @version $Id$
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
public class DataBaseExceptionDescriptor extends SQLExceptionDescriptor {

	private static final String SERVICE_NAME = "sys.error_message";

	private UncertainEngine engine;

	
	public DataBaseExceptionDescriptor(UncertainEngine engine) {
		this.engine = engine;
	}

	
	public CompositeMap process(ServiceContext context, Throwable exception) {
		return parseErrorMessage(context, exception);
	}

	
	private CompositeMap parseErrorMessage(ServiceContext context, Throwable exception) {
		CompositeMap error = new CompositeMap(ErrorMessage.ERROR_MESSAGE);
		String message = null;
		try {
			String errMsg = exception.getMessage();
			int endIndex = errMsg.indexOf("\n");
			int startIndex = errMsg.indexOf(": ") + 2;
			Integer errLineId = new Integer(-1);
			try{
				errLineId = Integer.valueOf(errMsg.substring(startIndex, endIndex));
			}catch(Exception e){}
			
			if(errLineId.intValue()!=-1){
				SqlServiceContext sqlServiceContext = SqlServiceContext.createSqlServiceContext(context.getObjectContext());
				sqlServiceContext.getParameter().put("lineId", errLineId);
	            
				DatabaseServiceFactory svcFactory = (DatabaseServiceFactory) engine.getObjectRegistry().getInstanceOfType(DatabaseServiceFactory.class);
				RawSqlService sqlService = svcFactory.getSqlService(SERVICE_NAME, context);
				CompositeMap resultMap = sqlService.queryAsMap(sqlServiceContext, FetchDescriptor.getDefaultInstance());
	
				CompositeMap msg = (CompositeMap) resultMap.getChilds().get(0);
				message = (String) msg.getObject("@MESSAGE");
				error.put(ErrorMessage.KEY_MESSAGE, message);
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
