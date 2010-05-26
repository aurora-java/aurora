/*
 * Created on 2010-5-26 上午10:53:53
 * $Id$
 */
package aurora.database.sql;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import aurora.database.DatabaseConstant;
import aurora.service.validation.Parameter;

public class CompositeStatement extends AbstractStatement {
    
    // List<ISqlStatement>
    List        childs;

    public CompositeStatement() {
        super(DatabaseConstant.TYPE_COMPOSITE_STATEMENT);
        childs = new LinkedList();
    }
    
    public void addStatement( ISqlStatement statement ){
        childs.add(statement);
    }
    
    public List getStatements(){
        return childs;
    }

    private void addParameters( Collection params, Map param_map ){
        if(params==null)
            return;
        Iterator it = params.iterator();
        while(it.hasNext()){
            Parameter param = (Parameter)it.next();
            String name = param.getName();
            if(name==null)
                name = param.getInput()?param.getInputPath():param.getOutputPath();
            param_map.put(name, param);
        }
    }

    /** add all child statement's parameters altogether and return merged parameters */
    public Collection getParameters() {
        //aurora.service.validation.Parameter
        HashMap param_map = new HashMap();
        addParameters( parameters, param_map);
        Iterator it = childs.iterator();
        while(it.hasNext()){
            Object obj = it.next();
            if(obj instanceof IStatementWithParameter){
                IStatementWithParameter stmt = (IStatementWithParameter)obj;
                addParameters( stmt.getParameters(), param_map);
            }
        }
        return param_map.values();
    }    
}
