@Calabash_Tests @tracking
Feature: Tracking

Background: 
Given I call the variables

Scenario: Track Valid
Given I select the country
And I wait to see the home
When I click on the overflow button
And I enter track my order
Then I should see track my order page
And I enter a valid track order number
And I press Track Order
And I wait for 3 seconds
Then I should see the order

Scenario: Track Invalid
And I wait to see the home
When I click on the overflow button
And I enter track my order
Then I should see track my order page
And I enter a invalid track order number
And I press Track Order
And I wait for 3 seconds
Then I should not see the order

Scenario: Track Valid after Valid
And I wait to see the home
When I click on the overflow button
And I enter track my order
Then I should see track my order page
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
And I wait to see the home
When I click on the overflow button
And I enter track my order
Then I should see track my order page
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
And I wait to see the home
When I click on the overflow button
And I enter track my order
Then I should see track my order page
And I enter a invalid track order number
And I press Track Order
And I wait for 3 seconds
Then I should not see the order
And I wait for 1 seconds
When I enter a valid track order number
And I press Track Order
And I wait for 3 seconds
Then I should see the order