package aurora.application.features.cache;

import java.util.logging.Level;

import uncertain.cache.INamedCacheFactory;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;

public class PeriodModeCacheProvider extends CacheProvider {

	protected int refreshInterval = -1;
	private Thread periodThread;

	public PeriodModeCacheProvider(IObjectRegistry registry,INamedCacheFactory cacheFactory) {
		super(registry,cacheFactory);
	}

	public void onInitialize() throws Exception {
		if(refreshInterval == -1)
			throw BuiltinExceptionFactory.createAttributeMissing(this, "refreshInterval");
		super.onInitialize();
		executePeriodMode();
	}
	private void executePeriodMode() {
		if (refreshInterval > 0) {
			periodThread = new Thread() {
				public void run() {
					while (true) {
						try {
							sleep(refreshInterval);
							reload();
						} catch (Exception e) {
							logger.log(Level.SEVERE, "", e);
							throw new RuntimeException(e);
						}
					}
				}
			};
			periodThread.start();
		}
	}
	public int getRefreshInterval() {
		return refreshInterval;
	}

	public void setRefreshInterval(int refreshInterval) {
		this.refreshInterval = refreshInterval;
	}
	public void onShutdown(){
		if(periodThread != null && periodThread.isAlive()){
			periodThread.interrupt();
		}
	}
}
