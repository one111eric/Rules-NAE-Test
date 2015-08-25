package impl;
import static com.jayway.restassured.RestAssured.given;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;

import model.NAE_Request_Body;
import model.NAE_Response_Body;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.http.ContentType;
public class NAE_Real_Util {

	public NAE_Real_Util(){
		
	}
	
	public String RequestBodyString(String filepath){
		String responsebody="";
		try {
			responsebody = new Scanner(new File(filepath))
					.useDelimiter("\\Z").next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return responsebody;
	}
	
	//Method getting real respnse, using X-Debug header true
	public Response getNAERealResponse(String filepath){
		String url="http://eel.qa.rules.vacsv.com/notify";
		Response response=given()
				.log()
				.all()
				.header("X-Debug",true)
				.body(RequestBodyString(filepath))
				.expect().statusCode(200)
				.post(url);
		response.prettyPrint();
		return response;
	}
}
