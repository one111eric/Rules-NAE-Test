package model;

/**
 * Class for provisions body object in provisions api
 *
 * @author Miao Xiang
 *
 */
public class ProvisionsBody {
	/**
	 * General constructor
	 */
	public ProvisionsBody() {

	}

	/**
	 * Constructor with two params
	 */
	public ProvisionsBody(String location, String xh) {
		this.location = location;
		this.xh = xh;
	}

	private String location;
	private String xh;

	/**
	 * getters and setters
	 */
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getXh() {
		return xh;
	}

	public void setXh(String xh) {
		this.xh = xh;
	}

}
