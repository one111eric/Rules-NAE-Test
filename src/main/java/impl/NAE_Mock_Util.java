package impl;

import static com.jayway.restassured.RestAssured.given;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.jayway.restassured.response.Response;

public class NAE_Mock_Util {
	// default constructor
	public NAE_Mock_Util() {
	}

	// Request Body String base on alarm_in_progress.json
	public String RequestBodyString() {
		String requestbody = "";
		// create a simple request body
		NAE_Request_Body body = new NAE_Request_Body();
		
		ObjectMapper mapper = new ObjectMapper();

		try {
			requestbody = mapper.writeValueAsString(body);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(requestbody);
		return requestbody;
	}

	// Response Body String based on notification.json
	public String ResponseBodyString() {
		String responsebody = "";
		NAE_Response_Body body = new NAE_Response_Body();
		body.setMessage("Alarm triggered by back door");
		ObjectMapper mapper = new ObjectMapper();
		try {
			responsebody = mapper.writeValueAsString(body);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responsebody;

	}

	// Method getting Mock response for SimpleMockTest.java
	
	public Response getMockResponse(String zone) {
		String url = "http://127.0.0.1:1080/nae";
		Response response = given()
				.log()
				.all()
				.body("{\n\"type\":\"ALARM_IN_PROGRESS\",\n\"zone\":\"" + zone
						+ "\"\n}").expect().statusCode(200).post(url);

		response.prettyPrint();
		return response;
	}

	// Method getting Mock response for NAEMockTest.java
	public Response getNAEMockResponse() {
		String url = "http://127.0.0.1:1080/nae";
		Response response = given().log().all().body(RequestBodyString())
				.expect().statusCode(200).post(url);

		response.prettyPrint();
		return response;
	}
}
