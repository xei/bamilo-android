@product_color
Feature: Navigate to a product with different colors

	Background: 
	Given I call the variables
    When I open the navigation menu
    And I press Search 
    When I enter a color search
   	And I press list item number 1
   	And I press list item number 1
   	* I wait for 2 seconds 
   	And I press list item number 1
   	And I press Got it