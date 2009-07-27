package aurora.presentation.component.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

/**
 * 
 * @author <a href="mailto:bobbie.zou@gmail.com">bobbie.zou</a>
 * 
 */
public class ComboBox extends TextField {
	private final String PROPERTITY_VALUE_FIELD = "valueField";
	private final String PROPERTITY_DISPLAY_FIELD = "displayField";	
	private final String PROPERTITY_DATA_MODEL = "dataModel";	
	private final String KEY_DATA_SOURCE = "dataSource";
	private String valueKey="value";
	private String promptKey="prompt";
	private String valueField;
	private String displayField;
	private ArrayList dm=new ArrayList();	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "combobox/ComboBox.css");
		addJavaScript(session, context, "core/TriggerField.js");
		addJavaScript(session, context, "combobox/ComboBox.js");
	}
	public void onCreateViewContent(BuildSession session, ViewContext view_context)  {
		super.onCreateViewContent(session, view_context);		
		CompositeMap view = view_context.getView();
		CompositeMap model = view_context.getModel();
		CompositeMap options = view.getChild("options");
		this.displayField=view.getString(PROPERTITY_DISPLAY_FIELD);
	    this.valueField=view.getString(PROPERTITY_VALUE_FIELD);
		if(options!=null){			
			createComboBoxDataModel(options);
		}				
		options=(CompositeMap)model.getObject(view.getString(KEY_DATA_SOURCE));	
		if(options!=null){
			this.valueKey=this.valueField;
			this.promptKey=this.displayField;
			createComboBoxDataModel(options);
		}
		addConfig(PROPERTITY_VALUE_FIELD, this.valueField);
		addConfig(PROPERTITY_DISPLAY_FIELD, this.displayField);
		addConfig(PROPERTITY_DATA_MODEL, new JSONArray(this.dm).toString());
		Map map = view_context.getMap();		
		map.put(PROPERTITY_CONFIG, getConfigString());
	}
	private void createComboBoxDataModel(CompositeMap options){		
		Iterator it = options.getChildIterator();		
        if( it != null){
            while( it.hasNext()){
            	CompositeMap option = (CompositeMap)it.next();
            	String value = option.getString(this.valueKey);
                String prompt = option.getString(this.promptKey);
                JSONObject record=new JSONObject();
                try {                	
                	record.put(this.valueField, value);
                	record.put(this.displayField, prompt);
                	dm.add(record);                	
        		} catch (JSONException e) {
        			e.printStackTrace();
        		}
            }
		}		
	}	
}
