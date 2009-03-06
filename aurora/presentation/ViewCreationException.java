/*
 * Created on 2007-8-4
 */
package aurora.presentation;

/**
 * Exception when creating a view
 * ViewCreationException
 * @author Zhou Fan
 *
 */
public class ViewCreationException extends Exception {

    public ViewCreationException() {
        super();
    }

    public ViewCreationException(String message) {
        super(message);
    }

    public ViewCreationException(Throwable cause) {
        super(cause);
    }

    public ViewCreationException(String message, Throwable cause) {
        super(message, cause);
    }

}
