package tracker.cache;

import org.apache.jcs.access.exception.CacheException;
import tracker.accounts.Account;

public class AccountsCache extends AbstractCache<Account> {
	public String getRegion() { return "accounts"; }

	public Account getById(Long id) throws Throwable {
		Account value;
		if (this.isEnabled()) {
			try {
				value = (Account)this.getCacheAccess().get("id-" + id);
			} catch (ClassCastException classCastException) {
				value = null;
			}
			if (value == null) {
				value = Account.byId(id);
				if (value != null) {
					this.put(value);
				}
			}
		} else {
			value = Account.byId(id);
		}
		return value;
	}

	public Account getByLogin(String login) throws Throwable {
		Account value;
		if (this.isEnabled()) {
			try {
				value = (Account)this.getCacheAccess().get("login-" + login);
			} catch (ClassCastException classCastException) {
				value = null;
			}
			if (value == null) {
				value = Account.byLogin(login);
				if (value != null) {
					this.put(value);
				}
			}
		} else {
			value = Account.byLogin(login);
		}
		return value;
	}

	public Account getByPasskey(String passkey) throws Throwable {
		Account value;
		if (this.isEnabled()) {
			try {
				value = (Account)this.getCacheAccess().get("passkey-" + passkey);
			} catch (ClassCastException classCastException) {
				value = null;
			}
			if (value == null) {
				value = Account.byPasskey(passkey);
				if (value != null) {
					this.put(value);
				}
			}
		} else {
			value = Account.byPasskey(passkey);
		}
		return value;
	}

	private static AccountsCache instance;

	public static AccountsCache getInstance() throws CacheException {
		if (instance == null) {
			synchronized (AccountsCache.class) {
				if (instance == null) {
					instance = new AccountsCache();
				}
			}
		}
		return instance;
	}

	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
