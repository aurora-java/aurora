/*
 * Created on 2008-2-21
 */
package aurora.bm;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.core.ConfigurationError;
import uncertain.datatype.DataType;
import uncertain.datatype.DataTypeRegistry;
import uncertain.ocm.OCManager;
import aurora.application.Namespace;
import aurora.database.profile.IDatabaseFactory;
import aurora.database.profile.IDatabaseProfile;
import aurora.service.validation.IParameter;
import aurora.service.validation.IParameterIterator;
import aurora.service.validation.Parameter;

public class BusinessModel extends DynamicObject {

    /** How to extend parent model's fields
     *  override: use all parent's fields, and override parent's config with self
     *  reference: only use self referred fields
     */
    public static final String KEY_EXTEND_MODE = "extendmode";
    
    public static final String VALUE_OVERRIDE = "override";
    
    public static final String VALUE_REFERENCE = "reference";

    public static final String KEY_EXTEND = "extend";

    public static final String KEY_OPERATIONS = "operations";

    public static final String SECTION_RELATIONS = "relations";

    public static final String SECTION_FIELDS = "fields";
    
    public static final String SECTION_QUERY_FIELDS = "query-fields";

    public static final String SECTION_PRIMARY_KEY = "primary-key";

    public static final String KEY_ALIAS = "alias";

    public static final String KEY_BASE_TABLE = "basetable";
    
    public static final String KEY_NAME = "name";
    
    public static final String KEY_MODEL_TYPE = "modeltype";
    
    public static final String KEY_DATABASE_TYPE = "databasetype";

    public static  String KEY_DATA_SOURCE_NAME="datasourcename";
    
    public static final String DEFAULT_FIELD_PROMPT_FORMAT = "bm.{0}.{1}";
    
	static final Field[] EMPTY_FIELDS = new Field[0];

    
    public static BusinessModel getInstance( CompositeMap context ){
        BusinessModel model = new BusinessModel();
        model.initialize(context);
        return model;
    }

    // ============= Singletons =====================
    // factory owner
    ModelFactory   modelFactory;
    // OCManager
    OCManager   mOcManager = OCManager.getInstance();
    
    // ============= Internal Map & Arrays ==========    
    // name -> Field
    Map         fieldMap;
    // All Fields in array
    Field[]     fieldsArray;
    // Primary key fields
    Field[]     pkFieldsArray;    
    // name -> Relation
    Map         relationMap;
    // relation prepared in array
    Relation[]  relationArray;    
    // name -> operation, name lower case
    Map         operationMap;
    // default operation without name
    Operation   defaultOperation;
    
    // ============= Parent model ===================
    BusinessModel   parent;


    public class BaseQueryFieldIterator implements IParameterIterator {
        
        int         id=0;
        
        public BaseQueryFieldIterator(){
            movePointer();
        }
        
        void movePointer(){
            if(fieldsArray==null)
                id=-1;
            if(id<0) return;
            
            while( id<fieldsArray.length && !fieldsArray[id].isForQuery())
                id++;
            if( id >= fieldsArray.length )
                id = -1;      
        }
        
        public boolean hasNext(){
            return id>=0;
        }
        
        public IParameter next(){
            if(!hasNext()) return null;
            Field f = fieldsArray[id++];
            movePointer();
            if(f.isReferenceField()) f = f.getReferredField();
            return new QueryFieldWrapper(f);
        }
        
    };  
    
    
    public class QueryFieldIterator implements IParameterIterator {
        
        Iterator mFieldsIt;
        
        public QueryFieldIterator(){
            List items = getQueryFieldsList();
            if(items!=null)
                mFieldsIt = items.iterator();
        }
        
        public boolean hasNext(){
            return mFieldsIt == null? false: mFieldsIt.hasNext();
        }
        
        public IParameter next(){
            if(!hasNext()) return null;
            CompositeMap map = (CompositeMap)mFieldsIt.next();
            QueryField qf = (QueryField)DynamicObject.cast(map, QueryField.class);
            String name = qf.getField();
            Field f = null;
            if(name!=null){
                f = getField(name);
                if(f==null)
                    throw new ConfigurationError("Cant' find field '"+name+"' in BusinessModel. Make sure the 'field' property in query-field refers to a pre-defined BusinessModel field");
                if(f.isReferenceField()) f = f.getReferredField();
                return new QueryFieldWrapper(f);       
            }else{
                f = (Field)qf.castTo(Field.class);
                return f;
            }

        }
        
    }
    
