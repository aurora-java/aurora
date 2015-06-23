package aurora.application.task;

public class WaitTask {

	public static void execute(Long millis) throws Exception{
		Thread.sleep(millis);
	}
}
