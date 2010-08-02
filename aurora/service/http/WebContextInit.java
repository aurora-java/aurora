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
import uncertain.ocm.IObjectRegistry;
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
        String config_file = "uncertain.xml";
        String pattern = ".*\\.config";

        uncertainEngine = new UncertainEngine(new File(config_dir), config_file);
        uncertainEngine.setName(servletContext.getServletContextName());
        DirectoryConfig dirConfig = uncertainEngine.getDirectoryConfig();
        dirConfig.setBaseDirectory(servletContext.getRealPath("/"));

        IObjectRegistry os = uncertainEngine.getObjectRegistry();

        os.registerInstance(ServletContext.class, servletContext);

        uncertainEngine.scanConfigFiles(pattern);
        
        /** @todo to be enhanced */
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
    	sb.append("  |  ").append("Running").append(" *");
    	int len = sb.length();
    	for(int i=0;i<len;i++){
    		line.append("*");
    	}
    	System.out.println(line.toString());
    	System.out.println(sb.toString());
    	System.out.println(line.toString());
    	
//        System.out.println("***** Aurora("+Version.getVersion()+") Application " + servlet_context.getResource("/").toExternalForm() + " starting up *****");
//        System.out.println("Aurora core version " + Version.getVersion());

        initUncertain(servlet_context);

        servlet_context.setAttribute(KEY_UNCERTAIN_ENGINE,uncertainEngine);

    }

    public UncertainEngine getUncertainEngine() {
        return uncertainEngine;
    }

    public void contextDestroyed(ServletContextEvent event) {
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
