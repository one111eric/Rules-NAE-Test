package model;
/**
 * Class for otherdata object in NAE response
 *
 * @author Miao Xiang
 *
 */
public class Otherdata {
	/**
     * all fields/objects included in otherdata object
     */
	public Otherdata(){
	}
	
private String message;
private String title;
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}

}
