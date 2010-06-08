/*
 * Created on 2008-6-19
 */
package aurora.service.validation;

/**
 * Iterates parameter for validation
 */
public interface IParameterIterator {
    
    public boolean hasNext();
    
    public IParameter next();

}
