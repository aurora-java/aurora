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
import aurora.presentation.BuildSession;
import aurora.presentation.IFreeMarkerTemplateProvider;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.FormViewConfig;
import aurora.presentation.component.std.config.FormViewFieldConfig;
import freemarker.template.Template;

@SuppressWarnings("unchecked")
public class FormView extends Component implements IViewBuilder{
	
	public static final String VERSION = "$Revision$";
	
	IObjectRegistry mObjectRegistry;

	public FormView(IObjectRegistry rg) {
		super(rg);
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
		String dataModel = lc.getDataModel();
		CompositeMap dm = new CompositeMap();
		if(dataModel != null) {
			dm = (CompositeMap)model.getObject(lc.getDataModel());
		}
		
		CompositeMap data = new CompositeMap();
		List records = dm.getChilds();
		if(records !=null){
			data = (CompositeMap)records.get(0);
		}
		Writer out = session.getWriter();
		try {
			String vt = lc.getViewType();//view.getString(VIEW_TYPE,DEFAULT_VIEW_TYPE);
			String wu = lc.getWidthUnit();//view.getString(WIDTH_UNIT,DEFAULT_WIDTH_UNIT);
			
			if(FormViewConfig.DEFAULT_VIEW_TYPE.equalsIgnoreCase(vt)) {
				generateADTitleHead(out,lc,data);
				generateADTable(out,lc);
				generateADFields(session,out,lc,data,wu);
				generateADFooter(out,lc);
			}else{
				generateTitleHead(out,lc,data);
				generateTable(out,lc);
				generateFields(session,out,lc,data,wu);
				generateFooter(out,lc);
			}
			
		} catch (Exception e) {
			throw new ViewCreationException(e);
		}
		
	}
	
	private void generateTitleHead(Writer out, FormViewConfig lc, CompositeMap model) throws ViewCreationException, IOException{
		String title = lc.getTitle();
		String style = lc.getStyle();
		out.write("<DIV class='formViewWrap' ");
		if(style !=null){
			out.write(" style='");
			out.write(style);
			out.write("'");			
		}
		out.write(">");
		if(title != null){
			out.write("<DIV class='title'>");
			out.write(title);
			out.write("</DIV>");
		}else {
			title = lc.getTitleText();
			if(title != null){
				out.write("<span class='title'>");
				Reader reader = null;
				Template t = null;
				StringWriter sw = null;
				try {
					IFreeMarkerTemplateProvider provider = (IFreeMarkerTemplateProvider) mObjectRegistry.getInstanceOfType(IFreeMarkerTemplateProvider.class);
					reader = new BufferedReader(new StringReader(title));
					t = new Template("title", reader, provider.getFreeMarkerConfiguration(), provider.getDefaultEncoding());
					sw = new StringWriter();
					Map p = new HashMap();
					p.put("model", model);
					t.process(p, sw);
					sw.flush();
					out.write(sw.toString());
					out.write("</span>");
				} catch (Exception e) {
					throw new aurora.presentation.ViewCreationException(e.getMessage());
				} finally {
					if(reader != null) reader.close();
					if(sw != null) sw.close();
				}
			}
		}
	}
	
	private void generateTable(Writer out, FormViewConfig lc) throws IOException{
		out.write("<TABLE cellSpacing='0' cellPadding='0' width='100%' border='0'");
		String defaultClass = "formView";
		String className = lc.getClassName();
		if(className !=null){
			defaultClass += " " + className;
		}
		String style = lc.getTableStyle();
		if(style != null){
			out.write(" style='");
			out.write(style);
			out.write("'");
		}
		out.write(" class='");
		out.write(defaultClass);
		out.write("'>");
	}
	
	
	private void generateFields(BuildSession session,Writer out, FormViewConfig lc, CompositeMap data,String wu) throws Exception {
		out.write("<TBODY>");
		List childs = lc.getSections();
		Iterator it = childs.iterator();
		int labelWidth = lc.getPromptWidth();
		int i=0;
		while(it.hasNext()){
			CompositeMap section = (CompositeMap)it.next();
			out.write("<TR><TD>");
			out.write("<TABLE cellSpacing='0' cellPadding='0' width='100%' border='0'");
			if(i==0)out.write(" class='top'");
			if(!it.hasNext())out.write(" class='bottom'");
			out.write("><TBODY><TR>");
			Iterator cit = section.getChildIterator();
			while(cit.hasNext()){
				CompositeMap c = (CompositeMap)cit.next();
				FormViewFieldConfig field = FormViewFieldConfig.getInstance(c);
				Integer fw = field.getPromptWidth();
				labelWidth = fw == null ? labelWidth : fw;
				out.write("<TD align='");
				out.write(lc.getPromptAlign());
				out.write("' class='label' width='");
				out.write(""+labelWidth);
				out.write(FormViewConfig.DEFAULT_WIDTH_UNIT.equals(wu) ? "%'" : "'");
				out.write(">");
				out.write(field.getPrompt());
				out.write("</TD>");
				out.write("<TD align='");
				out.write(field.getAlign());
				out.write("' class='field'");
				int w = field.getWidth();
				if(w!=0){
					out.write(" width='");
					out.write(""+w);
					out.write(FormViewConfig.DEFAULT_WIDTH_UNIT.equals(wu) ? "%'" : "'");
				}
				out.write(">");
				processContent(session,out,data,c);
				out.write("</TD>");
			}
			out.write("</TR></TBODY></TABLE>");
			i++;
		}
				
		out.write("</TBODY>");
	}
	
	
	
