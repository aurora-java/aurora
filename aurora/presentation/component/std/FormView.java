package aurora.presentation.component.std;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.ISingleton;
import aurora.presentation.BuildSession;
import aurora.presentation.IFreeMarkerTemplateProvider;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.FormViewConfig;
import aurora.presentation.component.std.config.FormViewFieldConfig;
import freemarker.template.Template;

@SuppressWarnings("unchecked")
public class FormView extends Component implements IViewBuilder, ISingleton{
	
	IObjectRegistry mObjectRegistry;

	public FormView(IObjectRegistry rg) {
		this.mObjectRegistry = rg;
	}
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		addStyleSheet(session, context, "form/form.css");
	}
	
	
	@Override
	public void buildView(BuildSession session, ViewContext view_context) throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		CompositeMap model = view_context.getModel();
		FormViewConfig lc = FormViewConfig.getInstance(view);
		CompositeMap dm = (CompositeMap)model.getObject(lc.getDataModel());
		CompositeMap data = new CompositeMap();
		List records = dm.getChilds();
		if(records !=null){
			data = (CompositeMap)records.get(0);
		}
		StringBuffer sb = new StringBuffer();
		generateTitleHead(sb,lc,data);
		generateTable(sb,lc);
		generateFields(sb,lc,data);
		generateFooter(sb,lc);
		Writer out = session.getWriter();
		try {
			out.write(sb.toString());
		} catch (Exception e) {
			throw new ViewCreationException(e);
		}
		
	}
	
	private void generateTitleHead(StringBuffer sb, FormViewConfig lc, CompositeMap model) throws ViewCreationException, IOException{
		String title = lc.getTitle();
		String style = lc.getStyle();
		sb.append("<DIV class='formViewWrap' ");
		if(style !=null){
			sb.append(" style='").append(style).append("'");			
		}
		sb.append(">");
		if(title != null){
			sb.append("<DIV class='title'>").append(title).append("</DIV>");
		}else {
			title = lc.getTitleText();
			if(title != null){
				sb.append("<span class='title'>");
				Reader reader = null;
				Template t = null;
				StringWriter out = null;
				try {
					IFreeMarkerTemplateProvider provider = (IFreeMarkerTemplateProvider) mObjectRegistry.getInstanceOfType(IFreeMarkerTemplateProvider.class);
					reader = new BufferedReader(new StringReader(title));
					t = new Template("title", reader, provider.getFreeMarkerConfiguration(), provider.getDefaultEncoding());
					out = new StringWriter();
					Map p = new HashMap();
					p.put("model", model);
					t.process(p, out);
					out.flush();
					sb.append(out.toString()).append("</span>");
				} catch (Exception e) {
					throw new aurora.presentation.ViewCreationException(e.getMessage());
				} finally {
					if(reader != null) reader.close();
					if(out != null) out.close();
				}
			}
		}
	}
	
	private void generateTable(StringBuffer sb, FormViewConfig lc){
		sb.append("<TABLE cellSpacing='0' cellPadding='0' width='100%' border='0'");
		String defaultClass = "formView";
		String className = lc.getClassName();
		if(className !=null){
			defaultClass += " " + className;
		}
		sb.append(" class='").append(defaultClass).append("'");			
		
		sb.append(">");
	}
	
	
	private void generateFields(StringBuffer sb, FormViewConfig lc, CompositeMap data) throws ViewCreationException, IOException {
		sb.append("<TBODY>");
		List childs = lc.getSections();
		Iterator it = childs.iterator();
		int labelWidth = lc.getPromptWidth();
		int i=0;
		while(it.hasNext()){
			CompositeMap section = (CompositeMap)it.next();
			sb.append("<TR><TD>");
			sb.append("<TABLE cellSpacing='0' cellPadding='0' width='100%' border='0'");
			if(i==0)sb.append(" class='top'");
			if(!it.hasNext())sb.append(" class='bottom'");
			sb.append("><TBODY><TR>");
			Iterator cit = section.getChildIterator();
			while(cit.hasNext()){
				CompositeMap c = (CompositeMap)cit.next();
				FormViewFieldConfig field = FormViewFieldConfig.getInstance(c);
				Integer fw = field.getPromptWidth();
				labelWidth = fw == null ? labelWidth : fw;
				sb.append("<TD align='").append(lc.getPromptAlign()).append("'").append(" class='label' width='").append(labelWidth).append("%'");
				sb.append(">").append(field.getPrompt()).append("</TD>");
				sb.append("<TD class='field' ");
				sb.append("width='").append(field.getWidth()).append("%'>");
				sb.append(processContent(data,c)).append("</TD>");
			}
			sb.append("</TR></TBODY></TABLE>");
			i++;
		}
				
		sb.append("</TBODY>");
	}
	
	private void generateFooter(StringBuffer sb, FormViewConfig lc) {
		sb.append("</TABLE></DIV>");
	}
	
	
	private String processContent(CompositeMap model,CompositeMap c) throws IOException,ViewCreationException{
		String content = c.getText();
		if(content != null){
			Reader reader = null;
			Template t = null;
			StringWriter out = null;
			try {
				IFreeMarkerTemplateProvider provider = (IFreeMarkerTemplateProvider) mObjectRegistry.getInstanceOfType(IFreeMarkerTemplateProvider.class);
				reader = new BufferedReader(new StringReader(content));
				t = new Template(c.getName(), reader, provider.getFreeMarkerConfiguration(), provider.getDefaultEncoding());
				out = new StringWriter();
				Map p = new HashMap();
				p.put("model", model);
				t.process(p, out);
				out.flush();
				return out.toString();
			} catch (Exception e) {
				throw new aurora.presentation.ViewCreationException(e.getMessage());
			} finally {
				if(reader != null) reader.close();
				if(out != null) out.close();
			}
		}else{
			FormViewFieldConfig column = FormViewFieldConfig.getInstance(c);
			return model.getString(column.getName(),"");
		}
	}

	@Override
	public String[] getBuildSteps(ViewContext context) {
		return null;
	}

}
