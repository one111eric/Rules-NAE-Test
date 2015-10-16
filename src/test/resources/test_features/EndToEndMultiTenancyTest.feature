@EndToEndMT-XRULES-1168
Feature: Test Multi-Tenancy support for AIP end to end 
    Scenario Outline:
	Given I have a new tenant "<tenant>" with the same AIP topic handler
	And I create "<location>" and "<site>" with "<type1>" AIP rule for "<tenant>"
	And I create "<location>" and "<site>" with "<type2>" AIP rule for xh tenant
	And I post an "<type3>" AIP event from this "<site>" to EEL
	Then I should receive at most <notif_count> notification messages on mock server
	Examples:
	|tenant|location|site|notif_count|type1|type2|type3|
	|Tenant3|MiaoLocation7|850527|2|valid|valid|valid|
	
	Scenario Outline:
	Given I have a new tenant "<tenant>" with the same AIP topic handler
	And I create "<location>" and "<site>" with "<type1>" AIP rule for "<tenant>"
	And I create "<location>" and "<site>" with "<type2>" AIP rule for xh tenant
	And I post an "<type3>" AIP event from this "<site>" to EEL
	Then I should receive <notif_count> notification messages on mock server
	Examples:
	|tenant|location|site|notif_count|type1|type2|type3|
#	|Tenant3|MiaoLocation7|850527|2|valid|valid|valid|
	|Tenant3|MiaoLocation7|850527|0|valid|valid|invalid|
	|Tenant3|MiaoLocation7|850527|1|valid|invalid|valid|
	|Tenant3|MiaoLocation7|850527|0|valid|invalid|invalid|
	|Tenant3|MiaoLocation7|850527|1|invalid|valid|valid|
	|Tenant3|MiaoLocation7|850527|0|invalid|valid|invalid|
	|Tenant3|MiaoLocation7|850527|0|invalid|invalid|valid|
	|Tenant3|MiaoLocation7|850527|0|invalid|invalid|invalid|
	
	Scenario Outline:
	Given I have a new tenant "<tenant>" with a modified AIP topic handler
	And I create "<location>" and "<site>" with "<type1>" AIP rule for "<tenant>"
	And I create "<location>" and "<site>" with "<type2>" AIP rule for xh tenant
	And I post an modified AIP event from this "<site>" to EEL 
	Then I should receive <notif_count> notification messages on mock server
	Examples:
	|tenant|location|site|notif_count|type1|type2|
	|Tenant3|MiaoLocation7|850527|1|valid|valid|
	|Tenant3|MiaoLocation7|850527|0|invalid|valid|
	|Tenant3|MiaoLocation7|850527|1|valid|invalid|
	|Tenant3|MiaoLocation7|850527|0|invalid|invalid|
	
	
	
	



