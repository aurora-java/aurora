/*
 * Created on 2007-12-26
 */
package aurora.service.validation;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.datatype.DataType;
import uncertain.datatype.DataTypeRegistry;
import aurora.service.ServiceContext;
import aurora.service.exception.DefaultExceptionDescriptor;
import aurora.service.exception.IExceptionDescriptor;

public class ParameterParser {
    
    static ParameterParser default_instance = new ParameterParser();
    
    public static ParameterParser getInstance(){
        return default_instance;
    }
    
    DataTypeRegistry    registry;
    
    public ParameterParser(){
        registry = DataTypeRegistry.getInstance();
    }
    
    public ParameterParser( DataTypeRegistry registry){
        this.registry = registry;
    }
    
    /*
    boolean parseSingleParameter(ServiceContext context, CompositeMap input, IParameter param, IExceptionDescriptor descriptor, ErrorDescription error_desc){
        boolean success = true;
        //System.out.println("parsing "+param.getName());
        try{
            String datatype = param.getDataType();
            if(datatype==null) datatype = "java.lang.String";
            DataType dt = registry.getDataType(datatype);
            if(dt==null)
                throw new IllegalArgumentException("can't find data type "+param.getDataType()+" in parameter "+param.getName());
            Object value = input.get(param.getName());
            if(value==null){
                value =  param.getDefaultValue();
                if(value==null&&param.isRequired())
                    throw new ParameterNullException(param.getName());
            }
            Object parsed_value = dt.convert(value);
            input.put(param.getName(), parsed_value);
        } catch (Exception ex) {
            success = false;
            //CompositeMap msg = descriptor.process(context, input, param.getName(), ex);
            CompositeMap msg = descriptor.process(context,  ex);
            if (msg != null)
                error_desc.addErrorMessage(msg);
        }
        return success;
    }
    
    ErrorDescription prepareErrorDescription(CompositeMap input){
        ErrorDescription error_desc = ErrorDescription.getFrom(input, false);
        if(error_desc==null)
            error_desc = ErrorDescription.createInstance();
        return error_desc;
    }
    
    public ErrorDescription parse( ServiceContext context, CompositeMap input, Parameter[] param_config, IExceptionDescriptor descriptor)
    {
        if(descriptor==null)
            descriptor = DefaultExceptionDescriptor.getInstance();
        ErrorDescription error_desc = prepareErrorDescription(input);
        boolean success = true;
        for(int i=0; i<param_config.length; i++){
            Parameter param = param_config[i];
            success = success && parseSingleParameter(context, input, param, descriptor, error_desc);
        }
        if(!success){
            error_desc.addTo(input);
            return error_desc;
        }else
            return null;
    }


    public ErrorDescription parse(ServiceContext context, CompositeMap input,
            Collection params, IExceptionDescriptor descriptor)
    {
        return parse(context, input, new ParameterListIterator(params.iterator()), descriptor);
    }
    
    
    public ErrorDescription parse(ServiceContext context, CompositeMap input,
            IParameterIterator params, IExceptionDescriptor descriptor)
    {
        if (descriptor == null)
            descriptor = DefaultExceptionDescriptor.getInstance();
        ErrorDescription error_desc = prepareErrorDescription(input);
        boolean success = true;
        int id=0;
        while(params.hasNext()) {
            IParameter param = params.next();
            success = success && parseSingleParameter(context, input, param, descriptor, error_desc);
            id++;
        }
        if(!success){
            error_desc.addTo(input);
            return error_desc;
        }else
            return null;
    }    
    
    */
    
    void parseSingleParameter( CompositeMap input, IParameter param )
        throws FieldValidationException
    {
            String datatype = param.getDataType();
            if(datatype==null) datatype = "java.lang.String";
            DataType dt = registry.getDataType(datatype);
            if(dt==null)
                throw new IllegalArgumentException("can't find data type "+param.getDataType()+" in parameter "+param.getName());
            Object value = input.get(param.getName());
            if(value==null){
                value =  param.getDefaultValue();
                if(value==null&&param.isRequired())
                    throw new ParameterNullException(param.getName());
            }
            Object parsed_value = null;
            if( value != null){ 
                if( !dt.getJavaType().isAssignableFrom(value.getClass())){
                    try{
                        parsed_value = dt.convert(value);
                    }catch(Exception ex){
                        throw new DatatypeMismatchException(dt.getJavaType(), param.getName(), value, ex);
                    }
                    input.put(param.getName(), parsed_value);
                }
            }
                    
    }
    
    public List parse( CompositeMap input, IParameterIterator params )
    {
        List    errors = null;
        while(params.hasNext()) {
            IParameter param = params.next();
            try{
                parseSingleParameter( input, param );
            } catch(Exception ex){
                if(errors==null) errors = new LinkedList();
                errors.add(ex);
            }
        }
        return errors;
    }
    
    public List parse( CompositeMap input, List param_iterator_list ){
        Iterator it = param_iterator_list.iterator();
        List errors = null;
        while(it.hasNext()){
            IParameterIterator params = (IParameterIterator)it.next();
            List lst = parse( input, params );
            if(lst!=null){
                if(errors==null)
                    errors = lst;
                else
                    errors.addAll(lst);
            }
        }
        return errors;
    }
}
