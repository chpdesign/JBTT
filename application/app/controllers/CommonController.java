package controllers;

import play.Logger;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;
import tracker.accounts.Account;
import tracker.cache.AccountsCache;

import java.sql.Timestamp;

public class CommonController extends Controller {
	public static Account currentAccount;

	protected static long started = 0;

	@Before
	protected static void updateCurrentUser() throws Throwable {
		CommonController.currentAccount = null;

		if (!session.contains("accountId")) {
			return;
		}

		Long accountId;
		try {
			accountId = Long.parseLong(session.get("accountId"));
		} catch (NumberFormatException numberFormatException) {
			session.clear();
			return;
		}

		Account account = AccountsCache.getInstance().getById(accountId);
		if (account == null) {
			session.clear();
			return;
		}

		account.setLastActivityDate(new Timestamp(System.currentTimeMillis()));
		account.save();

		CommonController.currentAccount = account;
		renderArgs.put("currentAccount", account);
	}




	@Before
	protected static void countBefore() throws Throwable {
		started = System.currentTimeMillis();
	}

	@After
	protected static void countAfter() throws Throwable {
		long finished = System.currentTimeMillis();
		Logger.debug("Generated with: " + (finished - started));
	}
}
