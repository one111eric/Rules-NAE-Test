package impl;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;

import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.given;

/**
 * Class for Event Firing Setups
 *
 * @author Miao Xiang
 *
 */
public class EventSetup {
	public static final String RULES_LOCATION_URL="http://rest-qa.rules.vacsv.com/locations/";
	public static final String MOLECULE_MAPPING_URL="http://molecule.qa.rules.vacsv.com/mappings/xh/";
	public static final String RULE_JSON="test_data/AipRule.json";
	public static final String EVENT_JSON="test_data/EventToEEL.json";
	private static final String AIP_EEL_ENDPOINT="http://eel.qa.rules.vacsv.com/elementsevent";
	                                      
	private NAE_Real_Util util=new NAE_Real_Util();
	
	private String location;
	private String site;
	private String event;
	private Map<String,String> headers=new HashMap<String,String>();

	public void eventSetup() throws Throwable {
		headers.put("Xrs-Tenant-Id", "xh");
		setupLocation();
		setupSite();
		setupRule();
		setupEvent();
	}
	
	//getters
	public String getLocation() {
		return location;
	}

	public String getSite() {
		return site;
	}

	public String getEvent() {
		return event;
	}
	
	public void setupLocation() throws Throwable {
        String locationName="MiaoLocation00001";
        String locationEndPoint=RULES_LOCATION_URL+locationName;         
        Response response=null;
        //delete the existing location
        response=given().log().all().headers(this.headers)
        		 .expect().statusCode(200)
        		 .delete(locationEndPoint);
        //setup location
        response=given().log().all().headers(this.headers)
        		 .expect().statusCode(200)
        	     .put(locationEndPoint);
        response.prettyPrint();
        this.location=locationName;
	}
	
	public void setupSite() throws Throwable {
        String siteId= "850520";
        String moleculeEndPoint=MOLECULE_MAPPING_URL+siteId;       
        Response response=null;
        response=given().log().all().headers(this.headers).body(this.location)
        		 .expect().statusCode(200)
        		 .put(moleculeEndPoint);
        response.prettyPrint();
		this.site=siteId;
	}

	public void setupRule() throws Throwable {
		String ruleNumber="1234";
		String myRule=util.getFile(RULE_JSON);
		System.out.println(myRule);
		myRule=myRule.replace("locationName", location);
        String ruleEndPoint=RULES_LOCATION_URL+location+"/rules/"+ruleNumber;
        Response response=null;
        response=given().log().all().headers(this.headers).body(myRule)
        		 .expect().statusCode(200)
        		 .put(ruleEndPoint);
        response.prettyPrint();
       	}
	
	public void setupEvent(){
		String myEvent=util.getFile(EVENT_JSON);
		System.out.println(myEvent);
		myEvent=myEvent.replace("siteId", getSite());
		System.out.println(myEvent);
		this.event=myEvent;
	}
	
	
	
	public void fireEvent(){
		Response response=null;
		response=given().log().all().body(this.event)
				.expect().statusCode(200)
				.post(AIP_EEL_ENDPOINT);
		response.prettyPrint();
	}
	
}
