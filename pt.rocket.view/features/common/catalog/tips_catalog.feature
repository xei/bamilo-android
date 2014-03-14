@tips_catalog
Feature: Tips Catalog

	Background: 
    Given I call the variables
    When I open the navigation menu
    And I enter Categories
	And I enter a valid Category
	And I press list item number 1
	* I wait for 1 seconds
	
	@tip_catalog_first_time
	Scenario: See the tip with fresh install
	Then I should see the catalog tip
	When I swipe left moving with 10 steps
	* I wait for 1 seconds
	Then I should not see the catalog tip
	
	@tip_catalog_second_time
	Scenario: Check the tip after closed one time
	Then I should not see the catalog tip