package glue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import impl.Commons;
import impl.EventSetup;
import impl.NAE_Real_Util;
import impl.TimeAndZone;

import org.apache.log4j.Logger;
import org.testng.Assert;

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

	/**
	 * Test: post an AIP event to EEL
	 */
	@When("^I check the number of request from \"([^\"]*)\" received by mock server$")
	public void getLocationRequestNumber(String location) {
		this.location = location;
		this.publishUrl = "/publish/xhs/qa/" + location
				+ "/notifications/alarm.json";

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
		List<TimeAndZone> list=new ArrayList<TimeAndZone>();
		if (type.equals("valid")) {
			EventSetup newEvent = new EventSetup();
			newEvent.eventSetup();
			newEvent.createUniqueEvent(newEvent.getEvent(), 1);
			newEvent.fireEvent();
			list.add(new TimeAndZone(newEvent.getEventTimestamp(), newEvent.getRuleTimeZone()));
		} else if (type.equals("invalid")) {
			EventSetup newEvent = new EventSetup();
			newEvent.eventSetup(INVALID_EVENT);
			newEvent.fireEvent();
			list.add(new TimeAndZone(newEvent.getEventTimestamp(), newEvent.getRuleTimeZone()));
		}
        this.timeList=list;
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
	public void checkMessageText(){
		List<String> lastRequestBodyList = util.getRequestPayloadList(
				publishUrl, notifReceived);
		for(int i=0;i<notifReceived;i++){
			String apnMessage=util.mapPayload(lastRequestBodyList.get(i)).getApns().getAlert();
			
			String gcmMessage=util.mapPayload(lastRequestBodyList.get(i)).getGcm().getOtherdata().getMessage();
			String readableTime=util.transformTime(timeList.get(i).getTimestamp()/1000, timeList.get(i).getTimezone());
			LOGGER.debug(readableTime);
			Assert.assertTrue(apnMessage.startsWith("An alarm was triggered at "+readableTime));
			Assert.assertTrue(apnMessage.endsWith("Slide to view details."));
			Assert.assertTrue(gcmMessage.startsWith("Since "+readableTime));
			Assert.assertTrue(gcmMessage.endsWith("Touch to view details."));
		}
		
	}

	/**
	 * Test: post multiple identical/unique AIP event to EEL
	 */
	@When("^I post (\\d+) number of \"([^\"]*)\" events with (\\d+) secs of delay to EEL$")
	public void postMutipleEvents(int n, String type, int x) throws Throwable {
		List<TimeAndZone> list=new ArrayList<TimeAndZone>();
		if (type.equals("identical")) {
			EventSetup newEvent = new EventSetup();
			newEvent.eventSetup();
			for (int i = 0; i < n; i++) {
				newEvent.fireEvent();
				list.add(new TimeAndZone(newEvent.getEventTimestamp(), newEvent.getRuleTimeZone()));
				Commons.delay(x * 1000);
			}
		} else if (type.equals("unique")) {
			EventSetup newEvent = new EventSetup();
			newEvent.eventSetup();
			for (int i = 0; i < n; i++) {
				newEvent.createUniqueEvent(newEvent.getEvent(), i);
				newEvent.fireEvent();
				list.add(new TimeAndZone(newEvent.getEventTimestamp(), newEvent.getRuleTimeZone()));
				Commons.delay(x * 1000);	
			}
		}
		this.timeList=list;
	}
	
	

}
