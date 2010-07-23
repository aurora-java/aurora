package aurora.database.features;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uncertain.composite.CompositeMap;
import aurora.bm.BusinessModel;
import aurora.bm.Field;
import aurora.bm.IModelFactory;
import aurora.bm.Operation;
import aurora.database.CompositeMapCreator;
import aurora.database.FetchDescriptor;
import aurora.database.ParsedSql;
import aurora.database.ResultSetLoader;
import aurora.database.SqlRunner;
import aurora.database.profile.IDatabaseFactory;
import aurora.database.service.BusinessModelService;
import aurora.database.service.BusinessModelServiceContext;

public class MultiLanguageStorage{
	final static String KEY_ML_MODEL="model";
	final static String KEY_ML_SEQUENCE="sequence";
	final static String KEY_ML_REF_TABLE="ref_table";
	final static String KEY_ML_REF_FIELD="ref_field";
	final static String KEY_ML_DESCRIPTION="description";
	final static String KEY_ML_PK_ID="pk_id";
	String mlModelString=null;
	String refTable=null;
	String refField=null;
	String mlDescription=null;
	String pkId=null;
	String sequence=null;
	BusinessModel mlModel=null;	
	String mlTable=null;
	CompositeMap dbProperties=null;
	public MultiLanguageStorage(IModelFactory modelFactory, IDatabaseFactory databaseFactory) throws Exception{	
		dbProperties=databaseFactory.getProperties();
		if(dbProperties==null)
			throw new Exception("Database Properties undifined");	
		CompositeMap mlProperties=dbProperties.getChild("multi-language-storage");
		if(mlProperties==null)
			throw new Exception("multi-language-storage Properties undifined");			
		refTable=mlProperties.getString(KEY_ML_REF_TABLE);
		if(refTable==null)
			throw new Exception("multi-language-storage ref_table undifined");		
		refField=mlProperties.getString(KEY_ML_REF_FIELD);
		if(refField==null)
			throw new Exception("multi-language-storage ref_field undifined");
		mlDescription=mlProperties.getString(KEY_ML_DESCRIPTION);
		if(mlDescription==null)
			throw new Exception("multi-language-storage description undifined");
		pkId=mlProperties.getString(KEY_ML_PK_ID);
		if(pkId==null)
			throw new Exception("multi-language-storage pk_id undifined");	
		mlModelString=mlProperties.getString(KEY_ML_MODEL);
		if(mlModelString==null)
			throw new Exception("multi-language-storage model undifined");
		sequence=mlProperties.getString(KEY_ML_SEQUENCE);
		if(sequence==null)
			throw new Exception("multi-language-storage sequence undifined");
		mlModel = modelFactory.getModel(mlModelString);		
		mlTable=mlModel.getBaseTable();
	}
    public void onPrepareBusinessModel(BusinessModel model){
    	 Field[] fields = model.getFields();
    	 boolean is_create=false;
    	 Field field=null;
    	 String alias=model.getAlias();
    	 String fieldName=null;
    	 String prompt=null;
    	 String multiLanguageDescField=null;
    	 for(int i=0,l=fields.length;i<l;i++){
    		 field=fields[i];
    		 if(field.getMultiLanguage()){
    			 prompt=field.getPrompt();
    			 fieldName=field.getName();
    			 multiLanguageDescField=field.getMultiLanguageDescField();
    			 for(int j=0;j<l;j++){
    				 field=fields[j];
    				 if(field.getName().equalsIgnoreCase(multiLanguageDescField)){
    					 if(!field.isExpression()){
    						 field.setExpression(createQuerySql(fieldName,alias));
    						 is_create=true;
    						 break;
    					 }
    				 }
    			 }
    			 if(!is_create){
    				 field=Field.createField(multiLanguageDescField);
    				 field.setPrompt(prompt);
    				 field.setForInsert(false);
    				 field.setForUpdate(false);
    				 field.setExpression(createQuerySql(fieldName,alias));
    				 model.addField(field);
    			 }
    		 }
    		 is_create=false;
    	 }
    	 model.makeReady();
    }

    String createQuerySql(String fieldName,String alias){
		StringBuffer sql=new StringBuffer();
		sql.append("(select ");
		sql.append(mlDescription);
		sql.append(" from ");
		sql.append(mlTable);
		sql.append(" where "+pkId+"="+alias+"."+fieldName+"" +
				" and Language=${"+dbProperties.getString("language_path")+"})");//language_path待修改		
		return sql.toString();
    }
	public void preCreateInsertStatement(BusinessModel model,
			BusinessModelServiceContext context)throws Exception{	
		StringBuffer sql=new StringBuffer();
		sql.append("select ");		
		Field[] fields = model.getFields();
		int count=0;
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];			
			if(field.getMultiLanguage()){
				if(count!=0)
					sql.append(",");
				sql.append(sequence+" as "+field.getName());
				count++;
			}
		}
		sql.append(" from dual");
		ParsedSql s = new ParsedSql(sql.toString());
		SqlRunner runner = new SqlRunner(context, s);
		runner.setConnectionName(context.getBusinessModel().getDataSourceName());
		ResultSet rs = null;
		rs = runner.query(new CompositeMap());
		ResultSetLoader loader = new ResultSetLoader();
		loader.setFieldNameCase(Character.LOWERCASE_LETTER);
		FetchDescriptor desc  = FetchDescriptor.fetchAll();
		CompositeMapCreator consumer = new CompositeMapCreator();
        loader.loadByResultSet( rs, desc, consumer ); 
        CompositeMap result=((CompositeMapCreator)consumer).getCompositeMap();
        CompositeMap currParamMap=context.getCurrentParameter();
        List list=result.getChilds();        
        CompositeMap child=(CompositeMap)list.get(0);
        Set kSet=child.keySet();        
        Iterator it=kSet.iterator();
        while (it.hasNext()) {
			String key = (String) it.next();			
			currParamMap.putString(key, child.getString(key));
		}
        context.setCurrentParameter(currParamMap);     
	}	

	public void postExecuteDmlStatement(BusinessModelServiceContext context) throws Exception {
		BusinessModel model=context.getBusinessModel();
		String operation=context.getOperation();
		Field[] fields = model.getFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];			
			if(field.getMultiLanguage()){
				if("insert".equalsIgnoreCase(operation.toLowerCase())||"update".equalsIgnoreCase(operation.toLowerCase())){			
					createMultiLanguageSql(context,field,"update");
				}else{
					createMultiLanguageSql(context,field,"delete");
				}														
			}
		}	
	}
	
	void createMultiLanguageSql(BusinessModelServiceContext context,Field mlFiled,String operation) throws Exception{		
		CompositeMap currentParameter=context.getCurrentParameter();
		BusinessModel bm = context.getBusinessModel();
		currentParameter.putString(refTable, bm.getBaseTable());
		currentParameter.putString(refField, mlFiled.getName());
		currentParameter.putString(mlDescription, currentParameter.getString(mlFiled.getMultiLanguageDescField()));
		currentParameter.putString(pkId,currentParameter.getString(mlFiled.getName()));				
		Operation op =mlModel.getOperation(operation);
		String sql=op.getSql();
		ParsedSql s = new ParsedSql();
		s.parse(sql);
		SqlRunner runner =BusinessModelService.createSqlRunner(context, s);
		runner.update(context.getCurrentParameter());
	}
}
