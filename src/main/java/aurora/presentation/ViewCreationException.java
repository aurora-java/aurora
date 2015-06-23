/*
 * Created on 2007-8-4
 */
package aurora.presentation;

import uncertain.composite.CompositeMap;
import uncertain.exception.BaseException;
import uncertain.util.resource.ILocatable;
import uncertain.util.resource.Location;

/**
 * Exception when creating a view
 * ViewCreationException
 * @author Zhou Fan
 *
 */
public class ViewCreationException extends BaseException {


    public ViewCreationException(String message) {
        super(message);
    }

    public ViewCreationException(Throwable cause) {
        super(cause);
    }

    public ViewCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param code
     * @param args
     * @param location
     */
    public ViewCreationException(String code, Object[] args, ILocatable location) {
        super(code, args, location);
    }

    /**
     * @param code
     * @param args
     * @param cause
     * @param config
     */
    public ViewCreationException(String code, Object[] args, Throwable cause,
            CompositeMap config) {
        super(code, args, cause, config);
    }

    /**
     * @param code
     * @param args
     * @param cause
     * @param location
     */
    public ViewCreationException(String code, Object[] args, Throwable cause,
            ILocatable location) {
        super(code, args, cause, location);
    }

    /**
     * @param code
     * @param args
     * @param cause
     * @param source
     * @param location
     */
    public ViewCreationException(String code, Object[] args, Throwable cause,
            String source, Location location) {
        super(code, args, cause, source, location);
    }
    
    

}
