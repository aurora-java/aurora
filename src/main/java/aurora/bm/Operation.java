/*
 * Created on 2010-5-25 上午11:23:16
 * $Id$
 */
package aurora.bm;

import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.composite.QualifiedName;
import uncertain.core.ConfigurationError;
import aurora.application.AuroraApplication;

public class Operation extends DynamicObject {

    public static final QualifiedName UPDATE_SQL = new QualifiedName(
            AuroraApplication.AURORA_BUSINESS_MODEL_NAMESPACE, "update-sql");

    public static final QualifiedName QUERY_SQL = new QualifiedName(
            AuroraApplication.AURORA_BUSINESS_MODEL_NAMESPACE, "query-sql");

    public static final String KEY_PARAMETERS = "parameters";
    public static final String KEY_NAME = "name";

    public static final String EXECUTE = "execute";

    public static final String DELETE = "delete";

    public static final String UPDATE = "update";

    public static final String INSERT = "insert";
    
    public static final String QUERY = "query";    

    String name;
    boolean mIsQuery = false;
    String mSql;



    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        putString(KEY_NAME, name);
    }

    /**
     * @return A List containing parameter config in CompositeMap
     */
    public List getParameters() {
        CompositeMap params = object_context.getChild(KEY_PARAMETERS);
        return params == null ? null : params.getChilds();
    }

    public boolean isQuery() {
        return mIsQuery;
    }

    public String getSql() {
        return mSql;
    }

    protected void prepare() {
        Iterator it = object_context.getChildIterator();
        if (it != null)
            while (it.hasNext()) {
                CompositeMap item = (CompositeMap) it.next();
                QualifiedName qname = item.getQName();
                if (UPDATE_SQL.equals(qname)) {
                    if (mSql != null)
                        throw new ConfigurationError("duplicate sql statement:"
                                + item.toXML());
                    mIsQuery = false;
                    mSql = item.getText();
                } else if (QUERY_SQL.equals(qname)) {
                    if (mSql != null)
                        throw new ConfigurationError("duplicate sql statement:"
                                + item.toXML());
                    mIsQuery = true;
                    mSql = item.getText();
                }
                /*
                 * else throw new
                 * ConfigurationError("Unknown operation:"+item.toXML());
                 */
            }
    }

    public DynamicObject initialize(CompositeMap context) {
        super.initialize(context);
        prepare();
        return this;
    }

    public static Operation createOperation(CompositeMap config) {
        Operation op = new Operation();
        op.initialize(config);
        return op;
    }

}
