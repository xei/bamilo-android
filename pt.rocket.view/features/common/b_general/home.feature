Feature: Initial app features
	Install app, open, choose country
	
	Background: 
	Given I call the variables
	
	@country_check
	Scenario: Initial country checker
	Given I should see the countries
	
	@server_check
	Scenario: Check selected server
	And I select the country
	Then I should see the corresponding server
	
	@language_check
	Scenario: Check language
	And I select the country
	When I open the navigation menu
	And I should see the login button
	
	@splash_check
	Scenario: Check the splash screen
	Given I start the app 
	Then I sould see the splash screen
	
	@home_check
	Scenario: Home should appear when i open the app
	* I wait for 5 seconds
	Then I should see the home
	
	@sidebar_check
	Scenario: Check the sidebar layout
	Given I open the navigation menu
	Then I should see the sidebar