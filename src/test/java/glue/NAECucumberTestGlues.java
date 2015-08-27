package glue;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import impl.NAE_Real_Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class NAECucumberTestGlues {

	private NAE_Real_Util util=new NAE_Real_Util();
	private JsonPath path;
	private Response response;
	private static final String INVALID_JSON = "test_data/Invalid_JSON.json";
	private static final String OVERSIZED_JSON = "test_data/Oversized_JSON.json";
	private static final String VALID_JSON = "test_data/Valid_JSON.json";
	private static final String INVALID_TYPE_JSON = "test_data/Invalid_Type.json";


	
	@When("^I post an invalid json body")
	public void InvalidJSON() {
		this.response = util.getNAERealResponse(INVALID_JSON, "POST");
		this.path = new JsonPath(response.asString());
		
	}

	@Then("^I should get an invalid json error message$")
	public void InvalidJSONTest() {
		Assert.assertEquals(response.getStatusCode(), 400);
		Assert.assertEquals(path.get("error"), "invalid json");
	}

	@When("^I post a Json file exceeding the character limit$")
	public void LargeJSON() {
		this.response = util.getNAERealResponse(OVERSIZED_JSON, "POST");
		this.path = new JsonPath(response.asString());
	}

	@Then("^I should get an request too large error message$")
	public void LargeJSONTest() {
		Assert.assertEquals(response.getStatusCode(), 413);
		Assert.assertEquals(path.get("error"), "request too large");

	}

	@When("^I make the http request with Verb \"([^\"]*)\"$")
	public void WrongVerb(String verb) {
        this.response=util.getNAERealResponse(VALID_JSON, verb);
        this.path=new JsonPath(response.asString());
	}

	@Then("^I should get http post required error message$")
	public void WrongVerbTest() {
		Assert.assertEquals(response.getStatusCode(), 400);
		Assert.assertEquals(path.get("error"), "http post required");
	}

	@When("^I post an valid json body with timestamp$")
	public void ValidJSON() {
        this.response=util.getNAERealResponse(VALID_JSON, "POST");
        this.path=new JsonPath(response.asString());
	}

	@Then("^I should get a valid response body with corrent time$")
	public void ValidJSONTest() {
        Assert.assertTrue(util.mapResponse(response));
	}

	@When("^I post an valid json body with unsupported event type$")
	public void WrongType() {
		this.response = util.getNAERealResponse(INVALID_TYPE_JSON, "POST");
		this.path = new JsonPath(response.asString());
	}

	@Then("^I should get a blanket response with status 200$")
	public void WrongTypeTest() {
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().asString().replaceAll(System.getProperty("line.separator"), "").replaceAll(" ", ""), "[]");
	}
	
}
