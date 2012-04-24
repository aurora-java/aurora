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

import freemarker.template.Template;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.ISingleton;
import aurora.presentation.BuildSession;
import aurora.presentation.IFreeMarkerTemplateProvider;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.ListViewColumnConfig;
import aurora.presentation.component.std.config.ListViewConfig;

@SuppressWarnings("unchecked")
public class ListView extends Component implements IViewBuilder, ISingleton {

	IObjectRegistry mObjectRegistry;

	public ListView(IObjectRegistry rg) {
		this.mObjectRegistry = rg;
	}
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		addStyleSheet(session, context, "list/list.css");
	}

	@Override
	public void buildView(BuildSession session, ViewContext view_context) throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		CompositeMap model = view_context.getModel();
		ListViewConfig lc = ListViewConfig.getInstance(view);
		StringBuffer sb = new StringBuffer();
		generateTable(sb,lc);
		generateHead(sb,lc,session);
		generateList((CompositeMap)model.getObject(lc.getDataModel()),sb,lc);
		generateFooter(sb,lc);
		Writer out = session.getWriter();
		try {
			out.write(sb.toString());
		} catch (Exception e) {
			throw new ViewCreationException(e);
		}
	}

	@Override
	public String[] getBuildSteps(ViewContext context) {
		return null;
	}
	
	
	private void generateTable(StringBuffer sb, ListViewConfig lc){
		sb.append("<TABLE cellSpacing='1' cellPadding='2' width='100%'");
		String defaultClass = "listView";
		String className = lc.getClassName();
		if(className !=null){
			defaultClass += " " + className;
			sb.append(" class='").append(defaultClass).append("'");			
		}
		String style = lc.getStyle();
		if(style !=null){
			sb.append(" style='").append(style).append("'");			
		}
		sb.append(">");
		
	}
	
	private void generateHead(StringBuffer sb, ListViewConfig lc,BuildSession session) {
		sb.append("<THEAD>");
		sb.append("<TR>");
		CompositeMap columns =  lc.getColumns();
		List childs = columns.getChilds();
		Iterator it = childs.iterator();
		while(it.hasNext()){
			CompositeMap c = (CompositeMap)it.next();
			ListViewColumnConfig column = ListViewColumnConfig.getInstance(c);
			sb.append("<TH style='text-align:").append(column.getHeadAlign()).append("'");
			sb.append(" width='").append(column.getWidth()).append("%'");
			sb.append(">").append(session.getLocalizedPrompt(column.getPrompt())).append("</TH>");
		}
		sb.append("</TR>");
		sb.append("</THEAD>");
	}
	
	
	private void generateList(CompositeMap data, StringBuffer sb, ListViewConfig lc) throws ViewCreationException, IOException {
		sb.append("<TBODY>");
		List list = null;
		if(data!= null){
			list = data.getChilds();
			if(list !=null){
				int i=0;
				CompositeMap columns =  lc.getColumns();
				List childs = columns.getChilds();
				Iterator it = list.iterator();
				while(it.hasNext()){
					if(i%2==0) {
						sb.append("<TR>");
					}else{
						sb.append("<TR class='alt'>");
					}
					CompositeMap record = (CompositeMap)it.next();
					Iterator cit = childs.iterator();
					while(cit.hasNext()){
						CompositeMap c = (CompositeMap)cit.next();
						ListViewColumnConfig column = ListViewColumnConfig.getInstance(c);
						sb.append("<TD align='").append(column.getAlign()).append("'");
						sb.append(">").append(processContent(record,c)).append("</TD>");//TODO:process type
					}
					i++;
					sb.append("</TR>");
				}
				
			}
		}
		sb.append("</TBODY>");
	}
	
	private String processContent(CompositeMap record,CompositeMap c) throws IOException,ViewCreationException{
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
				p.put("record", record);
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
			ListViewColumnConfig column = ListViewColumnConfig.getInstance(c);
			return record.getString(column.getName(),"");
		}
	}
	
	private void generateFooter(StringBuffer sb, ListViewConfig lc) {
		
		sb.append("</TABLE>");
	}


}
