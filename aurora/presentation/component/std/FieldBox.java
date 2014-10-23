package aurora.presentation.component.std;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.FieldBoxColumnConfig;
import aurora.presentation.component.std.config.FieldBoxConfig;
import aurora.presentation.component.std.config.FormConfig;
import aurora.presentation.component.std.config.GridLayoutConfig;

public class FieldBox extends Form {

	public static final String VERSION = "$Revision$";

	private static final String DEFAULT_HEAD_CLASS = "fieldbox_head";
	private static final String DEFAULT_BODY_CLASS = "fieldbox_body";
	protected static final String TITLE_CLASS = "fieldbox_layout-title";
	private CompositeMap fieldBoxColumns;

	public FieldBox(IObjectRegistry registry) {
		super(registry);
	}

	protected String getHeadClass() {
		return DEFAULT_HEAD_CLASS;
	}

	protected String getBodyClass() {
		return DEFAULT_BODY_CLASS;
	}

	protected String getClassName(BuildSession session, CompositeMap model,
			CompositeMap view) throws Exception {
		String cls = view.getString(ComponentConfig.PROPERTITY_CLASSNAME, "");
		String className = DEFAULT_TABLE_CLASS + " layout-fieldbox";
		String title = view.getString(FormConfig.PROPERTITY_TITLE, "");
		title = session.getLocalizedPrompt(uncertain.composite.TextParser.parse(title,model));
		if(!"".equals(title)) className += " " + TITLE_CLASS;
		className += " " + cls;
		return className;
	}

	public void buildView(BuildSession session, ViewContext view_context)
			throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		CompositeMap model = view_context.getModel();
		FieldBoxConfig fbc = FieldBoxConfig.getInstance(view);
		Integer fieldHeight = fbc.getFieldHeight(model);
		String fontStyle = fbc.getFontStyle(model);
		int padding = fbc.getPadding(model, FieldBoxConfig.DEFAULT_PADDING);
		fieldBoxColumns = view.getChild(FieldBoxConfig.PROPERTITY_FIELDBOX_COLUMNS);
		if(null != fontStyle){
			view.putString(FieldBoxConfig.PROPERTITY_STYLE, fontStyle+";"+fbc.getStyle());
		}
		if (null != fieldBoxColumns) {
			view.removeChild(fieldBoxColumns);
			List childs = fieldBoxColumns.getChilds();
			if (null != childs) {
				int length = childs.size();
				LinkedList[] list = new LinkedList[length];
				for (int i = 0; i < length; i++) {
					list[i] = new LinkedList();
				}
				Iterator it = childs.iterator();
				int i = 0, n = 0;
				while (it.hasNext()) {
					CompositeMap column = (CompositeMap) it.next();
					FieldBoxColumnConfig fbcc = FieldBoxColumnConfig.getInstance(column);
					Iterator fieldList = column.getChildIterator();
					Integer columnFieldHeight = FieldBoxColumnConfig.getInstance(column).getFieldHeight(model);
					if(null == columnFieldHeight){
						columnFieldHeight = fieldHeight;
					}
					if(null != fieldList){
						while (fieldList.hasNext()) {
							CompositeMap field = (CompositeMap) fieldList.next();
							String height = field.getString(FieldBoxConfig.PROPERTITY_HEIGHT);
							if("fieldGroup".equals(field.getName())){
								field.putInt(FieldBoxConfig.PROPERTITY_PADDING, padding);
							}
							String fieldFontStyle = TextParser.parse(field.getString(FieldBoxConfig.PROPERTITY_FONT_STYLE),model);
							if(null != fontStyle && null == fieldFontStyle){
								field.putString(FieldBoxConfig.PROPERTITY_FONT_STYLE, fontStyle);
							}
							if(null!= columnFieldHeight && null == height){
								field.putInt(FieldBoxConfig.PROPERTITY_HEIGHT, columnFieldHeight.intValue());
								field.putInt(FieldBoxConfig.PROPERTITY_FIELD_HEIGHT, columnFieldHeight.intValue());
							}
							list[i].add(field);
						}
						i++;
					}else if("".equals(fbcc.getTitle())){
						length--;
					}else{
						i++;
					}
				}
				
				boolean isEmpty = true;
				boolean emptyLine = true;
				int totalspan = 0;
				for (i = 0; i < length;isEmpty=true) {
					CompositeMap item = null;
					if (n < list[i].size()) {
						item = (CompositeMap) list[i].get(n);
						if(null !=item){
							int colspan = item.getInt(
									GridLayoutConfig.PROPERTITY_COLSPAN, 1);
							int rowspan = item.getInt(
									GridLayoutConfig.PROPERTITY_ROWSPAN, 1);
							totalspan += colspan*rowspan - 1;
							for (int j = 0; j < rowspan; j++) {
								for (int k = 0; k < colspan; k++) {
									if (j != 0 || k != 0) {
										if(n+j <= list[i + k].size()){
											list[i + k].add(n+j,null);
										}
									}
								}
							}
						}
						isEmpty = false;
						emptyLine = false;
					}
					if(null != item){
						view.addChild(item);
					}else if(isEmpty){
						view.addChild(new CompositeMap("span"));
					}
					i++;
					if(!emptyLine && i == length){
						i=0;
						n++;
						emptyLine=true;
					}
				}
				view.putInt(GridLayoutConfig.PROPERTITY_COLUMN, length);
				List children = view.getChilds();
				int len = children.size();
				length += (totalspan + len)%length;
				for(int m=1;m<=length;m++){
					children.remove(len-m);
				}
			}
		}
		super.buildView(session, view_context);
	}

	protected void afterBuildTop(BuildSession session, CompositeMap model,
			CompositeMap view, int columns) throws Exception {
		super.afterBuildTop(session, model, view, columns);
		Writer out = session.getWriter();
		FieldBoxConfig fbc = FieldBoxConfig.getInstance(view);
		if (null != fieldBoxColumns) {
			out.write("<tr height='0'>");
			Iterator it = fieldBoxColumns.getChildIterator();
			StringBuffer title_buff = new StringBuffer("<tr>");
			boolean hasTitle = false;
			while (it.hasNext()) {
				CompositeMap column = (CompositeMap) it.next();
				FieldBoxColumnConfig fbcc = FieldBoxColumnConfig
						.getInstance(column);
				if(null !=column.getChildIterator() || !"".equals(fbcc.getTitle())){
					Integer fieldWidth = fbcc.getFieldWidth(model) ==null? fbc
							.getFieldWidth(model) : fbcc.getFieldWidth(model);
					int labelWidth = fbcc.getLabelWidth(model) == null ? fbc
							.getLabelWidth(model) : fbcc.getLabelWidth(model).intValue();
					out.write("<th width='"+labelWidth+"'></th><td");
					if(null != fieldWidth){
						out.write(" width='"+fieldWidth+"'");
					}
					out.write("></td>");
					String title = fbcc.getTitle();
					if(!"".equals(title)){
						hasTitle = true;
						title_buff.append("<th colspan='2' class='fieldbox_column_head'><div>"+title+"</div></th>");
					}else{
						title_buff.append("<th colspan='2' class='fieldbox_column_head'>"+title+"</th>");
					}
				}
			}
			if(hasTitle){
				out.write(title_buff.append("</tr>").toString());
			}
			out.write("</tr>");
		}
		out.write("<tr height='0'><td colspan=" + columns * 2 + "></td></tr>");
	}
}
