package tracker.cache;

import org.apache.jcs.JCS;
import org.apache.jcs.access.CacheAccess;
import org.apache.jcs.access.exception.CacheException;
import tracker.Config;

public abstract class AbstractCache<ItemType extends ICache> {
	protected boolean enabled = true;

	public AbstractCache() {
		this.enabled = Config.getBoolean("cache." + this.getRegion() + ".enabled");
	}

	public synchronized void put(ItemType value) throws CacheException {
		if (!this.isEnabled()) {
			return;
		}

		for (String key : value.getCacheKeys()) {
			this.getCacheAccess().put(key, value);
		}
	}

	public void remove(ItemType value) throws CacheException {
		if (!this.isEnabled()) {
			return;
		}

		this.getCacheAccess().remove(value);
	}

	public CacheAccess getCacheAccess() throws CacheException {
		return JCS.getAccess(this.getRegion());
	}

	public String getRegion() {
		return "default";
	}

	public boolean isEnabled() {
		return this.enabled;
	}
}
