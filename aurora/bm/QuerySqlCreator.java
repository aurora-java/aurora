/*
 * Created on 2008-5-9
 */
package aurora.bm;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;

import uncertain.core.ConfigurationError;
import aurora.database.DBUtil;
import aurora.database.FetchDescriptor;
import aurora.database.IResultSetConsumer;
import aurora.database.ParsedSql;
import aurora.database.ResultSetLoader;
import aurora.database.SqlRunner;
import aurora.database.profile.IDatabaseFactory;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.service.ServiceOption;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.Join;
import aurora.database.sql.SelectField;
import aurora.database.sql.SelectSource;
import aurora.database.sql.SelectStatement;

public class QuerySqlCreator extends AbstractSqlCreator {

    public QuerySqlCreator(IModelFactory model_fact, IDatabaseFactory db_fact) {
        super(model_fact, db_fact);
    }

    public SelectField getSelectField(SelectSource source, String field_name,
            BusinessModel model) {
        Field f = model.getField(field_name);
        if (f == null)
            throw new ConfigurationError("field '" + field_name
                    + "' is not found in model " + model.getName());
        return source.createSelectField(f.getPhysicalName());
    }

    
    /** @todo refactor needed */
    public void createSelectStatement(BusinessModel model, SelectStatement stmt)
            throws IOException {
        SelectSource base_table = new SelectSource(model.getBaseTable());
        String alias = model.getAlias();
        if (alias != null)
            base_table.setAlias(alias);
        stmt.addSelectSource(base_table);

        HashMap ref_map = new HashMap();
        LinkedList ref_list = new LinkedList();
        Field[] fields = model.getFields();
        if (fields == null)
            throw new ConfigurationError("There is no field defined in model "
                    + model.getName());
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if (!f.isForSelect())
                continue;
            f.checkValidation();
            SelectField sf = null;
            if (f.isReferenceField()) {
                boolean need_create_join = false;
                String relation_name = f.getSourceModel();
                Relation relation = null;
                if (relation_name == null)
                    relation_name = f.getRelationName();
                if (relation_name == null)
                    throw new ConfigurationError(
                            "Must either set 'sourceModel' or 'relationName' for relation");
                String key = relation_name.toLowerCase();
                relation = (Relation) ref_map.get(key);
                if (relation == null) {
                    relation = model.getRelation(key);
                    if (relation == null)
                        throw new ConfigurationError(
                                "can't find specified relation named '"
                                        + relation_name
                                        + "' in model config for field "
                                        + f.getObjectContext().toXML());
                }
                if (!ref_map.containsKey(key)) {
                    ref_map.put(key, relation);
                    need_create_join = true;
                }
                BusinessModel ref_model = modelFactory.getModel(relation
                        .getReferenceModel());
                SelectSource ref_source = new SelectSource(ref_model
                        .getBaseTable());
                if (relation.getReferenceAlias() != null)
                    ref_source.setAlias(relation.getReferenceAlias());

                if (need_create_join) {
                    Join join = new Join(relation.getJoinType() + " JOIN",
                            base_table, ref_source);
                    Reference[] refs = relation.getReferences();
                    if (refs != null)
                        for (int n = 0; n < refs.length; n++) {
                            Reference ref = refs[n];
                            String exp = ref.getExpression();
                            if (exp != null)
                                join.getJoinConditions().addCondition(exp);
                            else {
                                SelectField local = getSelectField(base_table,
                                        ref.getLocalField(), model);
                                SelectField foreign = getSelectField(
                                        ref_source, ref.getForeignField(),
                                        ref_model);
                                join.addJoinField(local, foreign);
                            }
                        }
                    stmt.addJoin(join);
                }
                ref_list.add(ref_source);

                Field ref_field = ref_model.getField(f.getSourceField());
                if (ref_field == null)
                    throw new ConfigurationError(
                            "specified source field is not found in referenced table "
                                    + relation.getReferenceModel()
                                    + ". config source:"
                                    + f.getObjectContext().toXML());
                sf = ref_source.createSelectField(ref_field.getPhysicalName());
            } else if (f.isExpression()) {
                sf = new SelectField(f.getExpression());
            } else {
                sf = base_table.createSelectField(f.getPhysicalName());
            }
            if (!f.getName().equalsIgnoreCase(sf.getFieldName()))
                sf.setAlias(f.getName());
            stmt.addSelectField(sf);
        }

    }

    public void onCreateQueryStatement(BusinessModelServiceContext context)
            throws IOException {
        BusinessModel model = context.getBusinessModel();
        SelectStatement statement = new SelectStatement();
        createSelectStatement(model, statement);
        context.setStatement(statement);
    }

    public void onCreateQuerySql(ISqlStatement s,
            BusinessModelServiceContext context) {
        /*
         * ISqlStatement s = context.getStatement(); StringBuffer sql = new
         * StringBuffer(registry.getSql(s)); context.setSqlString(sql);
         */
        doCreateSql("select", s, context);
    }

    public void onExecuteQuery(BusinessModelServiceContext bmsc)
            throws Exception {
        IResultSetConsumer consumer = bmsc.getResultsetConsumer();
        if (consumer == null)
            return;
        FetchDescriptor desc = bmsc.getFetchDescriptor();
        if (desc == null)
            desc = new FetchDescriptor();
        ServiceOption option = bmsc.getServiceOption();
        StringBuffer sql = bmsc.getSqlString();
        ParsedSql s = new ParsedSql(sql.toString());
        SqlRunner runner = new SqlRunner(bmsc, s);
        runner.setConnectionName(bmsc.getBusinessModel().getDataSourceName());
        runner.setTrace(bmsc.isTrace());
        bmsc.setSqlRunner(runner);
        ResultSet rs = null;
        try {
            rs = runner.query(bmsc.getCurrentParameter());
            ResultSetLoader loader = new ResultSetLoader();
            if (option != null){
                loader.setFieldNameCase(option.getFieldCase());
                if(option.getRecordName()!=null)
                    loader.setElementName(option.getRecordName());
            }
            if (bmsc.getBusinessModel() != null
                    && bmsc.getBusinessModel().getFields() != null)
                loader
                        .loadByConfig(rs, desc, bmsc.getBusinessModel(),
                                consumer);
            else
                loader.loadByResultSet(rs, desc, consumer);
        } finally {
            DBUtil.closeResultSet(rs);
        }
    }

}
