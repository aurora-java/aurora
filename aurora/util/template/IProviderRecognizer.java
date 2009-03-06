/*
 * Created on 2007-8-23
 */
package aurora.util.template;

/**
 * Determine whether a IDynamicContent is suitable for specified IContentProvider
 * @author Zhou Fan
 *
 */
public interface IProviderRecognizer {
    
    public boolean accepts( IDynamicContent tag );
    
    public IContentProvider getProvider();

}
