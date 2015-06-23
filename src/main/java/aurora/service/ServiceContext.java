/*
 * Created on 2007-11-21
 */
package aurora.service;

import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.event.RuntimeContext;

/**
 * An Object based on CompositeMap to encapsulate necessary objects during service 
 * ServiceContext
 * @author Zhou Fan
 *
 */
public class ServiceContext extends RuntimeContext {
    /*
    public static final String MODE_NORMAL = "normal";    
    public static final String MODE_BATCH = "batch";
    */
    //public static final String KEY_SERVICE_EXCEPTION = "service_exception";
    //public static final String KEY_SERVICE_CURRENT_FAILED_RECORD = "service_failed_record";
    //public static final String KEY_SERVICE_FAILED_RECORD_LIST = "service_failed_record_list";
    //public static final String KEY_INVOKE_MODE = "invoke_mode";

    public static final String KEY_PARAMETER = "parameter";

    public static final String KEY_SESSION = "session";
    
    public static final String KEY_RESULT = "result";
    
    public static final String KEY_ERROR = "error"; 
    
    public static final String KEY_MODEL = "model";
    
    public static final String KEY_CURRENT_PARAMETER = "current_parameter";
    
    public static final String KEY_PARAMETER_PARSED = "__parameter_parsed__";
    
    public static final String KEY_SERVICE_NAME = "__service_name__";
    
    public static final String KEY_ERROR_DESCRIPTION = "__error_description__";
    
    public static final String KEY_REQUEST_TYPE = "__request_type__";
    
    public static ServiceContext createServiceContext( CompositeMap map ){
        ServiceContext context = new ServiceContext();
        context.initialize(map);
        return context;
    }

    protected CompositeMap getChildSection(String name){
        CompositeMap result = object_context.getChild(name);
        if(result==null) result = object_context.createChild(name);
        return result;
    }
    
    /** parameter for input */
    public CompositeMap getParameter(){
        return getChildSection(KEY_PARAMETER);
    }
    
    public void setParameter( CompositeMap param ){
        getParameter().copy(param);
    }
    
    public void setParameters( Map param ){
        CompositeMap p = getParameter();
        p.clear();
        if(param instanceof CompositeMap)
            setParameter((CompositeMap)param);
        else
            p.putAll(param);
    }
    
    public List getParameterList(){
        CompositeMap param = getParameter();
        return param.getChilds();
    }
    
    public CompositeMap getSession(){
        return getChildSection(KEY_SESSION);
    }
    
    public CompositeMap getResult(){
        return getChildSection(KEY_RESULT);
    }   
    
    public CompositeMap getError(){
        return getChildSection(KEY_ERROR);
    }
    
    public boolean hasError(){
        CompositeMap error = getObjectContext().getChild(KEY_ERROR);
        return error!=null;
    }
    
    public CompositeMap getModel(){
        return getChildSection(KEY_MODEL);
    }    
    
    public void setError(CompositeMap error){
        CompositeMap map = getObjectContext();
        error.setName(KEY_ERROR);
        CompositeMap old_error = map.getChild(KEY_ERROR);
        if(old_error!=null){
            map.removeChild(old_error);
        }
        map.addChild(error);
    }
    /*
    public ErrorDescription getErrorDescription()        
    {
        ErrorDescription desc = new ErrorDescription();
        desc.initialize(getError());
        return desc;
    }
    */
    
    /**
     * @return the record that caused failure in a batch update
     */
    
    /*
    public CompositeMap getCurrentFailedRecord(){
        return (CompositeMap)get(KEY_SERVICE_CURRENT_FAILED_RECORD);
    }
    
    public void setCurrentFailedRecord(CompositeMap record){
        put(KEY_SERVICE_CURRENT_FAILED_RECORD, record);
    }
    */
    
    /*
    public String getInvokeMode(){
        return getString(KEY_INVOKE_MODE, MODE_NORMAL);
    }
    
    public void setInvokeMode( String mode ){
        putString(KEY_INVOKE_MODE, mode);
    }
    
    
    public boolean isBatchMode(){
        return MODE_BATCH.equalsIgnoreCase(getInvokeMode());
    }
       
    
    public List getFailedRecordList(){
        List lst = (List)get(KEY_SERVICE_FAILED_RECORD_LIST);
        if(lst==null){
            lst = new LinkedList();
            put(KEY_SERVICE_FAILED_RECORD_LIST, lst);
        }
        return lst;
    }    
    */ 
    
    public String getServiceName(){
        return getString(KEY_SERVICE_NAME);
    }
    
    public void setServiceName(String service_name){
        put(KEY_SERVICE_NAME, service_name);
    }
        
    public CompositeMap getCurrentParameter(){
        CompositeMap map = (CompositeMap)get(KEY_CURRENT_PARAMETER);
        if(map==null)
            map = getParameter();
        return map;
    }
    
    public void setCurrentParameter( Map param ){
        if(param==null){
            getObjectContext().remove(KEY_CURRENT_PARAMETER);
            return;
        }
        CompositeMap p = null;
        if( param instanceof CompositeMap )
            p = (CompositeMap)param;
        else{
            p = new CompositeMap("current-parameter");
            p.putAll(param);
            p.setParent(getObjectContext());
        }
        CompositeMap parent = p.getParent();
        put(KEY_CURRENT_PARAMETER, p);
        p.setParent(parent);
    }
    
    public String getRequestType(){
        return getString(KEY_REQUEST_TYPE);
    }
    
    public void setRequestType(String type){
        putString(KEY_REQUEST_TYPE, type);
    }
    
    /*
    public boolean getParameterParsed(){
        CompositeMap param = getCurrentParameter();
        if( param != null )
            return param.getBoolean(KEY_PARAMETER_PARSED, false);
        return false;
    }
    
    public void setParameterParsed(boolean parsed){
        CompositeMap param = getCurrentParameter();
        if( param != null )
            param.put(KEY_PARAMETER_PARSED, new Boolean(parsed));
    }
    */

}
