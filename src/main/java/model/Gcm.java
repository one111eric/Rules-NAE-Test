package model;
/**
 * Class for gcm object in NAE response
 *
 * @author Miao Xiang
 *
 */
public class Gcm {
	private String collapse_key;
	private boolean delay_while_idle;
	private Otherdata otherdata;
	private String time_to_live;

	public Gcm() {
	}

	public String getCollapse_key() {
		return collapse_key;
	}

	public void setCollapse_key(String collapse_key) {
		this.collapse_key = collapse_key;
	}

	public boolean isDelay_while_idle() {
		return delay_while_idle;
	}

	public void setDelay_while_idle(boolean delay_while_idle) {
		this.delay_while_idle = delay_while_idle;
	}

	public Otherdata getOtherdata() {
		return otherdata;
	}

	public void setOtherdata(Otherdata otherdata) {
		this.otherdata = otherdata;
	}

	public String getTime_to_live() {
		return time_to_live;
	}

	public void setTime_to_live(String time_to_live) {
		this.time_to_live = time_to_live;
	}

}
