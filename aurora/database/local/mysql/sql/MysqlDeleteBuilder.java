package aurora.database.local.mysql.sql;

import aurora.database.profile.IDatabaseProfile;
import aurora.database.sql.DeleteStatement;
import aurora.database.sql.UpdateTarget;
import aurora.database.sql.builder.DefaultDeleteBuilder;

public class MysqlDeleteBuilder extends DefaultDeleteBuilder {
	public String createSql(DeleteStatement statement) {
		UpdateTarget target = statement.getUpdateTarget();
		if (target.getAlias() == null)
			target.setAlias("t");
		StringBuffer sql = new StringBuffer();
		sql.append(getKeyword(IDatabaseProfile.KEY_DELETE)).append(" ")
				.append(target.getAlias()).append(" ")
				.append(IDatabaseProfile.KEY_FROM).append(" ");
		sql.append(mRegistry.getSql(statement.getUpdateTarget())).append(" ");
		sql.append("\r\n");
		sql.append(createWherePart(statement));
		return sql.toString();
	}
}
