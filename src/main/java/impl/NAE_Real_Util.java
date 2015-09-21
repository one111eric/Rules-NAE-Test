package impl;

import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.jayway.restassured.RestAssured.given;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;

import model.NAE_Request_Body;
import model.NAE_Response_Body;
import model.Payload;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;
import org.testng.annotations.Test;

import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.jayway.restassured.response.Response;

import org.apache.log4j.Logger;

/**
 * Class for Genaral NAE action needs
 *
 * @author Miao Xiang
 *
 */

public class NAE_Real_Util {

	private static final String NAE_URL = NAE_Properties.NAE_URL;
	private static final String MOCK_SERVER = NAE_Properties.MOCK_SERVER;
	private static final int MOCK_SERVER_PORT = NAE_Properties.MOCK_SERVER_PORT;

	// Logger instance
	private static final Logger LOGGER = Logger.getLogger(NAE_Real_Util.class);

	// General Constructor
	public NAE_Real_Util() {

	}

	/**
	 * A general method to read file to string
	 * 
	 * @param filepath
	 *            : target file path
	 * @return String: JSON body as a string
	 */
	public String requestBodyString(String filePath) {
		String responsebody = "";
		try {
			responsebody = new Scanner(new File(filePath)).useDelimiter("\\Z")
					.next();
		} catch (FileNotFoundException e) {

			LOGGER.error(e);
		}
		return responsebody;
	}

