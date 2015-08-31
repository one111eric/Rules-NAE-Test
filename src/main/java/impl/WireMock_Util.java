package impl;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.util.List;

import org.junit.Test;

import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.github.tomakehurst.wiremock.http.RequestMethod;


public class WireMock_Util {
	private final static String VALID_RESPONSE_JSON="test_data/Valid_RESPONSE.json";
	private final static String VALID_JSON="test_data/Valid_JSON.json";
	private static final boolean WITH_X_DEBUG_HEADER=true;
	private static final boolean WITHOUT_X_DEBUG_HEADER=false;
	private NAE_Real_Util util=new NAE_Real_Util();
	public WireMock_Util(){
		
	}
	
	
	 @Test
	    public void findingRequests() {
		    
		    //util.getNAERealResponse(VALID_JSON, "POST", WITH_X_DEBUG_HEADER);
		    WireMock.configureFor("mock.rules.vacsv.com",8080);
		    //RequestPatternBuilder builder=new RequestPatternBuilder(RequestMethod.GET,urlMatching("/abc"));
		    RequestPatternBuilder builder=new RequestPatternBuilder(RequestMethod.POST,urlMatching("/publish/xhs/tps/209052550323032015Comcast.cust/notifications/alarm.json"));
		    
	        //List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/abc")));
	        List<LoggedRequest> reqs=findAll(builder);
	        
	        System.out.println(reqs.size());
	        //System.out.print(reqs.get(0).getBodyAsString());
	        
	    }
	
	
	
	
}
