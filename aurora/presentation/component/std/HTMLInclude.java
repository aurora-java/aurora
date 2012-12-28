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
import uncertain.composite.TextParser;
import aurora.database.service.BusinessModelService;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.service.ServiceThreadLocal;

public class HTMLInclude implements IViewBuilder {
	
	public static final String VERSION = "$Revision$";
	
	private IDatabaseServiceFactory factory;
	private static final String PROPERTITY_PATH_FIELD = "pathfield";
	private static final String PROPERTITY_MODEL = "model";
	private static final String PROPERTITY_PARAMS = "params";
	private static final String PROPERTITY_PATH = "path";
	private static final String PROPERTITY_VERSION = "version";
	private String articlePath;
	private String sourcePath;
	private String version;
	private String titlePattern = "<title>.*</title>";
	private String metaPattern = "<meta[^>]*>";
	private String headPattern = "<head>(.*)</head>";
	private String htmlPattern = ".*<html[^>]*>(.*)</html>.*";
	private String bodyPattern = "(.*)<body[^>]*>(.*)</body>(.*)";
	private String scriptPattern = "<script[^>]*src=([\"\'])([^\'\"]*)\\1[^>]*(/|.*/script)>";
	private String linkPattern = "<link[^>]*href=([\"\'])([^\'\"]*)\\1[^>]*/?>";
	private String imgPattern = "<img[^>]*src=([\"\'])([^\'\"]*)\\1[^>]*(/*)>";
	private ClassLoader mClassLoader = Thread.currentThread()
			.getContextClassLoader();

	public HTMLInclude(IDatabaseServiceFactory factory) {
		this.factory = factory;
	}

	public void buildView(BuildSession session, ViewContext view_context)
			throws IOException, ViewCreationException {
		Writer out = session.getWriter();
		try {
			init(session, view_context);
			if (null == articlePath)
				return;
			String source = getArticalSource(articlePath);
			if (null != source && !"".equals(source))
				out.write(source);
		} catch (ClassNotFoundException e) {
			out.write(e.getMessage());
		} catch (Exception e) {
			throw new ViewCreationException(e);
		}
	}

	private void init(BuildSession session, ViewContext view_context)
			throws Exception {
		CompositeMap context = ServiceThreadLocal.getCurrentThreadContext();
		if (context == null)
			throw new IllegalStateException(
					"No service context set in ThreadLocal yet");
		CompositeMap view = view_context.getView();
		CompositeMap base_model = view_context.getModel();
		String path = TextParser.parse(view.getString(PROPERTITY_PATH),
				base_model);
		version = TextParser.parse(view.getString(PROPERTITY_VERSION),
				base_model);
		if (null != path && !path.isEmpty()) {
			int begin = path.indexOf("/release");
			if (begin < 0)
				return;
			path = path.substring(begin, path.length());
			articlePath = "../.." + path;
			sourcePath = session.getContextPath()
					+ path.replaceAll("\\\\", "/").replaceAll("(.*/)[^/]*$",
							"$1");
			return;
		}
		String pathField = view.getString(PROPERTITY_PATH_FIELD);
		String model = view.getString(PROPERTITY_MODEL);
		CompositeMap params = view.getChild(PROPERTITY_PARAMS);
		Map map = new HashMap();
		if (null != params) {
			Iterator pit = params.getChildIterator();
			while (pit.hasNext()) {
				CompositeMap param = (CompositeMap) pit.next();
				map.put(param.get("name"), TextParser.parse(
						(String) param.get("value"), view_context.getModel()));
			}
		}
		BusinessModelService service = factory.getModelService(model, context);

		CompositeMap resultMap = service.queryAsMap(map);
		if (null == resultMap || null == resultMap.getChilds()) {
			throw new ClassNotFoundException("文章未找到，输入的路径不正确。");
		}
		Iterator it = resultMap.getChildIterator();
		while (it.hasNext()) {
			path = ((CompositeMap) it.next()).getString(pathField);
			if (null != path) {
				articlePath = "../.." + path;
				sourcePath = session.getContextPath()
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
				// throw new IOException("Can't get resource from " + path);
				return "<h3>关于此标签的文章还未发表</h3>";
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
		return parseImg(getSource(path));
	}

	private String pase(String source) {
		if (null == source || "".equals(source))
			return "";
		return replaceAll(
				linkPattern,
				replaceAll(
						scriptPattern,
						replaceAll(
								metaPattern,
								replaceAll(
										titlePattern,
										replaceAll(
												bodyPattern,
												replaceAll(
														headPattern,
														replaceAll(
																htmlPattern,
																replaceAll(
																		"</link>",
																		source,
																		""),
																"$1"), "$1"),
												"$1$2$3"), ""), ""),
						"<script src='" + sourcePath + "$2'></script>"),
				"<link rel='stylesheet' type='text/css' href='" + sourcePath
						+ "$2'/>");
	}

	private String parseImg(String source) {
		if (null == version || version.isEmpty())
			return pase(source);
		return replaceAll(imgPattern, pase(source), "<img src='release/"
				+ version + "/$2'/>");
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
