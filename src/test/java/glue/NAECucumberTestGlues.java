package glue;

import org.testng.Assert;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

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
	
	@When("^I post a valid json body 100 times without X-Debug header$")
	public void ValidJSONNoheader(){
		this.response=util.getNAERealResponse(VALID_JSON, "POST", WITHOUT_X_DEBUG_HEADER);
		this.path=new JsonPath(response.asString());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(path.get("status"), "processed");
	}
	@Then("^I should still get process successful message")
	public void ValidJSONHundredTimesTest(){
		for(int i=0;i<100;i++){
			ValidJSONNoheader();
		}
	}
}
