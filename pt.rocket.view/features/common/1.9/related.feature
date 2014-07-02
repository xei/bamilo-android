@Calabash_Tests @related
Feature: 1.9 Features - Realted Items

  	Background: 
    Given I call the variables
	
	@related_situation
   	Scenario: Situation
   	When I select the country
	* I wait for 5 seconds
	#And I Login
   	And I open the navigation menu
	And I enter Categories
	* I wait for 1 seconds
	And I enter a valid Category
	And I press list item number 1
	* I wait for 3 seconds
	And I press list item number 2
	* I wait for 5 seconds
	And I press Got it
	Then I should see the related items
	
	@related_navigation
	Scenario: Navigation through the products
	#When I select the country
	#* I wait for 5 seconds
	
	When I open the navigation menu
	And I enter Categories
	* I wait for 1 seconds
	And I enter a valid Category
	And I press list item number 1
	* I wait for 3 seconds
	And I press list item number 2
	* I wait for 5 seconds
	
	#And I press Got it
	
	And I swipe up moving with 10 steps
	And I swipe up moving with 10 steps
	And I swipe up moving with 10 steps
	And I swipe up moving with 10 steps
	And I swipe up moving with 10 steps
	And I swipe up moving with 10 steps
	And I swipe left on botton moving with 10 steps
 		
	@related_image
	Scenario: Click on related item
	#When I select the country
	#* I wait for 5 seconds
	
	When I open the navigation menu
	And I enter Categories
	* I wait for 1 seconds
	And I enter a valid Category
	And I press list item number 1
	* I wait for 3 seconds
	And I press list item number 2
	* I wait for 5 seconds
	
	#And I press Got it
	
	And I swipe up moving with 10 steps
	And I swipe up moving with 10 steps
	And I swipe up moving with 10 steps
	And I swipe up moving with 10 steps
	And I swipe up moving with 10 steps
	And I swipe up moving with 10 steps
	And I press on a related item
	* I wait for 5 seconds
	
	@related_search
	Scenario: Open a product by search
	#When I select the country
	#* I wait for 5 seconds
    
    When I click on the search bar
	And I write a valid result on the search bar
	Then I press list item number 1
	* I wait for 2 seconds	
	And I press list item number 1
	* I wait for 3 seconds
   	Then I should see the related items
   	
   	@related_home
   	Scenario: Open a product on home
   	#When I select the country
	#* I wait for 5 seconds
	
	When I press on a related item
	Then I should not see the related items
	
