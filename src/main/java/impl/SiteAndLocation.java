package impl;

import static com.jayway.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import com.jayway.restassured.response.Response;

public class SiteAndLocation {
	private Map<String, String> headers = new HashMap<String, String>();
	public SiteAndLocation(){
		headers.put("Xrs-Tenant-Id", "xh");
	}
	public void deleteLocation(String locationName){
		String locationUrl=NAE_Properties.LOCATION_ENDPOINT+locationName+"/";
		Response response = null;
		// delete the existing location
		response = given().log().all().headers(this.headers)
				.delete(locationUrl);
		response.prettyPrint();
		Assert.assertEquals(ServerStatusCodes.OK,response.getStatusCode());
	}
	
	public void deleteSite(String site){
		String siteUrl=NAE_Properties.MOLECULE_MAPPING_ENDPOINT+site+"/";
		Response response = null;
		// delete the existing location
		response = given().log().all().headers(this.headers)
				.delete(siteUrl);
		response.prettyPrint();
		Assert.assertEquals(ServerStatusCodes.OK,response.getStatusCode());
	}
}
