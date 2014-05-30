@Calabash_Tests 
Feature: Country switch

	Background: 
	Given I call the variables
	And I select the country
	And I wait for 10 seconds

#Login
	And I open the navigation menu
	* I wait for 5 seconds
	Then I choose the Sign In option
	* I wait for 5 seconds
    When I enter a valid username
  	And I enter the password
  	And I press Login Button
  	* I wait for 10 seconds
    Then I open the navigation menu
    * I wait for 10 seconds
	And I should see sign out button
	
	#When I open the navigation menu
	And I enter Categories
	* I wait for 3 seconds
	And I enter a valid Category
	* I wait for 3 seconds
	And I press list item number 1
	* I wait for 3 seconds
	And I press list item number 1
	* I wait for 3 seconds
	Then I press Got it
	And I add product to cart
	* I wait for 5 seconds
	Then I should see the item was added to shopping cart message
 	When I go to cart
	When I open the navigation menu
	
	@country_switch
	Scenario: Country switch
	* I wait for 10 seconds
	And I press Choose Country
	* I wait for 15 seconds
	And I press list item number 10
	* I wait for 1 seconds
	Then I should see the clear the cart message
	And I press Yes
	* I wait for 20 seconds
	When I press the cart icon
	* I wait for 20 seconds
	Then I should see the empty message
	