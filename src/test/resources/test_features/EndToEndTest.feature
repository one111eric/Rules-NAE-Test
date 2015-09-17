@EndToEnd-XRULES-347
Feature: End to End test for Alarm in Progress functionality.
         Mock server should get an request with notification message when I post an event to EEL.

Scenario Outline: send a single event to a location and verify it goes through
  When I check the number of request from "<location>" received by mock server
  Then I should get a number
  And I post an "<type>" Event of that location to EEL
  Then I should see the number of the request to that location increased by <y>
  And the request body is in correct json format
  And the timestamp is correct
  Examples:
  |location|type|y|
  |MiaoLocation00001|valid|1|
  |MiaoLocation00001|invalid|0|
  
Scenario Outline: Send n number of identical/unique events with x secs delay and check the result
  When I check the number of request from "<location>" received by mock server
  Then I should get a number
  When I post <n> number of "<type>" events with <x> secs of delay to EEL
  Then I should see the number of the request to that location increased by <y>
  And the request body is in correct json format
  Examples: 
  |location|n|type|x|y|
  |MiaoLocation00001|10|identical|1|3|
  |MiaoLocation00001|10|unique|1|10|
  
  
