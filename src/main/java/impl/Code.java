package impl;

import java.util.ArrayList;
import java.util.List;

public class Code {
	private String type;
	private String app;
    private String location;
    private List<String> transport;
    private Params params;
    private Data data;
	public Code() {
		//this.zone="";
		this.type="";
		this.app="";
		this.location="";
		this.transport=new ArrayList<String>();
		this.transport.add("emo");
		this.params=new Params();
		this.data=new Data();
		data.setZone("front door");
			
	}
    public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<String> getTransport() {
		return transport;
	}

	public void setTransport(List<String> transport) {
		this.transport = transport;
	}

	public Params getParams() {
		return params;
	}

	public void setParams(Params params) {
		this.params = params;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
}
