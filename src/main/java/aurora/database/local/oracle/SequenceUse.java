/*
 * Created on 2010-5-11 下午04:36:58
 * $Id$
 */
package aurora.database.local.oracle;

public class SequenceUse {
    
    String      fieldName;
    String      sequence;
    boolean     autoReturn = true;
    
    /** name of sequence that will generated for specified field */
    public String getSequenceName() {
        return sequence;
    }
    
    public void setSequenceName(String sequence) {
        this.sequence = sequence;
    }
    
    /** whether generated sequence value will be automatically returned into parameter
     * (using returning into clause)
     */
    public boolean getAutoReturn() {
        return autoReturn;
    }
    
    public void setAutoReturn(boolean autoReturn) {
        this.autoReturn = autoReturn;
    }
    
    /** name of field that uses specified sequence */
    public String getFieldName() {
        return fieldName;
    }
    
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public String toString(){
        return "[sequence-use]"+sequence+" -> "+fieldName;
    }

}
