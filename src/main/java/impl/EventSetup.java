package impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import model.ProvisionsBody;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.given;

/**
 * Class for Event Firing Setups
 *
 * @author Miao Xiang
 *
 */
public class EventSetup {
	public static final String RULES_LOCATION_URL = "http://rest.qa.rules.comcast.com/locations/";
	public static final String MOLECULE_MAPPING_URL = "http://molecule.qa.rules.vacsv.com/mappings/xh/";
	public static final String RULE_JSON = "test_data/AipRule.json";
	public static final String EVENT_JSON = "test_data/EventToEEL.json";

	private NAE_Real_Util util = new NAE_Real_Util();

	private String location;
	private String site;
	private String event;
	private Map<String, String> headers = new HashMap<String, String>();

	private static final Logger LOGGER = Logger.getLogger(EventSetup.class);

	/**
	 * Method that setup a default location/siteid/rule/event
	 */
	public void eventSetup() throws Throwable {
		headers.put("Xrs-Tenant-Id", "xh");
		setupProvisions();
		setupRule();
		setupEvent();
	}

	/**
	 * Method that setup a location/siteid/rule/event based on event JSON body
	 * 
	 * @param eventJson
	 *            : event JSON body
	 */
	public void eventSetup(String eventJson) throws Throwable {
		headers.put("Xrs-Tenant-Id", "xh");
		setupProvisions();
		setupRule();
		setupEvent(eventJson);
	}

	// getters
	public String getLocation() {
		return location;
	}

	public String getSite() {
		return site;
	}

	public String getEvent() {
		return event;
	}

	/**
	 * Method that setup location/siteId using provisions api
	 */
	public void setupProvisions() throws Throwable {
		String locationName = "MiaoLocation00001";
		String siteId = "850520";
		String locationEndPoint = RULES_LOCATION_URL + locationName;
		Response response = null;
		// delete the existing location
		response = given().log().all().headers(this.headers).expect()
				.statusCode(200).delete(locationEndPoint);
		// Setup provisions, including location and siteId
		String provisionEndPoint = NAE_Properties.PROVISION_ENDPOINT;
		String provisionsBody = provisionsBodyToString(locationName, siteId);
		response = given().log().all().headers(this.headers)
				.body(provisionsBody).expect().statusCode(200)
				.post(provisionEndPoint);
		response.prettyPrint();
		this.location = locationName;
		this.site = siteId;

	}

	/**
	 * Out-dated method that gets quoted out.
	 */
	/*
	 * public void setupLocation() throws Throwable { String locationName =
	 * "MiaoLocation00001"; String locationEndPoint = RULES_LOCATION_URL +
	 * locationName; Response response = null; // delete the existing location
	 * response = given().log().all().headers(this.headers).expect()
	 * .statusCode(200).delete(locationEndPoint); // setup location response =
	 * given().log().all().headers(this.headers).expect()
	 * .statusCode(200).put(locationEndPoint); response.prettyPrint();
	 * this.location = locationName; }
	 * 
	 * public void setupSite() throws Throwable { String siteId = "850520";
	 * String moleculeEndPoint = MOLECULE_MAPPING_URL + siteId; Response
	 * response = null; response = given().log().all().headers(this.headers)
	 * .body(this.location).expect().statusCode(200) .put(moleculeEndPoint);
	 * response.prettyPrint(); this.site = siteId; }
	 */
	/**
	 * Method that setup a rule to a location
	 */
	public void setupRule() throws Throwable {
		String ruleNumber = "1234";
		String myRule = util.getFile(RULE_JSON);
		System.out.println(myRule);
		myRule = myRule.replace("locationName", location);
		String ruleEndPoint = RULES_LOCATION_URL + location + "/rules/"
				+ ruleNumber;
		Response response = null;
		response = given().log().all().headers(this.headers).body(myRule)
				.expect().statusCode(200).put(ruleEndPoint);
		response.prettyPrint();
	}

	/**
	 * Method that setup an event JSON body
	 */
	public void setupEvent() {
		String myEvent = util.getFile(EVENT_JSON);
		myEvent = myEvent.replace("siteId", getSite());
		this.event = myEvent;
	}

	/**
	 * Method that setup an event based on an existing even JSON
	 * 
	 * @param eventJson
	 *            : file path of event JSON in resources folder
	 */
	public void setupEvent(String eventJson) {
		String myEvent = util.getFile(eventJson);
		myEvent = myEvent.replace("siteId", getSite());
		this.event = myEvent;
	}

	/**
	 * Method that post an event to EEL
	 */
	public void fireEvent() {
		Response response = null;
		response = given().log().all().body(this.event).expect()
				.statusCode(200).post(NAE_Properties.EEL_EVENT_ENDPOINT);
		response.prettyPrint();
	}

	/**
	 * Method that create unique events by using different timestamp
	 * 
	 * @param eventJson
	 *            : file path in resources folder of a sample event JSON
	 * @param n
	 *            : number of different events we want to generate
	 */
	public void createUniqueEvent(String eventJson, int n) {
		String uniqueEvent = this.event;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(uniqueEvent);
			JsonNode contentNode = rootNode.path("content");

			JsonNode timestamp = contentNode.path("timestamp");
			String timestampString = String.valueOf(timestamp.getLongValue());

			JsonNode eventId = contentNode.path("eventId");
			String eventIdString = eventId.getTextValue();

			long currentTimestamp = System.currentTimeMillis();
			long newTimestamp = currentTimestamp + 60 * 1000 * n;
			String newTimestampString = String.valueOf(newTimestamp);
			String newEventIdString = newTimestampString;
			uniqueEvent = uniqueEvent.replace(timestampString,
					newTimestampString)
					.replace(eventIdString, newEventIdString);
			LOGGER.debug(newEventIdString);
		} catch (JsonProcessingException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		this.event = uniqueEvent;
	}

	/**
	 * Method that transform an provision body object to JSON String
	 * 
	 * @param location
	 *            : location name
	 * @param siteId
	 *            : site ID
	 * @return String: JSON body for provisions API as a string
	 */
	private String provisionsBodyToString(String location, String siteId) {
		String provisionsBody = "";
		ProvisionsBody newProvisionsBody = new ProvisionsBody(location, siteId);
		ObjectMapper mapper = new ObjectMapper();
		try {
			provisionsBody = mapper.writeValueAsString(newProvisionsBody);
		} catch (JsonGenerationException e) {
			LOGGER.error(e);
		} catch (JsonMappingException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return provisionsBody;
	}

	// simple unit test to print current timestamp
	// @Test
	public void printTime() {
		long currentTimestamp = System.currentTimeMillis();
		System.out.println(currentTimestamp);
	}

}
