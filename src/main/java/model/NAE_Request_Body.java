package model;

/**
 * Class for NAE request json body
 *
 * @author Miao Xiang
 *
 */
public class NAE_Request_Body {
	/**
     * a code object is wrapped in the request sent to NAE
     */
	private Code code;
	public NAE_Request_Body()
	{
		
	}
	
	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}
	

}
