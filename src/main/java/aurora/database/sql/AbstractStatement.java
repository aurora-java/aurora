/*
 * Created on 2008-3-28
 */
package aurora.database.sql;

import java.util.Collection;
import java.util.LinkedList;

import aurora.service.validation.Parameter;

public abstract class AbstractStatement implements ISqlStatement,
        IStatementWithParameter {

    ISqlStatement parent;
    String type;
    Collection parameters;

    public AbstractStatement(String type) {
        setType(type);
    }

    public String getType() {
        return type;
    }

    protected void setType(String type) {
        this.type = type;
    }

    public ISqlStatement getParent() {
        return parent;
    }

    public void setParent(ISqlStatement parent) {
        this.parent = parent;
    }

    /*
     * public String toSql( ISqlBuilder creator ){ if( this instanceof
     * ISimpleSqlText) return toString(); else return creator.createSql(this); }
     */

    public Collection getParameters() {
        return parameters;
    }

    public void setParameters(Collection parameters) {
        this.parameters = parameters;
    }

    public void addParameter(Parameter param) {
        if (parameters == null)
            parameters = new LinkedList();
        parameters.add(param);
    }

}
