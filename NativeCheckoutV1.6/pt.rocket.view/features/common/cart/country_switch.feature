
Feature: Country switch

	Background: 
	#@install,@start,@country,@login_s,@add_to_cart
	Given I call the variables
	When I open the navigation menu
	
	@country_switch
	Scenario: Country switch
	And I press Choose Country
	And I press list item number 7
	Then I should see the clear the cart message
	And I press Yes
	* I wait for 5 seconds
	When I press the cart icon
	Then I should see the empty message
	