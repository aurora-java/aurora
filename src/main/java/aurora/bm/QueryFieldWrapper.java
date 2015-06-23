/*
 * Created on 2008-6-19
 */
package aurora.bm;

import aurora.service.validation.IParameter;

public class QueryFieldWrapper implements IParameter {
    
    Field field;
    
    public QueryFieldWrapper( Field field){
        this.field = field;
    }

    public String getDataType() {
        return field.getDataType();
    }

    public Object getDefaultValue() {
        return field.getDefaultValue();
    }

    public String getName() {
        return field.getName();
    }

    public String getInputPath() {
        return field.getInputPath();
    }

    public boolean isRequired() {
        return false;
    }

}
