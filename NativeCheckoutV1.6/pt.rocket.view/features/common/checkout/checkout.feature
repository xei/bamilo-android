Feature: checkout feature

    Background: 
    Given I call the variables
    Then I go to cart
     
    @checkout_cc_i
    Scenario: invalid CC
    * I wait for 1 seconds
    When I press "Proceed to Checkout"
    * I wait for 10 seconds
    When I press "Next"
    * I wait for 5 seconds 
    When I press "Credit Card"
    
    
    Scenario: valid CC
    And I wait for 5 seconds
 	Then I proceed to checkout
 	And I wait for 25 seconds
	Then I scroll to next button
 	And I wait for 5 seconds
 	Then I proceed to the next step of the checkout staging
 	And I wait for 15 seconds
 	And I choose the COD payment method
 	And I wait for 3 seconds
  	Then I choose payment method
 	And I wait for 10 seconds
 	And I confirm the order
 	And I wait for 10 seconds
 	Then I press the button to continue shopping
 	And I wait for 5 seconds
 	Then I open the navigation menu
	And I should see sign out button
 	