Feature: Test Multi-Tenancy support for AIP end to end 
	Scenario:
	When I create a new tenant with the same AIP topic handler
	And I post an same AIP event to EEL from this tenant's site
	Then I should receive two notification messages on mock server.
	
	Scenario:
	When I create a new tenant with a modified AIP topic handler
	And I post an modified event to EEL to this tenant's site
	Then I should receive one notification message on mock serve
	
	Scenario:
	When I created a new tenant with the same AIP topic handler
	And I post an invalid event JSON to EEL from this tenant's site
	Then I should receive zero notification message on mock server


