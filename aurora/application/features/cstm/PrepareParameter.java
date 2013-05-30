package aurora.application.features.cstm;

import uncertain.datatype.DataType;

public class PrepareParameter {

	private DataType dataType;
	private Object value;

	public PrepareParameter() {

	}
	public PrepareParameter(DataType dataType, Object value) {
		this.dataType = dataType;
		this.value = value;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
