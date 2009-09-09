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
import aurora.service.validation.ErrorMessage;

/**
 * OracleExceptionDescriptor.
 * 
 * @version $Id: OracleExceptionDescriptor.java v 1.0 2009-4-28 znjqolf Exp $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
public class OracleExceptionDescriptor extends SQLExceptionDescriptor {

	private static final String SERVICE_NAME = "sys.error_message_query";

	private UncertainEngine engine;

	
	public OracleExceptionDescriptor(UncertainEngine engine) {
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
			Integer errLineId = Integer.valueOf(errMsg.substring(startIndex, endIndex));

			SqlServiceContext sqlServiceContext = SqlServiceContext.createSqlServiceContext(context.getObjectContext());
			sqlServiceContext.getParameter().put("lineId", errLineId);
            
			DatabaseServiceFactory svcFactory = (DatabaseServiceFactory) engine.getObjectRegistry().getInstanceOfType(DatabaseServiceFactory.class);
			RawSqlService sqlService = svcFactory.getSqlService(SERVICE_NAME, context);
			CompositeMap resultMap = sqlService.queryAsMap(sqlServiceContext, FetchDescriptor.getDefaultInstance());
			/*
			CompositeMapCreator cmc = new CompositeMapCreator();
			sqlServiceContext.getParameter().put("lineId", errLineId);
			sqlService.query(sqlServiceContext, cmc, FetchDescriptor.getDefaultInstance());
			CompositeMap resultMap = cmc.getCompositeMap();
			*/
			CompositeMap msg = (CompositeMap) resultMap.getChilds().get(0);
			message = (String) msg.getObject("@MESSAGE");
			error.put(ErrorMessage.KEY_MESSAGE, message);
			return error;
		} catch (Exception e) {
			return super.getParsedError(context, exception);
		}
	}
}
