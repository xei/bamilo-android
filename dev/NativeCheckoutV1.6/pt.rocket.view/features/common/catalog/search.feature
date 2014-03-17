@search
Feature: Search feature

    
  	Background: 
    Given I call the variables
    When I open the navigation menu
    And I press Search 
    Then I should see the search message
    
  	@search_i
  	Scenario: I search an invalid product
	When I enter a invalid search
	Then I should see the no suggestion message
	When I open the navigation menu
	And I go to home
   
   	@search_s
	Scenario: I search a valid product
   	When I enter a valid search
   	And I press "Search Field"
   	And I press list item number 1
   	* I wait for 2 seconds 
   	And I press list item number 1
   	Then I should see the add to cart button
   	When I open the navigation menu
	And I go to home
	
	@sort
	Scenario: Test all sorting filters
	When I enter a valid search
	* I wait for 2 seconds 
   	And I press list item number 1
   	Then I should see the filter popularity
   	When I swipe left moving with 10 steps
   	Then I should see the filter price up
   	When I swipe left moving with 10 steps
   	Then I should see the filter price down
   	When I swipe left moving with 10 steps
   	Then I should see the filter name
   	When I swipe left moving with 10 steps
   	Then I should see the filter brand
   	When I open the navigation menu
	And I go to home