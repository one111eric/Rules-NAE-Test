@NAE-XRule-739
Feature: Test Notification Action Executor. 
         With X-Debug header check the corresponding error message and notification Json payload
         Without X-Debug header to check thread pool issue and check if mock server gets the notification.

Scenario: Invalid Json Request body 
	When I post an invalid json body 
	Then I should get an invalid json error message 
	
Scenario: Empty Json Request body
    When I post an empty json body
    Then I should get an empty json body error message
    
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
		
#Scenario: Valid Json request body and check timestamp Transformation 
#	When I post an valid json body with timestamp
#	Then I should get a valid response body with correct time 
#	
#Scenario: invalid/unsupported event type 
#	When I post an valid json body with unsupported event type 
#	Then I should get a blanket response with status 200
#	
#Scenario: Check mock server has received the request from NAE
#    When I post a valid body
#    Then I should see the number of the request log increased by 1
#    And the request body is in correct json payload format	
	
#Scenario: Send an invalid Json request body 100 times then a valid Json request 100 times 
#          to verify job queue won't be full
#         When I post an invalid json body 100 times without X-Debug header
#         Then I should still get process successful message
#         When I post a valid json body 100 times without X-Debug header
#         Then I should still get process successful message

