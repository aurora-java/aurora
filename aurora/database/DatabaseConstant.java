/*
 * Created on 2008-4-2
 */
package aurora.database;

public final class DatabaseConstant {

    /** Statement type constants */
    public static final String TYPE_INSERT = "insert";
    public static final String TYPE_UPDATE = "update";
    public static final String TYPE_SELECT = "select";
    public static final String TYPE_DELETE = "delete";
    public static final String TYPE_INSERT_SELECT = "insert_select";
    public static final String TYPE_MERGE = "merge";
    public static final String TYPE_LOCK_TABLE = "lock_table";
    public static final String TYPE_PROCEDURE = "procedure_call";
    public static final String TYPE_RAW_SQL = "raw_sql";
    public static final String TYPE_COMPOSITE_STATEMENT = "composite_statement";
    /** Part of statement fragment */
    public static final int PART_FIELDS    = 0;
    public static final int PART_FROM    = 1;
    public static final int PART_WHERE  = 2;
    public static final int PART_JOINS = 3;
    public static final int PART_GROUP_BY  = 4;
    public static final int PART_HAVING    = 5;
    public static final int PART_ORDER_BY  = 6;
    /** Order by */
    public static final String ASCENT = "ASC";
    public static final String DESCENT = "DESC";
    /** logging */
    public static final String AURORA_DATABASE_LOGGING_TOPIC = "aurora.database";

}
