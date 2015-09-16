@EndToEnd-XRULES-347
Feature: End to End test for Alarm in Progress functionality.
         Mock server should get an request with notification message when I post an event to EEL.

Scenario Outline:
  When I check the number of request from "<location>" received by mock server
  Then I should get a number
  And I post an Event of that location to EEL
  Then I should see the number of the request to that location increased by 1
  And the request body is in correct json format
  And the timestamp is correct
  Examples:
  |location|
  |MiaoLocation00001|