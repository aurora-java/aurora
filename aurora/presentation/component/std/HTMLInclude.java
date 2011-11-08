package aurora.presentation.component.std;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Pattern;

import aurora.database.service.IDatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.ComponentConfig;

public class HTMLInclude implements IViewBuilder {

	private IDatabaseServiceFactory factory;
	private String id;
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
		} catch (Exception e) {
			throw new ViewCreationException(e);
		}
		String source = getArticalSource(articalPath);
		if (null != source && !"".equals(source))
			out.write("<div id ='"+id+"'>" + source + "</div>");
	}

	private void init() throws Exception {
		if (null == id) {
			throw new ViewCreationException(
					"The property 'id' of The artical component is required.");
		}
		SqlServiceContext ssc = factory.createContextWithConnection();
		Connection conn = ssc.getConnection();
		Statement st = conn.createStatement();
		ResultSet rs = null;
		try {
			rs = st
					.executeQuery("select sa.artical_path from sys_artical sa where sa.artical_id = '"
							+ id + "'");
			if (!rs.next())
				throw new ViewCreationException(
						"Invalid property 'id' for match the 'artical_id'");
			String path = rs.getString(1);
			if (null != path) {
				articalPath = "../.." + path;
				sourcePath = contextPath
						+ path.replaceAll("\\\\", "/").replaceAll(
								"(.*/)[^/]*$", "$1");
			}
		} finally {
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
			if (ssc != null)
				ssc.freeConnection();
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
										source, ""), "$1"), "$1"), "$1$2$3"), ""),
				""), "<script src='" + sourcePath + "$2'></script>"),
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
