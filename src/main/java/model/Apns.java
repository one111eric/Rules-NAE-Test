package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Apns {
private String alert;
private int badge;
@JsonProperty("content-available")
private int content_available;
private int expires;
@JsonProperty("loc_key")
private String loc_key;
private String otherdata;
private String sound;
public String getAlert() {
	return alert;
}
public void setAlert(String alert) {
	this.alert = alert;
}
public int getBadge() {
	return badge;
}
public void setBadge(int badge) {
	this.badge = badge;
}
public int getContent_available() {
	return content_available;
}
public void setContent_available(int content_available) {
	this.content_available = content_available;
}
public int getExpires() {
	return expires;
}
public void setExpires(int expires) {
	this.expires = expires;
}
public String getLoc_key() {
	return loc_key;
}
public void setLoc_key(String loc_key) {
	this.loc_key = loc_key;
}
public String getOtherdata() {
	return otherdata;
}
public void setOtherdata(String otherdata) {
	this.otherdata = otherdata;
}
public String getSound() {
	return sound;
}
public void setSound(String sound) {
	this.sound = sound;
}


}
