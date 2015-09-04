package impl;

import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.jayway.restassured.RestAssured.given;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;

import model.NAE_Request_Body;
import model.NAE_Response_Body;
import model.Payload;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
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

    private static final String NAE_URL=NAE_Properties.NAE_URL;
    private static final String MOCK_SERVER=NAE_Properties.MOCK_SERVER;
    private static final int MOCK_SERVER_PORT=NAE_Properties.MOCK_SERVER_PORT;
	
    //Logger instance
    private static final Logger LOGGER = Logger.getLogger(NAE_Real_Util.class);
	// General Constructor
	public NAE_Real_Util() {

	}

	// A general method to read file to string
	public String requestBodyString(String filepath) {
		String responsebody = "";
		try {
			responsebody = new Scanner(new File(filepath)).useDelimiter("\\Z")
					.next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			//LOGGER.error(e);
		}
		return responsebody;
	}

	// Method that get file from resources folder
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
			e.printStackTrace();
			//LOGGER.error(e);
		}
		return result.toString();
	}

	// Method getting response from NAE Api, using X-Debug header or not
	public Response getNAERealResponse(String filepath, String Verb, boolean Header) {
		Response response = null;
		if(Header){
		switch (Verb) {
		case "POST":
			response = given().log().all().header("X-Debug", true)
					.body(getFile(filepath))
					.post(NAE_URL);
			response.prettyPrint();
			break;
		case "GET":
			response = given().log().all().header("X-Debug", true)
					.get(NAE_URL);
			response.prettyPrint();
			break;
		case "PUT":
			response = given().log().all().header("X-Debug", true)
					.body(getFile(filepath))
					.put(NAE_URL);
			response.prettyPrint();
			break;
		case "DELETE":
			response = given().log().all().header("X-Debug", true)
					.body(getFile(filepath))
					.delete(NAE_URL);
			response.prettyPrint();
		default:
		}
		}
		else{
			switch (Verb) {
			case "POST":
				response = given().log().all()
						.body(getFile(filepath))
						.post(NAE_URL);
				response.prettyPrint();
				break;
			case "GET":
				response = given().log().all()
						.get(NAE_URL);
				response.prettyPrint();
				break;
			case "PUT":
				response = given().log().all()
						.body(getFile(filepath))
						.put(NAE_URL);
				response.prettyPrint();
				break;
			case "DELETE":
				response = given().log().all()
						.body(getFile(filepath))
						.delete(NAE_URL);
				response.prettyPrint();
			default:
			}
		}
		return response;
	}	
	

	// Method to transform Unix millisecond timestamp to readable time based on
	// Timezone
	public String transformTime(long timestamp, String timezone) {
		String timestring;
		Date time = new Date(timestamp * 1000);
		SimpleDateFormat formatter = new SimpleDateFormat("hh:mma");
		TimeZone tz = TimeZone.getTimeZone(timezone);
		formatter.setTimeZone(tz);
		timestring = formatter.format(time).toLowerCase();
		return timestring;
	}
	
	//Method getting the expected time from timestamp in request json
	public String expectedTime(String requestfilepath){
		String expectedtime="";
		String requestbody=getFile(requestfilepath);
		ObjectMapper mapper =new ObjectMapper();
		try {
			NAE_Request_Body newbody=mapper.readValue(requestbody, NAE_Request_Body.class);
			long timestamp=newbody.getCode().getData().getTimestamp()/1000;
			String timezone=newbody.getCode().getParams().getTimezone();
            expectedtime=transformTime(timestamp,timezone);		
		} catch (JsonParseException e) {
			//e.printStackTrace();
			LOGGER.error(e);
		} catch (JsonMappingException e) {
			//e.printStackTrace();
			LOGGER.error(e);
		} catch (IOException e) {
			//e.printStackTrace();
			LOGGER.error(e);
		}
		return expectedtime;
	}

	// Method to map a json response to NAE_Response_Body object to test if
	// response format matches expected format
	public boolean mapResponse(Response response) {
		boolean IsMapSuccess = false;
		String responsebody = response.body().asString();
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<NAE_Response_Body> list = mapper.readValue(responsebody,
					new TypeReference<List<NAE_Response_Body>>() {
					});
			IsMapSuccess = true;
		} catch (JsonParseException e) {
			//e.printStackTrace();
			LOGGER.error(e);
		} catch (JsonMappingException e) {
			//e.printStackTrace();
			LOGGER.error(e);
		} catch (IOException e) {
			//e.printStackTrace();
			LOGGER.error(e);
		}
		return IsMapSuccess;
	}
	
	//Get the endpoint field value from NAE response
	public String getEndPoint(Response response){
	    String url="";
	    String responsebody = response.body().asString();
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<NAE_Response_Body> list = mapper.readValue(responsebody,
					new TypeReference<List<NAE_Response_Body>>() {
					});
			url=list.get(0).getEndpoint();
		} catch (JsonParseException e) {
			//e.printStackTrace();
			LOGGER.error(e);
		} catch (JsonMappingException e) {
			//e.printStackTrace();
			LOGGER.error(e);
		} catch (IOException e) {
			//e.printStackTrace();
			LOGGER.error(e);
		}
	    		return url;
	}
	
    //Get the request count sent to the mock server based on requested url
	public int countRequests(String url){
		WireMock.configureFor(MOCK_SERVER, MOCK_SERVER_PORT);
		RequestPatternBuilder builder=new RequestPatternBuilder(RequestMethod.POST,urlMatching(url));
		List<LoggedRequest> reqs=findAll(builder);
		
		return reqs.size();
	}
	
	//Get the payload object in the request sent to the mock server
	public String getRequestPayload(String url){
		String requestpayload="";
		WireMock.configureFor(MOCK_SERVER, MOCK_SERVER_PORT);
		RequestPatternBuilder builder=new RequestPatternBuilder(RequestMethod.POST,urlMatching(url));
		List<LoggedRequest> reqs=findAll(builder);
		int count=reqs.size();
		if(count>=1){
			requestpayload=reqs.get(count-1).getBodyAsString();
		}
		return requestpayload;
	}
	
	//Method trying to map request into NAE request object,return false if fail
	//to map
	public boolean mapRequest(String requestpayload){
		boolean IsMapSuccess=false;
		ObjectMapper mapper=new ObjectMapper();
		try {
			Payload payload=mapper.readValue(requestpayload, Payload.class);
			IsMapSuccess=true;
		} catch (JsonParseException e) {
			//e.printStackTrace();
			LOGGER.error(e);
		} catch (JsonMappingException e) {
			//e.printStackTrace();
			LOGGER.error(e);
		} catch (IOException e) {
			//e.printStackTrace();
			LOGGER.error(e);
		}
		return IsMapSuccess;
	}
	
	//a simple unit test for time transform method
	//@Test
	public void timetest() throws ParseException {
		NAE_Real_Util util = new NAE_Real_Util();
		System.out.println(util.expectedTime("test_data/Valid_JSON.json"));
		//LOGGER.log(p, util.ExpectedTime("test_data/Valid_JSON.json"));
	}

}
