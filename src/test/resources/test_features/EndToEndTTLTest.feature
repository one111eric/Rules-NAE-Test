@EndToEndTTL-XRULES-1530
Feature: Test Rule with State (SessionId and TTL) 
Scenario Outline: Basic end to end to verify new rule works 
	Given "<location>" with site "<site_id>" has a rule with TTL <ttl_time> seconds 
	When I check the number of requests from "<location>" received by mock server
	Then I should have a number
	And I post an AIP Event of "<location>" to EEL with SessionId "<SessionId>" 
	Then I should see the number of the request to location "<location>" increased by <x>
	#And I write the data to file
	Examples: 
		|location|site_id|ttl_time|SessionId|x|
		|MiaoLocation10|850601|10|miaoSession1|1|
		
Scenario Outline: test how different delays and session names affect results
                  1. post two events with same sessionId with wait time within TTL
                  2. post two events with different sessionId with wait time within TTL
                  3. post two events with same sessionId with wait time beyond TTL
	Given "<location>" with site "<site_id>" has a rule with TTL <ttl_time> seconds 
	When I check the number of requests from "<location>" received by mock server
	Then I should have a number
	And I post an AIP Event of "<location>" to EEL with SessionId "<SessionId>" 
	And I wait <wait_time> seconds 
	And I post an AIP Event of "<location>" to EEL with SessionId "<SessionId1>"
	Then I should see the number of the request to location "<location>" increased by <x>
	
	Examples: 
		|location|site_id|ttl_time|SessionId|wait_time|SessionId1|x|
		|MiaoLocation10|850601|10|miaoSession2|5|miaoSession2|1|
		|MiaoLocation10|850601|10|miaoSession3|5|miaoSession4|2|
		|MiaoLocation10|850601|10|miaoSession5|11|miaoSession5|2|
		
Scenario Outline: test how new Session Id behave for two locations
                  1. two locations with different sessionId
                  2. two locations with same sessionId
    Given "<location1>" with site "<site_id1>" has a rule with TTL <ttl_time> seconds
    Given "<location2>" with site "<site_id2>" has a rule with TTL <ttl_time> seconds
    When I check the number of all requests received by mock server
	Then I should get a number of all requests received by mock server
    And I post an AIP Event of "<location1>" with "<site_id1>" to EEL with SessionId "<SessionId1>"
	And I post an AIP Event of "<location2>" with "<site_id2>" to EEL with SessionId "<SessionId2>"
	Then I should see the number of all requests to mock server increased by <x>
	
	Examples:
	|location1|location2|site_id1|site_id2|ttl_time|SessionId1|SessionId2|x|
	|MiaoLocation11|MiaoLocation12|850602|850603|10|MiaoSession6|MiaoSession7|2|
	|MiaoLocation11|MiaoLocation12|850602|850603|10|MiaoSession8|MiaoSession8|2|