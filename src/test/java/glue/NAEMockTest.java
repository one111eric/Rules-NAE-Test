package glue;

import org.testng.annotations.Test;


//import org.testng.AssertJUnit;
import static org.mockserver.integration.ClientAndProxy.startClientAndProxy;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import impl.NAE_Mock_Util;
import impl.NAE_Real_Util;

import org.mockserver.integration.ClientAndProxy;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.model.JsonSchemaBody;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import com.jayway.restassured.path.json.JsonPath;

public class NAEMockTest {
	private ClientAndProxy proxy;
	private ClientAndServer mockServer;

	NAE_Mock_Util newUtil = new NAE_Mock_Util();
    NAE_Real_Util realutil=new NAE_Real_Util();
	// Start Mock Server before test
	//@BeforeTest
	public void startProxy() {
		mockServer = startClientAndServer(1080);
		proxy = startClientAndProxy(1090);

	}

	// Shut Down Mock Server after test
	//@AfterTest
	public void stopProxy() {
		proxy.stop();
		mockServer.stop();
	}

	// Inject a Stub to mock server
	public void InjectStub() {
		String requestbody = newUtil.RequestBodyString();
		String responsebody = newUtil.ResponseBodyString();

		mockServer
				.when(request()
						.withMethod("POST")
						.withPath("/nae")

						//.withBody(
								//JsonSchemaBody
									//	.jsonSchemaFromResource("src/main/resources/templates/RequestBodySchema.json"))
				// .withBody(JsonBody.json(requestbody,MatchType.ONLY_MATCHING_FIELDS))

				// .withBody(new JsonBody(requestbody))

				 .withBody(requestbody)
				)
				.respond(
						response()
								.withStatusCode(200)
								.withHeaders(
										new Header("Content-Type",
												"application/json; charset=utf-8"),
										new Header("Cache-Control",
												"public, max-age=3600"))
								//.withBody(
									//	JsonSchemaBody
										//		.jsonSchemaFromResource("src/main/resources/templates/ResponseBodySchema.json"))
				 .withBody(responsebody));
				;
	}

	// Test
	//@Test
	public void MockTest() {
		InjectStub();
		JsonPath path = new JsonPath(newUtil.getNAEMockResponse().asString());
		String message = path.get("message").toString();
		System.out.println("message: " + message);
		Assert.assertTrue(message
				.equals("Alarm triggered at Wed Aug 19 13:27:11 PDT 2015"));
	}
	@Test
	public void realtest(){
		//String responsebody=realutil.getNAERealResponse("src/test/resources/test_data/Invalid_JSON.json","POST").body().asString();
		//System.out.print(responsebody);
		System.out.print(realutil.getNAERealResponse("src/test/resources/test_data/Invalid_JSON.json","POST",true).getStatusCode());
		//Assert.assertEquals(realutil.getNAERealResponse("src/test/resources/test_data/Valid_JSON.json","POST").getStatusCode(),200);
	}

}
