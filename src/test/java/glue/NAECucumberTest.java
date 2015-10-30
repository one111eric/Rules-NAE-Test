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

  tags={"@EndToEnd-XRULES-347, @EndToEndTTL-XRULES-1530"})
//@EndToEndMT-XRULES-1168,@EndToEnd-XRULES-347,  

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
				writer.write("eventId"+","+"submittedTimeMillis"+","+"mockReceivedTimeMillis"+",\n");
				writer.flush();
				writer.close();
			}
			else{
				file.delete();
				file.createNewFile();
				Writer writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("times.csv"))); 
					writer.write("eventId"+","+"submittedTimeMillis"+","+"mockReceivedTimeMillis"+",\n");
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
}

