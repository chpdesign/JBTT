package tracker.cache;

import org.apache.jcs.access.exception.CacheException;
import tracker.accounts.ActiveTorrents;

public class ActiveTorrentsCache extends AbstractCache<ActiveTorrents> {
	public String getRegion() { return "activetorrents"; }

	public ActiveTorrents getByAccountId(Long id) throws Throwable {
		ActiveTorrents value;
		if (this.isEnabled()) {
			try {
				value = (ActiveTorrents)this.getCacheAccess().get("accountId-" + id);
			} catch (ClassCastException classCastException) {
				value = null;
			}

			if (value == null) {
				value = ActiveTorrents.byAccountId(id);
				if (value != null) {
					this.put(value);
				}
			}
		} else {
			value = ActiveTorrents.byAccountId(id);
		}
		return value;
	}

	private static volatile ActiveTorrentsCache instance;

	public static ActiveTorrentsCache getInstance() throws CacheException {
		if (instance == null) {
			synchronized (ActiveTorrentsCache.class) {
				if (instance == null) {
					instance = new ActiveTorrentsCache();
				}
			}
		}
		return instance;
	}

	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
