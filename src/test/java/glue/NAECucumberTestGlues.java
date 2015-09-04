package glue;

import org.testng.Assert;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import impl.NAE_Real_Util;
import impl.NAE_Properties;

import org.apache.log4j.Logger;

/**
 * Class for NAE test glues
 *
 * @author Miao Xiang
 *
 */

public class NAECucumberTestGlues {
	
	
	// Logger instance
	private static final Logger LOGGER = Logger
			.getLogger(NAECucumberTestGlues.class);

	// define Test Json files (test data)
	private static final String INVALID_JSON = "test_data/Invalid_JSON.json";
	private static final String OVERSIZED_JSON = "test_data/Oversized_JSON.json";
	private static final String VALID_JSON = "test_data/Valid_JSON.json";
	private static final String INVALID_TYPE_JSON = "test_data/Invalid_Type.json";
	private static final String EMPTY_JSON = "test_data/Empty_JSON.json";

	// variable will be used for tests
	private NAE_Real_Util util = new NAE_Real_Util();
	private JsonPath path;
	private Response response;
	private String publishurl = "";
	private int beforerequest;
	private int afterrequest;

	@When("^I post an invalid json body")
	public void invalidJSON() {
		this.response = util.getNAERealResponse(INVALID_JSON, "POST",
				NAE_Properties.WITH_X_DEBUG_HEADER);
		this.path = new JsonPath(response.asString());
	}

	@Then("^I should get an invalid json error message$")
	public void invalidJSONTest() {
		Assert.assertEquals(response.getStatusCode(), 400);
		Assert.assertEquals(path.get("error"), "invalid json");
	}

	@When("^I post a Json file exceeding the character limit$")
	public void largeJSON() {
		this.response = util.getNAERealResponse(OVERSIZED_JSON, "POST",
				NAE_Properties.WITH_X_DEBUG_HEADER);
		this.path = new JsonPath(response.asString());
	}

	@Then("^I should get an request too large error message$")
	public void largeJSONTest() {
		Assert.assertEquals(response.getStatusCode(), 413);
		Assert.assertEquals(path.get("error"), "request too large");
	}

	@When("^I make the http request with Verb \"([^\"]*)\"$")
	public void wrongVerb(String verb) {
		this.response = util.getNAERealResponse(VALID_JSON, verb,
				NAE_Properties.WITH_X_DEBUG_HEADER);
		this.path = new JsonPath(response.asString());
	}

	@Then("^I should get http post required error message$")
	public void wrongVerbTest() {
		Assert.assertEquals(response.getStatusCode(), 400);
		Assert.assertEquals(path.get("error"), "http post required");
	}

	@When("^I post an valid json body with timestamp$")
	public void validJSON() {
		this.response = util.getNAERealResponse(VALID_JSON, "POST",
				NAE_Properties.WITH_X_DEBUG_HEADER);
		this.path = new JsonPath(response.asString());
		delay(30000);
	}

	@Then("^I should get a valid response body with correct time$")
	public void validJSONTest() {
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(util.mapResponse(response));
		String expectedTime = util.expectedTime(VALID_JSON);
		// System.out.println(expectedTime);
		LOGGER.debug(expectedTime);
		Assert.assertTrue(response.body().asString().contains(expectedTime));
	}

	@When("^I post an valid json body with unsupported event type$")
	public void wrongType() {
		this.response = util.getNAERealResponse(INVALID_TYPE_JSON, "POST",
				NAE_Properties.WITH_X_DEBUG_HEADER);
		this.path = new JsonPath(response.asString());
	}

	@Then("^I should get a blanket response with status 200$")
	public void wrongTypeTest() {
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(
				response.getBody().asString()
						.replaceAll(System.getProperty("line.separator"), "")
						.replaceAll(" ", ""), "[]");
	}

	@When("^I post an empty json body$")
	public void emptyJSON() {
		this.response = util.getNAERealResponse(EMPTY_JSON, "POST",
				NAE_Properties.WITH_X_DEBUG_HEADER);
		this.path = new JsonPath(response.asString());
	}

	@Then("^I should get an empty json body error message$")
	public void emptyJSONTest() {
		Assert.assertEquals(response.getStatusCode(), 400);
		Assert.assertEquals(path.get("error"), "empty body");
	}

	@When("^I post a valid body$")
	public void oneValidBody() {
		this.response = util.getNAERealResponse(VALID_JSON, "POST",
				NAE_Properties.WITH_X_DEBUG_HEADER);
		this.path = new JsonPath(response.asString());
	}

	@Then("^I should see the number of the request log increased by 1$")
	public void requestsCount() {
		delay(30000);
		String endpoint = util.getEndPoint(response);
		this.publishurl = endpoint.replace(NAE_Properties.MOCKSERVER_ADDRESS,
				"");
		Assert.assertNotEquals(publishurl, "");
		LOGGER.debug(publishurl);
		beforerequest = util.countRequests(publishurl);
		LOGGER.debug(beforerequest);

		this.response = util.getNAERealResponse(VALID_JSON, "POST",
				NAE_Properties.WITHOUT_X_DEBUG_HEADER);
		delay(30000);
		afterrequest = util.countRequests(publishurl);
		LOGGER.debug(afterrequest);
		int increased = afterrequest - beforerequest;
		Assert.assertEquals(increased, 1);
	}

	@And("^the request body is in correct json payload format$")
	public void validPayload() {

		String requestpayload = util.getRequestPayload(publishurl);
		Assert.assertTrue(util.mapRequest(requestpayload));
	}

	@When("^I post an invalid json body 100 times without X-Debug header$")
	public void invalidJSONHundredTime() {
		for (int i = 0; i < 100; i++) {
			this.response = util.getNAERealResponse(INVALID_JSON, "POST",
					NAE_Properties.WITHOUT_X_DEBUG_HEADER);
			this.path = new JsonPath(response.asString());
		}
	}

	@When("^I post a valid json body 100 times without X-Debug header$")
	public void validJSONHundredTime() {
		for (int i = 0; i < 100; i++) {
			this.response = util.getNAERealResponse(VALID_JSON, "POST",
					NAE_Properties.WITHOUT_X_DEBUG_HEADER);
			this.path = new JsonPath(response.asString());
		}
	}

	@Then("^I should still get process successful message$")
	public void verifyAfterHundred() {
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(path.get("status"), "processed");
	}

	@When("^I check the number of notification request")
	public void checkRequestNumber() {
		this.publishurl = "/publish/xhs/tps/209052550323032015Comcast.cust/notifications/alarm.json";
	}

	@Then("^I should get the number")
	public void getRequestNumber() {

		int requestnumber = util.countRequests(publishurl);
		Assert.assertTrue(requestnumber >= 0);
		LOGGER.debug("Number of Notification sent to Mock Server: "
				+ requestnumber);
	}

	// A general method to add a thread wait
	public void delay(int n) {
		try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
			// e.printStackTrace();
			LOGGER.error(e);
		}
	}
}
