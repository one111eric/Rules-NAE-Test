package impl;


import org.apache.log4j.Logger;


public class Commons {
	private static final Logger LOGGER=Logger.getLogger(Commons.class);
	public static void delay(int n) {
		try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}
	}
	
	
	
}
