package aurora.application.features.cache;

import java.util.Timer;
import java.util.TimerTask;

import uncertain.cache.INamedCacheFactory;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;

public class PeriodModeCacheProvider extends CacheProvider {

	protected int refreshInterval = -1;

	public PeriodModeCacheProvider(IObjectRegistry registry, INamedCacheFactory cacheFactory) {
		super(registry, cacheFactory);
	}

	public void initialize() {
		if (refreshInterval == -1)
			throw BuiltinExceptionFactory.createAttributeMissing(this, "refreshInterval");
		super.initialize();
	}

	protected void initReloadTimer() {
		reloadTimer = new Timer(getCacheName()+"_reload_timer");
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				while (!shutdown) {
					synchronized (reloadLock) {
						try {
							reloadLock.wait(refreshInterval);
						} catch (InterruptedException e) {
						}
						if (!shutdown)
							reload();
					}
				}
			}
		};
		reloadTimer.schedule(timerTask, 0);
	}

	public int getRefreshInterval() {
		return refreshInterval;
	}

	public void setRefreshInterval(int refreshInterval) {
		this.refreshInterval = refreshInterval;
	}
}
