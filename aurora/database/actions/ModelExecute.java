/*
 * Created on 2008-6-11
 */
package aurora.database.actions;

import uncertain.proc.ProcedureRunner;
import aurora.database.service.DatabaseServiceFactory;

public class ModelExecute extends AbstractModelAction {
    
    String  operation;

    public ModelExecute( DatabaseServiceFactory  svcFactory) {
        super(svcFactory);
    }

    public void run(ProcedureRunner runner) throws Exception {
        prepareRun(runner.getContext());
        String op = operation==null?"execute":operation;
        mService.executeDml(null, op);
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

}
