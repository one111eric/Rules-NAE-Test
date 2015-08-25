package model;

public class NAE_Response_Body {
//private String message;
//public String getMessage() {
//	return message;
//}
//public void setMessage(String message) {
//	this.message = message;
//}
//public NAE_Response_Body(){
//	this.message="";
//}
	//change response format
	
	private String Api;
	private String App_id;
	private String Endpoint;
	private String Handler;
	private String Headers;
	private Payload payload;
	private String protocol;
	private String verb;
	private String tx_id;
	public NAE_Response_Body(){
		
	}
	public String getApi() {
		return Api;
	}
	public void setApi(String api) {
		Api = api;
	}
	public String getApp_id() {
		return App_id;
	}
	public void setApp_id(String app_id) {
		App_id = app_id;
	}
	public String getEndpoint() {
		return Endpoint;
	}
	public void setEndpoint(String endpoint) {
		Endpoint = endpoint;
	}
	public String getHandler() {
		return Handler;
	}
	public void setHandler(String handler) {
		Handler = handler;
	}
	public String getHeaders() {
		return Headers;
	}
	public void setHeaders(String headers) {
		Headers = headers;
	}
	public Payload getPayload() {
		return payload;
	}
	public void setPayload(Payload payload) {
		this.payload = payload;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getVerb() {
		return verb;
	}
	public void setVerb(String verb) {
		this.verb = verb;
	}
	public String getTx_id() {
		return tx_id;
	}
	public void setTx_id(String tx_id) {
		this.tx_id = tx_id;
	}
	
	
	
	
}
