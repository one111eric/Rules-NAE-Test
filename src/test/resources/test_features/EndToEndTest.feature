@EndToEnd-XRULES-347
Feature: End to End test for Alarm in Progress functionality.
         Mock server should get an request with notification message when I post an event to EEL.

Scenario Outline: send a single event to a location and verify it goes through
  When I check the number of request from "<location>" received by mock server
  Then I should get a number
  And I post an "<type>" Event of that location to EEL
  Then I should see the number of the request to that location increased by <y>
  And the request body is in correct json format
  #And the messages are in correct format with correct timestamp
  Examples:
  |location|type|y|
  |424242qa|valid|1|
  |424242qa|invalid|0|
  
Scenario Outline: Send n number of identical/unique events with x secs delay and check the result
  When I check the number of request from "<location>" received by mock server
  Then I should get a number
  When I post <n> number of "<type>" events with <x> secs of delay to EEL
  Then I should see the number of the request to that location increased by equal or less than <y>
  And the request body is in correct json format
  Examples: 
  |location|n|type|x|y|
  |424242qa|10|identical|1|6|
  |424242qa|10|unique|1|10|
  
  
