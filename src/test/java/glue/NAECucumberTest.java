package glue;

import org.testng.annotations.Test;


import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;

@CucumberOptions(plugin="json:target/cucumber.json",glue={"glue"},features={"src/test/resources/test_features/MOCK_Request_Number.feature"})


public class NAECucumberTest extends AbstractTestNGCucumberTests{

	
}
