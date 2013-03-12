/*
 * Created on 2007-11-20
 */
package aurora.service.http;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import uncertain.core.DirectoryConfig;
import uncertain.core.UncertainEngine;
import uncertain.mbean.MBeanRegister;
import uncertain.mbean.UncertainEngineWrapper;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.PackageMapping;
import aurora.application.Version;

public class WebContextInit implements ServletContextListener {

    //public static final String KEY_LOG_PATH = "log-path";
    public static final String KEY_UNCERTAIN_ENGINE = UncertainEngine.class.getName();

    UncertainEngine uncertainEngine;
    
    public static UncertainEngine getUncertainEngine( ServletContext context ){
        return (UncertainEngine)context.getAttribute(KEY_UNCERTAIN_ENGINE);
    }

    public WebContextInit() {

    }

    public void initUncertain(ServletContext servletContext) throws Exception {

        String config_dir = servletContext.getRealPath("/WEB-INF");
        if(config_dir==null){
        	config_dir=servletContext.getResource("/WEB-INF").getFile();
        }
        String config_file = "uncertain.xml";
        //String pattern = ".*\\.config";

        uncertainEngine = new UncertainEngine(new File(config_dir), config_file);
        uncertainEngine.setName(servletContext.getServletContextName());
        DirectoryConfig dirConfig = uncertainEngine.getDirectoryConfig();
        String basePath=servletContext.getRealPath("/");
        if(basePath==null){
        	basePath=servletContext.getResource("/").getFile();
        }
        dirConfig.setBaseDirectory(basePath);
        // load aurora builtin package
        uncertainEngine.getPackageManager().loadPackageFromRootClassPath("aurora_builtin_package");

        IObjectRegistry os = uncertainEngine.getObjectRegistry();
        os.registerInstance(ServletContext.class, servletContext);

        //uncertainEngine.scanConfigFiles(pattern);
        uncertainEngine.startup();
        
        /** TODO to be enhanced */
        IObjectRegistry reg = uncertainEngine.getObjectRegistry();
        HttpServiceFactory fact = (HttpServiceFactory)reg.getInstanceOfType(HttpServiceFactory.class);
        if(fact==null){
            fact = new HttpServiceFactory(uncertainEngine);
            fact.getCompositeLoader().setBaseDir(dirConfig.getBaseDirectory());
            reg.registerInstance(fact);
        }
    }

    public void init(ServletContext servlet_context) throws Exception {
    	StringBuffer sb = new StringBuffer(),line = new StringBuffer();   	
    	
    	sb.append("* ").append("Aurora-").append(Version.getVersion());
    	sb.append("  |  ").append(servlet_context.getResource("/").toExternalForm());
    	sb.append("  |  ").append(servlet_context.getServletContextName());
    	sb.append(" *");
    	int len = sb.length();
    	for(int i=0;i<len;i++){
    		line.append("*");
    	}
    	System.out.println(line.toString());
    	System.out.println(sb.toString());
    	System.out.println(line.toString());
    	
        initUncertain(servlet_context);

        servlet_context.setAttribute(KEY_UNCERTAIN_ENGINE,uncertainEngine);

    }

    public UncertainEngine getUncertainEngine() {
        return uncertainEngine;
    }

    public void contextDestroyed(ServletContextEvent event) {
        if(uncertainEngine!=null)
            uncertainEngine.shutdown();
    	
    }

    public void contextInitialized(ServletContextEvent event) {
        try {
            ServletContext context = event.getServletContext();
            init(context);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
