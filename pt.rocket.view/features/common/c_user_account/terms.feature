@Calabash_Tests @terms
Feature: Terms and Conditions

  	Background: 
	Given I call the variables
    
    @register_t
    Scenario: I check the Terms and Conditions page.
    And I select the country
	And I wait for 5 seconds
    Then I open the navigation menu
    And I choose the Sign In option
    * I wait for 1 seconds
    And I press the create account button
    * I wait for 5 seconds
    When I check Terms and Conditions
    Then I should see the Terms and Conditions