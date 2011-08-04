package jobs;

import org.apache.jcs.JCS;
import play.exceptions.UnexpectedException;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import tracker.Config;

@OnApplicationStart
public class Bootstrap extends Job {
    public void doJob() {
		try {
			Config.load();

			JCS.setConfigFilename("application/conf/cache.ccf");
//			AccountsCache.getInstance();
//			TorrentsCache.getInstance();

			// TODO: clean tmp/uploads directory
		} catch (Throwable exception) {
			throw new UnexpectedException(exception);
		}
    }
}