	private void generateADTitleHead(Writer out, FormViewConfig lc, CompositeMap model) throws ViewCreationException, IOException{
		String title = lc.getTitle();
		String style = lc.getStyle();
		out.write("<TABLE cellSpacing='0' cellPadding='0' width='100%' border='0' class='adFormWrap'>");
		out.write("<TR>");
		out.write("<TD class='tl' width='14'>");
		out.write("</TD>");
		out.write("<TD>");
		out.write("<DIV class='adFormView' ");
		if(style !=null){
			out.write(" style='");
			out.write(style);
			out.write("'");			
		}
		out.write(">");
		if(title != null){
			out.write("<TABLE cellSpacing='0' cellPadding='0' border='0' style='margin-top:10px;position:relative;left:-1px'><TR><TD class='title' >");
			out.write(title);
			out.write("</TD><TD class='tr'></TD></TR></TABLE>");
		}
		
	}
	
	private void generateADTable(Writer out, FormViewConfig lc) throws IOException{
		out.write("<TABLE cellSpacing='0' cellPadding='0' width='100%' border='0'");
		String defaultClass = "";
		String className = lc.getClassName();
		if(className !=null){
			defaultClass += " " + className;
		}
		String style = lc.getTableStyle();
		if(style != null){
			out.write(" style='");
			out.write(style);
			out.write("'");
		}
		out.write(" class='");
		out.write(defaultClass);
		out.write("'>");
	}
	
	
	private void generateADFields(BuildSession session,Writer out, FormViewConfig lc, CompositeMap data,String wu) throws Exception {
		out.write("<TBODY>");
		List childs = lc.getSections();
		Iterator it = childs.iterator();
		int labelWidth = lc.getPromptWidth();
		int i=0;
		while(it.hasNext()){
			CompositeMap section = (CompositeMap)it.next();
			out.write("<TR><TD>");
			out.write("<TABLE cellSpacing='0' cellPadding='0' width='100%' border='0'");
			if(i==0)out.write(" class='top'");
			if(!it.hasNext())out.write(" class='bottom'");
			out.write("><TBODY><TR>");
			Iterator cit = section.getChildIterator();
			while(cit.hasNext()){
				CompositeMap c = (CompositeMap)cit.next();
				FormViewFieldConfig field = FormViewFieldConfig.getInstance(c);
				Integer fw = field.getPromptWidth();
				labelWidth = fw == null ? labelWidth : fw;
				out.write("<TD align='");
				out.write(lc.getPromptAlign());
				out.write("' class='label' width='");
				out.write(""+labelWidth);
				out.write(FormViewConfig.DEFAULT_WIDTH_UNIT.equals(wu) ? "%'" : "'");
				out.write(">");
				out.write(field.getPrompt());
				out.write("</TD>");
				out.write("<TD align='");
				out.write(field.getAlign());
				out.write("' class='field'");
				int w = field.getWidth();
				if(w!=0){
					out.write(" width='");
					out.write(""+w);
					out.write(FormViewConfig.DEFAULT_WIDTH_UNIT.equals(wu) ? "%'" : "'");
				}
				out.write(">");
				processContent(session,out,data,c);
				out.write("</TD>");
			}
			out.write("</TR></TBODY></TABLE>");
			i++;
		}
				
		out.write("</TBODY>");
	}
	
	private void generateADFooter(Writer out, FormViewConfig lc) throws IOException {
		out.write("</TABLE></DIV>");
		out.write("</TD>");
		out.write("</TR>");
		out.write("</TABLE>");
	}
	
	
	private void generateFooter(Writer out, FormViewConfig lc) throws IOException {
		out.write("</TABLE></DIV>");
	}
	
	
	private void processContent(BuildSession session, Writer out, CompositeMap model,CompositeMap c) throws Exception{
		String content = c.getText();
		if(content != null && !"".equals(content.trim())){
			Reader reader = null;
			Template t = null;
			StringWriter sw = null;
			try {
				IFreeMarkerTemplateProvider provider = (IFreeMarkerTemplateProvider) mObjectRegistry.getInstanceOfType(IFreeMarkerTemplateProvider.class);
				reader = new BufferedReader(new StringReader(content));
				t = new Template(c.getName(), reader, provider.getFreeMarkerConfiguration(), provider.getDefaultEncoding());
				sw = new StringWriter();
				Map p = new HashMap();
				p.put("model", model);
				t.process(p, sw);
				sw.flush();
				out.write(sw.toString());
			} catch (Exception e) {
				throw new aurora.presentation.ViewCreationException(e.getMessage());
			} finally {
				if(reader != null) reader.close();
				if(sw != null) sw.close();
			}
		}else if(c.getChilds() != null){
			Iterator it = c.getChildIterator();
			if(it != null){
				while(it.hasNext()){
					CompositeMap v = (CompositeMap)it.next();
					session.buildView(model, v);
				}
			}
		}else {
			FormViewFieldConfig column = FormViewFieldConfig.getInstance(c);
			out.write(model.getString(column.getName(),""));
		}
	}

	@Override
	public String[] getBuildSteps(ViewContext context) {
		return null;
	}

}
