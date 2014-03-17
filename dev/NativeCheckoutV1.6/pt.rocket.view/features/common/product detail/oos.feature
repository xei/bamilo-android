@product_oos
Feature: Out of orders scenarios

Background: 
    Given I call the variables
    When I open the navigation menu
    And I press Search 
    When I enter a OOS search
   	And I press list item number 1
   	And I press list item number 1
   	* I wait for 2 seconds 
   	And I press list item number 1
   	And I press Got it