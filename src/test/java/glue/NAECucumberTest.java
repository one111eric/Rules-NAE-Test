package glue;




import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.log4j.Logger;
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

  tags={"@EndToEndMT-XRULES-1168, @EndToEnd-XRULES-347, @EndToEndTTL-XRULES-1530"})
// 

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
			}
			else{
				file.delete();
				file.createNewFile();
				Writer writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("times.csv"))); 
					writer.write("eventId"+","+"submittedTimeMillis"+","+"mockReceivedTimeMillis"+"\n");
					writer.flush();
					writer.close();
			}
		}
		catch(IOException e){
			LOGGER.debug(e);
		}
	}
}

