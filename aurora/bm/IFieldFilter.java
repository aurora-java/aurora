/*
 * Created on 2008-5-27
 */
package aurora.bm;

/**
 * Interface to do extra field filting when composing
 * sql statement or generate UI from BusinessModel
 * @author Zhou Fan
 *
 */
public interface IFieldFilter {
    
    /**
     * Whether specified field is acceptable
     */
    public boolean accepts( Field f );

}
