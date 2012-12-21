/*
 * Created on 2012-12-20 下午3:26:59
 * $Id$
 */
package aurora.service.exception;

import java.sql.SQLException;

import uncertain.ocm.IObjectRegistry;
import uncertain.proc.AbstractEntry;
import uncertain.proc.IExceptionHandle;
import uncertain.proc.ProcedureRunner;
import aurora.application.util.LanguageUtil;
import aurora.service.ServiceContext;
import aurora.service.validation.ErrorMessage;

public class SqlErrorMessage extends AbstractEntry implements IExceptionHandle {

    int code;
    String message;
    String translatedCode;

    IObjectRegistry reg;

    public SqlErrorMessage(IObjectRegistry r) {
        this.reg = r;
    }

    public SqlErrorMessage() {

    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTranslatedCode() {
        return translatedCode;
    }

    public void setTranslatedCode(String translatedCode) {
        this.translatedCode = translatedCode;
    }

    @Override
    public boolean handleException(ProcedureRunner runner, Throwable exception) {
        if (!(exception instanceof SQLException))
            return false;
        SQLException sex = (SQLException) exception;
        if (sex.getCause() != null && sex.getCause() instanceof SQLException) {
            sex = (SQLException) sex.getCause();
        }
        int c = sex.getErrorCode();
        if (c != code)
            return false;

        String errorCode = translatedCode == null ? Integer.toString(code)
                : translatedCode;
        String errorMsg = reg == null ? message : LanguageUtil
                .getTranslatedMessage(reg, message, runner.getContext());
        ServiceContext scx = ServiceContext.createServiceContext(runner
                .getContext());
        ErrorMessage msg = new ErrorMessage(errorCode, errorMsg, null);
        scx.setError(msg.getObjectContext());
        scx.putBoolean("success", false);
        return true;
    }
    
    public void run( ProcedureRunner runner ) throws Exception {
        handleException(runner, runner.getException());
    }

}
