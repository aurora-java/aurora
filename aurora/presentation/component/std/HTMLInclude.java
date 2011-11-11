package aurora.presentation.component.std;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import uncertain.composite.CompositeMap;
import aurora.database.service.BusinessModelService;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.service.ServiceThreadLocal;

public class HTMLInclude implements IViewBuilder {

	private IDatabaseServiceFactory factory;
	private String id;
	private String model = "doc.doc_artical";
	private static final String PROPERTITY_ARTICAL_ID = "artical_id";
	private static final String PROPERTITY_ARTICAL_PATH = "artical_path";
	private String contextPath;
	private String articalPath;
	private String sourcePath;
	private String titlePattern = "<title>.*</title>";
	private String metaPattern = "<meta[^>]*>";
	private String headPattern = "<head>(.*)</head>";
	private String htmlPattern = ".*<html[^>]*>(.*)</html>.*";
	private String bodyPattern = "(.*)<body[^>]*>(.*)</body>(.*)";
	private String scriptPattern = "<script[^>]*src=([\"\'])([^\'\"]*)\\1[^>]*(/|.*/script)>";
	private String linkPattern = "<link[^>]*href=([\"\'])([^\'\"]*)\\1[^>]*/?>";
	private ClassLoader mClassLoader = Thread.currentThread()
			.getContextClassLoader();

	public HTMLInclude(IDatabaseServiceFactory factory) {
		this.factory = factory;
	}

	public void buildView(BuildSession session, ViewContext view_context)
			throws IOException, ViewCreationException {
		contextPath = session.getContextPath();
		Writer out = session.getWriter();
		id = view_context.getView().getString(ComponentConfig.PROPERTITY_ID);
		try {
			init();
		}catch (ClassNotFoundException e) {
			out.write(e.getMessage());
			return;
		}
		catch (Exception e) {
			throw new ViewCreationException(e);
		}
		String source = getArticalSource(articalPath);
		if (null != source && !"".equals(source))
			out.write(source);
	}

	private void init() throws Exception {
		if (null == id) {
			throw new ViewCreationException(
					"The property 'id' of The artical component is required.");
		}
		CompositeMap context = ServiceThreadLocal.getCurrentThreadContext();
		if (context == null)
			throw new IllegalStateException(
					"No service context set in ThreadLocal yet");

		BusinessModelService service = factory.getModelService(model, context);
		Map map = new HashMap();
        map.put(PROPERTITY_ARTICAL_ID, id);
        CompositeMap resultMap = service.queryAsMap(map);
        if(null == resultMap || null ==  resultMap.getChilds()){
        	throw new ClassNotFoundException(
			"文章未找到，输入的路径不正确。");
        }
        Iterator it = resultMap.getChildIterator();
        while(it.hasNext()){
        	String path = ((CompositeMap)it.next()).getString(PROPERTITY_ARTICAL_PATH);
        	if (null != path) {
				articalPath = "../.." + path;
				sourcePath = contextPath
						+ path.replaceAll("\\\\", "/").replaceAll(
								"(.*/)[^/]*$", "$1");
				break;
			}
        }
	}

	private String getSource(String path) throws IOException {
		InputStream stream = null;
		try {
			URL url = mClassLoader.getResource(path);
			String file = url == null ? null : url.getFile();
			boolean need_stream = false;
			if (file == null)
				need_stream = true;
			else {
				File f = new File(file);
				if (!f.exists())
					need_stream = true;
			}
			if (need_stream) {
				stream = mClassLoader.getResourceAsStream(path);
			} else {
				stream = new FileInputStream(file);
			}
			if (stream == null)
				throw new IOException("Can't get resource from " + path);
			StringBuffer sb = new StringBuffer();
			int begin;
			byte[] buffer = new byte[1024];
			while ((begin = stream.read(buffer)) != -1) {
				sb.append(new String(buffer, 0, begin));
			}
			return sb.toString();
		} finally {
			if (stream != null)
				stream.close();
		}
	}

	private String getArticalSource(String path) throws IOException {
		return pase(getSource(path));
	}

	private String pase(String source) {
		if (null == source || "".equals(source))
			return "";
		return replaceAll(linkPattern, replaceAll(scriptPattern, replaceAll(
				metaPattern, replaceAll(titlePattern,
						replaceAll(bodyPattern, replaceAll(headPattern,
								replaceAll(htmlPattern, replaceAll("</link>",
										source, ""), "$1"), "$1"), "$1$2$3"),
						""), ""), "<script src='" + sourcePath
				+ "$2'></script>"),
				"<link rel='stylesheet' type='text/css' href='" + sourcePath
						+ "$2'/>");
	}

	private String replaceAll(String regex, CharSequence input,
			String replacement) {
		return Pattern.compile(regex, Pattern.DOTALL).matcher(input)
				.replaceAll(replacement);
	}

	public String[] getBuildSteps(ViewContext context) {
		return null;
	}

}
