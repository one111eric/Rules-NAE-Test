package impl;

/**
 * Properties used by NAE/ENDtoEnd test
 * Mainly server endpoints
 */
public class NAE_Properties {
	public static final String NAE_URL = "http://nae.tps.i.xrs.vacsv.com/notify";
	public static final String MOCK_SERVER = "mock.rules.vacsv.com";
	public static final int MOCK_SERVER_PORT = 8080;
	public static final boolean WITH_X_DEBUG_HEADER = true;
	public static final boolean WITHOUT_X_DEBUG_HEADER = false;
	public static final String MOCKSERVER_ADDRESS = "http://mock.rules.vacsv.com:8080";
	public static final String EEL_ENDPOINT = System.getProperty("eelURL");
	//public static final String EEL_ENDPOINT = "http://eel.tps.rules.comcast.com/";
	public static final String REST_ENDPOINT = System.getProperty("restURL");
	//public static final String REST_ENDPOINT = "http://rest.tps.rules.comcast.com/";
	public static final String PROVISION_ENDPOINT = System.getProperty("restURL")+"provisions";
	//public static final String PROVISION_ENDPOINT = "http://rest.tps.rules.comcast.com/provisions";
	public static final String EEL_EVENT_ENDPOINT = System.getProperty("eelURL")+"elementsevent";
	//public static final String EEL_EVENT_ENDPOINT = "http://eel.tps.rules.comcast.com/elementsevent";
	public static final String EEL_HEALTH_ENDPOINT= System.getProperty("eelURL")+"health";
	//public static final String EEL_HEALTH_ENDPOINT= "http://eel.tps.rules.comcast.com/health";
    public static final String LOCATION_ENDPOINT = REST_ENDPOINT + "locations/";
    public static final String MOLECULE_ENDPOINT = System.getProperty("moleculeURL");
    //public static final String MOLECULE_ENDPOINT = "http://molecule.tps.i.xrs.vacsv.com/";
	public static final String MOLECULE_MAPPING_ENDPOINT = MOLECULE_ENDPOINT
			+ "mappings/xh/";
	public static final String PROD_ES_URL="http://efk1:efk1efk1@prod.esapi.sjc.i.sv.comcast.com/";
	public static final String EEL_ENVIRONMENT="eel.tps.A";
	public static final String NAE_ENVIROMENT="nae.tps.A";
}
