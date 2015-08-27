package glue;

import org.junit.runner.RunWith;
import cucumber.api.junit.Cucumber;
import cucumber.api.CucumberOptions;

@CucumberOptions(glue={"glue"},features={"src/test/resources/test_features/"})

@RunWith(Cucumber.class)
public class NAECucumberTest {

	
}
