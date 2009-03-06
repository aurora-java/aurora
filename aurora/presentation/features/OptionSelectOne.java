/*
 * Created on 2007-8-27
 */
package aurora.presentation.features;

import java.util.Iterator;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class OptionSelectOne  implements ISingleton {

    public static final String EVENT_CREATE_OPTION_BUILDER = "CreateOptionBuilder";
    
    public static final String OPTIONS = "options";
    
    public static boolean isValueEqual( Object value, Object compared){
        if( value == null) return compared == null;
        else if(compared == null) return false;
        else return value.toString().equals(compared.toString());
    }
    
    public void onCreateViewContent( BuildSession session, ViewContext context )
        throws Exception
    {
        session.fireBuildEvent(EVENT_CREATE_OPTION_BUILDER, context);
        IOptionBuilder printer = (IOptionBuilder)context.getInstance(IOptionBuilder.class);
        if(printer==null){
            return;
        }
        CompositeMap model = context.getModel();
        CompositeMap view  = context.getView();
        StringBuffer buf = new StringBuffer();
        
        Object field_value = FieldBinding.getFieldValue(context);
        
        String data_source = view.getString(DataBinding.KEY_DATA_SOURCE); 
        String value_field = view.getString(DataBinding.KEY_VALUE_FIELD);
        String display_field = view.getString(DataBinding.KEY_DISPLAY_FIELD);
        
        
        CompositeMap options = view.getChild("options");
        Iterator oit = options == null?null:options.getChildIterator();
        if( oit != null)
            while( oit.hasNext()){
                CompositeMap option = (CompositeMap)oit.next();
                String value =  option.getString(DataBinding.KEY_VALUE);
                if(value!=null) value = context.parseString(value);
                String prompt = session.getLocalizedPrompt( option.getString(DataBinding.KEY_PROMPT) );
                String str = printer.createOption(option, value, prompt==null?"":prompt, isValueEqual(value, field_value));
                if(str!=null) buf.append(str);
            }            
        
        CompositeMap binded_options = (CompositeMap)model.getObject(data_source);
        if(binded_options!=null){
            Iterator it = binded_options.getChildIterator();
            if(it!=null)
                while(it.hasNext())
                {
                    CompositeMap item = (CompositeMap)it.next();
                    Object value = item.getObject(value_field);
                    Object prompt = item.getObject(display_field);
                    String str = printer.createOption(item, value, prompt==null?"":prompt.toString(), isValueEqual(value, field_value));
                    if(str!=null) buf.append(str);
                }
        }

        context.getMap().put(OPTIONS, buf.toString());
        
    }
    
}
