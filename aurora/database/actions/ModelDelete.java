/*
 * Created on 2010-3-31 下午01:26:41
 * $Id$
 */
package aurora.database.actions;

import uncertain.proc.ProcedureRunner;
import aurora.database.service.DatabaseServiceFactory;

public class ModelDelete extends AbstractModelAction {

    public ModelDelete(DatabaseServiceFactory svcFactory) {
        super(svcFactory);
    }

    public void run(ProcedureRunner runner) throws Exception {
        prepareRun(runner.getContext());
        mService.deleteByPK(null);
    }

}