	/**
	 * Method that get file from resources folder
	 * 
	 * @param fileName
	 *            : file name in resources folder
	 * @return String: JSON body as a string
	 */
	public String getFile(String fileName) {
		StringBuilder result = new StringBuilder("");
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());
		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}
			scanner.close();
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return result.toString();
	}

	/**
	 * Method getting response from NAE Api
	 * 
	 * @param filePath
	 *            : request json body file
	 * @param verb
	 *            : HTTP request method
	 * @param header
	 *            : True or False depends on if X-Debug header is included
	 * @return Response: RestAssured response
	 */
	public Response getNAERealResponse(String filePath, String verb,
			boolean header) {
		Response response = null;
		if (header) {
			switch (verb) {
			case "POST":
				response = given().log().all().header("X-Debug", true)
						.body(getFile(filePath)).post(NAE_URL);
				response.prettyPrint();
				break;
			case "GET":
				response = given().log().all().header("X-Debug", true)
						.get(NAE_URL);
				response.prettyPrint();
				break;
			case "PUT":
				response = given().log().all().header("X-Debug", true)
						.body(getFile(filePath)).put(NAE_URL);
				response.prettyPrint();
				break;
			case "DELETE":
				response = given().log().all().header("X-Debug", true)
						.body(getFile(filePath)).delete(NAE_URL);
				response.prettyPrint();
			default:
			}
		} else {
			switch (verb) {
			case "POST":
				response = given().log().all().body(getFile(filePath))
						.post(NAE_URL);
				response.prettyPrint();
				break;
			case "GET":
				response = given().log().all().get(NAE_URL);
				response.prettyPrint();
				break;
			case "PUT":
				response = given().log().all().body(getFile(filePath))
						.put(NAE_URL);
				response.prettyPrint();
				break;
			case "DELETE":
				response = given().log().all().body(getFile(filePath))
						.delete(NAE_URL);
				response.prettyPrint();
			default:
			}
		}
		return response;
	}

	/**
	 * Method to transform Unix millisecond timestamp to readable time based on
	 * Timezone
	 * 
	 * @param timestamp
	 *            : Unix timestamp in millisecond
	 * @param timeZone
	 *            : Timezone like "PST" "EST"
	 * @return String: readable time string
	 */
	public String transformTime(long timeStamp, String timeZone) {
		String timeString;
		Date time = new Date(timeStamp * 1000);
		SimpleDateFormat formatter = new SimpleDateFormat("hh:mma");
		TimeZone tz = TimeZone.getTimeZone(timeZone);
		formatter.setTimeZone(tz);
		timeString = formatter.format(time).toLowerCase();
		return timeString;
	}

	/**
	 * Method getting the expected time from timestamp in request json body
	 * 
	 * @param requestFilePath
	 *            : request json body file
	 * @return String: expected time string for verification in test
	 */
	public String expectedTime(String requestFilePath) {
		String expectedtime = "";
		String requestBody = getFile(requestFilePath);
		ObjectMapper mapper = new ObjectMapper();
		try {
			NAE_Request_Body newbody = mapper.readValue(requestBody,
					NAE_Request_Body.class);
			long timestamp = newbody.getCode().getData().getTimestamp() / 1000;
			String timezone = newbody.getCode().getParams().getTimezone();
			expectedtime = transformTime(timestamp, timezone);
		} catch (JsonParseException e) {
			LOGGER.error(e);
		} catch (JsonMappingException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return expectedtime;
	}

	/**
	 * Method to map a json response to NAE_Response_Body object to test if
	 * response format match expected format
	 * 
	 * @param Response
	 *            : response getting from NAE api
	 * @return boolean: true or false to determine if successful
	 */
	public boolean mapResponse(Response response) {
		boolean isMapSuccess = false;
		String responseBody = response.body().asString();
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<NAE_Response_Body> list = mapper.readValue(responseBody,
					new TypeReference<List<NAE_Response_Body>>() {
					});
			isMapSuccess = true;
		} catch (JsonParseException e) {
			LOGGER.error(e);
		} catch (JsonMappingException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return isMapSuccess;
	}

	/**
	 * Method getting response from NAE Api
	 * 
	 * @param Response
	 *            : Json response from NAE api
	 * @return String: "endpoint" field value in response body
	 */
	public String getEndPoint(Response response) {
		String url = "";
		String responseBody = response.body().asString();
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<NAE_Response_Body> list = mapper.readValue(responseBody,
					new TypeReference<List<NAE_Response_Body>>() {
					});
			url = list.get(0).getEndpoint();
		} catch (JsonParseException e) {
			LOGGER.error(e);
		} catch (JsonMappingException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return url;
	}

	/**
	 * Method getting the request count sent to the mock server based on
	 * requested URL
	 * 
	 * @param url
	 *            : requested URL from NAE
	 * @return int: number of requests
	 */
	public int countRequests(String url) {
		WireMock.configureFor(MOCK_SERVER, MOCK_SERVER_PORT);
		// RequestPatternBuilder builder=new
		// RequestPatternBuilder(RequestMethod.POST,urlMatching(url));
		RequestPatternBuilder builder = new RequestPatternBuilder(
				RequestMethod.POST, urlMatching(url));
		List<LoggedRequest> reqs = findAll(builder);
		return reqs.size();
	}

	/**
	 * Method that prints all requests based on requested URL
	 * 
	 * @param url
	 *            : requested URL from NAE
	 */
	public void printAllRequest(String url) {
		WireMock.configureFor(MOCK_SERVER, MOCK_SERVER_PORT);
		RequestPatternBuilder builder = new RequestPatternBuilder(
				RequestMethod.POST, urlMatching(url));
		List<LoggedRequest> reqs = findAll(builder);
		int listSize = reqs.size();
		for (int i = 0; i < listSize; i++) {
			LOGGER.debug(reqs.get(i).getBodyAsString());
		}
	}

	/**
	 * Method getting the payload object in the request sent tot the mock server
	 * 
	 * @param url
	 *            : requested URL from NAE
	 * @return String: payload object mapped into a string
	 */
	public String getRequestPayload(String url) {
		String requestPayload = "";
		WireMock.configureFor(MOCK_SERVER, MOCK_SERVER_PORT);
		RequestPatternBuilder builder = new RequestPatternBuilder(
				RequestMethod.POST, urlMatching(url));
		List<LoggedRequest> reqs = findAll(builder);
		int count = reqs.size();
		if (count >= 1) {
			requestPayload = reqs.get(count - 1).getBodyAsString();
		}
		return requestPayload;
	}

	/**
	 * Method getting the payload object in the request sent tot the mock server
	 * 
	 * @param url
	 *            : requested URL from NAE
	 * @param int n: number of last requests
	 * @return String: payload object mapped into a string
	 */
	public List<String> getRequestPayloadList(String url, int n) {
		String requestPayload = "";
		List<String> reqsList = new ArrayList<String>();
		WireMock.configureFor(MOCK_SERVER, MOCK_SERVER_PORT);
		RequestPatternBuilder builder = new RequestPatternBuilder(
				RequestMethod.POST, urlMatching(url));
		List<LoggedRequest> reqs = findAll(builder);
		int count = reqs.size();
		if (count >= 1 && n >= 1) {
			for (int i = n; i >= 1; i--) {
				requestPayload = reqs.get(count - i).getBodyAsString();
				reqsList.add(requestPayload);
			}
		}
		return reqsList;
	}

	// Method trying to map request into NAE request object,return false if fail
	// to map
	/**
	 * Method mapping request into NAE request object, return false if fail
	 * 
	 * @param String
	 *            : request json payload
	 * @return boolean: True of False to determine if successful
	 */
	public boolean mapRequest(String requestPayload) {
		boolean isMapSuccess = false;
		ObjectMapper mapper = new ObjectMapper();
		try {
			Payload payload = mapper.readValue(requestPayload, Payload.class);
			isMapSuccess = true;
		} catch (JsonParseException e) {
			// e.printStackTrace();
			LOGGER.error(e);
		} catch (JsonMappingException e) {
			// e.printStackTrace();
			LOGGER.error(e);
		} catch (IOException e) {
			// e.printStackTrace();
			LOGGER.error(e);
		}
		return isMapSuccess;
	}

	/**
	 * Method posting an event into EEL
	 * 
	 * @param String
	 *            : event json path
	 * @return Response: http response
	 */
	public Response sendEventToEEL(String eventJson) {
		Response response = null;
		response = given().log().all().header("X-Debug", true)
				.body(getFile(eventJson))
				.post(NAE_Properties.EEL_EVENT_ENDPOINT);
		// response.prettyPrint();
		return response;
	}

	/**
	 * Method posting an event into EEL
	 * 
	 * @param String
	 *            : event json string
	 * @return Response: http response
	 */
	public Response sendEventToEELByString(String eventJsonString) {
		Response response = null;
		response = given().log().all().header("X-Debug", true)
				.body(eventJsonString).post(NAE_Properties.EEL_EVENT_ENDPOINT);
		// response.prettyPrint();
		return response;
	}

	/**
	 * Method mapping request into NAE request object, return false if fail
	 * 
	 * @param String
	 *            : request json payload
	 * @param int: a modifier to control time
	 * @return boolean: True of False to determine if successful
	 */

	public String modifyEventJson(String eventJson, int n) {
		String modifiedEvent = getFile(eventJson);
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(modifiedEvent);
			JsonNode contentNode = rootNode.path("content");
			JsonNode timestamp = contentNode.path("timestamp");
			String timestampString = String.valueOf(timestamp.getLongValue());
			long newTimestamp = timestamp.getLongValue() + 60 * 1000 * n;
			String newTimestampString = String.valueOf(newTimestamp);
			modifiedEvent = modifiedEvent.replace(timestampString,
					newTimestampString);

		} catch (JsonProcessingException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return modifiedEvent;
	}

	// unit test for event modification
	// @Test
	public void eventtest() {
		modifyEventJson("test_data/EventToEEL.json", 1);
	}

	// a simple unit test for time transform method
	// @Test
	public void timetest() throws ParseException {
		NAE_Real_Util util = new NAE_Real_Util();
		LOGGER.debug(util.expectedTime("test_data/Valid_JSON.json"));
	}

}
