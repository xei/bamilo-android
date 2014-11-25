@Calabash_Tests @f_2.1 @pdv_2.1
Feature: 2.1 Features - PDV

Background: 
* I call the variables

Scenario: Choose Country
* I select the country

Scenario: Cross in product zoom
* I wait to see the home
* I open the navigation menu
* I wait for 1 seconds
* I enter a valid Category
* I wait for 1 seconds
* I enter a valid Category
* I wait for 3 seconds
* I press Got it
* I press gridview item number 1
* I wait for 5 seconds
* I press Got it
* I press the image
* I press the close button on image

Scenario: Size section
* I wait to see the home
* I click on search
* I enter a variation search
* I wait for 1 seconds
* I press list item number 1
* I wait for 3 seconds
* I press gridview item number 1
* I wait for 3 seconds
* I should see size

Scenario: Specifications
* I wait to see the home
* I click on search
* I enter a variation search
* I wait for 1 seconds
* I press list item number 1
* I wait for 3 seconds
* I press gridview item number 1
* I wait for 3 seconds
* I swipe up moving with 10 steps
* I should see the specifications

