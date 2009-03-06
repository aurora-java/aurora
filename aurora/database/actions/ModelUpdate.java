/*
 * Created on 2008-6-11
 */
package aurora.database.actions;

import uncertain.proc.ProcedureRunner;
import aurora.database.service.DatabaseServiceFactory;

public class ModelUpdate extends AbstractModelAction {

    public ModelUpdate( DatabaseServiceFactory  svcFactory) {
        super(svcFactory);
    }
    
    /*
     * 
implements IFeature
    public int attachTo( CompositeMap map, Configuration config) {
        String name = map.getName();
        Action = name.substring(name.indexOf('-')+1);
        System.out.println("action is:"+Action);
        return IFeature.NORMAL;
    }
    */

    public void run(ProcedureRunner runner) throws Exception {
        prepareRun(runner);
        service.updateByPK(null);
    }

}
