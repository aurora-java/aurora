/*
 * Created on 2010-12-10 上午11:03:10
 * $Id$
 */
package aurora.bm;

/**
 * Thrown when user requires an BusinessModel operation that is not enabled 
 */
public class DisabledOperationException extends Exception {

    public DisabledOperationException() {
    }

    public DisabledOperationException(String message) {
        super(message);
    }

    public DisabledOperationException(Throwable cause) {
        super(cause);
    }

    public DisabledOperationException(String message, Throwable cause) {
        super(message, cause);
    }

}
