package aurora.application.util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeMapComparator;
import uncertain.core.UncertainEngine;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.IPerObjectLoggingConfig;
import uncertain.ocm.IObjectRegistry;
import aurora.security.ResourceNotDefinedException;
import aurora.service.ServiceContext;

public class LoggingConfig implements ILoggingConifg {

	public static final String BASE_DIR = "base_dir";
	public static final String TRACE_FLAG = "trace_flag";
	public static final String NAME = "name";
	public static final String PATH = "path";
	public static final String PARENT_PATH = "parent_path";
	private String[] bmOperations = new String[] { "query", "update", "insert", "delete", "batch_update", "execute" };
	IObjectRegistry mRegistry;
	IPerObjectLoggingConfig perConfig;
	String web_home = "";
	String mBaseDir = "";

	public LoggingConfig(IObjectRegistry registry) {
		this.mRegistry = registry;
	}

	public void setLogginConfig(CompositeMap parameter) throws IOException {
		if (parameter == null)
			throw new RuntimeException("paramter error. 'parameter' can not be null.");
		CompositeMap context = parameter.getParent();
		if (mRegistry == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(context.asLocatable(), LoggingConfig.class);
		perConfig = (IPerObjectLoggingConfig) mRegistry.getInstanceOfType(IPerObjectLoggingConfig.class);
		if (perConfig == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(context.asLocatable(),
					IPerObjectLoggingConfig.class);
		UncertainEngine ue = (UncertainEngine) mRegistry.getInstanceOfType(UncertainEngine.class);
		if (ue == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(context.asLocatable(), UncertainEngine.class);
		File configDirectory = ue.getConfigDirectory();
		if (configDirectory == null)
			throw new RuntimeException("Not defind configDirectory in UncertainEngine.");
		File webHomeFile = configDirectory.getParentFile();
		web_home = webHomeFile.getCanonicalPath().replaceAll("\\\\", "/");
		if (parameter.getChildIterator() != null)
			for (Iterator it = parameter.getChildIterator(); it.hasNext();) {
				CompositeMap record = (CompositeMap) it.next();
				String baseDir = record.getString(BASE_DIR);
				if (baseDir == null)
					baseDir = "";
				File baseDirFile = new File(webHomeFile, baseDir);
				if (baseDirFile == null || !baseDirFile.exists()) {
					throw new ResourceNotDefinedException(baseDir);
				}
				File file = new File(webHomeFile, record.getString(PATH));
				String traceFlag = record.getString(TRACE_FLAG);
				boolean trace = false;
				if ("Y".equalsIgnoreCase(traceFlag))
					trace = true;
				setObjectNameTraceFlag(file, baseDirFile, trace);
			}

	}

	public void getLoggingConfig(CompositeMap parameter, String baseDir, String fileExt) throws Exception {
		mBaseDir = baseDir;
		if (mBaseDir == null)
			mBaseDir = "";
		if (parameter == null)
			throw new RuntimeException("paramter error. 'parameter' can not be null.");
		CompositeMap context = parameter.getParent();
		if (mRegistry == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(context.asLocatable(), LoggingConfig.class);
		perConfig = (IPerObjectLoggingConfig) mRegistry.getInstanceOfType(IPerObjectLoggingConfig.class);
		if (perConfig == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(context.asLocatable(),
					IPerObjectLoggingConfig.class);
		UncertainEngine ue = (UncertainEngine) mRegistry.getInstanceOfType(UncertainEngine.class);
		if (ue == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(context.asLocatable(), UncertainEngine.class);
		File configDirectory = ue.getConfigDirectory();
		if (configDirectory == null)
			throw new RuntimeException("can not find configDirectory.");
		File webHomeFile = configDirectory.getParentFile();
		web_home = webHomeFile.getCanonicalPath().replaceAll("\\\\", "/");
		File baseDirFile = new File(webHomeFile, mBaseDir);
		if (baseDirFile == null || !baseDirFile.exists()) {
			throw new ResourceNotDefinedException(mBaseDir);
		}
		CompositeMap result = new CompositeMap("result");
		serachFile(baseDirFile, baseDirFile, fileExt, result);
		ServiceContext service = ServiceContext.createServiceContext(parameter.getParent());
		if (result.getChilds() != null) {
			Collections.sort(result.getChilds(), new CompositeMapComparator(new String[] { PATH }));
		}
		service.getModel().addChild(result);
	}

	protected void serachFile(File file, File baseDirFile, String fileExt, CompositeMap result) throws IOException {
		if (baseDirFile == null)
			return;
		if (file.isFile()) {
			if (file.getName().endsWith("." + fileExt)) {
				result.addChild(fileToRecord(file, baseDirFile));
			}
		} else if (file.isDirectory()) {
			File[] subFiles = file.listFiles();
			int childCount = result.getChilds() != null ? result.getChilds().size() : 0;
			if (subFiles != null) {
				for (int i = 0; i < subFiles.length; i++) {
					serachFile(subFiles[i], baseDirFile, fileExt, result);
				}
			}
			if (!file.equals(baseDirFile)) {
				int newChildCount = result.getChilds() != null ? result.getChilds().size() : 0;
				if (newChildCount > childCount)
					result.addChild(fileToRecord(file, baseDirFile));
			}
		}

	}

	protected CompositeMap fileToRecord(File file, File baseDirFile) throws IOException {
		if (file == null)
			return null;
		CompositeMap record = new CompositeMap("record");
		record.put(BASE_DIR, mBaseDir);
		record.put(NAME, file.getName());
		String path = formatPath(file.getCanonicalPath().substring(web_home.length()));
		record.put(PATH, path);
		if (file.getParent() == null || file.getParent() == null) {
			record.put(PARENT_PATH, "");
		} else {
			record.put(PARENT_PATH, formatPath(file.getParentFile().getCanonicalPath().substring(web_home.length())));
		}
		String traceFlag = "N";
		boolean trace = perConfig.getTraceFlag(getObjectNameFromFile(file, baseDirFile));
		if (trace) {
			traceFlag = "Y";
		}
		record.put(TRACE_FLAG, traceFlag);
		return record;
	}

	protected String formatPath(String path) {
		if (path == null)
			return null;
		path = path.replaceAll("\\\\", "/");
		if (path.startsWith("/"))
			return path.substring(1);
		return path;
	}

	protected void setObjectNameTraceFlag(File file, File baseDir, boolean flag) throws IOException {
		if (file == null || baseDir == null)
			return;
		String object_name = getObjectNameFromFile(file, baseDir);
		perConfig.setTraceFlag(object_name, flag);
		if (file.getName().toLowerCase().endsWith(".bm")) {
			for (int i = 0; i < bmOperations.length; i++) {
				perConfig.setTraceFlag(object_name + "_" + bmOperations[i], flag);
			}
		}
	}

	protected String getObjectNameFromFile(File file, File baseDir) throws IOException {
		if (file == null || baseDir == null)
			return null;
		String object_name = "";
		if (file.getName().toLowerCase().endsWith(".bm")) {
			object_name = file.getCanonicalPath().substring(baseDir.getCanonicalPath().length());
			object_name = object_name.replaceAll("\\\\", ".");
			object_name = object_name.replaceAll("/", ".");
			object_name = object_name.substring(0, object_name.length() - ".bm".length());
			if (object_name.startsWith("."))
				object_name = object_name.substring(1);
		} else {// if(file.getName().toLowerCase().endsWith(".screen")||file.getName().toLowerCase().endsWith(".svc")){
			object_name = formatPath(file.getCanonicalPath().substring(baseDir.getCanonicalPath().length()));
		}
		return object_name;
	}
}
