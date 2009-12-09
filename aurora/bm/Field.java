/*
 * Created on 2008-1-24
 */
package aurora.bm;

import java.io.IOException;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.core.ConfigurationError;
import aurora.service.validation.IParameter;

public class Field extends DynamicObject implements IParameter {
    
    public static final String KEY_RELATION_NAME = "relationname";
    public static final String KEY_SOURCE_MODEL = "sourcemodel";
    public static final String KEY_SOURCE_FIELD = "sourcefield";
    public static final String KEY_EXPRESSION = "expression";
    public static final String KEY_NAME = "name";
    public static final String KEY_PARAMETER_PATH = "parameterpath";
    public static final String KEY_DATA_TYPE = "datatype";    
    public static final String KEY_DATABASE_TYPE = "databasetype";
    public static final String KEY_PHYSICAL_NAME = "physicalname";
    public static final String KEY_UPDATE_EXPRESSION = "updateexpression";
    public static final String KEY_INSERT_EXPRESSION = "insertexpression";    
    public static final String KEY_QUERY_EXPRESSION = "queryexpression";
    public static final String KEY_IS_PRIMARYKEY = "isprimarykey";
    public static final String KEY_REQUIRED = "required";
    public static final String KEY_DEFAULT_VALUE = "defaultvalue"; 
    public static final String KEY_FOR_QUERY = "forquery";
    public static final String KEY_FOR_INSERT = "forinsert";    
    public static final String KEY_FOR_UPDATE = "forupdate";
    public static final String REF_FIELD = "ref-field";
    
    BusinessModel       owner;
    
    public static Field getInstance(CompositeMap context){
        Field field = new Field();
        field.initialize(context);
        return field;
    }
    
    public static String defaultParamExpression( String name ){
        return "${" + name + "}";
    }
    
    public String getName(){
        return getString(KEY_NAME);
    }
    
    public void setName(String name){
        putString(KEY_NAME, name);
    }
    
    public String getInputPath(){
        String path = getString(KEY_PARAMETER_PATH);
        if(path!=null) 
            return path;
        else
            return getString(KEY_PARAMETER_PATH, "@"+getName() );
    }
    
    public void setParameterPath( String path ){
        putString( KEY_PARAMETER_PATH, path );
    }
    
    public String getDataType(){
        return getString(KEY_DATA_TYPE);
    }
    
    public void setDataType(String data_type){
        putString(KEY_DATA_TYPE, data_type);
    }
    
    public String getDatabaseType(){
        return getString(KEY_DATABASE_TYPE);
    }
    
    public void setDatabaseType(String database_type){
        putString(KEY_DATABASE_TYPE, database_type);
    }
    
    public String getPhysicalName(){
        String name = getString(KEY_PHYSICAL_NAME);
        if(name==null) name = getName();
        return name;
    }
    
    public void setPhysicalName( String name ){
        putString(KEY_PHYSICAL_NAME, name);
    }
    
    /*
    public Model getParent(){
        return Model.getInstance(getObjectContext().getParent());
    }
    */
    
    public boolean isReferenceField(){
        return REF_FIELD.equals(getObjectContext().getName());
    }
    
    public boolean isExpression(){
        return getObjectContext().containsKey(KEY_EXPRESSION);
    }
    
    public String getExpression(){
        return getString(KEY_EXPRESSION);
    }
    
    public void setExpression(String exp){
        putString(KEY_EXPRESSION, exp);
    }
    
    public String getSourceField(){
        return getString(KEY_SOURCE_FIELD);
    }
    
    public void setSourceField(String field){
        putString(KEY_SOURCE_FIELD, field);
    }
    
    public String getSourceModel(){
        return getString(KEY_SOURCE_MODEL);
    }
    
    public void setSourceModel(String model_name){
        putString(KEY_SOURCE_MODEL, model_name);
    }
    
    public String getRelationName(){
        return getString(KEY_RELATION_NAME);
    }
    
    public String getUpdateExpression(){
        String exp = getString(KEY_UPDATE_EXPRESSION);
        if(exp==null) exp = defaultParamExpression(getInputPath());
        return exp;
    }
    
