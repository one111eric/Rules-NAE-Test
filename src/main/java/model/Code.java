package model;

import java.util.List;

/**
 * Class for code object in NAE request
 *
 * @author Miao Xiang
 *
 */
public class Code {
	private String location;
	private String tenant;
	private String type;
	private List<String> transport;
    private Params params;
    private Data data;
	public Code() {
			
	}
    public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
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
