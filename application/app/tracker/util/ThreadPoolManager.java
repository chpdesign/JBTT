package tracker.util;

import tracker.Config;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolManager {
	private ScheduledThreadPoolExecutor threadPool;

	public ScheduledFuture<?> schedule(Runnable runnable, long delay) {
		try {
			return this.threadPool.schedule(runnable, delay, TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException e) {
			return null; /* shutdown, ignore */
		}
	}

	protected ThreadPoolManager() {
		this.threadPool = new ScheduledThreadPoolExecutor(Config.getInt("threads.poolSize"), new ScheduledThreadFactory());
	}

	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	protected static ThreadPoolManager instance;

	public static ThreadPoolManager getInstance() {
		if (instance == null) {
			synchronized (ThreadPoolManager.class) {
				if (instance == null) {
					instance = new ThreadPoolManager();
				}
			}
		}
		return instance;
	}

	private class ScheduledThreadFactory implements ThreadFactory {
		private AtomicInteger threadNumber = new AtomicInteger(1);

		public Thread newThread(Runnable runnable) {
			return this.newThread(runnable, Thread.NORM_PRIORITY);
		}

		public Thread newThread(Runnable runnable, int priority) {
			Thread thread = new Thread(runnable);
			thread.setName("scheduledThread-" + this.threadNumber.getAndIncrement());
			thread.setPriority(priority);
			return thread;
		}
	}
}
