package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class QueryFormConfig extends ComponentConfig {
	
	public static final String VERSION = "$Revision";
	public static final String TAG_NAME = "queryForm";
	
	public static final String PROPERTITY_EXPAND = "expand";
	public static final String PROPERTITY_TITLE = "title";
	public static final String PROPERTITY_RESULT_TARGET = "resulttarget";
	public static final String PROPERTITY_DEFAULT_QUERY_FIELD = "defaultqueryfield";
	public static final String PROPERTITY_DEFAULT_QUERY_HINT = "defaultqueryhint";
	public static final String PROPERTITY_DEFAULT_QUERY_PROMPT = "defaultqueryprompt";
	public static final String PROPERTITY_QUERY_HOOK = "queryhook";
	public static final String PROPERTITY_CREATE_SEARCH_BUTTON = "createsearchbutton";
	
	
	public static QueryFormConfig getInstance(){
		QueryFormConfig model = new QueryFormConfig();
        model.initialize(QueryFormConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static QueryFormConfig getInstance(CompositeMap context){
		QueryFormConfig model = new QueryFormConfig();
        model.initialize(QueryFormConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public String getResultTarget(){
		return getString(PROPERTITY_RESULT_TARGET);
	}
	
	public void setResultTarget(String resultTarget){
		putString(PROPERTITY_RESULT_TARGET, resultTarget);
	}
	
	public String getQueryHook(){
		return getString(PROPERTITY_QUERY_HOOK);
	}
	
	public void setQueryHook(String queryHook){
		putString(PROPERTITY_QUERY_HOOK, queryHook);
	}
	
	public String getDefaultQueryField(){
		return getString(PROPERTITY_DEFAULT_QUERY_FIELD);
	}
	
	public void setDefaultQueryField(String defaultQueryField){
		putString(PROPERTITY_DEFAULT_QUERY_FIELD, defaultQueryField);
	}
	
	public String getDefaultQueryHint(){
		return getString(PROPERTITY_DEFAULT_QUERY_HINT);
	}
	
	public void setDefaultQueryHint(String defaultQueryHint){
		putString(PROPERTITY_DEFAULT_QUERY_HINT, defaultQueryHint);
	}
	
	public String getDefaultQueryPromt(){
		return getString(PROPERTITY_DEFAULT_QUERY_PROMPT);
	}
	
	public void setDefaultQueryPromt(String defaultQueryPromt){
		putString(PROPERTITY_DEFAULT_QUERY_PROMPT, defaultQueryPromt);
	}
	
	public String getTitle(){
		return getString(PROPERTITY_TITLE,"");
	}
	
	public void setTitle(String title){
		putString(PROPERTITY_TITLE, title);
	}
	
	public boolean isExpand(){
		return getBoolean(PROPERTITY_EXPAND,false);
	}
	
	public void setExpand(boolean expand){
		putBoolean(PROPERTITY_EXPAND, expand);
	}
	
	public boolean isCreateSearchButton(){
		return getBoolean(PROPERTITY_CREATE_SEARCH_BUTTON,true);
	}
	
	public void setCreateSearchButton(boolean createSearchButton){
		putBoolean(PROPERTITY_CREATE_SEARCH_BUTTON, createSearchButton);
	}
}
