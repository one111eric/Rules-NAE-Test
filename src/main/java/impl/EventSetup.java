package impl;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.ProvisionsBody;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.jayway.restassured.RestAssured.given;

/**
 * Class for Event Firing Setups Inlcuding setting up location/siteId mapping
 * Inject rules based on tenants etc.
 * 
 * @author Miao Xiang
 *
 */
public class EventSetup {
	public static final String RULES_LOCATION_URL = NAE_Properties.REST_ENDPOINT+"locations/";
			//"http://rest.tps.rules.comcast.com/locations/";
	public static final String MOLECULE_MAPPING_URL = NAE_Properties.MOLECULE_MAPPING_ENDPOINT;
			//"http://molecule.tps.rules.vacsv.com/mappings/xh/";
	public static final String RULE_JSON = "test_data/TpsAipRule.json";
	public static final String RULE_JSON_NEW="test_data/TpsAipRule.json";
	public static final String INVALID_RULE_JSON = "test_data/InvalidRule.json";
	public static final String EVENT_JSON = "test_data/TpsAipEvent.json";
    public static final String EVENT_JSON_NEW="test_data/TpsAipEvent.json";
	private NAE_Real_Util util = new NAE_Real_Util();

	private String session;
	private String location;
	private String site;
	private String event;
	private String rule;
	private Map<String, String> headers = new HashMap<String, String>();
    private Map<String, String> eventHeaders = new HashMap<String, String>();
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

	/**
	 * Method that setup a location/siteid/rule/event based on location name,
	 * siteid, tenant name and if rule is valid
	 * 
	 * @param locationName
	 *            : location name
	 * @param siteId
	 *            : site ID
	 * @param tenantName
	 *            : tenant name
	 * @param isRuleValid
	 *            : rule valid or not
	 */
	public void eventSetup(String locationName, String siteId,
			String tenantName, boolean isRuleValid) throws Throwable {
		setupProvisions(locationName, siteId, tenantName);
		setupRule(locationName, tenantName, isRuleValid);
		setupEvent();
	}

