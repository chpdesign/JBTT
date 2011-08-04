package controllers;

import controllers.site.Profile;
import play.Logger;
import tracker.accounts.Account;
import tracker.cache.AccountsCache;

public class SecuredController extends CommonController {
	protected static void checkAuthentification() throws Throwable {
		if (session.get("accountId") == null) {
			flash.put("returnUrl", request.url);
			Profile.login();
		}

		Long accountId = Long.parseLong(session.get("accountId"));
        Account account = AccountsCache.getInstance().getById(accountId);
		if (account == null) {
			Logger.error("Некорректный ID учетной записи в сессии пользователя. ID: " + accountId + " Token: " + session.getAuthenticityToken());
			session.clear();
			Profile.login();
		}
	}
}
