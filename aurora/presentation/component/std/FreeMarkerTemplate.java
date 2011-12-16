package aurora.presentation.component.std;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.ISingleton;
import aurora.application.features.IFreeMarkerTemplateProvider;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import freemarker.template.Template;

public class FreeMarkerTemplate implements IViewBuilder, ISingleton {
	IObjectRegistry mObjectRegistry;

	public FreeMarkerTemplate(IObjectRegistry rg) {
		this.mObjectRegistry = rg;
	}

	@SuppressWarnings("unchecked")
	public void buildView(BuildSession session, ViewContext view_context) throws IOException, aurora.presentation.ViewCreationException {
		String str = "";
		Reader reader = null;
		Template t = null;
		StringWriter out = null;
		try {
			CompositeMap model = view_context.getModel();
			CompositeMap view = view_context.getView();
			IFreeMarkerTemplateProvider provider = (IFreeMarkerTemplateProvider) mObjectRegistry.getInstanceOfType(IFreeMarkerTemplateProvider.class);
			reader = new BufferedReader(new StringReader(view.getText()));
			t = new Template(view.getName(), reader, provider.getFreeMarkerConfiguration(), provider.getDefaultEncoding());
			out = new StringWriter();
			Map p = new HashMap();
			p.put("view", view);
			p.put("model", model);
			t.process(p, out);
			out.flush();
			str = out.toString();
			
//			System.out.println(view.getPrefix());
//			System.out.println(view.getNamespaceURI());
			//TODO: view.getNamespaceMapping()是null,其他命名空间如何取? 
			StringBuffer sb = new StringBuffer();
			sb.append("<").append(view.getPrefix()).append(":screen ").append("xmlns:").append(view.getPrefix()).append("=\"")
			  .append(view.getNamespaceURI()).append("\">").append(str).append("</").append(view.getPrefix()).append(":screen>");
			CompositeLoader cl = new CompositeLoader();
			List list = new ArrayList();
			CompositeMap c = cl.loadFromString(sb.toString(), provider.getDefaultEncoding());
			list.add(c);
			session.buildViews(model, list);
		} catch (Exception e) {
			throw new aurora.presentation.ViewCreationException(e.getMessage());
		} finally {
			if(reader != null) reader.close();
			if(out != null) out.close();
		}
	}

	public String[] getBuildSteps(ViewContext context) {
		return null;
	}
}
