package glue;

import impl.EventSetup;
import impl.NAE_Real_Util;

import org.apache.log4j.Logger;

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
	
	
	@Given("^\"([^\"]*)\" has a rule with TTL (\\d+) seconds$")
	public void setupTTLRuleToLocation(String locationName,int ttlTime){
		es=new EventSetup();
		
	}
	@And("^I post an Event of \"([^\"]*)\" to EEL with SessionId \"([^\"]*)\"$")
	public void postEventWithSessionId(String locationName,String sessionId){
		
	}
	@And("^I wait (\\d+) seconds$")
	public void threadWait(int waitTime){
		
	}
	@When("^I check the number of all requests received by mock server")
	public void getAllRequestsNumber(){
		
	}
	
	@Then("^I should see the number of the request to mock server increased by (\\d+)$")
	public void checkAllRequestsIncreasedBy(int x){
		
	}
}
