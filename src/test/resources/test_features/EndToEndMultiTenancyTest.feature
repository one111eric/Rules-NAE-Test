@EndToEndMT-XRULES-1168
Feature: Test Multi-Tenancy support for AIP end to end 
	Scenario Outline:
	Given I have a new tenant "<tenant>" with the same AIP topic handler
	And I create "<location>" and "<site>" with AIP rule for "<tenant>"
	And I create "<location>" and "<site>" with AIP rule for xh tenant
	And I post an AIP event from this "<site>" to EEL
	Then I should receive <number> notification messages on mock server
	Examples:
	|tenant|location|site|number|
	|Tenant3|MiaoLocation7|850527|2|
	
	Scenario Outline:
	Given I have a new tenant "<tenant>" with a modified AIP topic handler
	And I create "<location>" and "<site>" with AIP rule for "<tenant>"
	And I create "<location>" and "<site>" with AIP rule for xh tenant
	And I post an modified AIP event from this "<site>" to EEL 
	Then I should receive <number> notification messages on mock server
	Examples:
	|tenant|location|site|number|
	|Tenant3|MiaoLocation7|850527|1|
	
	Scenario Outline:
	Given I have a new tenant "<tenant>" with the same AIP topic handler
	And I create "<location>" and "<site>" with AIP rule for "<tenant>"
	And I post an invalid event JSON from this tenant's site to EEL 
	Then I should receive <number> notification message on mock server
	Examples:
	|tenant|location|site|number|
	|Tenant3|MiaoLocation7|850527|0|
	
	



