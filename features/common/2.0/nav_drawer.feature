@Calabash_Tests @nav_drawer @f_2.0
Feature: 2.0 Features - Navigation Drawer

Background: 
* I call the variables

Scenario: Choose Country
* I select the country

Scenario: Navigation Drawer Tabs
* I wait to see the home
* I open the navigation menu
* I wait for 2 seconds
* I should see only one column

Scenario: Click in the Cart
* I wait to see the home
* I open the navigation menu
* I wait for 5 seconds
* I click on cart
* I wait for 2 seconds
* I should see the empty cart message

Scenario: Click in home section
* I wait to see the home
* I open the navigation menu
* I wait for 2 seconds
* I click on home
* I wait to see the home

Scenario: Click in choose country section
* I wait to see the home
* I open the navigation menu
* I wait for 2 seconds
* I press Choose Country
* I wait for 2 seconds
* I should see the choose country page

Scenario: Click in one category
* I wait to see the home
* I open the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I should see the back button
* I wait for 2 seconds
* I press the back button