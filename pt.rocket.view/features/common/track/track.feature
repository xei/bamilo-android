@Calabash_Tests @teste123
Feature: Tracking

Background: 
Given I call the variables

Scenario: Track Valid
When I select the country
And I wait for 5 seconds
And I open the navigation menu
And I press Order Status
And I wait for 1 seconds
And I enter a valid track order number
And I press Track Order
And I wait for 3 seconds
Then I should see the order



Scenario: Track Invalid
When I open the navigation menu
And I press Order Status
And I wait for 1 seconds
And I enter a invalid track order number
And I press Track Order
And I wait for 3 seconds
Then I should not see the order

Scenario: Track Valid after Valid
When I open the navigation menu
And I press Order Status
And I wait for 1 seconds
And I enter a valid track order number
And I press Track Order
And I wait for 3 seconds
Then I should see the order
And I wait for 1 seconds
When I enter a valid track order number
And I press Track Order
And I wait for 3 seconds
Then I should see the order

Scenario: Track Valid after Invalid
When I open the navigation menu
And I press Order Status
And I wait for 1 seconds
And I enter a invalid track order number
And I press Track Order
And I wait for 3 seconds
Then I should not see the order
And I wait for 1 seconds
When I enter a valid track order number
And I press Track Order
And I wait for 3 seconds
Then I should see the order

Scenario: Track Invalid after Valid
When I open the navigation menu
And I press Order Status
And I wait for 1 seconds
And I enter a invalid track order number
And I press Track Order
And I wait for 3 seconds
Then I should not see the order
And I wait for 1 seconds
When I enter a valid track order number
And I press Track Order
And I wait for 3 seconds
Then I should see the order