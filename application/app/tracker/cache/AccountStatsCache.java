package tracker.cache;

import org.apache.jcs.access.exception.CacheException;
import tracker.accounts.AccountStats;

public class AccountStatsCache extends AbstractCache<AccountStats> {
	public String getRegion() { return "accountstats"; }

	public AccountStats getByAccountId(Long id) throws Throwable {
		AccountStats value;
		if (this.isEnabled()) {
			try {
				value = (AccountStats)this.getCacheAccess().get("accountId-" + id);
			} catch (ClassCastException classCastException) {
				value = null;
			}
			if (value == null) {
				value = AccountStats.byAccountId(id);
				if (value != null) {
					this.put(value);
				}
			}
		} else {
			value = AccountStats.byAccountId(id);
		}
		return value;
	}

	private static volatile AccountStatsCache instance;

	public static AccountStatsCache getInstance() throws CacheException {
		if (instance == null) {
			synchronized (AccountStatsCache.class) {
				if (instance == null) {
					instance = new AccountStatsCache();
				}
			}
		}
		return instance;
	}

	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
