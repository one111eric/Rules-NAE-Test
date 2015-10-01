package impl;

public class TimeAndZone {
	private Long timestamp;
	private String timezone;
    public TimeAndZone(Long time,String zone){
    	this.timestamp=time;
    	this.timezone=zone;
    }
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
