package impl;

/**
 * Class for holding timestamp and timezone
 *
 * @author Miao Xiang
 *
 */
public class TimeAndZone {
	private Long timestamp;
	private String timezone;
	//constructor
    public TimeAndZone(Long time,String zone){
    	this.timestamp=time;
    	this.timezone=zone;
    }
  //getters and setters
	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
}