    public class GeneralFieldIterator implements IParameterIterator {
        
        int id=0;
        String operation;
        
        void movePointer(){
            if(fieldsArray==null){
                id = -1;
                return;
            }
            while( id<fieldsArray.length && !fieldsArray[id].isForOperation(operation))
                id++;
            if( id >= fieldsArray.length )
                id = -1;            
        }
        
        public GeneralFieldIterator(String operation){
            this.operation = operation;
            movePointer();
        }
        
        public boolean hasNext(){
            return id>0;
        }
        
        public IParameter next(){
            if(id<0) return null;
            Field f = fieldsArray[id++];
            if(f.isReferenceField()) f = f.getReferredField();
            movePointer();
            return f;
        }
        
    };
    
    // parameter list for validation
    public String getName(){
        return getString(KEY_NAME);
    }
    
    public void setName(String name){
        putString(KEY_NAME, name);
    }
    
    public String getBaseTable(){
        return getString(KEY_BASE_TABLE);
    }    
   
    public void setBaseTable(String table){
        put(KEY_BASE_TABLE, table);
    }
    
    public String getAlias(){
        return getString(KEY_ALIAS);
    }
    
    public void setAlias(String alias){
        putString(KEY_ALIAS, alias);
    }
    
    public String getDatabaseType(){
        return getString(KEY_DATABASE_TYPE);
    }
    
    public void setDatabaseType(String type){
        putString(KEY_DATABASE_TYPE, type);
    }    

    public String getDataSourceName() {
        return getString(KEY_DATA_SOURCE_NAME);
    }

    public void setDataSourceName(String dataSourceName) {
        putString(KEY_DATA_SOURCE_NAME, dataSourceName);
    }    
    /**
     * Get all query fields
     * @return query field items as CompositeMap 
     */
    public List getQueryFieldsList(){
        return getChildSection(SECTION_QUERY_FIELDS);
    }
    
    public  QueryField[] getQueryFieldsArray(){
        List flds = getChildSection("query-fields");
        if(flds!=null)
            return (QueryField[])DynamicObject.castToArray(flds, QueryField.class);
        else
            return null;
    }       
    
    protected List getChildSection(String name){
        CompositeMap childs_map = object_context.getChild(name);
        if(childs_map!=null)
            return childs_map.getChilds();
        else
            return null;        
    }
    
    public Field getField( String name ){
        assert name!=null;
        if(fieldMap==null) makeReady();
        String key = name.toLowerCase();
        Field f =  (Field)fieldMap.get(key);
        if(f!=null)
            return f;
        else{
            if( parent != null)
                return parent.getField(name);
            else
                return null;
        }
    }
    
    protected CompositeMap getChildSectionNotNull( String section_name ){
        CompositeMap fields = object_context.getChild(section_name);
        if(fields==null){
            fields = object_context.createChild(section_name);
            fields.setNameSpaceURI(Namespace.AURORA_BUSINESS_MODEL_NAMESPACE);
        }
        return fields;
    }
    
    /**
     * Do remember to call makeReady() after modify model
     * @param f
     */
    public void addField( Field f ){
        getChildSectionNotNull(SECTION_FIELDS).addChild(f.getObjectContext());
    }
    
    /**
     * Get types of each field in an array
     * @param registry An instance of DataTypeRegistry that provides lookup 
     * between data type name and DataType instance
     * @return An array of DataType
     */
    public DataType[] getFieldTypeArray( DataTypeRegistry registry ){
        Field[] fields = getFields();
        if(fields==null) return null;
        DataType[] fieldTypeArray = new DataType[fields.length];
        for(int i=0; i<fields.length; i++){
            //String datatype = fields[i].getDatabaseType();
            String datatype = fields[i].getDataType();
            if(datatype==null) datatype="java.lang.String";
            // revised
            fieldTypeArray[i] = registry.getDataType(datatype);
            if(fieldTypeArray[i]==null) throw new IllegalArgumentException("Unknown data type "+datatype);
            i++;
      }        
      return fieldTypeArray;
    }
    
