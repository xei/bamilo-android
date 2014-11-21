@Calabash_Tests @f_2.1 @pdv_2.1
Feature: 2.1 Features - PDV

Background: 
Given I call the variables

Scenario: Choose Country
Given I select the country

Scenario: Cross in product zoom
Given I wait to see the home
When I open the navigation menu
* I wait for 1 seconds
And I enter a valid Category
* I wait for 1 seconds
And I enter a valid Category
* I wait for 3 seconds
And I press Got it
And I press gridview item number 1
* I wait for 5 seconds
Then I press Got it
And I press the image
And I press the close button on image

Scenario: Size section
Given I wait to see the home
When I click on search
And I enter a variation search
* I wait for 1 seconds
And I press list item number 1
* I wait for 3 seconds
And I press gridview item number 1
* I wait for 3 seconds
And I should see size

Scenario: Specifications
Given I wait to see the home
When I click on search
And I enter a variation search
* I wait for 1 seconds
And I press list item number 1
* I wait for 3 seconds
And I press gridview item number 1
* I wait for 3 seconds
And I swipe up moving with 10 steps
And I should see the specifications

