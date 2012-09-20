package aurora.bm;

import java.util.HashSet;

public class RelationFields {
	public Relation relation;
	public HashSet<Field> fieldSet = new HashSet<Field>();
	
	public RelationFields(Relation relation){
		this.relation = relation;
	}
	
	public Relation getRelation() {
		return relation;
	}
	public void setRelation(Relation relation) {
		this.relation = relation;
	}
	
	public void addField(Field field){
		if(field != null)
			fieldSet.add(field);
	}
	
	public void removeField(Field field){
		if(field != null)
			fieldSet.remove(field);
	}
	
	public HashSet<Field> getFieldSet() {
		return fieldSet;
	}
	public void setFieldSet(HashSet<Field> fieldSet) {
		this.fieldSet = fieldSet;
	}
}
