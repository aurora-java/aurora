/*
 * Created on 2008-3-29
 */
package aurora.database.sql;

/**
 * Sql statement that can be directly output to sql strings without thinking of database difference 
 * @author Zhou Fan
 */
public interface ISimpleSqlText {
    
    public String toSql();

}
