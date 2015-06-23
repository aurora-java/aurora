/*
 * Created on 2010-3-31 下午01:26:41
 * Author: Zhou Fan
 */
package aurora.database.actions;

import uncertain.proc.ProcedureRunner;
import aurora.database.service.DatabaseServiceFactory;

public class ModelInsert extends AbstractModelAction {

    public ModelInsert(DatabaseServiceFactory svcFactory) {
        super(svcFactory);
    }

    public void run(ProcedureRunner runner) throws Exception {
        prepareRun(runner.getContext());
        mService.insert(null);
    }

}
