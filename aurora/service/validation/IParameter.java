/*
 * Created on 2008-6-18
 */
package aurora.service.validation;

public interface IParameter {

    public String getName();
    
    public String getInputPath();
    
    public boolean isRequired();
    
    public String getDataType();
    
    public Object getDefaultValue();
}
