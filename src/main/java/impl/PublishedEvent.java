package impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jayway.restassured.path.json.JsonPath;

public class PublishedEvent {
	public PublishedEvent(String eventId, long eventTimestamp) {
		this.eventId = eventId;
		this.eventTimestamp = eventTimestamp;
	}

	private long eventTimestamp;
	private long receivedTimestamp;
	private long logTimestamp;
	private String eventId;
	private boolean ifReceived=false;
	private boolean ifPublished=false;

	public String getEventId() {
		return eventId;
	}
    
	public long getReceivedTimestamp() {
		return receivedTimestamp;
	}
	
	public long getLogTimestamp() {
		return logTimestamp;
	}
	
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public boolean isIfPublished() {
		return ifPublished;
	}
	
	public boolean isIfReceived(){
		return ifReceived;
	}

	public void setIfPublished(boolean ifPublished) {
		this.ifPublished = ifPublished;
	}

	@SuppressWarnings("deprecation")
	public void checkifPublished() throws ClientProtocolException, IOException,
			ParseException {
		String dateIndex = getDateIndex("UTC", eventTimestamp);
		// "xrs-2016.01.05";
		String typeIndex = "/csv-rules-eel";
		Commons.delay(15000);
		String eelEnv=NAE_Properties.EEL_ENVIRONMENT;
		String startQuery=NAE_Properties.PROD_ES_URL
				+ dateIndex
				+ typeIndex
				+ "/_search?q=tx.traceId:"
				+ "%22"
				+ this.eventId 
				+ "%22%20AND%20env.name:%22"
				+ eelEnv
				+"%22%20AND%20event:%22accepted%22";
		
		System.out.println(startQuery);
		HttpClient client = new DefaultHttpClient();
		HttpGet startRequest = new HttpGet(startQuery);
		HttpResponse startResponse = client.execute(startRequest);
		BufferedReader rd = new BufferedReader(new InputStreamReader(startResponse
				.getEntity().getContent()));

		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			buffer.append(line);
		}
		String responseString = buffer.toString();
		System.out.println(responseString);
		JsonPath path = new JsonPath(responseString);
		int eelAcceptedCount = path.getInt("hits.total");
		System.out.println("Found: " + eelAcceptedCount);
		if (eelAcceptedCount == 1) {
			String hitsPath = "hits.hits";
			List<Object> logList = path.getList(hitsPath);
			Object logHit = logList.get(0);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			String logHitString = mapper.writeValueAsString(logHit);
			JsonPath log = new JsonPath(logHitString);
			String timestampString = log.getString("_source.log.timestamp");
			System.out.println(timestampString);
//			String date = timestampString.substring(0, 10);
//			System.out.println(date);
//			String time = timestampString.substring(11, 23);
//			System.out.println(time);
//			String timeNew = date + " " + time;
//			SimpleDateFormat dateFormat = new SimpleDateFormat(
//					"yyyy-MM-dd HH:mm:ss.SSS");
//			TimeZone tz = TimeZone.getTimeZone("UTC");
//			dateFormat.setTimeZone(tz);
//			Date parseDate = dateFormat.parse(timeNew);
//			long timestamp = parseDate.getTime();
			long timestamp = kibanaTimeConversion(timestampString);
			System.out.println(timestamp);
			this.receivedTimestamp = timestamp;
			this.ifReceived = true;
		}

		else {
			this.ifReceived = false;
			System.out.printf("%d accepted by EEL\n",eelAcceptedCount);
		}
		
		String endQuery = NAE_Properties.PROD_ES_URL
				+ dateIndex
				+ typeIndex
				+ "/_search?q="
				+ "%22"
				+ this.eventId 
				+ "%22%20AND%20env.name:%22"
				+ NAE_Properties.NAE_ENVIROMENT
				+"%22%20AND%20event:%22published_event%22";
		// %20AND%20event%3A%22reached_service%22"
		System.out.println(endQuery);
		// this.eventId
		HttpGet endRequest = new HttpGet(endQuery);
		HttpResponse endResponse = client.execute(endRequest);
		// Assert.assertEquals(200,response.getStatusLine().getStatusCode());
		rd = new BufferedReader(new InputStreamReader(endResponse.getEntity().getContent()));
		buffer = new StringBuffer();
		line = "";
		while ((line = rd.readLine()) != null) {
			buffer.append(line);
		}
		responseString = buffer.toString();
		System.out.println(responseString);
		path = new JsonPath(responseString);
		int naePublishedCount = path.getInt("hits.total");
		System.out.println("Found: " + naePublishedCount);
		if (naePublishedCount == 1) {
			String hitsPath = "hits.hits";
			List<Object> logList = path.getList(hitsPath);
			Object logHit = logList.get(0);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			String logHitString = mapper.writeValueAsString(logHit);
			JsonPath log = new JsonPath(logHitString);
			String timestampString = log.getString("_source.log.timestamp");
			System.out.println(timestampString);
//			String date = timestampString.substring(0, 10);
//			System.out.println(date);
//			String time = timestampString.substring(11, 23);
//			System.out.println(time);
//			String timeNew = date + " " + time;
//			SimpleDateFormat dateFormat = new SimpleDateFormat(
//					"yyyy-MM-dd HH:mm:ss.SSS");
//			TimeZone tz = TimeZone.getTimeZone("UTC");
//			dateFormat.setTimeZone(tz);
//			Date parseDate = dateFormat.parse(timeNew);
			long timestamp = kibanaTimeConversion(timestampString);
			System.out.println(timestamp);
			this.logTimestamp = timestamp;
			this.ifPublished = true;
		}

		else {
			this.ifPublished = false;
			System.out.printf("%d published by NAE\n",naePublishedCount);
		}

	}
    
	public long kibanaTimeConversion(String kibanaLogTime) throws ParseException{
		long unixTime;
		String date = kibanaLogTime.substring(0, 10);
		System.out.println(date);
		String time = kibanaLogTime.substring(11, 23);
		System.out.println(time);
		String timeNew = date + " " + time;
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		TimeZone tz = TimeZone.getTimeZone("UTC");
		dateFormat.setTimeZone(tz);
		Date parseDate = dateFormat.parse(timeNew);
		unixTime = parseDate.getTime();
		return unixTime;
	}
	
	public String getDateIndex(String timezone, long eventTimestamp) {
		String dateIndex = "";
		dateIndex = "xrs-" + transformTime(eventTimestamp, timezone);
		return dateIndex;
	}

	public String transformTime(long timeStamp, String timeZone) {
		String timeString;
		Date time = new Date(timeStamp);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
		TimeZone tz = TimeZone.getTimeZone(timeZone);
		formatter.setTimeZone(tz);
		timeString = formatter.format(time).toLowerCase();
		return timeString;
	}
}
