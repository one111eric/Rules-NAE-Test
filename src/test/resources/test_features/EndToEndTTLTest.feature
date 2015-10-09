@EndToEndTTL-XRULES-1530
Feature: Test Rule with State (SessionId and TTL) 
Scenario Outline: Basic end to end to verify new rule works 
	Given "<location>" with site "<site_id>" has a rule with TTL <ttl_time> seconds 
#	When I check the number of request from "<location>" received by mock server
#	Then I should get a number
#	And I post an Event of "<location>" to EEL with SessionId "<SessionId>" 
#	Then I should see the number of the request to that location increased by <x>
#	
	Examples: 
		|location|site_id|ttl_time|SessionId|x|
		|MiaoLocation00005|850601|10|miaoSession1|1|
#		
#Scenario Outline: test how different delays and session names affect results
#	Given "<location>" has a rule with TTL <ttl_time> seconds 
#	When I check the number of request from "<location>" received by mock server
#	Then I should get a number
#	And I post an Event of "<location>" to EEL with SessionId "<SessionId>" 
#	And I wait <wait_time> seconds 
#	And I post an Event of "<location>" to EEL with SessionId "<SessionId1>"
#	Then I should see the number of the request to that location increased by <x>
#	
#	Examples: 
#		|location|ttl_time|SessionId|wait_time|SessionId1|x|
#		|MiaoLocation5|10|miaoSession2|5|miaoSession2|1|
#		|MiaoLocation5|10|miaoSession3|5|miaoSession4|2|
#		|MiaoLocation5|10|miaoSession5|11|miaoSession5|2|
#		
#Scenario Outline: test how new Session Id behave for two location
#    Given "<location1>" has a rule with TTL <ttl_time> seconds
#    Given "<location2>" has a rule with TTL <ttl_time> seconds
#    When I check the number of all requests received by mock server
#	Then I should get a number
#    And I post an Event of "<location1>" to EEL with SessionId "<SessionId1>"
#	And I post an Event of "<location2>" to EEL with SessionId "<SessionId2>"
#	Then I should see the number of the request to mock server increased by <x>
#	
#	Examples:
#	|location1|location2|ttl_time|SessionId1|SessionId2|x|
#	|MiaoLocation6|MiaoLocation7|10|MiaoSession6|MiaoSession7|2|