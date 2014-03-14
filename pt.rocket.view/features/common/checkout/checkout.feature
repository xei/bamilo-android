Feature: checkout feature

    Background: 
    Given I call the variables
    Then I go to cart
     
    @checkout_cc_i
    Scenario: invalid CC
	When I go to checkout