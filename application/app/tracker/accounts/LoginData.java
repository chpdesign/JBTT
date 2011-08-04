package tracker.accounts;

import play.mvc.Scope;
import tracker.cache.AccountsCache;

public class LoginData {
	public String login;
	public String password;

	public boolean authenticate(Scope.Session session) throws Throwable {
		Account account = AccountsCache.getInstance().getByLogin(this.login);

		if (account == null) {
			return false;
		}

		String passwordHash = Account.hashPassword(this.login, this.password);

		if (!account.getPasswordHash().equalsIgnoreCase(passwordHash)) {
			return false;
		}

		session.put("accountId", account.getId());
		return true;
	}
}
