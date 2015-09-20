package model;

public class ProvisionsBody {
	public ProvisionsBody(){
		
	}
	public ProvisionsBody(String location, String xh){
		this.location=location;
		this.xh=xh;
	}
private String location;
private String xh;
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
