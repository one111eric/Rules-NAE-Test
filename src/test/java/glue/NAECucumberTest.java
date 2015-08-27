package glue;

import org.testng.annotations.Test;


import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;

@CucumberOptions(glue={"glue"},features={"src/test/resources/test_features/"})


public class NAECucumberTest extends AbstractTestNGCucumberTests{

	
}
