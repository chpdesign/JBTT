package library;

public class Scheduler {
	private Scheduler() throws Throwable {
		new Thread(new Runnable() {
			public void run() {
				/* ... */

				// Thread.sleep(1000L);
			}
		}).start();
	}

	private static Scheduler instance = null;

	public static Scheduler getInstance() throws Throwable {
		if (instance == null) {
			synchronized (instance) {
				if (instance == null) {
					instance = new Scheduler();
				}
			}
		}
		return instance;
	}
}
