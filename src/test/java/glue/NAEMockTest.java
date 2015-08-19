package glue;

import org.testng.annotations.Test;
//import org.testng.AssertJUnit;
import static org.mockserver.integration.ClientAndProxy.startClientAndProxy;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;



import impl.NAE_Mock_Util;
import org.mockserver.integration.ClientAndProxy;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.jayway.restassured.path.json.JsonPath;

public class NAEMockTest {
	private ClientAndProxy proxy;
	private ClientAndServer mockServer;
    
    
	NAE_Mock_Util newUtil = new NAE_Mock_Util();

	
    //Start Mock Server before test
	@BeforeTest
	public void startProxy() {
		mockServer = startClientAndServer(1080);
		proxy = startClientAndProxy(1090);
		
	}
    //Shut Down Mock Server after test
	@AfterTest
	public void stopProxy() {
		proxy.stop();
		mockServer.stop();
	}
    
	//Inject a Stub to mock server and then verify the result
	public void InjectStub() 
	{
		String requestbody=newUtil.RequestBodyString();
		String responsebody=newUtil.ResponseBodyString();
		
		mockServer.when(
				request()
						.withMethod("POST")
						.withPath("/nae")
						.withBody(requestbody)
		).respond(
				response()
						.withStatusCode(200)
						.withHeaders(
								new Header("Content-Type",
										"application/json; charset=utf-8"),
								new Header("Cache-Control",
										"public, max-age=3600"))
						.withBody(responsebody));
	}
	//Test
	@Test
	public void MockTest()
	{
		InjectStub();
		JsonPath path = new JsonPath(newUtil.getNAEMockResponse()
				.asString());
		String message = path.get("message").toString();
		System.out.println("message: " + message);
		Assert.assertTrue(message.equals("Alarm triggered by back door"));
	}
	
}
