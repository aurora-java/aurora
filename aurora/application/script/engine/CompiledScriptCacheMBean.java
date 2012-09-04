package aurora.application.script.engine;

public interface CompiledScriptCacheMBean {
	public int getScriptSize();

	public void clearScriptCache();

	public String getScriptDetail(int idx);
}
