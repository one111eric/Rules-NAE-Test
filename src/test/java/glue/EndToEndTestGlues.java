package glue;

import impl.Commons;
import impl.EventSetup;
import impl.NAE_Properties;
import impl.NAE_Real_Util;

import org.apache.log4j.Logger;
import org.testng.Assert;

import com.comcast.csv.drivethru.api.HTTPRequestManager.METHOD;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class EndToEndTestGlues {
	private static final Logger LOGGER=Logger.getLogger(EndToEndTestGlues.class);
	
	private String location;
	private int requestNumber;
	private String publishUrl = "";
	private NAE_Real_Util util=new NAE_Real_Util();
	
	
	
	/**
     * Test: post an AIP event to EEL
     */
	@When("^I check the number of request from \"([^\"]*)\" received by mock server$")
	public void getLocationRequestNumber(String location){
		this.location=location;
		this.publishUrl="/publish/xhs/qa/"+location+"/notifications/alarm.json";
		
	}
	
	@Then("^I should get a number$")
	public void verifyRequestNumber(){
		this.requestNumber=util.countRequests(publishUrl);
		Assert.assertTrue(requestNumber>=0);
		LOGGER.debug("Number of Notification sent to Mock Server from "+location+" : "
				+ requestNumber);
	}
	
	@And("^I post an Event of that location to EEL$")
	public void postEventToEEL() throws Throwable {
	    EventSetup newEvent=new EventSetup();
	    newEvent.eventSetup();
	    //Commons.delay(15000);
	    newEvent.fireEvent();
	    
	    
	}
	
	@Then("^I should see the number of the request to that location increased by 1$")
	public void checkRequestIncreasedByOne(){
		Commons.delay(15000);
		int newRequestNumber=util.countRequests(publishUrl);
		LOGGER.debug(publishUrl);
		LOGGER.debug("Number of Notification sent to Mock Server from "+location+" : "
				+ newRequestNumber);
		int increased=newRequestNumber-requestNumber;
		Assert.assertEquals(increased,1);
	}
	
	@And("^the request body is in correct json format$")
	public void checkReqeustFormat(){
		String lastRequestBody=util.getRequestPayload(publishUrl);
		Assert.assertTrue(util.mapRequest(lastRequestBody));
		LOGGER.debug(lastRequestBody);
		//util.printAllRequest(publishUrl);
		
	}
	
	@And("^the timestamp is correct$")
	public void checkTimestamp(){
		
	}
}
