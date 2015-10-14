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
import java.util.List;

import impl.Commons;
import impl.EventSetup;
import impl.NAE_Properties;
import impl.NAE_Real_Util;
import impl.TimeData;

import org.apache.log4j.Logger;
import org.junit.Assert;

import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * Class for AIP end to end Multi-Tenancy test glues
 *
 * @author Miao Xiang
 *
 */
public class EndToEndMTTestGlues {
	private static final Logger LOGGER = Logger
			.getLogger(EndToEndMTTestGlues.class);
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
					RequestMethod.POST, urlMatching("/publish/xhs/qa/.*")).withHeader("X-B3-TraceId", equalTo(eventId));
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
	
	/**
	 * Test for another tenant with AIP topic handler
	 */
	@Given("^I have a new tenant \"([^\"]*)\" with the same AIP topic handler$")
	public void newTenantExists(String tenantName) {
		Assert.assertTrue(util.verifyTenantExist(tenantName));
		Assert.assertTrue(util.verifyTopicHandlerExist(tenantName, AIP_HANDLER));
	}

	@And("^I create \"([^\"]*)\" and \"([^\"]*)\" with \"([^\"]*)\" AIP rule for \"([^\"]*)\"$")
	public void provisionNewTenant(String locationName, String siteId,
			String type, String tenantName) throws Throwable {
		if (type.equals("valid")) {
			es = new EventSetup();
			es.eventSetup(locationName, siteId, tenantName, true);
			this.location = es.getLocation();
			this.site = es.getSite();
			this.event = es.getEvent();
		} else {
			es = new EventSetup();
			es.eventSetup(locationName, siteId, tenantName, false);
			this.location = es.getLocation();
			this.site = es.getSite();
			this.event = es.getEvent();
		}

	}

	@And("^I create \"([^\"]*)\" and \"([^\"]*)\" with \"([^\"]*)\" AIP rule for xh tenant$")
	public void provisionXhTenant(String locationName, String siteId,
			String type) throws Throwable {
		if (type.equals("valid")) {
			es = new EventSetup();
			es.eventSetup(locationName, siteId, "xh", true);
			this.location = es.getLocation();
			this.site = es.getSite();
			this.event = es.getEvent();
		} else {
			es = new EventSetup();
			es.eventSetup(locationName, siteId, "xh", false);
			this.location = es.getLocation();
			this.site = es.getSite();
			this.event = es.getEvent();
		}
	}

	@And("^I post an \"([^\"]*)\" AIP event from this \"([^\"]*)\" to EEL$")
	public void postAipEvent(String type, String siteId) {
		this.publishUrl = "/publish/xhs/qa/" + location
				+ "/notifications/alarm.json";
		this.requestNumber = util.countRequests(publishUrl);
		Assert.assertTrue(requestNumber >= 0);
		LOGGER.debug("Number of Notification sent to Mock Server from "
				+ location + " : " + requestNumber);
		if (type.equals("valid")) {
			String currentTimestamp=String.valueOf(System.currentTimeMillis());
			String eventId=es.getEventId();
			es.createUniqueEvent(es.getEvent(), 0);
			es.fireEvent();
			TimeData td=new TimeData(eventId,currentTimestamp);
			timeDataList.add(td);
		} else {
			es.setupEvent(INVALID_EVENT);
			es.createUniqueEvent(es.getEvent(), 0);
			String currentTimestamp=String.valueOf(System.currentTimeMillis());
			String eventId=es.getEventId();
			es.fireEvent();
			TimeData td=new TimeData(eventId,currentTimestamp);
			timeDataList.add(td);
		}
	}

	@Then("^I should receive (\\d+) notification messages on mock server$")
	public void verifyNotifCount(int notifCount) {
		Commons.delay(20000);
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
		Assert.assertEquals(increased, notifCount);
	}

	/**
	 * Test for another tenant with modified AIP topic handler
	 */
	@Given("^I have a new tenant \"([^\"]*)\" with a modified AIP topic handler$")
	public void newTenantTopicHandlerHExists(String tenantName) {
		Assert.assertTrue(util.verifyTopicHandlerExist(tenantName, "/xhstest"));
	}

	@And("^I post an modified AIP event from this \"([^\"]*)\" to EEL$")
	public void postModifiedAipEvent(String siteId) {
		this.publishUrl = "/publish/xhs/qa/" + location
				+ "/notifications/alarm.json";
		this.requestNumber = util.countRequests(publishUrl);
		Assert.assertTrue(requestNumber >= 0);
		LOGGER.debug("Number of Notification sent to Mock Server from "
				+ location + " : " + requestNumber);
		es.createUniqueEvent(es.getEvent(), 0);
		String modifiedEvent = es.getEvent()
				.replace(
						"/xhs/tps/" + siteId
								+ "/panel/panelStatus/AlarmInProgress",
						"/xhstest/tps/" + siteId
								+ "/panel/panelStatus/AlarmInProgress");
		es.setEvent(modifiedEvent);
		String currentTimestamp=String.valueOf(System.currentTimeMillis());
		String eventId=es.getEventId();
		es.fireEvent();
		TimeData td=new TimeData(eventId,currentTimestamp);
		timeDataList.add(td);
	}

}
