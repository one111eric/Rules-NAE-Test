package model;

/**
 * Class for data object in NAE request
 *
 * @author Miao Xiang
 *
 */
public class Data {
	private long timestamp;
	private String zone;
	public Data(){
		
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	
    
}
