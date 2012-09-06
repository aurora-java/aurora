package aurora.application.script.engine;

public interface CompiledScriptCacheMBean {
	public int getScriptSize();

	public int getMaxCacheSize();

	public void setMaxCacheSize(int size);

	public void clearScriptCache();

	public String getScriptDetail(int idx);

}
