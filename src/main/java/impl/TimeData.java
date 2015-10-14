package impl;

public class TimeData {
String eventId;
String currentTimestamp;
String receiveTimestamp;
public TimeData(){
	
}
public TimeData(String eventId,String currentTimestamp){
	this.eventId=eventId;
	this.currentTimestamp=currentTimestamp;
}
public String getEventId() {
	return eventId;
}
public void setEventId(String eventId) {
	this.eventId = eventId;
}
public String getCurrentTimestamp() {
	return currentTimestamp;
}
public void setCurrentTimestamp(String currentTimestamp) {
	this.currentTimestamp = currentTimestamp;
}
public String getReceiveTimestamp() {
	return receiveTimestamp;
}
public void setReceiveTimestamp(String receiveTimestamp) {
	this.receiveTimestamp = receiveTimestamp;
}

}
