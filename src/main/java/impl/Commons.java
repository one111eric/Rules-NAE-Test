package impl;

import org.apache.log4j.Logger;

/**
 * Class for common methods used across all tests
 *
 * @author Miao Xiang
 *
 */
public class Commons {
	private static final Logger LOGGER = Logger.getLogger(Commons.class);

	/**
	 * Method to add a delay to current process
	 *
	 * @param int: number of milliseconds of delay
	 *
	 */
	public static void delay(int n) {
		try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}
	}

}
