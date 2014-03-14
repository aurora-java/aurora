/*
 * Created on 2009-5-6
 */
package aurora.presentation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.core.UncertainEngine;
import aurora.service.http.WebContextInit;

public class ResourceDispatchServlet extends HttpServlet implements
        IResourceUrlMapper {

    /**
     * 
     */
    private static final long serialVersionUID = -4772573956894794840L;
    public static final int BUFFER_SIZE = 10000;
    ServletConfig mConfig;
    ServletContext mServletContext;
    PresentationManager mPresentationManager;
    UncertainEngine mUncertainEngine;
    String mBasePath = "resource";
    
    private static void setString(StringBuffer buf, String content) {
        if (content == null)
            throw new IllegalArgumentException();
        buf.setLength(0);
        buf.append(content);
    }

    private static void parseRequestLine(String root_path, String uri,
            StringBuffer package_name, StringBuffer theme_name,
            StringBuffer resource) {
        int begin_id = uri.indexOf(root_path) + root_path.length();
        final int end_id = uri.indexOf('/', begin_id + 1);
        if (begin_id < 0 || end_id < 0)
            throw new IllegalArgumentException();
        if (begin_id >= end_id)
            throw new IllegalArgumentException();
        begin_id++;
        setString(package_name, uri.substring(begin_id, end_id));

        final String file_name = uri.substring(end_id + 1);
        final int theme_end_id = file_name.indexOf('/');
        setString(theme_name, file_name.substring(0, theme_end_id));
        setString(resource, file_name.substring(theme_end_id + 1));
    }

    private void initInternal() throws ServletException {
        if (mPresentationManager == null) {
            if (mUncertainEngine == null) {
                mUncertainEngine = WebContextInit
                        .getUncertainEngine(mServletContext);
                // mUncertainEngine =
                // (UncertainEngine)mServletContext.getAttribute("uncertain");
                if (mUncertainEngine == null)
                    throw new ServletException(
                            "Can't get uncertain engine from servlet context");
            }
            mPresentationManager = (PresentationManager) mUncertainEngine
                    .getObjectRegistry().getInstanceOfType(
                            PresentationManager.class);
            if (mPresentationManager == null)
                throw new ServletException(
                        "Can't get PresentationManager instance from UncertainEngine");
            mPresentationManager.setResourceUrlMapper(this);
        }
    }

    @Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
    	doService(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doService(req, resp);
	}

	/**
     * Dispatch resource request
     */
    public void doService(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // checkInit();
        final String root_path = request.getServletPath();
        final String uri = request.getRequestURI();
        final StringBuffer package_name = new StringBuffer();
        final StringBuffer theme = new StringBuffer();
        final StringBuffer resource = new StringBuffer();
        // System.out.println("root_path:"+root_path);
        // System.out.println("uri:"+uri);
        try {
            parseRequestLine(root_path, uri, package_name, theme, resource);
            final ViewComponentPackage pkg = mPresentationManager
                    .getPackage(package_name.toString());
            if (pkg == null)
                throw new IllegalArgumentException();
            final File rf = pkg.getResourceFile(theme.toString(), resource
                    .toString());
            if (rf == null)
                throw new IllegalArgumentException();
            writeResponseFile(rf, response);
            // response.getWriter().println("to serve "+rf.getPath());
        } catch (final IllegalArgumentException ex) {
            response.sendError(404);
        }
    }

    public void writeResponseFile(File resource, HttpServletResponse response)
            throws IOException {
        final String mime_type = mServletContext
                .getMimeType(resource.getPath());
        if (mime_type != null)
            response.setContentType(mime_type);
        response.setContentLength((int) resource.length());
        FileInputStream fis = null;
        OutputStream ois = null;
        try {
            fis = new FileInputStream(resource);
            ois = response.getOutputStream();
            final byte[] buf = new byte[BUFFER_SIZE];
            int off = 0;
            do {
                off = fis.read(buf);
                if (off == -1)
                    break;
                ois.write(buf, 0, off);
            } while (true);
        } finally {
            if (fis != null)
                fis.close();
        }
    }

    public void init(ServletConfig config) throws ServletException {
        this.mConfig = config;
        this.mServletContext = config.getServletContext();
        initInternal();
        final String p = config.getInitParameter("basePath");
        if (p != null)
            mBasePath = p;
    }

    public String getResourceUrl(String package_name, String theme,
            String resource_path) {
        final StringBuffer buf = new StringBuffer();
        buf.append(mBasePath);
        buf.append("/");
        buf.append(package_name);
        buf.append("/");
        buf.append(theme);
        buf.append("/");
        buf.append(resource_path);
        return buf.toString();
    }

    /*
     * public static void main(String[] args) throws Exception { String
     * root_path = "/resource"; String uri =
     * "/resource/my.pkg/default/css/a.css"; StringBuffer package_name = new
     * StringBuffer(); StringBuffer theme = new StringBuffer(); StringBuffer
     * resource = new StringBuffer(); parseRequestLine(root_path, uri,
     * package_name, theme, resource); System.out.println(package_name);
     * System.out.println(theme); System.out.println(resource); }
     */
}
