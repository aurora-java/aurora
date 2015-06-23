/*
 * Created on 2010-5-11 下午04:34:49
 * $Id$
 */
package aurora.database.local.oracle;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uncertain.core.ConfigurationError;
import aurora.bm.BusinessModel;
import aurora.bm.Field;
import aurora.database.local.oracle.sql.OracleInsertStatement;
import aurora.database.local.oracle.sql.ReturningIntoStatement;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.InsertStatement;
import aurora.service.validation.Parameter;

/**
 * Implements sequence generated primary key feature
 * For one primary key table:
 * <sequence-pk sequenceName="some_sequence_s" />
 * or, simpley:
 * <sequence-pk /> to use default sequence name convention: "table_name" + "_S"
 * 
 * For multiple primary key table:
 * <sequence-pk>
 *  <sequence-use autoReturn="true" sequenceName="some_sequence1_s" fieldName="pk_field1" />
 *  <sequence-use autoReturn="true" sequenceName="some_sequence2_s" fieldName="pk_field2" />  
 * </sequence-pk>
 */
public class SequencePk {
    
    String              sequenceName;
    boolean             autoReturn = true;
    List                sequences = new LinkedList();
    
    public SequencePk(){
        
    }
    
    /**
     *  prepare sequence use list
     */
    public void onPrepareBusinessModel( BusinessModel model ){
        if(sequences.size()==0){
            Field[] fields = model.getPrimaryKeyFields();
            
            if(fields.length==1){
                //only one pk, use sequence name defined in head
                SequenceUse su = new SequenceUse();
                su.setAutoReturn(autoReturn);
                su.setFieldName(fields[0].getPhysicalName());
                if(sequenceName!=null)
                    su.setSequenceName(sequenceName);
                else{
                    su.setSequenceName(model.getBaseTable()+"_S");
                }
                addSequenceUse(su);
            }else{
                for(int i=0; i<fields.length; i++){
                    Field fld = fields[i];
                    SequenceUse su = new SequenceUse();
                    su.setAutoReturn(autoReturn);
                    su.setSequenceName(fld.getPhysicalName()+"_S");
                    su.setFieldName(fld.getPhysicalName());
                    addSequenceUse(su);
                }
            }
        }
        // set pk fields's insert expression to sequence_name.nextval
        for(Iterator it = sequences.iterator(); it.hasNext(); ){
            SequenceUse su = (SequenceUse)it.next();
            Field fld = model.getField(su.getFieldName());
            if(fld==null)
                throw new ConfigurationError("Sequence generated field "+su.getFieldName()+" is not found");
            if(fld.getString(Field.KEY_INSERT_EXPRESSION)==null){
                fld.setInsertExpression(su.getSequenceName()+".NEXTVAL");
                //System.out.println("Setting "+fld.getObjectContext().toXML());
            }
        }
    }
    
    /**
     * Add returning into clause for insert statement
     */
    public void postPopulateInsertStatement(BusinessModelServiceContext bmsc, ISqlStatement stmt,BusinessModel bm){
        if(!(stmt instanceof InsertStatement) )
            return;
        if(sequences==null)
            return;
        if(sequences.size()==0)
            return;
        OracleInsertStatement ois = new OracleInsertStatement((InsertStatement)stmt);
        ReturningIntoStatement ris = new ReturningIntoStatement();
        for(Iterator it = sequences.iterator(); it.hasNext();){
            SequenceUse su = (SequenceUse)it.next();
            if(!su.getAutoReturn()) continue;
            String field_name = su.getFieldName();
            Field fld = bm.getField(field_name);  
            ris.addField(field_name, Field.defaultParamExpression(fld.getParameterPath()) );
            Parameter param = new Parameter();
            param.setDataType(fld.getDataType());
            param.setInput(false);
            param.setOutput(true);
            param.setOutputPath(fld.getParameterPath());
            param.setName(fld.getName());
            ois.addParameter(param);
        }
        if(ris.getFields().size()>0){
            ois.setReturningInto(ris);
            bmsc.setStatement(ois);
        }
    }
    
    public void addSequenceUse( SequenceUse su ){
        sequences.add(su);
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public boolean getAutoReturn() {
        return autoReturn;
    }

    public void setAutoReturn(boolean autoReturn) {
        this.autoReturn = autoReturn;
    }

    public List getSequences() {
        return sequences;
    }

    public void setSequences(List sequences) {
        this.sequences = sequences;
    }

}
