Feature: Prepare the app

	@install
	Scenario: I Install the app
	Given I install the app
	
	@start
	Scenario: I start the app
	Given I start the app
	
	@shutdown
	Scenario: I shutdown the app
	Given I shutdown the app
	
	@country
	Scenario: I choose the country
	Given I call the variables
	And I select the country