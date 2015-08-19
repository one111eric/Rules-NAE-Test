package impl;

public class NAE_Request_Body {
	private Code code;
    
	public NAE_Request_Body()
	{
		this.code=new Code();
		
		code.setType("ALARM_IN_PROGRESS");
		code.setApp("myapp");
		code.setLocation("209052550323032015Comcast.cust");
		//code.setZone("back door");
	}
	
	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}
	

}
