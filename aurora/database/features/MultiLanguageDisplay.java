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
import aurora.database.IResultSetConsumer;
import aurora.database.ParsedSql;
import aurora.database.ResultSetLoader;
import aurora.database.SqlRunner;
import aurora.database.profile.IDatabaseFactory;
import aurora.database.rsconsumer.CompositeMapCreator;
import aurora.database.service.BusinessModelService;
import aurora.database.service.BusinessModelServiceContext;

public class MultiLanguageDisplay {
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

    public MultiLanguageDisplay(IModelFactory modelFactory,
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
        mlModel = modelFactory.getModelForRead(mlModelString);
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
/*
    public void postFetchResultSet(BusinessModel bm, IResultSetConsumer consumer){
        if(consumer.getResult() instanceof CompositeMap){
            CompositeMap data = (CompositeMap)consumer.getResult();
            if(data!=null)
                System.out.println(data.toXML());
        }
    }
*/
}
