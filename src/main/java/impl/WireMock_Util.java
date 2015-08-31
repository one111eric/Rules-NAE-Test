package impl;
import static com.github.tomakehurst.wiremock.client.WireMock.*;


import java.util.List;

import org.junit.Test;

import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.github.tomakehurst.wiremock.http.RequestMethod;


public class WireMock_Util {
	public WireMock_Util(){
		
	}
	
	
	 @Test
	    public void findingRequests() {
		    WireMock.configureFor("localhost",5678);
		    RequestPatternBuilder builder=new RequestPatternBuilder(RequestMethod.GET,urlMatching("/abc"));
	        //List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/abc")));
	        List<LoggedRequest> reqs=findAll(builder);
	        System.out.println(reqs.size());
	        
	    }
	
	
	
	
}
