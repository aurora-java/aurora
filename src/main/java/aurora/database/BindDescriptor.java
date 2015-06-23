/*
 * Created on 2008-5-27
 */
package aurora.database;

public class BindDescriptor {
    
    public static final String COLUMN_SEPARATOR = "\t";
    
    String      accessPath;
    Object      value;
    int         index;
    boolean     isOutput;
    String      databaseType;
    
    public BindDescriptor(){
        
    }
    
    public String toString(){
        StringBuffer buf = new StringBuffer();
        buf.append("No.").append(index).append(COLUMN_SEPARATOR);
        buf.append("Access path:").append(accessPath).append(COLUMN_SEPARATOR);
        buf.append("Data type of passed value :");
        if(value!=null)
            buf.append(value.getClass().getName());
        else
            buf.append("[null]");
        buf.append(COLUMN_SEPARATOR);
        buf.append("Value:").append(value).append(COLUMN_SEPARATOR);
        buf.append("Output:").append(isOutput).append(COLUMN_SEPARATOR);
        buf.append("Database Type:").append(databaseType).append(COLUMN_SEPARATOR);
        return buf.toString();
    }
    
    /**
     * @param accessPath
     * @param value
     * @param index
     */
    public BindDescriptor(String accessPath, Object value, int index) {
        super();
        this.accessPath = accessPath;
        this.value = value;
        this.index = index;
    }
    /**
     * @return the accessPath
     */
    public String getAccessPath() {
        return accessPath;
    }
    /**
     * @param accessPath the accessPath to set
     */
    public void setAccessPath(String accessPath) {
        this.accessPath = accessPath;
    }
    /**
     * @return the databaseType
     */
    public String getDatabaseType() {
        return databaseType;
    }
    /**
     * @param databaseType the databaseType to set
     */
    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }
    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }
    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }
    /**
     * @return the isOutput
     */
    public boolean isOutput() {
        return isOutput;
    }
    /**
     * @param isOutput the isOutput to set
     */
    public void setOutput(boolean isOutput) {
        this.isOutput = isOutput;
    }
    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }
    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

}
