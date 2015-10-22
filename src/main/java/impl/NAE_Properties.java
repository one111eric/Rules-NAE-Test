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
	public static final String EEL_ENDPOINT = "http://eel.tps.rules.comcast.com/";
	public static final String REST_ENDPOINT = "http://rest.tps.rules.comcast.com/";
	public static final String PROVISION_ENDPOINT = "http://rest.tps.rules.comcast.com/provisions";
	public static final String EEL_EVENT_ENDPOINT = "http://eel.tps.rules.comcast.com/elementsevent";
    public static final String EEL_HEALTH_ENDPOINT= "http://eel.tps.rules.comcast.com/health";
    public static final String LOCATION_ENDPOINT = REST_ENDPOINT + "locations/";
	public static final String MOLECULE_ENDPOINT = "http://molecule.tps.i.xrs.vacsv.com/";
	public static final String MOLECULE_MAPPING_ENDPOINT = MOLECULE_ENDPOINT
			+ "mappings/xh/";
}
