package jobs;

import play.Logger;
import play.exceptions.UnexpectedException;
import play.jobs.Job;
import tracker.DatabaseFactory;

/** Запуск каждую минуту **/
// @On("0 * * * * ?")
// @Every("5s")
public class Statistics extends Job {
    public void doJob() {
		try {
			String statistics = "Statistics:\n";
			statistics += "DB Busy Connections: " + DatabaseFactory.getInstance().getBusyConnectionCount() + "\n";
			statistics += "DB Idle Connections: " + DatabaseFactory.getInstance().getIdleConnectionCount() + "\n";
			statistics += "--------------------";
			Logger.info(statistics);
		} catch (Throwable e) {
			throw new UnexpectedException(e);
		}
    }
}
