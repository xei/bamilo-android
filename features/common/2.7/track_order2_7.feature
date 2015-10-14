@Calabash_Tests @f_2.7 @track_order
Feature: 2.7 Features - Track Order

Background:
* I call the variables


Scenario: Track Valid
* I select the country
* I wait for 3 seconds
* I wait to see the home
* I wait for 3 seconds
* I click on the overflow button
* I wait for 2 seconds
* I enter track my order
* I wait for 3 seconds
* I should see track my order page
* I wait for 2 seconds
* I enter a valid track order number
* I press Track Order
* I wait for 3 seconds
* I should see the order

Scenario: Track Invalid
* I wait for 3 seconds
* I wait to see the home
* I wait for 2 seconds
* I click on the overflow button
* I enter track my order
* I wait for 3 seconds
* I should see track my order page
* I wait for 2 seconds
* I enter a invalid track order number
* I press Track Order
* I wait for 3 seconds
* I should not see the order

Scenario: Track Valid after Valid
* I wait to see the home
* I wait for 2 seconds
* I click on the overflow button
* I enter track my order
* I wait for 3 seconds
* I should see track my order page
* I enter a valid track order number
* I press Track Order
* I wait for 3 seconds
* I should see the order
* I wait for 1 seconds
* I enter a valid track order number
* I press Track Order
* I wait for 3 seconds
* I should see the order

Scenario: Track Valid after Invalid
* I wait to see the home
* I wait for 2 seconds
* I click on the overflow button
* I enter track my order
* I wait for 3 seconds
* I should see track my order page
* I enter a invalid track order number
* I wait for 2 seconds
* I press Track Order
* I wait for 3 seconds
* I should not see the order
* I wait for 1 seconds
* I enter a valid track order number
* I wait for 2 seconds
* I press Track Order
* I wait for 3 seconds
* I should see the order

Scenario: Track Invalid after Valid
* I wait to see the home
* I wait for 2 seconds
* I click on the overflow button
* I enter track my order
* I wait for 3 seconds
* I should see track my order page
* I enter a invalid track order number
* I wait for 2 seconds
* I press Track Order
* I wait for 3 seconds
* I should not see the order
* I wait for 1 seconds
* I enter a valid track order number
* I press Track Order
* I wait for 3 seconds
* I should see the order