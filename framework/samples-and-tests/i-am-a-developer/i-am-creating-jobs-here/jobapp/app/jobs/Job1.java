package jobs;
import play.jobs.*;
import play.*;
@OnApplicationStart(async=true)
public class Job1 extends Job {
  public void doJob() throws Exception{
      Logger.info("Job starting");
      Thread.sleep(1 * 1000);
      Logger.info("Job done");
  }
}
