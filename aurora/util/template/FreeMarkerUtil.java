package aurora.util.template;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import uncertain.composite.CompositeMap;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 
 * @version $Id: FreeMarkerUtil.java,v 1.2 2008/06/16 02:20:16 njq Exp $
 * @author <a href="mailto:njq.niu@hand-china.com">vincent</a>
 */
public class FreeMarkerUtil {

	private static Configuration cfg = new Configuration();

	static {
		try {
			// Specify the data source where the template files come from.
			// Here I set a file directory for it:
			// TODO Set the actural path here
			
//			cfg.setDefaultEncoding("UTF-8");
//			cfg.setLocale(Locale.CHINA);
			cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static File getWebApplicationRoot(ServletContext context) {
		return new File(context.getRealPath("."));
	}

	/**
	 * Merge the template and model into a new string.
	 * 
	 * @param templateUrlString
	 * @param model
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String processTemplate(ServletContext context, String template, Map model) throws TemplateException, IOException {

		model.put("statics", BeansWrapper.getDefaultInstance().getStaticModels());
		cfg.setDirectoryForTemplateLoading(getWebApplicationRoot(context));
		Template temp = cfg.getTemplate(template);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer out = new OutputStreamWriter(baos);
		temp.process(model, out);
		out.flush();
		return baos.toString();

	}

}
