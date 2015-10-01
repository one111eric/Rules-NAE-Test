package glue;




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

  tags={"@EndToEnd-XRULES-347,@EndToEndMT-XRULES-1168"})


@Test
public class NAECucumberTest extends AbstractTestNGCucumberTests{
   
	
}
