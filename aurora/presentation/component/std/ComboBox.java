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
import aurora.presentation.features.DataBinding;

/**
 * 
 * @author <a href="mailto:bobbie.zou@gmail.com">bobbie.zou</a>
 * 
 */
public class ComboBox extends TextField {
	private static final String PROPERTITY_VALUE_FIELD = "valueField";
	private static final String PROPERTITY_DISPLAY_FIELD = "displayField";	
	private static final String PROPERTITY_DATA_MODEL = "dataModel";	
	private static final String KEY_DATA_SOURCE = "dataSource";
	private String KEY_VALUE;
	private String KEY_PROMPT;
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
		CompositeMap options = view.getChild("options");
		this.KEY_PROMPT=view.getString(PROPERTITY_DISPLAY_FIELD);
	    this.KEY_VALUE=view.getString(PROPERTITY_VALUE_FIELD);
		createComboBoxDataModel(options);
		CompositeMap model = view_context.getModel();
		options=(CompositeMap)model.getObject(view.getString(KEY_DATA_SOURCE));		
		createComboBoxDataModel(options);
		addConfig(PROPERTITY_VALUE_FIELD, this.KEY_VALUE);
		addConfig(PROPERTITY_DISPLAY_FIELD, this.KEY_PROMPT);
		addConfig(PROPERTITY_DATA_MODEL, new JSONArray(this.dm).toString());
		Map map = view_context.getMap();		
		map.put(PROPERTITY_CONFIG, getConfigString());
	}
	private void createComboBoxDataModel(CompositeMap options){		
		Iterator it = options == null?null:options.getChildIterator();		
        if( it != null){
            while( it.hasNext()){
            	CompositeMap option = (CompositeMap)it.next();
            	String value = option.getString(this.KEY_VALUE);
                String prompt = option.getString(this.KEY_PROMPT);
                JSONObject record=new JSONObject();
                try {                	
                	record.put(this.KEY_VALUE, value);
                	record.put(this.KEY_PROMPT, prompt);
                	dm.add(record);                	
        		} catch (JSONException e) {
        			e.printStackTrace();
        		}
            }
		}		
	}	
}
