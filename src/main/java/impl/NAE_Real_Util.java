package impl;

import static com.jayway.restassured.RestAssured.given;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

import model.NAE_Request_Body;
import model.NAE_Response_Body;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.http.ContentType;

public class NAE_Real_Util {
	private static final String NAE_URL = "http://eel.qa.rules.vacsv.com/notify";

	//General Constructor
	public NAE_Real_Util() {

	}
   //A general method to read file to string
	public String RequestBodyString(String filepath) {
		String responsebody = "";
		try {
			responsebody = new Scanner(new File(filepath)).useDelimiter("\\Z")
					.next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return responsebody;
	}

	//Method that get file from resources folder
	private String getFile(String fileName) {
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
		}
		return result.toString();
	}

	// Method getting response from NAE Api, using X-Debug header true
	public Response getNAERealResponse(String filepath, String Verb) {
		Response response = null;
		switch (Verb) {
		case "POST":
			response = given().log().all().header("X-Debug", true)
					.body(getFile(filepath))
					// .expect().statusCode(200)
					.post(NAE_URL);
			response.prettyPrint();
			break;
		case "GET":
			response = given().log().all().header("X-Debug", true)
			// .expect().statusCode(200)
					.get(NAE_URL);
			response.prettyPrint();
			break;
		case "PUT":
			response = given().log().all().header("X-Debug", true)
					.body(getFile(filepath))
					// .expect().statusCode(200)
					.put(NAE_URL);
			response.prettyPrint();
			break;
		case "DELETE":
			response = given().log().all().header("X-Debug", true)
					.body(getFile(filepath))
					// .expect().statusCode(200)
					.delete(NAE_URL);
			response.prettyPrint();
		default:
		}
		return response;
	}
	
	//Method to transform Unix millisecond timestamp to readable time based on Timezone
	public String getTime(long timestamp,String timezone){
        String timestring;
		Date time=new Date(timestamp*1000);
		SimpleDateFormat formatter = new SimpleDateFormat("hh:mma");
		TimeZone tz=TimeZone.getTimeZone(timezone);
		formatter.setTimeZone(tz);
		timestring=formatter.format(time);
	    return timestring;
	}
	
	//Method to map a json response to NAE_Response_Body object to test if response format matches expected format
	public boolean mapResponse(Response response){
		boolean IsMapSuccess=false;
		String responsebody=response.body().asString();
		ObjectMapper mapper=new ObjectMapper();
		try {
			NAE_Response_Body body=mapper.readValue(responsebody, NAE_Response_Body.class);
			IsMapSuccess=true;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return IsMapSuccess;
	}
	
	
	@Test
	public void timetest() throws ParseException{
	NAE_Real_Util util=new NAE_Real_Util();
	String x=util.getTime(1428686740,"EST");
	}
	
	
}