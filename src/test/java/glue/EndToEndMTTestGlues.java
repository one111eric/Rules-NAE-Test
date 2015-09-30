package glue;

import java.util.List;

import impl.Commons;
import impl.EventSetup;
import impl.NAE_Real_Util;

import org.apache.log4j.Logger;
import org.junit.Assert;

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
	private static final String AIP_HANDLER="/xhs"; 
	
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
        if(type.equals("valid")){
		es=new EventSetup();
		es.eventSetup(locationName,siteId,tenantName,true);
		this.location=es.getLocation();
		this.site=es.getSite();
		this.event=es.getEvent();
        }
        else {
        	es=new EventSetup();
    		es.eventSetup(locationName,siteId,tenantName,false);
    		this.location=es.getLocation();
    		this.site=es.getSite();
    		this.event=es.getEvent();
        }
		
	}

	@And("^I create \"([^\"]*)\" and \"([^\"]*)\" with \"([^\"]*)\" AIP rule for xh tenant$")
	public void provisionXhTenant(String locationName, String siteId,
			String type) throws Throwable {
		if(type.equals("valid")){
			es=new EventSetup();
			es.eventSetup(locationName,siteId,"xh",true);
			this.location=es.getLocation();
			this.site=es.getSite();
			this.event=es.getEvent();
		}
		else {
			es=new EventSetup();
			es.eventSetup(locationName,siteId,"xh",false);
			this.location=es.getLocation();
			this.site=es.getSite();
			this.event=es.getEvent();
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
           if(type.equals("valid")){
        	   es.createUniqueEvent(es.getEvent(), 0);
        	   es.fireEvent();
           }
           else {
        	   es.setupEvent(INVALID_EVENT);
        	   es.createUniqueEvent(es.getEvent(), 0);
        	   es.fireEvent();
           }
	}

	@Then("^I should receive (\\d+) notification messages on mock server$")
	public void verifyNotifCount(int notifCount) {
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
		Assert.assertEquals(increased, notifCount);
	}

	/**
	 * Test for another tenant with modified AIP topic handler
	 */
	@Given("^I have a new tenant \"([^\"]*)\" with a modified AIP topic handler$")
	public void newTenantTopicHandlerHExists(String tenantName){
		Assert.assertTrue(util.verifyTopicHandlerExist(tenantName, "/xhstest"));
	}
	
	@And("^I post an modified AIP event from this \"([^\"]*)\" to EEL$")
	public void postModifiedAipEvent(String siteId){
		this.publishUrl = "/publish/xhstest/qa/" + location
				+ "/notifications/alarm.json";
		this.requestNumber = util.countRequests(publishUrl);
		Assert.assertTrue(requestNumber >= 0);
		LOGGER.debug("Number of Notification sent to Mock Server from "
				+ location + " : " + requestNumber);
		es.createUniqueEvent(es.getEvent(), 0);
		es.getEvent().replace("/xhs/tps/siteId/panel/panelStatus/AlarmInProgress", "/xhstest/tps/siteId/panel/panelStatus/AlarmInProgress");
		es.fireEvent();
	}
	
}
