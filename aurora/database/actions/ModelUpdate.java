/*
 * Created on 2008-6-11
 */
package aurora.database.actions;

import uncertain.proc.ProcedureRunner;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.ServiceOption;

public class ModelUpdate extends AbstractModelAction {

    public ModelUpdate( DatabaseServiceFactory  svcFactory) {
        super(svcFactory);
    }
    
    


    public void run(ProcedureRunner runner) throws Exception {
        prepareRun(runner.getContext());
        mService.updateByPK(null);
    }




    protected void prepareServiceOption(ServiceOption option) {
        transferServiceOption(option, ServiceOption.KEY_UPDATE_PASSED_FIELD_ONLY);
    }

}
