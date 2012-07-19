package aurora.application.features.cache;

import java.util.logging.Level;

import uncertain.cache.INamedCacheFactory;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;

public class PeriodModeCacheProvider extends CacheProvider {

	protected int refreshInterval = -1;
	private Thread periodThread;
	private boolean shutdown = false;
	public PeriodModeCacheProvider(IObjectRegistry registry,INamedCacheFactory cacheFactory) {
		super(registry,cacheFactory);
	}

	public void initialize() {
		if(refreshInterval == -1)
			throw BuiltinExceptionFactory.createAttributeMissing(this, "refreshInterval");
		super.initialize();
		executePeriodMode();
	}
	private void executePeriodMode() {
		if (refreshInterval > 0) {
			periodThread = new Thread() {
				public void run() {
					while (!shutdown) {
						try {
							sleep(refreshInterval);
							reload();
						} catch (Exception e) {
							logger.log(Level.SEVERE, "", e);
//							throw new RuntimeException(e);
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
	@Override
	public void shutdown(){
		shutdown = true;
		if(periodThread != null && periodThread.isAlive()){
			periodThread.interrupt();
		}
	}
}
