package model;

public class Payload {
    public Payload()
    {
    	
    }
	private Apns apns;
	private Gcm gcm;
	public Apns getApns() {
		return apns;
	}
	public void setApns(Apns apns) {
		this.apns = apns;
	}
	public Gcm getGcm() {
		return gcm;
	}
	public void setGcm(Gcm gcm) {
		this.gcm = gcm;
	}
	
	
}
