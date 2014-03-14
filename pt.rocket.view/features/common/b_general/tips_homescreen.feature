@tips_home
Feature: Tips on homescreen

	Background: 
    Given I call the variables
        	
	@tip_home_first_time
	Scenario: See the tip with fresh install
	Then I should see the home tip
	When I swipe left moving with 10 steps
	Then I should not see the home tips
	
	@tip_home_second_time
	Scenario: Check the tip after closed one time
	Then I should not see the home tip