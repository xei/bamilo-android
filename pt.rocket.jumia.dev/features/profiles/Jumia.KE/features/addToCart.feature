@KE @add_to_cart_KE
Feature: Add To Cart feature

  Background:
 	When I verify app for KE venture
    Then I choose the Kenya venture
    And I wait for 10 seconds
    Then I open the navigation menu
    And I choose the Sign In option
    
    
    @add_to_cart_test_KE
   Scenario: add prodcut to cart
    And I wait for 5 seconds
    Then I enter a valid username
  	And I enter the password
  	Then I press "Login Button"
  	And I wait for 10 seconds
    Then I open the navigation menu
	And I should see sign out button
	And I enter Categories
	Then I enter a valid Category
	And I press list item number 1
 	And I wait for 5 seconds
	And I press list item number 1
	And I wait for 5 seconds
	Then I add product to cart
 	And I wait for 5 seconds
 	Then I go to cart
 	And I wait for 5 seconds
 	
 	