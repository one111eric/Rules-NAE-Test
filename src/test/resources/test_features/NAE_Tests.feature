Feature: Test Notification Action Executor. 

Scenario: Invalid Json Request body 
	When I post an invalid json body 
	Then I should get an invalid json error message 
	
Scenario: Invalid Json size 
	When I post a Json file exceeding the character limit 
	Then I should get an request too large error message 
	
Scenario Outline: Wrong request method 
	When I make the http request with Verb "<verb>"
	Then I should get http post required error message 
	Examples: 
		|verb|
		|GET|
		|PUT|
		|DELETE|
		
Scenario: Valid Json request body and check timestamp Transformation 
	When I post an valid json body with timestamp 
	Then I should get a valid response body with corrent time 
	
Scenario: invalid/unsupported event type 
	When I post an valid json body with unsupported event type 
	Then I should get a blanket response with status 200