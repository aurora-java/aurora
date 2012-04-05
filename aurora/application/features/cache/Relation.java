package aurora.application.features.cache;


public class Relation{
	private String relation_id;
	private String pkColumns;
	private String mapColumns;
	private boolean isMultiValue=true;
	public Relation(){
		
	}
	public Relation(String pkColumns,boolean isMultiValue,String mapColumns){
		this.pkColumns = pkColumns;
		this.isMultiValue = isMultiValue;
		this.mapColumns = mapColumns;
	}
	public String getPkColumns() {
		return pkColumns;
	}
	public void setPkColumns(String pkColumns) {
		this.pkColumns = pkColumns;
	}
	public boolean getIsMultiValue() {
		return isMultiValue;
	}
	public void setIsMultiValue(boolean isMultiValue) {
		this.isMultiValue = isMultiValue;
	}
	public String getRelation_id() {
		if(relation_id == null)
			return getMapColumns();
		return relation_id;
	}
	public void setRelation_id(String relation_id) {
		this.relation_id = relation_id;
	}

	public String getMapColumns() {
		if(mapColumns == null)
			return getPkColumns();
		return mapColumns;
	}
	public void setMapColumns(String mapColumns) {
		this.mapColumns = mapColumns;
	}
	
}
