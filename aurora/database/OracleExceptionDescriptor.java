/*
 * Project: Aurora
 * 
 * Copyright(c) 2009 www.hand-china.com
 * All rights reserved.
 */
package aurora.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.RawSqlService;
import aurora.database.service.SqlServiceContext;
import aurora.service.ServiceContext;
import aurora.service.validation.ErrorMessage;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;

/**
 * OracleExceptionDescriptor.
 * 
 * @version $Id: OracleExceptionDescriptor.java v 1.0 2009-4-28 04:52:25
 *          znjqolf Exp $
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
		PreparedStatement pst = null;
		ResultSet rs = null;
		String message = null;
		Connection conn = null;
		try {
			String errMsg = exception.getMessage();
			int endIndex = errMsg.indexOf("\n");
			int startIndex = errMsg.indexOf(": ") + 2;
			Integer errLineId = new Integer(Integer.parseInt(errMsg.substring(startIndex, endIndex)));
			conn = getConnection(context);

			SqlServiceContext sqlServiceContext = SqlServiceContext.createSqlServiceContext(context.getObjectContext());
			DatabaseServiceFactory svcFactory = (DatabaseServiceFactory) engine.getObjectSpace().getInstanceOfType(DatabaseServiceFactory.class);
			RawSqlService sqlService = svcFactory.getSqlService(SERVICE_NAME, context);
			CompositeMapCreator cmc = new CompositeMapCreator();
			sqlServiceContext.getParameter().put("lineId", errLineId);
			sqlService.query(sqlServiceContext, cmc, FetchDescriptor.getDefaultInstance());
			CompositeMap resultMap = cmc.getCompositeMap();
			CompositeMap msg = (CompositeMap) resultMap.getChilds().get(0);
			message = (String) msg.getObject("@MESSAGE");
			error.put(ErrorMessage.KEY_MESSAGE, message);
			return error;
		} catch (Exception e) {
			return super.getParsedError(context, exception);
		} finally {
			try {
				DBUtil.closeConnection(conn);
				DBUtil.closeResultSet(rs);
				DBUtil.closeStatement(pst);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private Connection getConnection(ServiceContext context) {
		return (Connection) context.getInstanceOfType(Connection.class);
	}
}
