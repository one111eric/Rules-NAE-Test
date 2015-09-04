package model;

import java.io.IOException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Class for Payload object in NAE response
 *
 * @author Miao Xiang
 *
 */
public class Payload {
	/**
     * all fields/objects included in payload object
     */
	@JsonProperty("apns")
	private Apns apns;
    @JsonProperty("gcm")
	private Gcm gcm;
	public Payload(){
		
	}
	//Constructor with string parameter to avoid mapping error
    public Payload(String x) throws JsonParseException, JsonMappingException, IOException
    {   
    	ObjectMapper mapper=new ObjectMapper();
    	Payload payload=mapper.readValue(x, Payload.class);
    	this.apns=payload.getApns();
    	this.gcm=payload.getGcm();
    }
    
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
