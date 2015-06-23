package aurora.database.actions;

import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class ModelJoin extends AbstractEntry {

	private String models;
	private String valueField;
	private String shareField;
	private String joinField;

	public String getModels() {
		return models;
	}

	public void setModels(String models) {
		this.models = models;
	}

	public String getValueField() {
		return valueField;
	}

	public void setValueField(String valueFileld) {
		this.valueField = valueFileld;
	}

	public String getShareField() {
		return shareField;
	}

	public void setShareField(String shareField) {
		this.shareField = shareField;
	}

	public String getJoinField() {
		return joinField;
	}

	public void setJoinField(String joinField) {
		this.joinField = joinField;
	}

	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		String[] shareFiles = this.getShareField().split(",");
		String[] valueFiles = this.getValueField().split(",");
		String[] modellist =models.split(",");
		if (modellist.length!=2){
			throw new Exception("joinModels number must be two");
		}
		CompositeMap cmrow = (CompositeMap) context.getObject(modellist[0]);
		CompositeMap cmcolumn = (CompositeMap) context
				.getObject(modellist[1]);
		Iterator rows = cmrow.getChildIterator();
		if (rows != null) {
			while (rows.hasNext()) {
				Iterator columns = cmcolumn.getChildIterator();
				CompositeMap row = (CompositeMap) rows.next();
				if (columns == null)
					break;
				else {
					while (columns.hasNext()) {

						CompositeMap column = (CompositeMap) columns.next();
						String[] joinFields = this.getJoinField().split(",");
						for (int i = 0; i < joinFields.length; i++) {
							if (row.getString(joinFields[i]).equals(
									column.getString(joinFields[i]))) {

								for (int j = 0; j < shareFiles.length; j++) {
									row.putString(column
											.getString(shareFiles[j]), column
											.getString(valueFiles[j]));
								}
							}
						}
					}
				}
			}
		}
		CompositeMap model =(CompositeMap)context.getObject("/model");
		model.addChilds(cmrow.getChilds());
		if(cmrow != null){
			model.put("totalCount", cmrow.get("totalCount"));	
		}
	}

}
