@KE @checkout_KE
Feature: checkout feature

  Background:
 	When I verify app for KE venture
    Then I choose the Kenya venture
    And I wait for 10 seconds
    Then I open the navigation menu
    And I choose the Sign In option
    
    
    @checkout_test_KE
   Scenario: add prodcut to cart
    And I wait for 5 seconds
    Then I enter a valid username
  	And I enter the password
  	Then I press "Login Button"
  	And I wait for 10 seconds
    Then I open the navigation menu
	And I should see "Sign Out"
	And I enter Categories
	Then I enter a valid Category
	And I press list item number 1
 	And I wait for 5 seconds
	And I press list item number 3
	And I wait for 5 seconds
	Then I add product to cart
 	And I wait for 5 seconds
 	Then I go to cart
 	And I wait for 5 seconds
 	Then I proceed to checkout
 	And I wait for 30 seconds
 	Then I scroll to next button
 	And I wait for 5 seconds
 	Then I proceed to the next step of the checkout staging
 	And I wait for 25 seconds
 	And I choose the test payment method
 	And I wait for 20 seconds
  	Then I choose payment method
 	And I wait for 20 seconds
 	And I confirm the order
 	And I wait for 20 seconds
# 	Then I choose payment method
# 	And I confirm the order
 	