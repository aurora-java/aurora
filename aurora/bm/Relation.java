/*
 * Created on 2008-5-8
 */
package aurora.bm;

import java.util.Iterator;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import aurora.database.sql.Join;

public class Relation extends DynamicObject {
    
    public static final String KEY_REF_ALIAS = "refalias";
    public static final String KEY_JOIN_TYPE = "jointype";
    public static final String KEY_NAME = "name";
    public static final String KEY_REFERENCE_MODEL = "refmodel";

    public static Relation getInstance( CompositeMap context ){
        Relation relation = new Relation();
        relation.initialize(context);
        return relation;
    }
    
    /**
     * Full name of referenced model, with represents the relation table
     * @return
     */
    public String getReferenceModel(){
        return getString(KEY_REFERENCE_MODEL);
    }
    
    public void setReferenceModel( String ref){
        put(KEY_REFERENCE_MODEL, ref);
    }
    
    /**
     * Get alias of referenced table, which can be used in join expression
     * if one table is referenced more than once, and can't use just table
     * name to distinguish actual table 
     * @return A String alias 
     */
    public String getReferenceAlias(){
        String str =getString(KEY_REF_ALIAS);
        if(str==null)
            str = getName();
        return str;
    }
    
    public void setReferenceAlias(String alias){
        putString(KEY_REF_ALIAS, alias);
    }
    
    /**
     * Get name of relation. If name property is not set, 'refModel' will be
     * used to identify this relation. If one table is referenced more than 
     * once, each relation must has a unique name.
     * @return name of this relation
     */
    public String getName(){
        String name = getString(KEY_NAME);
        if(name==null) name = getReferenceModel();
        return name;
    }
    
    public void setName(String name){
        putString(KEY_NAME, name);
    }
    
    public String getJoinType(){
        String type = getString(KEY_JOIN_TYPE);
        return type==null?Join.TYPE_INNER_JOIN:type.toUpperCase();
    }
    
    public void setJoinType( String join_type){
        if(!Join.isTypeValid(join_type))
            throw new IllegalArgumentException("Invalid join type:"+join_type);
        putString(KEY_JOIN_TYPE, join_type.toUpperCase());
    }

    public void checkValidation() {
        String type = getJoinType();
        if(!Join.isTypeValid(type))
            throw new IllegalArgumentException("<relation>: Invalid join type:"+type);
        if(getReferenceModel()==null)
            throw new IllegalArgumentException("<relation>: Must set 'reference' property");
    }
    
    public Reference[] getReferences(){
        Iterator it = getObjectContext().getChildIterator();
        if(it==null) return null;
        Reference[] refs = new Reference[getObjectContext().getChilds().size()];
        int i=0;
        while(it.hasNext()){
            Reference ref = Reference.getInstance((CompositeMap)it.next());
            refs[i++] = ref;
        }
        return refs;
    }
    
    public boolean isNeedDatabaseJoin(){
        return getBoolean(BusinessModel.KEY_NEED_DATABASE_JOIN, true);
    }
    
    public void setNeedDatabaseJoin(boolean b){
        putBoolean(BusinessModel.KEY_NEED_DATABASE_JOIN,b);
    }

}
