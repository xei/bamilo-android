Feature: Initial app features
	Install app, open, choose country
	
	Background: 
	Given I call the variables
	
	@country_check @Calabash_Tests @teste123
	Scenario: Initial country checker
	* I wait for 5 seconds
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
	
	@splash_check @Calabash_Tests @teste123
	Scenario: Check the splash screen
	#Given I start the app 
	Then I sould see the splash screen
	
	@home_check @Calabash_Tests @teste123
	Scenario: Home should appear when i open the app
	When I select the country
	* I wait for 5 seconds
	Then I should see the home
	
	@sidebar_check @Calabash_Tests @teste123
	Scenario: Check the sidebar layout
	#When I select the country
	* I wait for 5 seconds
	Given I open the navigation menu
	Then I should see the sidebar