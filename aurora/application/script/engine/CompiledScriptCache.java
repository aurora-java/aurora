package aurora.application.script.engine;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;

/**
 * Every piece of script will be compiled before execute.And the compilation is
 * independent from runtime scope,So we can cache the compiled script,to reduce
 * the full-execute time(each piece of script will only be compiled once).<br/>
 * 
 * @author jessen
 * 
 */
public class CompiledScriptCache {
	private static CompiledScriptCache instance = new CompiledScriptCache();

	private ConcurrentHashMap<Key, Script> scriptCache = new ConcurrentHashMap<Key, Script>(
			256);
	/**
	 * The script file cache with timestamp, so if a script file is modified, it
	 * will be replaced when next access
	 */
	private ConcurrentHashMap<Key, Object[]> libScriptCache = new ConcurrentHashMap<Key, Object[]>(
			256);

	private CompiledScriptCache() {

	}

	public static CompiledScriptCache getInstance() {
		return instance;
	}

	/**
	 * try to get Script from cache,if not success(not exists,or optimizeLevel
	 * not compatible ) then {@code source} will be compiled,and cached
	 * 
	 * @param source
	 * @param cx
	 * @param sourceName
	 * @return
	 */
	public synchronized Script getScript(String source, Context cx,
			String sourceName) {
		Key k = new Key(source, cx.getOptimizationLevel());
		Script s = scriptCache.get(k);
		if (s == null) {
			s = cx.compileString(source, sourceName, 0, null);
			scriptCache.put(k, s);
		}
		return s;
	}

	/**
	 * 
	 * {@link #getScript(String , Context , String )}<br/>
	 * the sourceName is &lt;Unknown source&gt;
	 * 
	 * @param source
	 * @param cx
	 * @return
	 */
	public Script getScript(String source, Context cx) {
		return getScript(source, cx, "<Unknown source>");
	}

	/**
	 * 
	 * @param file
	 * @param cx
	 * @return
	 */
	public synchronized Script getScript(File file, Context cx) {
		Key k = new Key(file, cx.getOptimizationLevel());
		Object[] objs = libScriptCache.get(k);
		Script script = null;
		if (objs != null && k.lastModif == (Long) objs[1])
			script = (Script) objs[0];
		if (script == null) {
			FileReader fr = null;
			try {
				fr = new FileReader(file);
				script = cx.compileReader(fr, file.getName(), 0, null);
				libScriptCache.put(k, new Object[] { script, k.lastModif });
			} catch (Exception e) {
				return null;
			} finally {
				if (fr != null)
					try {
						fr.close();
					} catch (IOException e) {
					}
			}
		}
		return script;
	}

	static class Key {
		String source;
		File file;
		int optLevel;
		long lastModif;

		Key(Object s, int level) {
			if (s instanceof String)
				source = (String) s;
			else if (s instanceof File) {
				file = (File) s;
				lastModif = file.lastModified();
			}
			optLevel = level;
		}

		@Override
		public int hashCode() {
			if (file != null)
				return file.hashCode() + optLevel;
			if (source != null)
				return source.hashCode() + optLevel;
			return super.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Key))
				return false;
			Key k = (Key) obj;
			return eq(source, k.source) && eq(file, k.file)
					&& optLevel == k.optLevel;
		}

		static boolean eq(Object o1, Object o2) {
			if (o1 == null)
				return o2 == null;
			return o1.equals(o2);
		}

	}
}
