/*
 * Created on 2010-5-26 上午10:53:53
 * $Id$
 */
package aurora.database.sql;

import aurora.database.DatabaseConstant;

public class CompositeStatement extends AbstractStatement {

    public CompositeStatement() {
        super(DatabaseConstant.TYPE_COMPOSITE_STATEMENT);
    }

}
