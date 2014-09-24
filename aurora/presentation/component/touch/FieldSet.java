package aurora.presentation.component.touch;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.ComponentConfig;

@SuppressWarnings("unchecked")
public class FieldSet extends Component implements IViewBuilder {

	private static final String PROPERTITY_TITLE = "title";
	private static final String PROPERTITY_LABEL_WIDTH = "labelwidth";

	public void buildView(BuildSession session, ViewContext view_context)
			throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		CompositeMap model = view_context.getModel();

		/** ID属性 **/
		String id = view.getString(ComponentConfig.PROPERTITY_ID, "");
		if (!"".equals(id)) {
			id = "id=\"" + id + "\"";
		}
		String style = view.getString(ComponentConfig.PROPERTITY_STYLE, "");
		if (!"".equals(style)) {
			style = "style=\"" + style + "\"";
		}
		String title = view.getString(PROPERTITY_TITLE);
		String labelWidth = view.getString(PROPERTITY_LABEL_WIDTH, "40%");
		if (labelWidth.indexOf("%") == -1) {
			labelWidth += "px";
		}
		Iterator it = view.getChildIterator();
		Writer out = session.getWriter();
		out.write("<div " + id + " class='touch-fieldset' " + style + ">");
		out.write("<div class='touch-box-vertical'>");
		if (null != title) {
			out.write("<div class='touch-fieldset-title'>");
			out.write(title);
			out.write("</div>");
		}
		if (null != it) {
			out.write("<div class='touch-fieldset-body'>");
			out.write("<div class='touch-fieldset-body-inner'>");
			while (it.hasNext()) {
				CompositeMap field = (CompositeMap) it.next();
				out.write("<div class='touch-fieldset-field");
				String required = field.getString(InputField.PROPERTITY_REQUIRED, "");
				String disabled = field.getString(InputField.PROPERTITY_DISABLED, "");
				if(!"".equals(disabled)){
					out.write(" touch-field-disabled");
				}
				if(!"".equals(required)){
					out.write(" touch-field-required");
				}
				out.write("'>");
				try {
					String prompt = field.getString(InputField.PROPERTITY_PROMPT, "");
					if (!"".equals(prompt)) {
						out.write("<div class='touch-fieldset-field-label' style='width:"+labelWidth+"'>");
						out.write("<span>"+prompt+"</span>");
						out.write("</div>");
					}
					out.write("<div class='touch-fieldset-field-input-outer'>");
					out.write("<div class='touch-fieldset-field-input'>");
						session.buildView(model, field);
					out.write("</div>");
					out.write("</div>");
				} catch (Exception e) {
					throw new ViewCreationException(e);
				}
				out.write("</div>");
			}
			out.write("</div>");
			out.write("</div>");
		}
		out.write("</div>");
		out.write("</div>");
	}

	@Override
	public String[] getBuildSteps(ViewContext context) {
		// TODO Auto-generated method stub
		return null;
	}
}
