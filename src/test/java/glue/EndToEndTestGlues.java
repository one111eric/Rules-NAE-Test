package glue;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

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
import impl.TimeAndZone;
import impl.TimeData;

import org.apache.log4j.Logger;
import org.testng.Assert;

import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class for AIP end to end test glues
 *
 * @author Miao Xiang
 *
 */
public class EndToEndTestGlues {
	private static final Logger LOGGER = Logger
			.getLogger(EndToEndTestGlues.class);

	private String location;
	private int requestNumber;
	private String publishUrl = "";
	private NAE_Real_Util util = new NAE_Real_Util();
	private int notifReceived;
	private static final String INVALID_EVENT = "test_data/Invalid_Event.json";
	private List<TimeAndZone> timeList;
	private List<TimeData> timeDataList=new ArrayList<TimeData>();

	@After
	//And("^I write the data to file$")
	public void afterScenario(){
		for(TimeData td : timeDataList){
			String eventId=td.getEventId();
			//LOGGER.debug(eventId);
			//Commons.delay(10000);
			WireMock.configureFor(NAE_Properties.MOCK_SERVER, NAE_Properties.MOCK_SERVER_PORT);
			RequestPatternBuilder builder = new RequestPatternBuilder(
					RequestMethod.POST, urlMatching("/locations/424242qa/.*"));
			//.withHeader("X-B3-TraceId", equalTo(eventId));
			List<LoggedRequest> reqs = findAll(builder);
			int listSize = reqs.size();
			if(listSize>=1){
				
			    //LOGGER.debug(eventId+ ", "+ td.getCurrentTimestamp()+ ", "+ reqs.get(listSize-1).getHeader("X-B3-TraceId")+": "+reqs.get(listSize-1).getBodyAsString());
				try(Writer writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("times.csv",true)))){
					writer.write(reqs.get(listSize-1).getHeader("X-B3-TranceId")+","+td.getCurrentTimestamp()+","+reqs.get(listSize-1).getLoggedDate().getTime()+",\n");
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
	
	/**
	 * Test: post an AIP event to EEL
	 */
	@When("^I check the number of request from \"([^\"]*)\" received by mock server$")
	public void getLocationRequestNumber(String location) {
		this.location = location;
		this.publishUrl = "/locations/" + location +"/.*";
				//+ "/notifications/alarm.json";
	}

	@Then("^I should get a number$")
	public void verifyRequestNumber() {
		this.requestNumber = util.countRequests(publishUrl);
		Assert.assertTrue(requestNumber >= 0);
		LOGGER.debug("Number of Notification sent to Mock Server from "
				+ location + " : " + requestNumber);
	}

	@And("^I post an \"([^\"]*)\" Event of that location to EEL$")
	public void postEventToEEL(String type) throws Throwable {
		Assert.assertTrue(type.equals("valid") || type.equals("invalid"));
		List<TimeAndZone> list = new ArrayList<TimeAndZone>();
		if (type.equals("valid")) {
			EventSetup newEvent = new EventSetup();
			newEvent.eventSetup();
			newEvent.createUniqueEvent(newEvent.getEvent(), 1);
			String currentTimestamp=String.valueOf(System.currentTimeMillis());
			String eventId=newEvent.getEventId();
			newEvent.fireEvent();
			list.add(new TimeAndZone(newEvent.getEventTimestamp(), newEvent
					.getRuleTimeZone()));
			TimeData td=new TimeData(eventId,currentTimestamp);
			timeDataList.add(td);
		} else if (type.equals("invalid")) {
			EventSetup newEvent = new EventSetup();
			newEvent.eventSetup(INVALID_EVENT);
			newEvent.fireEvent();
			list.add(new TimeAndZone(newEvent.getEventTimestamp(), newEvent
					.getRuleTimeZone()));
		}
		this.timeList = list;
	}

	@Then("^I should see the number of the request to that location increased by (\\d+)$")
	public void checkRequestIncreasedBy(int y) {
		Commons.delay(30000);
		int newRequestNumber = util.countRequests(publishUrl);
		LOGGER.debug(publishUrl);
		LOGGER.debug("Number of Notification sent to Mock Server from "
				+ location + " : " + newRequestNumber);
		int increased = newRequestNumber - requestNumber;
		this.notifReceived = increased;
		LOGGER.debug("Number of new Notification sent to Mock Server from "
				+ location + " : " + notifReceived);
		List<String> lastRequestBodyList = util.getRequestPayloadList(
				publishUrl, notifReceived);
		for (int i = 0; i < notifReceived; i++) {
			LOGGER.debug(lastRequestBodyList.get(i));
		}
		Assert.assertEquals(increased, y);
		// Assert.assertTrue(increased>=y&&increased <=y*2);

	}
	@Then("^I should see the number of the request to that location increased by equal or less than (\\d+)$")
	public void checkRequestIncreasedByLess(int y) {
		Commons.delay(30000);
		int newRequestNumber = util.countRequests(publishUrl);
		LOGGER.debug(publishUrl);
		LOGGER.debug("Number of Notification sent to Mock Server from "
				+ location + " : " + newRequestNumber);
		int increased = newRequestNumber - requestNumber;
		this.notifReceived = increased;
		LOGGER.debug("Number of new Notification sent to Mock Server from "
				+ location + " : " + notifReceived);
		List<String> lastRequestBodyList = util.getRequestPayloadList(
				publishUrl, notifReceived);
		for (int i = 0; i < notifReceived; i++) {
			LOGGER.debug(lastRequestBodyList.get(i));
		}
		Assert.assertTrue(increased<= y);
		// Assert.assertTrue(increased>=y&&increased <=y*2);

	}  
	@And("^the request body is in correct json format$")
	public void checkRequestFormat() {
		List<String> lastRequestBodyList = util.getRequestPayloadList(
				publishUrl, notifReceived);
		for (int i = 0; i < notifReceived; i++) {
			Assert.assertTrue(util.mapRequest(lastRequestBodyList.get(i)));
			// LOGGER.debug(lastRequestBodyList.get(i));
		}
	}

	@And("^the messages are in correct format with correct timestamp$")
	public void checkMessageText() {
		List<String> lastRequestBodyList = util.getRequestPayloadList(
				publishUrl, notifReceived);
		for (int i = 0; i < notifReceived; i++) {
			String apnMessage = util.mapPayload(lastRequestBodyList.get(i))
					.getApns().getAlert();

			String gcmMessage = util.mapPayload(lastRequestBodyList.get(i))
					.getGcm().getOtherdata().getMessage();
			String readableTime = util.transformTime(timeList.get(i)
					.getTimestamp() / 1000, timeList.get(i).getTimezone());
			LOGGER.debug(readableTime);
			Assert.assertTrue(apnMessage
					.startsWith("An alarm was triggered by Front Door at " + readableTime));
			//Assert.assertTrue(apnMessage.endsWith("Slide to view details."));
			Assert.assertTrue(gcmMessage.startsWith("\u003ch\u003eAlarm in Progress!\n\u003cb\u003eTriggered by Front Door at " + readableTime));
			//Assert.assertTrue(gcmMessage.endsWith("Touch to view details."));
		}

	}

	/**
	 * Test: post multiple identical/unique AIP event to EEL
	 */
	@When("^I post (\\d+) number of \"([^\"]*)\" events with (\\d+) secs of delay to EEL$")
	public void postMutipleEvents(int n, String type, int x) throws Throwable {
		List<TimeAndZone> list = new ArrayList<TimeAndZone>();
		if (type.equals("identical")) {
			EventSetup newEvent = new EventSetup();
			newEvent.eventSetup();
			for (int i = 0; i < n; i++) {
				newEvent.fireEvent();
				list.add(new TimeAndZone(newEvent.getEventTimestamp(), newEvent
						.getRuleTimeZone()));
				Commons.delay(x * 1000);
			}
		} else if (type.equals("unique")) {
			EventSetup newEvent = new EventSetup();
			newEvent.eventSetup();
			for (int i = 0; i < n; i++) {
				newEvent.createUniqueEvent(newEvent.getEvent(), i);
				String currentTimestamp=String.valueOf(System.currentTimeMillis());
				String eventId=newEvent.getEventId();
				newEvent.fireEvent();
				list.add(new TimeAndZone(newEvent.getEventTimestamp(), newEvent
						.getRuleTimeZone()));
				TimeData td=new TimeData(eventId,currentTimestamp);
				timeDataList.add(td);
				Commons.delay(x * 1000);
			}
		}
		this.timeList = list;
	}
	
	

}
