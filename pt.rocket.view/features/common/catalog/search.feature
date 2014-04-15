@search
Feature: Search feature

    
  	Background: 
    Given I call the variables
    
  	@search_i
  	Scenario: I search an invalid product
    And I select the country
	And I wait for 5 seconds
    When I open the navigation menu
    And I press Search 
    Then I should see the search message
	When I enter a invalid search
	Then I should see the no suggestion message
	When I open the navigation menu
	And I go to home
   
   	@search_s
	Scenario: I search a valid product
    When I open the navigation menu
    And I press Search 
    Then I should see the search message
   	When I enter a valid search
   	And I press "Search Field"
   	And I press list item number 1
   	* I wait for 2 seconds 
   	And I press list item number 1
   	Then I should see the add to cart button
   	When I open the navigation menu
	And I go to home