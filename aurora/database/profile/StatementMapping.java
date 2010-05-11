/*
 * Created on 2010-5-11 下午12:13:28
 * $Id$
 */
package aurora.database.profile;

/**
 * A simple DTO to encapsulate a statement registered in config file
 * StatementMapping
 */
public class StatementMapping {
    

    public StatementMapping(Class statementClass) {
        super();
        this.statementClass = statementClass;
    }

    Class       statementClass;
    
    public StatementMapping(){

    }

    public Class getStatementClass() {
        return statementClass;
    }

    public void setStatementClass(Class statementClass) {
        this.statementClass = statementClass;
    }
}
