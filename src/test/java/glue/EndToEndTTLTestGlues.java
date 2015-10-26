package glue;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.jayway.restassured.RestAssured.given;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import impl.Commons;
import impl.EventSetup;
import impl.NAE_Properties;
import impl.NAE_Real_Util;
import impl.ServerStatusCodes;
import impl.TimeData;

import org.apache.log4j.Logger;
import org.testng.Assert;

import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.jayway.restassured.response.Response;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class for AIP end to end TTL feature test
 *
 * @author Miao Xiang
 *
 */
public class EndToEndTTLTestGlues {
	private static final Logger LOGGER = Logger
			.getLogger(EndToEndTTLTestGlues.class);
	private EventSetup es;
	private String location;
	private String site;
	private String event;
	private int requestNumber;
	private String publishUrl = "";
	private NAE_Real_Util util = new NAE_Real_Util();
	private int notifReceived;
	private static final String INVALID_EVENT = "test_data/WrongTopicEvent.json";
	private static final String AIP_HANDLER = "/xhs";
	private int eventPosted=0;
	private List<TimeData> timeDataList=new ArrayList<TimeData>();
	
//	@Before
//	public void beforeScenario(){
//		this.timeDataList=new ArrayList<TimeData>();
//		
//	}
	
	@After
	//And("^I write the data to file$")
	public void afterScenario(){
		for(TimeData td : timeDataList){
			String eventId=td.getEventId();
			//LOGGER.debug(eventId);
			//Commons.delay(10000);
			WireMock.configureFor(NAE_Properties.MOCK_SERVER, NAE_Properties.MOCK_SERVER_PORT);
			RequestPatternBuilder builder = new RequestPatternBuilder(
					RequestMethod.POST, urlMatching("/locations/424242qa/.*")).withHeader("X-B3-TraceId", equalTo(eventId));
			List<LoggedRequest> reqs = findAll(builder);
			int listSize = reqs.size();
			if(listSize>=1){
				
			    //LOGGER.debug(eventId+ ", "+ td.getCurrentTimestamp()+ ", "+ reqs.get(listSize-1).getHeader("X-B3-TraceId")+": "+reqs.get(listSize-1).getBodyAsString());
				try(Writer writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("times.csv",true)))){
					writer.write(eventId+","+td.getCurrentTimestamp()+","+reqs.get(listSize-1).getLoggedDate().getTime()+",\n");
					writer.flush();
				    writer.close();
				} catch (FileNotFoundException e) {
					LOGGER.error(e);
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
		}
	}
	
	@Given("^\"([^\"]*)\" with site \"([^\"]*)\" has a rule with TTL (\\d+) seconds$")
	public void setupTTLRuleToLocation(String locationName,String siteId,int ttlTime) throws Throwable{
		es=new EventSetup();
		es.eventSetupNew(locationName,siteId,"xh",true,ttlTime);
	}
	@When("^I check the number of requests from \"([^\"]*)\" received by mock server$")
	public void getLocationRequestNumber(String location) {
		this.location = location;
		this.publishUrl = "/locations/" + location
				+ "/.*";

	}
	
	@Then("^I should have a number$")
	public void verifyRequestNumber() {
		this.requestNumber = util.countRequests(publishUrl);
		Assert.assertTrue(requestNumber >= 0);
		LOGGER.debug("Number of Notification sent to Mock Server from "
				+ location + " : " + requestNumber);
	}
	
	
	@And("^I post an AIP Event of \"([^\"]*)\" to EEL with SessionId \"([^\"]*)\"$")
	public void postEventWithSessionId(String locationName,String sessionId){
		LOGGER.debug(this.eventPosted);
		es.createUniqueEventNew(es.getEvent(), this.eventPosted, sessionId);
		LOGGER.debug(es.getEventId());
		String currentTimestamp=String.valueOf(System.currentTimeMillis());
		String eventId=es.getEventId();
		es.fireEvent();
		this.eventPosted+=1;
		TimeData td=new TimeData(eventId,currentTimestamp);
		timeDataList.add(td);
	}
	
	@And("^I post an AIP Event of \"([^\"]*)\" with \"([^\"]*)\" to EEL with SessionId \"([^\"]*)\"$")
	public void postEventWithSessionIdSiteId(String locationName,String siteId, String sessionId){
		LOGGER.debug(this.eventPosted);
		es.setupEventNew(siteId);
		es.createUniqueEventNew(es.getEvent(), this.eventPosted, sessionId);
		LOGGER.debug(es.getEventId());
		String currentTimestamp=String.valueOf(System.currentTimeMillis());
		String eventId=es.getEventId();
		es.fireEvent();
		this.eventPosted+=1;
		TimeData td=new TimeData(eventId,currentTimestamp);
		timeDataList.add(td);
	}
	
	
	@And("^I wait (\\d+) seconds$")
	public void threadWait(int waitTime){
		Commons.delay(waitTime*1000);
	}
	
	@Then("^I should see the number of the request to location \"([^\"]*)\" increased by (\\d+)$")
	public void checkRequestsIncreasedBy(String locationName,int x){
		Commons.delay(25000);
		int newRequestNumber = util.countRequests(publishUrl);
		LOGGER.debug(publishUrl);
		LOGGER.debug("Number of Notification sent to Mock Server from "
				+ locationName + " : " + newRequestNumber);
		int increased = newRequestNumber - requestNumber;
		this.notifReceived = increased;
		LOGGER.debug("Number of new Notification sent to Mock Server from "
				+ location + " : " + notifReceived);
		List<String> lastRequestBodyList = util.getRequestPayloadList(
				publishUrl, notifReceived);
		for (int i = 0; i < notifReceived; i++) {
			LOGGER.debug(lastRequestBodyList.get(i));
		}
		Assert.assertEquals(increased, x);
	}
	
	@When("^I check the number of all requests received by mock server$")
	public void getAllRequestNumber(){
		this.publishUrl = "/publish/xhs/qa/.*";
	}
	
	@Then("^I should get a number of all requests received by mock server$")
	public void veryfyAllRequestNumber(){
		this.requestNumber = util.countRequests(publishUrl);
		Assert.assertTrue(requestNumber >= 0);
		LOGGER.debug("Number of Notification sent to Mock Server from all locations" + " : " + requestNumber);
	}
	
	@Then("^I should see the number of all requests to mock server increased by (\\d+)$")
	public void checkAllRequestsIncreasedBy(int x){
		Commons.delay(25000);
		int newRequestNumber = util.countRequests(publishUrl);
		LOGGER.debug(publishUrl);
		LOGGER.debug("Number of Notification sent to Mock Server from all locations"
				+ " : " + newRequestNumber);
		int increased = newRequestNumber - requestNumber;
		this.notifReceived = increased;
		LOGGER.debug("Number of new Notification sent to Mock Server from all locations"
				 + " : " + notifReceived);
		List<String> lastRequestBodyList = util.getRequestPayloadList(
				publishUrl, notifReceived);
		for (int i = 0; i < notifReceived; i++) {
			LOGGER.debug(lastRequestBodyList.get(i));
		}
		Assert.assertEquals(increased, x);
	}
}
