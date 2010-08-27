/*
 * Created on 2008-4-2
 */
package aurora.database.sql.builder;

import java.util.Iterator;
import java.util.List;

import aurora.database.profile.DatabaseProfile;
import aurora.database.profile.IDatabaseProfile;
import aurora.database.sql.ConditionList;
import aurora.database.sql.IAliasSettable;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.Join;
import aurora.database.sql.OrderByField;
import aurora.database.sql.SelectField;
import aurora.database.sql.SelectSource;
import aurora.database.sql.SelectStatement;
import aurora.database.sql.StringConcatenater;

public class DefaultSelectBuilder extends AbstractSqlBuilder {

    public static final String EMPTY_WHERE = "";

    /*
    boolean useJoinKeyword = true;

    public boolean useJoinKeyword() {
        return useJoinKeyword;
    }
    */
    

    public boolean isUseJoinKeyword(){
        return DatabaseProfile.isUseJoinKeyword( getDatabaseProfile() );
    }

    public String createSql(ISqlStatement sqlStatement) {
        if (sqlStatement instanceof SelectStatement) {
            return createSql((SelectStatement) sqlStatement);
        } else if (sqlStatement instanceof SelectSource)
            return createSql((SelectSource) sqlStatement);
        else if (sqlStatement instanceof SelectField)
            return createSql((SelectField) sqlStatement);
        else if (sqlStatement instanceof OrderByField)
            return ((OrderByField) sqlStatement).toSql(mRegistry);
        else
            return null;
    }

    public String createSql(SelectField field) {
        return field.getNameForOperate();
    }

    public String getFromPart(SelectStatement stmt) {
        boolean useJoinKeyword = isUseJoinKeyword();
        StringBuffer result = new StringBuffer();
        result.append(getKeyword(IDatabaseProfile.KEYWORD_FROM)).append(" ");
        boolean is_use_join = false;
        List lst = stmt.getJoins();
        if (lst != null)
            if (lst.size() > 0)
                is_use_join = true;
        is_use_join = is_use_join & useJoinKeyword;
        if (is_use_join) {
            int id=0;
            Iterator it = lst.iterator();
            while(it.hasNext()){
                Join join = (Join)it.next();
                join.setOrder(id++);
                result.append(mRegistry.getSql(join));
            }
        } else {
            StringConcatenater list = new StringConcatenater();
            Iterator it = stmt.getFromListForRead().iterator();
            while (it.hasNext()) {
                SelectSource source = (SelectSource) it.next();
                list.append(mRegistry.getSql(source));
            }
            result.append(list.getContent());
        }
        return result.toString();
    }

    public String getWherePart(SelectStatement stmt) {
        StringBuffer buf = new StringBuffer();
        ConditionList where = stmt.getWhereClause();
        if (isUseJoinKeyword()) {
            if (where.size() == 0)
                return EMPTY_WHERE;
            else {
                buf.append(getKeyword(IDatabaseProfile.KEYWORD_WHERE)).append(
                        " ");
                buf.append(mRegistry.getSql(where));
                return buf.toString();
            }
        } else {
            ConditionList join_conditions = stmt.getJoinConditions();
            if (join_conditions.size() == 0 && where.size() == 0)
                return EMPTY_WHERE;
            else {
                ConditionList all = new ConditionList();
                if (where.size() > 0)
                    all.addCondition(where);
                if (join_conditions.size() > 0)
                    all.addCondition(join_conditions);
                buf.append(getKeyword(IDatabaseProfile.KEYWORD_WHERE)).append(
                        " ");
                buf.append(mRegistry.getSql(all));
                return buf.toString();
            }
        }
    }

    public String getSelectFields(SelectStatement stmt) {
        StringBuffer result = new StringBuffer();
        result.append(getKeyword(IDatabaseProfile.KEYWORD_SELECT)).append(" ");
        // select fields
        Iterator it = stmt.getFieldsForRead().iterator();
        StringConcatenater flds = new StringConcatenater();
        while (it.hasNext()) {
            ISqlStatement field = (ISqlStatement) it.next();
            String field_name = mRegistry.getSql(field);
            if (field instanceof IAliasSettable) {
                String alias = ((IAliasSettable) field).getAlias();
                if (alias != null)
                    field_name = field_name + " "
                            + getKeyword(IDatabaseProfile.KEYWORD_AS) + " "
                            + alias;
            }
            flds.append(field_name);
        }
        result.append(flds.getContent());
        return result.toString();
    }

    public String createSql(SelectSource source) {
        StringBuffer result = new StringBuffer();
        if (source.isSubQuery()) {
            result.append("( ");
            result.append(createSql(source.getSubQuery()));
            result.append(") ");
        } else {
            result.append(source.getTableName());
        }
        if (source.getAlias() != null) {
            result.append(" ");
            result.append(source.getAlias());
        }
        return result.toString();
    }

    public String createOrderByPart(SelectStatement statement) {
        StringConcatenater list = new StringConcatenater();
        List order_by = statement.getOrderByFields();
        if (order_by != null) {
            Iterator it = order_by.iterator();
            while (it.hasNext()) {
                OrderByField field = (OrderByField) it.next();
                list.append(field.toSql(mRegistry));
            }
            String content = list.getContent();
            if (content.length() == 0)
                return null;
            return getKeyword(IDatabaseProfile.KEYWORD_ORDER_BY) + " "
                    + content;
        } else
            return null;
    }

    public String createSql(SelectStatement stmt) {
        StringBuffer result = new StringBuffer();
        stmt.createDefaultAlias("t");
        // select fields
        result.append(getSelectFields(stmt)).append("\r\n");
        // from
        result.append(getFromPart(stmt)).append("\r\n");
        // where
        result.append(getWherePart(stmt));
        // order by
        String order_by = createOrderByPart(stmt);
        if (order_by != null)
            result.append("\r\n").append(order_by);
        return result.toString();
    }

}
