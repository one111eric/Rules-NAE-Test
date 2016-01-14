package glue;

import impl.SiteAndLocation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;











import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;

/**
 * Test Initializer
 *
 * @author Miao Xiang
 *
 */

@CucumberOptions(plugin={"pretty","html:target/cucumber","json:target/cucumber.json"},glue={"glue"},features={"src/test/resources/test_features/" },

  tags={"@EndToEnd-XRULES-347"})
//@EndToEndMT-XRULES-1168,@EndToEnd-XRULES-347,  , @EndToEndTTL-XRULES-1530

@Test
public class NAECucumberTest extends AbstractTestNGCucumberTests{
	private static final Logger LOGGER = Logger
			.getLogger(NAECucumberTest.class);
	@BeforeTest
	public void clearCSV(){
		try{
			File file=new File("times.csv");
			if(!file.exists()){
				file.createNewFile();
				Writer writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("times.csv"))); 
				writer.write("eventId"+","+"submittedTimeMillis"+","+"mockReceivedTimeMillis"+ ","+ "TransactionTime"+",\n");
				writer.flush();
				writer.close();
			}
			else{
				file.delete();
				file.createNewFile();
				Writer writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("times.csv"))); 
					writer.write("eventId"+","+"submittedTimeMillis"+","+"mockReceivedTimeMillis"+ ","+ "TransactionTime"+",\n");
					writer.flush();
					writer.close();
			}
		}
		catch(IOException e){
			LOGGER.debug(e);
		}
	}
	
	@AfterTest
	public void clearSitesAndLocations(){
		SiteAndLocation newSiteAndLocation=new SiteAndLocation();
		String sitesFile="src/test/resources/test_data/sites.txt";
		String locationsFile="src/test/resources/test_data/locations.txt";
		
		try(BufferedReader br=new BufferedReader(new FileReader(sitesFile))){
			String line;
			while((line=br.readLine())!=null){
				newSiteAndLocation.deleteSite(line);
			}
		} catch (FileNotFoundException e) {
			LOGGER.error(e);		} 
		  catch (IOException e) {
			LOGGER.error(e);
		}
		
		try(BufferedReader br=new BufferedReader(new FileReader(locationsFile))){
			String line;
			while((line=br.readLine())!=null){
				newSiteAndLocation.deleteLocation(line);
			}
		} catch (FileNotFoundException e) {
			LOGGER.error(e);		} 
		  catch (IOException e) {
			LOGGER.error(e);
		}
		
	}
	@AfterTest
	public void generateReports() {
		String dataFile = "times.csv";
		String data = "<!DOCTYPE html><html><head><style>td {border: 1px solid black;}</style></head><body><div><table>";
		String line = "";
		String cvsSplitBy = ",";
		BufferedReader br = null;
        int rowCount=0;
        long minLatency=0;
        long maxLatency=0;
        long averageLatency=0;
        long sumLatency=0;
        List<Long> latencies=new ArrayList<Long>();
		try {
			br = new BufferedReader(new FileReader(dataFile));
			while ((line = br.readLine()) != null) {
				data += "<tr>";
				// use comma as separator
				String[] info = line.split(cvsSplitBy);
				for (int i = 0; i < info.length; i++) {
					data += "<td>" + info[i] + "</td>";
				}
				data += "</tr>";
				if(rowCount>0){
					latencies.add(Long.valueOf(info[3]));
					sumLatency+=Long.valueOf(info[3]);
				}
				rowCount=rowCount+1;
			}
			minLatency=latencies.get(latencies.indexOf(Collections.min(latencies)));
			maxLatency=latencies.get(latencies.indexOf(Collections.max(latencies)));
            averageLatency=sumLatency/latencies.size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		data += "</table></div>";
		data += "<div>Summary:<table><tr><th>Min</th><th>Max</th><th>Average</th></tr>";
		data += "<tr><td>"+minLatency+"</td><td>"+maxLatency+"</td><td>"+averageLatency+"</td></tr></table></div>";
		
		data += "</body></html>";
		System.out.println(data);
		try {
			File file = new File("LatencyReport.html");
			if (!file.exists()) {
				file.createNewFile();
				Writer writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream("LatencyReport.html")));
				writer.write(data);
				writer.flush();
				writer.close();
			} else {
				file.delete();
				file.createNewFile();
				Writer writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream("LatencyReport.html")));
				writer.write(data);
				writer.flush();
				writer.close();
			}
		} catch (IOException e) {
			LOGGER.debug(e);
		}

	}
}

