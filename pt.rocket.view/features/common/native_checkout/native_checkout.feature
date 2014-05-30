@native_checkout @Calabash_Tests
Feature: Native Checkout

    Background: 
    Given I call the variables
    And I select the country
    And I wait for 5 seconds
    
#Login
	And I open the navigation menu
	* I wait for 2 seconds
	Then I choose the Sign In option
	* I wait for 5 seconds
    When I enter a valid username
  	And I enter the password
  	And I press Login Button
  	* I wait for 10 seconds
    Then I open the navigation menu
    * I wait for 10 seconds
	And I should see sign out button
    
   	#Add product to cart
   	#When I open the navigation menu
	And I enter Categories
	* I wait for 2 seconds
	And I enter a valid Category
	And I press list item number 1
	* I wait for 3 seconds
	And I press list item number 1
	* I wait for 3 seconds
	Then I press Got it
	And I add product to cart
	* I wait for 5 seconds
	Then I should see the item was added to shopping cart message
 	When I go to cart
 	Then I should not see the no items message
    
    Scenario: Valid Checkout
    When I go to cart
    And I press Proceed to Checkout
    And I wait for 15 seconds
    And I press Next
    And I wait for 7 seconds
    And I press Next
    And I wait for 5 seconds
    And I press Pay on Delivery
    And I press Next
    And I wait for 5 seconds
    And I press Confirm Order
    And I wait for 20 seconds
    Then I should see the thank you screen
    