    public void setUpdateExpression(String source){
        putString(KEY_UPDATE_EXPRESSION, source);
    }

    public String getInsertExpression(){
        String exp = getString(KEY_INSERT_EXPRESSION);
        if(exp==null) exp = defaultParamExpression(getInputPath());
        return exp;
    }
    
    public void setInsertExpression(String source){
        putString(KEY_INSERT_EXPRESSION, source);
    }
    
    public String getQueryExpression(){
        return getString(KEY_QUERY_EXPRESSION);
    }
    
    public void setQueryExpression(String source){
        putString(KEY_QUERY_EXPRESSION, source);
    }    
    
    public void setRelationName(String name){
        putString(KEY_RELATION_NAME, name);
    }
    
    public boolean isPrimaryKey(){
        return getBoolean(KEY_IS_PRIMARYKEY,false);
    }
    
    public void setPrimaryKey( boolean is_pk ){
        putBoolean(KEY_IS_PRIMARYKEY, is_pk);
    }
    
    public boolean isForInsert(){
        if( isReferenceField() )
            return false;
        return getBoolean(KEY_FOR_INSERT, true);
    }
    
    public boolean isForUpdate(){
        Boolean b = getBoolean(KEY_FOR_UPDATE);
        if(b==null){
            return !isPrimaryKey() && !isExpression() && !isReferenceField();
        }
        else
            return b.booleanValue();
    }
    
    public boolean isForQuery(){
        Boolean b = getBoolean(KEY_FOR_QUERY);
        if(b==null){
            return !isExpression();
        }
        else
            return b.booleanValue();
    }
    
    public boolean isForAction( String action ){
        if(action==null) throw new IllegalArgumentException("action name is null");
        String key = "for" + action.toLowerCase();
        Boolean b = getBoolean(key);
        return b==null?false:b.booleanValue();
    }
    
    public boolean getRequired(){
        return isPrimaryKey() || getBoolean(KEY_REQUIRED, false);
    }
    
    public void setRequired(boolean required){
        putBoolean(KEY_REQUIRED, required);
    }
    
    public boolean isRequired(){
        return getRequired();
    }
    
    public Object getDefaultValue(){
        return get(KEY_DEFAULT_VALUE);
    }
    
    public void setDefaultValue( Object value ){
        put(KEY_DEFAULT_VALUE, value);
    }
    
    public String getReferredModelName(){
        if(!isReferenceField())
            throw new IllegalArgumentException("this field is not a reference field");
        if(owner==null) throw new IllegalStateException("BusinessModel that owns this field is not set");
        String model = getSourceModel();
        if(model==null){            
            String name = this.getRelationName();
            if(name==null) throw new IllegalStateException("Must set 'sourceModel' or 'relationName' for this referrence field");
            Relation r = owner.getRelation(name);
            if(r==null) throw new IllegalStateException("Can't find relation "+name);
            model = r.getReferenceModel();
            if(model==null) throw new IllegalStateException("'referenceModel' is not set for relation "+r.getObjectContext().toXML());
        }
        return model;
    }
    
    public BusinessModel getRefferedModel()
    {
        ModelFactory factory = owner.getModelFactory();
        if(factory==null)
            throw new IllegalStateException("ModelFactory instance is not set for BusinessModel");
        String name = getReferredModelName();
        try{
            return factory.getModelForRead(name);
        }catch(IOException ex){
            throw new RuntimeException("Can't load BusinessModel "+name, ex);
        }        
    }
    
    public Field getReferredField()
    {
        String sf = getSourceField();
        if(sf==null) throw new ConfigurationError("'sourceField' is not set for this referrence field");
        BusinessModel model = getRefferedModel();        
        Field f = model.getField(sf);
        if(f==null) throw new IllegalStateException("Can't find referred field '"+sf+"' in referred model");
        return f;
    }

    /**
     * @return the owner
     */
    public BusinessModel getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(BusinessModel owner) {
        this.owner = owner;
    }

}