    public DataType[] getFieldTypeArray(){
        return getFieldTypeArray(DataTypeRegistry.getInstance());
    }
   
    protected void loadFields(){        
        if(fieldMap==null)
            fieldMap = new HashMap();
        else
            fieldMap.clear();   
        List fields = getChildSection(SECTION_FIELDS);
        if(fields==null) return;     
        Field[] array = new Field[fields.size()];
        Iterator it = fields.iterator();
        int i=0;
        while(it.hasNext()){
            CompositeMap field_map = (CompositeMap)it.next();
            Field f = Field.getInstance(field_map);
            f.setOwner(this);
            f.checkValidation();
            String name = f.getName();
            if(name==null)
                throw new ConfigurationError("Field No."+(i+1)+" has no name: "+f.getObjectContext().toXML());
            fieldMap.put(name.toLowerCase(), f);            
            array[i++] = f;
        }
        fieldsArray = array;
        // load PK fields
        CompositeMap pk_conf = getObjectContext().getChild(SECTION_PRIMARY_KEY);
        if(pk_conf==null)
            pkFieldsArray = EMPTY_FIELDS;
        else{            
            if(pk_conf.getChilds()==null)
                pkFieldsArray = EMPTY_FIELDS;
            else{
                pkFieldsArray = new Field[pk_conf.getChilds().size()];
                int n=0;
                it = pk_conf.getChildIterator();
                if(it!=null)
                    while(it.hasNext()){
                        CompositeMap field = (CompositeMap) it.next();
                        String name = field.getString(Field.KEY_NAME);
                        if(name==null) throw new ConfigurationError("<primary-key>: Must set 'name' property for a primary key field. Config source:"+field.toXML());
                        Field f = getField(name.toLowerCase()); 
                            //(Field)fieldMap.get(name.toLowerCase());
                        if(f==null) throw new ConfigurationError("<primary-key>: Field '"+name+"' is not found in field definition. Config source:"+field.toXML());                    f.setPrimaryKey(true);
                        pkFieldsArray[n++] = f;
                    }
            }
        }
    }

    public Field[] getFields(){
        if(fieldsArray==null) loadFields();
        return fieldsArray;
    }
    
    public Field[] getPrimaryKeyFields(){
        if(pkFieldsArray==null) loadFields();
        return pkFieldsArray;
    }
    
    protected void loadRelations(){
        List relations = getChildSection(SECTION_RELATIONS);
        if(relations==null) return;
        if(relationMap==null)
            relationMap = new HashMap();
        else
            relationMap.clear();
        relationArray = new Relation[relations.size()];
        int n=0;
        Iterator it = relations.iterator();
        while(it.hasNext()){
            CompositeMap relation_map = (CompositeMap)it.next();
            Relation relation = Relation.getInstance(relation_map);
            String name = relation.getName().toLowerCase();
            relationMap.put(name, relation);
            relationArray[n++] = relation;
        }        
    }
    
    public Relation getRelation(String name){
        return (Relation)relationMap.get(name.toLowerCase());
    }
    
    public Relation[] getRelations(){
        return relationArray;
    }
    
    public DataFilter[] getDataFilters(){
        List lst = getChildSection(DataFilter.KEY_DATA_FILTERS);
        if(lst==null) return null;
        return (DataFilter[])DynamicObject.castToArray(lst, DataFilter.class);
    }
 
    
    public IParameterIterator getParameterForQuery(){
        if(getQueryFieldsList()!=null)
            return new QueryFieldIterator();
        else
            return new BaseQueryFieldIterator();
    }
    /*
    public IParameterIterator getParameterForInsert(){
        return null;
    }
    
    public IParameterIterator getParameterForUpdate(){
        return null;
    }
    
    public IParameterIterator getParameterForDelete(){
        return null;
    }
    */
    /** Get parameter iterator for specified operation
     * @param operation name of operation
     * @return If the operation is defined in <operations> part, and this operation has <parameter> config,
     *  then operation defined parameter will be returned.
     *  Else a BM level general parameter config will be returned. 

     */
    public IParameterIterator getParameterForOperation( String operation ){
        if("query".equalsIgnoreCase(operation))
            return getParameterForQuery();
        else{
            List params = null;
            Operation op = getOperation(operation);
            if(op!=null)
                params = op.getParameters();
            if(params!=null)
                return new PredefinedParameterIterator(params);
            else
                return new GeneralFieldIterator(operation);
        }
    }
    
