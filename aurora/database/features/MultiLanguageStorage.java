package aurora.database.features;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import aurora.bm.BusinessModel;
import aurora.bm.Field;
import aurora.bm.IModelFactory;
import aurora.bm.Operation;
import aurora.bm.Relation;
import aurora.database.DBUtil;
import aurora.database.DatabaseConstant;
import aurora.database.FetchDescriptor;
import aurora.database.ParsedSql;
import aurora.database.ResultSetLoader;
import aurora.database.SqlRunner;
import aurora.database.profile.IDatabaseFactory;
import aurora.database.rsconsumer.CompositeMapCreator;
import aurora.database.service.BusinessModelService;
import aurora.database.service.BusinessModelServiceContext;

public class MultiLanguageStorage extends MultiLanguageDisplay {
/*    
    final static String KEY_ML_MODEL = "model";
    final static String KEY_ML_SEQUENCE = "sequence";
    final static String KEY_ML_REF_TABLE = "ref_table";
    final static String KEY_ML_REF_FIELD = "ref_field";
    final static String KEY_ML_DESCRIPTION = "description";
    final static String KEY_ML_PK_ID = "pk_id";
    String mlModelString = null;
    String refTable = null;
    String refField = null;
    String mlDescription = null;
    String pkId = null;
    String sequence = null;
    BusinessModel mlModel = null;
    String mlTable = null;
    CompositeMap dbProperties = null;
    ILogger mLogger = null;
    boolean hasMlFields = false;

    public MultiLanguageStorage(IModelFactory modelFactory,
            IDatabaseFactory databaseFactory) throws IOException {

        dbProperties = databaseFactory.getProperties();
        if (dbProperties == null)
            throw new ConfigurationError("Database Properties undifined");
        CompositeMap mlProperties = dbProperties
                .getChild("multi-language-storage");
        if (mlProperties == null)
            throw new ConfigurationError(
                    "multi-language-storage Properties undifined");
        refTable = mlProperties.getString(KEY_ML_REF_TABLE);
        if (refTable == null)
            throw new ConfigurationError(
                    "multi-language-storage ref_table undifined");
        refField = mlProperties.getString(KEY_ML_REF_FIELD);
        if (refField == null)
            throw new ConfigurationError(
                    "multi-language-storage ref_field undifined");
        mlDescription = mlProperties.getString(KEY_ML_DESCRIPTION);
        if (mlDescription == null)
            throw new ConfigurationError(
                    "multi-language-storage description undifined");
        pkId = mlProperties.getString(KEY_ML_PK_ID);
        if (pkId == null)
            throw new ConfigurationError(
                    "multi-language-storage pk_id undifined");
        mlModelString = mlProperties.getString(KEY_ML_MODEL);
        if (mlModelString == null)
            throw new ConfigurationError(
                    "multi-language-storage model undifined");
        sequence = mlProperties.getString(KEY_ML_SEQUENCE);
        if (sequence == null)
            throw new ConfigurationError(
                    "multi-language-storage sequence undifined");
        mlModel = modelFactory.getModel(mlModelString);
        mlTable = mlModel.getBaseTable();
    }

    public void onPrepareBusinessModel(BusinessModel model) {
        Field[] fields = model.getFields();
        boolean is_create = false;
        Field field = null;
        Field refield = null;
        String alias = model.getAlias();
        String fieldName = null;
        String prompt = null;
        String multiLanguageDescField = null;
        for (int i = 0, l = fields.length; i < l; i++) {
            field = fields[i];
            if (field.isReferenceField()) {
                CompositeMap cmap = (CompositeMap) field.getReferredField()
                        .getObjectContext().clone();
                cmap.copy(field.getObjectContext());
                refield = Field.getInstance(cmap);// field.getReferredField();
                Relation relation = model.getRelation(field.getRelationName());
                alias = relation.getReferenceAlias();
                if (alias == null)
                    alias = field.getRelationName();
                fieldName = field.getSourceField();
            } else {
                refield = field;
                fieldName = refield.getName();
            }

            if (refield.getMultiLanguage()) {
                hasMlFields = true;
                prompt = refield.getPrompt();
                multiLanguageDescField = refield.getMultiLanguageDescField();
                for (int j = 0; j < l; j++) {
                    Field f = fields[j];
                    if (f.getName().equalsIgnoreCase(multiLanguageDescField)) {
                        if (!f.isExpression()) {
                            f.setExpression(createQuerySql(fieldName, alias));
                            is_create = true;
                            break;
                        }
                    }
                }
                if (!is_create) {
                    Field f = Field.createField(multiLanguageDescField);
                    f.setPrompt(prompt);
                    f.setForInsert(false);
                    f.setForUpdate(false);
                    f.setExpression(createQuerySql(fieldName, alias));
                    model.addField(f);
                }
            }
            is_create = false;
        }
        model.makeReady();
    }

    String createQuerySql(String fieldName, String alias) {
        StringBuffer sql = new StringBuffer();
        sql.append("(select ");
        sql.append(mlDescription);
        sql.append(" from ");
        sql.append(mlTable);
        sql.append(" where " + pkId + "=" + alias + "." + fieldName + ""
                + " and Language=${" + dbProperties.getString("language_path")
                + "})");
        return sql.toString();
    }
    */

