@track
Feature: track the orders


	Background: 
	Given I call the variables
    When I open the navigation menu
    
    @track_valid
    Scenario: Track the order successfully 
    When I press Order Status
    And I enter a valid track order number
    And I press Track Order
    * I wait for 1 seconds
    Then I should see the order
    When I open the navigation menu
 	And I go to home
    
    @track_invalid
    Scenario: Track an invalid order 
    When I press Order Status
    And I enter an invalid track order number
    And I press Track Order
    * I wait for 1 seconds
    Then I should not see the order
    When I open the navigation menu
 	And I go to home
    
    @track_valid_valid
    Scenario: Track a valid order after another valid
    When I press Order Status
    And I enter a valid track order number
    And I press Track Order
    * I wait for 1 seconds
    Then I should see the order
    When I press Track Order
    * I wait for 1 seconds
    Then I should see the order
    When I open the navigation menu
 	And I go to home
    
    @track_invalid_valid
    Scenario: Track a valid order after one that is invalid
    When I press Order Status
    And I enter an invalid track order number
    And I press Track Order
    * I wait for 1 seconds
    Then I should not see the order
    And I enter a valid track order number
    And I press Track Order
    * I wait for 1 seconds
    Then I should see the order
    When I open the navigation menu
 	And I go to home
    
    @track_valid_invalid
    Scenario: Track a invalid order after one valid
    When I press Order Status
    And I enter a valid track order number
    And I press Track Order
    * I wait for 1 seconds
    Then I should see the order
    When I enter an invalid track order number
    And I press Track Order
    * I wait for 1 seconds
    Then I should not see the order
    When I open the navigation menu
 	And I go to home