    /**
     * @return a List containing aurora.service.validation.Parameter
     */
    public List getParameterForOperationInList( String operation ){
        List result = new LinkedList();
        IParameterIterator it = getParameterForOperation(operation);
        if(it!=null)
            while(it.hasNext()){
                IParameter obj = it.next();
                Parameter param = null;
                // create parameter from CompositeMap
                if(obj instanceof DynamicObject){
                    param = new Parameter();
                    CompositeMap m = ((DynamicObject)obj).getObjectContext();
                    mOcManager.populateObject(m, param);
                    result.add(param);
                }else{
                    param = new Parameter(obj);
                }
            }
        return result;
    }
    
    public void makeReady(){
        // Build field map
        loadFields();
        loadRelations();
        prepareOperationMap();
    }

    /**
     * @return the modelFactory
     */
    public ModelFactory getModelFactory() {
        return modelFactory;
    }

    /**
     * @param modelFactory the modelFactory to set
     */
    public void setModelFactory(ModelFactory modelFactory) {
        this.modelFactory = modelFactory;        
    }
    
    public IDatabaseProfile getDatabaseProfile( IDatabaseFactory fact ){
        String db_type = getDatabaseType();
        if(db_type==null)
            return fact.getDefaultDatabaseProfile();
        else{
            IDatabaseProfile profile = fact.getDatabaseProfile(db_type);
            if(profile==null)
                throw new ConfigurationError("Unknown database type:"+db_type);
            return profile;
        }
    }
    
    public Operation getOperation( String name ){
        if(operationMap==null)
            return defaultOperation;
        return (Operation)operationMap.get(name.toLowerCase());        
    }
    
    public Operation getDefaultOperation(){
        return defaultOperation;
    }
    
    protected void prepareOperationMap(){
        CompositeMap ops = object_context.getChild(KEY_OPERATIONS);
        if(ops==null)
            return;
        Iterator it = ops.getChildIterator();
        if(it==null)
            return;
        if(operationMap==null)
            operationMap = new HashMap();
        else
            operationMap.clear();
        while(it.hasNext()){
            CompositeMap item = (CompositeMap)it.next();
            Operation op = Operation.createOperation(item);
            String name = op.getName();
            if(name==null){
                if(defaultOperation!=null)
                    throw new ConfigurationError("Can only have one default operation");
                defaultOperation = op;
            }else{
                name = name.toLowerCase();
                if(operationMap.containsKey(name))
                    throw new ConfigurationError("Operation "+name+" already defined");
            }   
            operationMap.put(name, op);
        }
    }

    protected void setOcManager(OCManager ocManager) {
        mOcManager = ocManager;
    }
    
    public String getModelType(){
        return getString(KEY_MODEL_TYPE);
    }
    
    public void setModelType( String type ){
        putString(KEY_MODEL_TYPE, type);
    }
    
    public String getFieldPrompt( Field field, String default_prompt_pattern ){
        String prompt = field.getPrompt();
        if(prompt!=null)
            return prompt;
        String name = getBaseTable();
        if(name==null)
            name = getName();
        String field_name = field.getName();
        String result = MessageFormat.format(default_prompt_pattern, new Object[]{name,field_name} );
        return result.toLowerCase();
    }
    
    public String getFieldPrompt( Field field){
        return getFieldPrompt(field,DEFAULT_FIELD_PROMPT_FORMAT);
    }
    
    public String getExtend(){
        return getString(KEY_EXTEND);
    }
    
    public void setExtend( String base ){
        putString(KEY_EXTEND, base);
    }
    
    public String getExtendMode(){
        return getString(KEY_EXTEND_MODE);
    }
    
    public void setExtendMode( String mode ){
        putString(KEY_EXTEND_MODE, mode);
    }
    
    public BusinessModel getParent(){
        return parent;
    }
    
    protected void setParent( BusinessModel parent ){
        this.parent = parent;
    }
    
    public static String getDefaultAlias( int index ){
        return "t"+index;
    }
    
    public static String getDefaultAlias(){
        return "t1";
    }


}
