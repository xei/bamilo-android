Feature: Prepare the app

@install
Scenario: I Install the app
* I install the app
	
@start
Scenario: I start the app
* I start the app
	
@shutdown
Scenario: I shutdown the app
* I shutdown the app
	
@country @Calabash_Tests @f_pre_1.9
Scenario: I choose the country
* I call the variables
* I select the country