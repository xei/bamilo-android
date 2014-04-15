@native_checkout
Feature: Native Checkout

    Background: 
    Given I call the variables
    And I select the country
    And I wait for 5 seconds
    
    #Login
    And I Login
    
   	#Add product to cart
   	And I add a product
    
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
    