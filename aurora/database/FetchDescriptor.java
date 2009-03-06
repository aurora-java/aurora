/*
 * Created on 2007-12-28
 */
package aurora.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import uncertain.composite.CompositeMap;

public class FetchDescriptor {
    
    public static final int DEFAULT_PAGE_SIZE = 50;
    public static final String DEFAULT_PAGENUM_PARAM = "pagenum";
    public static final String DEFAULT_PAGESIZE_PARAM = "pagesize";
    public static final String DEFAULT_RECORD_PARAM = "recordnum";
    
    boolean         fetchAll = true;
    int             offSet  = 0;
    int             pageSize = DEFAULT_PAGE_SIZE;
    int             pageNum  = 1;
    boolean         rsScrollable = false;
    
    static FetchDescriptor default_instance = new FetchDescriptor();
    
    static Number getNumber(CompositeMap param, String key){
        Object obj = param.get(key);
        if(obj instanceof Number)
            return (Number)obj;
        if(obj instanceof String)
            try{
                return new Integer(Integer.parseInt((String)obj));
            }catch(NumberFormatException ex){
                return null;
            }
        return null;
    }

    public static FetchDescriptor createInstance( int offset, int pageSize){
        FetchDescriptor desc = new FetchDescriptor();
        desc.offSet = offset;
        desc.pageSize = pageSize;
        desc.fetchAll = false;
        return desc;
    }
    
    public static FetchDescriptor createFromParameter( 
            CompositeMap    params, 
            String          page_num_param,
            String          page_size_param,
            String          recordnum_param){
        FetchDescriptor desc = new FetchDescriptor();
        Number pagesize = getNumber(params,page_size_param);
        if(pagesize!=null) desc.setPageSize( pagesize.intValue());
        Number pagenum = getNumber(params,page_num_param);
        if(pagenum!=null){
            desc.fetchAll = false;
            desc.setPageNum(pagenum.intValue());
        }else{
            Number offset = getNumber(params,recordnum_param);
            if(offset!=null) {
                desc.fetchAll = false;
                desc.offSet = offset.intValue();
            }
        }        
        if(desc.offSet<0) desc.offSet = 0;
        return desc;
    }
    
    public static FetchDescriptor createFromParameter( CompositeMap params ) {
        return createFromParameter(params, DEFAULT_PAGENUM_PARAM, DEFAULT_PAGESIZE_PARAM, DEFAULT_RECORD_PARAM);
    }
    
    public static FetchDescriptor getDefaultInstance(){
        return default_instance;
    }
    
    public static FetchDescriptor fetchAll(){
        return default_instance;
    }
    
    public FetchDescriptor(){
        
    }
    
    void calOffset(){
        offSet = (pageNum-1) * pageSize;
    }
    
    public FetchDescriptor(int offSet, int pageSize){
        this.offSet = offSet;
        this.pageSize = pageSize;
        fetchAll = false;
    }
    
    /**
     * @return the fetchAll
     */
    public boolean getFetchAll() {
        return fetchAll;
    }
    /**
     * @param fetchAll the fetchAll to set
     */
    public void setFetchAll(boolean fetchAll) {
        this.fetchAll = fetchAll;
    }
    /**
     * @return the offSet
     */
    public int getOffSet() {
        return offSet;
    }
    /**
     * @param offSet the offSet to set
     */
    public void setOffSet(int offSet) {
        this.offSet = offSet;
    }
    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }
    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(int pageSize) {
        if(pageSize<0) pageSize = DEFAULT_PAGE_SIZE;
        this.pageSize = pageSize;
        calOffset();
    }
    /**
     * @return the resultSetScrollable
     */
    public boolean getResultSetScrollable() {
        return rsScrollable;
    }
    /**
     * @param resultSetScrollable the resultSetScrollable to set
     */
    public void setResultSetScrollable(boolean rsScrollable) {
        this.rsScrollable = rsScrollable;
    }
    
    /**
     * 
     * @param rs
     * @return whether ResultSet has more rows
     * @throws SQLException
     */
    public boolean locate( ResultSet rs)
        throws SQLException
    {
        if(rsScrollable)
            return rs.absolute(offSet);
        else{
            for(int i=0; i<=offSet; i++){                
                if(!rs.next())
                    return false;
            }
            return true;
        }
            
    }

    /**
     * @return the pageNum
     */
    public int getPageNum() {
        return pageNum;
    }

    /**
     * @param pageNum the pageNum to set
     */
    public void setPageNum(int pageNum) {
        if(pageNum<1) pageNum=1;
        this.pageNum = pageNum;
        calOffset();
    }

}