	// new setup for new Event and Rule
	public void eventSetupNew(String locationName, String siteId,
			String tenantName, boolean isRuleValid, 
			int aliveTime) throws Throwable {
		//this.session = sessionId;
		setupProvisions(locationName, siteId, tenantName);
		setupRuleNew(locationName, tenantName, isRuleValid, aliveTime);
		setupEventNew();
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

	public void setEvent(String eventJson) {
		this.event = eventJson;
	}
	
	public String getSession(){
		return session;
	}

	/**
	 * Method that setup location/siteId using provisions api
	 */
	public void setupProvisions() throws Throwable {
		String locationName = "424242qa";
		String siteId = "424242qa";
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
	 * Method setting up location/site based on their names and tenant name
	 * 
	 * @param locationName
	 *            : location name
	 * @param siteId
	 *            : site ID
	 * @param tenantName
	 *            : tenant name
	 */
	public void setupProvisions(String locationName, String siteId,
			String tenantName) {
		Response response = null;
		// Setup provisions, with location name and siteId
		String provisionEndPoint = NAE_Properties.PROVISION_ENDPOINT + "/";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Xrs-Tenant-Id", tenantName);
		String provisionsBody = provisionsBodyToString(locationName, siteId);
		response = given().log().all().headers(headers).body(provisionsBody)
				.expect().statusCode(ServerStatusCodes.OK)
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
		myRule = myRule.replace("locationName", location);
		String ruleEndPoint = RULES_LOCATION_URL + location + "/rules/"
				+ ruleNumber;
		Response response = null;
		response = given().log().all().headers(this.headers).body(myRule)
				.expect().statusCode(200).put(ruleEndPoint);
		response.prettyPrint();
		this.rule = myRule;
	}

	/**
	 * Method that setup a rule to a location
	 * 
	 * @param lacationName
	 *            : location name
	 */
	public void setupRule(String locationName) throws Throwable {
		String ruleNumber = "1234";
		String myRule = util.getFile(RULE_JSON);

		myRule = myRule.replace("locationName", location);
		String ruleEndPoint = RULES_LOCATION_URL + location + "/rules/"
				+ ruleNumber;
		Response response = null;
		response = given().log().all().headers(this.headers).body(myRule)
				.expect().statusCode(200).put(ruleEndPoint);
		response.prettyPrint();
		this.rule = myRule;
	}

	/**
	 * Method that setup a rule based on a location, tenant name, and if the
	 * rule is valid
	 * 
	 * @param locationName
	 *            : location name
	 * @param tenantName
	 *            : tenant name
	 * @param isRuleValid
	 *            : rule valid or not
	 */
	public void setupRule(String locationName, String tenantName,
			boolean isRuleValid) throws Throwable {
		String ruleNumber = "1234";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Xrs-Tenant-Id", tenantName);
		String myRule = "";
		if (isRuleValid) {
			myRule = util.getFile(RULE_JSON);
		} else {
			myRule = util.getFile(INVALID_RULE_JSON);
		}
		myRule = myRule.replace("locationName", location).replace("xh",
				tenantName).replace("?alivetime","10");;
		String ruleEndPoint = RULES_LOCATION_URL + location + "/rules/"
				+ ruleNumber;
		Response response = null;
		response = given().log().all().headers(headers).body(myRule).expect()
				.statusCode(200).put(ruleEndPoint);
		response.prettyPrint();
		this.rule = myRule;

	}

	//setup new rule
	public void setupRuleNew(String locationName, String tenantName,
			boolean isRuleValid, int aliveTime) throws Throwable {
		String ruleNumber = "1234";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Xrs-Tenant-Id", tenantName);
		String myRule = "";
		if (isRuleValid) {
			myRule = util.getFile(RULE_JSON_NEW);
		} else {
			myRule = util.getFile(INVALID_RULE_JSON);
		}
		myRule = myRule.replace("locationName", location).replace("xh",
				tenantName).replace("?alivetime",String.valueOf(aliveTime));
		String ruleEndPoint = RULES_LOCATION_URL + location + "/rules/"
				+ ruleNumber;
		Response response = null;
		response = given().log().all().headers(headers).body(myRule).expect()
				.statusCode(200).put(ruleEndPoint);
		response.prettyPrint();
		this.rule = myRule;

	}
	/**
	 * Method that setup an event JSON body
	 */
	public void setupEvent() {
		String myEvent = util.getFile(EVENT_JSON);
		myEvent = myEvent.replace("siteId", getSite());
		this.event = myEvent;
	}
	
	//setup new event
	public void setupEventNew(){
		String myEvent = util.getFile(EVENT_JSON_NEW);
		myEvent = myEvent.replace("siteId", getSite());
		//myEvent=myEvent.replace("{SesionId}", getSession());
		this.event = myEvent;
	}
	
	public void setupEventNew(String siteId){
		String myEvent = util.getFile(EVENT_JSON_NEW);
		myEvent = myEvent.replace("siteId", siteId);
		//myEvent=myEvent.replace("{SesionId}", getSession());
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
		
		
		eventHeaders.put("X-Debug", "true");
		response = given().log().all().headers(eventHeaders).body(this.event).post(NAE_Properties.EEL_EVENT_ENDPOINT);
		response.prettyPrint();
		LOGGER.debug("Status: "+ response.statusCode());
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
			//add a new traceId header to enforce universal traceId
			this.eventHeaders.put("X-B3-TraceId",newEventIdString);
			LOGGER.debug(newEventIdString);
		} catch (JsonProcessingException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		this.event = uniqueEvent;
	}

	public void createUniqueEventNew(String eventJson, int n, String sessionId) {
		String uniqueEvent = this.event;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(uniqueEvent);
			JsonNode contentNode = rootNode.path("content");

			JsonNode timestamp = contentNode.path("timestamp");
			JsonNode metaDataNode=contentNode.path("metadata");
			JsonNode sessionIdNode = metaDataNode.path("alarmSessionId");
			String timestampString = String.valueOf(timestamp.getLongValue());

			JsonNode eventId = contentNode.path("eventId");
			String eventIdString = eventId.getTextValue();

			long currentTimestamp = System.currentTimeMillis();
			long newTimestamp = currentTimestamp + 60 * 1000 * n;
			String newTimestampString = String.valueOf(newTimestamp);
			String newEventIdString = newTimestampString;
			uniqueEvent = uniqueEvent.replace(timestampString,
					newTimestampString)
					.replace(eventIdString, newEventIdString).replace(sessionIdNode.getTextValue(), sessionId);
			//add a new traceId header to enforce universal traceId
			this.eventHeaders.put("X-B3-TraceId",newEventIdString);
			LOGGER.debug(newEventIdString);
			LOGGER.debug(sessionIdNode.asText());
			LOGGER.debug(sessionId);
		} catch (JsonProcessingException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		this.event = uniqueEvent;
	}
	
	
	/**
	 * Method that get the timestamp from a event JSON
	 */
	public Long getEventTimestamp() {
		Long timestampTime = new Long(0);
		String eventBody = this.event;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(eventBody);
			JsonNode contentNode = rootNode.path("content");
			JsonNode timestampNode = contentNode.path("timestamp");
			long timestamp = timestampNode.getLongValue();
			timestampTime = new Long(timestamp);
		} catch (JsonProcessingException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return timestampTime;
	}

	public String getEventId(){
		String eventId="";
		String eventBody = this.event;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(eventBody);
			JsonNode contentNode = rootNode.path("content");
			JsonNode eventIdNode = contentNode.path("eventId");
			eventId = eventIdNode.getTextValue();
		} catch (JsonProcessingException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return eventId;
	}
	/**
	 * Method that gets the timezone field value from the AIP rule JSON
	 */
	public String getRuleTimeZone() {
		// default timeZone
		String timeZone = "GMT";
		String rule = util.getFile(RULE_JSON);
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(rule);
			JsonNode actionNode = rootNode.path("actions");
			JsonNode codeNode = actionNode.get(0).path("code");
			JsonNode paramsNode = codeNode.path("params");
			JsonNode timezoneNode = paramsNode.path("timezone");
			timeZone = timezoneNode.getTextValue();
		} catch (JsonProcessingException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return timeZone;
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

	 //@Test
	 public void testTimezone() throws Throwable{
	 EventSetup es=new EventSetup();
	 //es.setupRule();
	 LOGGER.debug(es.getRuleTimeZone());
	 }

	// simple unit test to print current timestamp
	// @Test
	public void printTime() {
		long currentTimestamp = System.currentTimeMillis();
		System.out.println(currentTimestamp);
	}
	

}
