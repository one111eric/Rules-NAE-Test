package glue;

import org.testng.Assert;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import impl.NAE_Real_Util;



public class NAECucumberTestGlues {

	private NAE_Real_Util util=new NAE_Real_Util();
	private JsonPath path;
	private Response response;
	private static final String INVALID_JSON = "test_data/Invalid_JSON.json";
	private static final String OVERSIZED_JSON = "test_data/Oversized_JSON.json";
	private static final String VALID_JSON = "test_data/Valid_JSON.json";
	private static final String INVALID_TYPE_JSON = "test_data/Invalid_Type.json";
    private static final String EMPTY_JSON="test_data/Empty_JSON.json"; 
	private static final boolean WITH_X_DEBUG_HEADER=true;
	private static final boolean WITHOUT_X_DEBUG_HEADER=false;
	private static final String MOCKSERVER_ADDRESS="http://mock.rules.vacsv.com:8080";
	private String publishurl="";
	private int beforerequest;
	private int afterrequest;
    
	@When("^I post an invalid json body")
	public void InvalidJSON() {
		this.response = util.getNAERealResponse(INVALID_JSON, "POST",WITH_X_DEBUG_HEADER);
		this.path = new JsonPath(response.asString());
		
	}

	@Then("^I should get an invalid json error message$")
	public void InvalidJSONTest() {
		Assert.assertEquals(response.getStatusCode(), 400);
		Assert.assertEquals(path.get("error"), "invalid json");
	}

	@When("^I post a Json file exceeding the character limit$")
	public void LargeJSON() {
		this.response = util.getNAERealResponse(OVERSIZED_JSON, "POST",WITH_X_DEBUG_HEADER);
		this.path = new JsonPath(response.asString());
	}

	@Then("^I should get an request too large error message$")
	public void LargeJSONTest() {
		Assert.assertEquals(response.getStatusCode(), 413);
		Assert.assertEquals(path.get("error"), "request too large");

	}

	@When("^I make the http request with Verb \"([^\"]*)\"$")
	public void WrongVerb(String verb) {
        this.response=util.getNAERealResponse(VALID_JSON, verb,WITH_X_DEBUG_HEADER);
        this.path=new JsonPath(response.asString());
	}

	@Then("^I should get http post required error message$")
	public void WrongVerbTest() {
		Assert.assertEquals(response.getStatusCode(), 400);
		Assert.assertEquals(path.get("error"), "http post required");
	}

	@When("^I post an valid json body with timestamp$")
	public void ValidJSON() {
        this.response=util.getNAERealResponse(VALID_JSON, "POST",WITH_X_DEBUG_HEADER);
        this.path=new JsonPath(response.asString());
        delay(30000);
	}

	@Then("^I should get a valid response body with corrent time$")
	public void ValidJSONTest() {
		Assert.assertEquals(response.getStatusCode(),200);
        Assert.assertTrue(util.mapResponse(response));
        String expectedtime=util.ExpectedTime(VALID_JSON);
        System.out.println(expectedtime);
        Assert.assertTrue(response.body().asString().contains(expectedtime));
	}

	@When("^I post an valid json body with unsupported event type$")
	public void WrongType() {
		this.response = util.getNAERealResponse(INVALID_TYPE_JSON, "POST",WITH_X_DEBUG_HEADER);
		this.path = new JsonPath(response.asString());
	}

	@Then("^I should get a blanket response with status 200$")
	public void WrongTypeTest() {
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().asString().replaceAll(System.getProperty("line.separator"), "").replaceAll(" ", ""), "[]");
	}
	
	@When("^I post an empty json body$")
	public void EmptyJSON(){
		this.response=util.getNAERealResponse(EMPTY_JSON, "POST",WITH_X_DEBUG_HEADER);
		this.path=new JsonPath(response.asString());
	}
	@Then("^I should get an empty json body error message$")
	public void EmptyJSONTest(){
		Assert.assertEquals(response.getStatusCode(), 400);
		Assert.assertEquals(path.get("error"), "empty body");
	}
	
	@When("^I post a valid body$")
	public void OneValidBody(){
		this.response=util.getNAERealResponse(VALID_JSON, "POST",WITH_X_DEBUG_HEADER);
		this.path=new JsonPath(response.asString());
	}
	
    @Then("^I should see the number of the request log increased by 1$")
    public void RequestsCount(){
    	
    	String endpoint=util.getEndPoint(response);
		this.publishurl=endpoint.replace(MOCKSERVER_ADDRESS, "");
		Assert.assertNotEquals(publishurl, "");
		//delay(10000);
    	System.out.println(publishurl);
        beforerequest=util.countRequests(publishurl);
        System.out.println(beforerequest);
        
        this.response=util.getNAERealResponse(VALID_JSON, "POST", WITHOUT_X_DEBUG_HEADER);
        delay(30000);
		afterrequest=util.countRequests(publishurl);
		delay(5000);
		System.out.println(afterrequest);
		int increased=afterrequest-beforerequest;
		Assert.assertEquals(increased, 1);
    }
    
    @And("^the request body is in correct json payload format$")
    public void ValidPayload(){
    	
    	String requestpayload=util.getRequestPayload(publishurl);
    	Assert.assertTrue(util.mapRequest(requestpayload));
    }
    
    @When("^I post a valid json body 100 times without X-Debug header$")
    public void ValidJSONOneTime(){
    	this.response=util.getNAERealResponse(VALID_JSON, "POST",WITHOUT_X_DEBUG_HEADER);
        this.path=new JsonPath(response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(path.get("status"), "processed");
        delay(1000);
    }
    @Then("^I should still get process successful message$")
    public void ValidJSONHundredTime(){
    	for(int i=0;i<100;i++){
    		ValidJSONOneTime();
    	}
    }
    
    @When("^I check the number of notification request")
    public void CheckRequestNumber(){
    	this.publishurl="/publish/xhs/tps/209052550323032015Comcast.cust/notifications/alarm.json";
    }
    @Then("^I should get the number")
    public void GetRequestNumber(){
    	
    	int requestnumber=util.countRequests(publishurl);
    	System.out.println("Number of Notification sent to Mock Server: "+requestnumber);
    }
    public void delay(int n){
    	try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
