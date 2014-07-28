@Calabash_Tests @nav_drawer @f_2.0
Feature: 2.0 Features - Navigation Drawer

Background: 
Given I call the variables

Scenario: Choose Country
When I select the country

Scenario: Navigation Drawer Tabs
Given I wait to see the home
When I open the navigation menu
Then I should see only one column

Scenario: Click in the Cart
Given I wait to see the home
When I open the navigation menu
And I click on cart
Then I wait to see the cart

Scenario: Click in home section
Given I wait to see the home
When I open the navigation menu
And I click on home
Then I wait to see the home

Scenario: Click in choose country section
Given I wait to see the home
When I open the navigation menu
And I press Choose Country
Then I should see the choose country page

Scenario: Click in one category
Given I wait to see the home
When I open the navigation menu
And I enter a valid Category
Then I should see the back button
And I press the back button