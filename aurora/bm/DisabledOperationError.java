/*
 * Created on 2010-12-10 上午11:03:10
 * $Id$
 */
package aurora.bm;

/**
 * Thrown when user requires an BusinessModel operation that is not enabled 
 */
public class DisabledOperationError extends Error {

    public DisabledOperationError() {
    }

    public DisabledOperationError(String message) {
        super(message);
    }

    public DisabledOperationError(Throwable cause) {
        super(cause);
    }

    public DisabledOperationError(String message, Throwable cause) {
        super(message, cause);
    }

}