    /**
     * @param modelFactory
     * @param databaseFactory
     * @throws IOException
     */
    public MultiLanguageStorage(IModelFactory modelFactory,
            IDatabaseFactory databaseFactory) throws IOException {
        super(modelFactory, databaseFactory);
        // TODO Auto-generated constructor stub
    }

    public void preCreateInsertStatement(BusinessModel model,
            BusinessModelServiceContext context) throws Exception {
        if (!hasMlFields)
            return;
        if (mLogger == null) {
            mLogger = LoggingContext.getLogger(context.getObjectContext(),
                    DatabaseConstant.AURORA_DATABASE_LOGGING_TOPIC);
        }
        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        Field[] fields = model.getFields();
        int count = 0;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getMultiLanguage()) {
                if (count != 0)
                    sql.append(",");
                sql.append(sequence + " as " + field.getName());
                count++;
            }
        }
        sql.append(" from dual");
        ParsedSql s = new ParsedSql(sql.toString());
        SqlRunner runner = new SqlRunner(context, s);
        runner
                .setConnectionName(context.getBusinessModel()
                        .getDataSourceName());
        ResultSet rs = null;
        try {
            rs = runner.query(new CompositeMap());
            ResultSetLoader loader = new ResultSetLoader();
            loader.setFieldNameCase(Character.LOWERCASE_LETTER);
            FetchDescriptor desc = FetchDescriptor.fetchAll();
            CompositeMapCreator consumer = new CompositeMapCreator();
            loader.loadByResultSet(rs, desc, consumer);
            CompositeMap result = ((CompositeMapCreator) consumer)
                    .getCompositeMap();
            CompositeMap currParamMap = context.getCurrentParameter();
            List list = result.getChilds();
            CompositeMap child = (CompositeMap) list.get(0);
            Set kSet = child.keySet();
            Iterator it = kSet.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                currParamMap.put(key, child.get(key));
            }
            context.setCurrentParameter(currParamMap);
        } finally {
            DBUtil.printTraceInfo("MultiLanguageStorage Insert", mLogger,
                    runner);
            DBUtil.closeResultSet(rs);
        }
    }

    public void postExecuteDmlStatement(BusinessModelServiceContext context)
            throws Exception {
        if (!hasMlFields)
            return;
        BusinessModel model = context.getBusinessModel();
        String operation = context.getOperation();
        Field[] fields = model.getFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (!field.isReferenceField())
                if (field.getMultiLanguage()) {
                    if ("insert".equalsIgnoreCase(operation.toLowerCase())
                            && field.isForInsert() && model.getOperation("insert")==null) {
                        createMultiLanguageSql(context, field, "update");
                    } else if ("update".equalsIgnoreCase(operation
                            .toLowerCase())
                            && field.isForUpdate()  && model.getOperation("update")==null) {
                        createMultiLanguageSql(context, field, "update");
                    } else if ("delete".equalsIgnoreCase(operation
                            .toLowerCase())){
                        createMultiLanguageSql(context, field, "delete");
                    } else
                        throw new RuntimeException("Unknown operation:"+operation);
                }
        }
    }

    void createMultiLanguageSql(BusinessModelServiceContext context,
            Field mlFiled, String operation) throws Exception {
        if (mLogger == null) {
            mLogger = LoggingContext.getLogger(context.getObjectContext(),
                    DatabaseConstant.AURORA_DATABASE_LOGGING_TOPIC);
        }
        CompositeMap currentParameter = context.getCurrentParameter();
        BusinessModel bm = context.getBusinessModel();
        currentParameter.putString(refTable, bm.getBaseTable().toUpperCase());
        currentParameter.putString(refField, mlFiled.getName().toUpperCase());
        currentParameter.putString(mlDescription, currentParameter
                .getString(mlFiled.getMultiLanguageDescField()));
        currentParameter.putString(pkId, currentParameter.getString(mlFiled
                .getName()));
        Operation op = mlModel.getOperation(operation);
        String sql = op.getSql();
        ParsedSql s = new ParsedSql();
        s.parse(sql);
        SqlRunner runner = BusinessModelService.createSqlRunner(context, s);
        try {
            runner.update(context.getCurrentParameter());
        } finally {
            DBUtil.printTraceInfo("MultiLanguageStorage " + operation, mLogger,
                    runner);
        }
    }

}
