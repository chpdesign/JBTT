package controllers.site;

import controllers.CommonController;
import play.Logger;
import tracker.util.bbcode.BBString;

public class Index extends CommonController {

    public static void index() throws Throwable {
		Logger.info("---");

//		Runnable runnable = new Runnable() {
//			public void run() {
//				try {
//					for (int i = 177354; i < 1000000; i++) {
//						Account.create("test" + i, "123098", "test" + i + "@lafayette.su", "Test " + i);
//					}
//				} catch (Throwable exception) {
//					throw new UnexpectedException(exception);
//				}
//			}
//		};
//		Thread thread = new Thread(runnable);
//		thread.run();

//		ScheduledFuture scheduledFuture = Executors.newSingleThreadScheduledExecutor().schedule(runnable, 5, TimeUnit.SECONDS);
//		Logger.info("Scheduled");

		// Config.load();
		Logger.info("---");
    }

	public static void notImplemented() throws Throwable {
		throw new Exception("Not implemented");
	}

	public static void test2() throws Throwable {
//		Configuration configuration = new Configuration();
//
//		configuration.setCacheMode(Configuration.CacheMode.LOCAL);
//		configuration.setTransactionManagerLookupClass("org.jboss.cache.transaction.GenericTransactionManagerLookup");
//
//
//		CacheFactory factory = new DefaultCacheFactory();
//		Cache cache = factory.createCache("replSync-service.xml"); // expects this file to be in classpath
//		cache.stop();
//
//		renderText("finished2");
	}

	public static void test() throws Throwable {
		BBString bbstring = new BBString();

//		HtmlUtils.test();


//		// ConcurrentLinkedHashMap<Long, Account> cache = ConcurrentLinkedHashMap.;
//
////		JCS.defineRegion("testReg");
//
//		Account account1 = Account.byId(1L);
//		Account account2 = Account.byId(1L);
//
//		// Сохраняем первый аккаунт.
////		AccountsCache.getInstance().put(account1);
//
//		// Обновляем его информацию.
//		account1.setEmail("731621@gmail.com");
////		account1.setEmail("goryuchkin@gmail.com");
//		account1.save();
//
//		// Получаем из кэша третий аккаунт.
//		Account account3 = AccountsCache.getInstance().getById(account1.getId());
//		Account account4 = AccountsCache.getInstance().getByPasskey(account1.getPasskey());
//
//
//		Logger.info("1: " + account1.getEmail()); // Измененный.
//		Logger.info("2: " + account2.getEmail()); // Оригинальный, полученный из БД перед изменением первого.
//		Logger.info("3: " + account3.getEmail()); // Полученный из кэша после изменения.
//		Logger.info("4: " + account4.getEmail()); // Из кэша по passkey.
//
//		renderText("finished");
	}
}
