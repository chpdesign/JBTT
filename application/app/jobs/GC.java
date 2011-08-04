package jobs;

import play.Logger;
import play.jobs.Job;
import play.jobs.On;

/** Запуск каждую минуту **/
// @On("0 * * * * ?")
public class GC extends Job {
    public void doJob() {
       //do stuff
		Logger.info("GC started!");

		System.runFinalization();
		System.gc();
    }
}
