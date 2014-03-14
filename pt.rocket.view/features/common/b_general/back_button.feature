@back
Feature: Press back and show close screen

	Background: 
    Given I call the variables
        	
	Scenario: See the tip with fresh install
	When I go back
	Then I should see the close message