@Calabash_Tests
Feature: Newsletter

  	Background: 
    Given I call the variables
	
	@newsletter_checkbox
   	Scenario: See the newsletter checkbox on register
   	When I select the country
	* I wait for 5 seconds
	
	And I open the navigation menu
    And I choose the Sign In option
    * I wait for 1 seconds
    And I press the create account button
    * I wait for 1 seconds
    Then I should see the newsletter checkbox
    
	@newsletter_register
    Scenario: I register with newsletter on
    When I select the country
	* I wait for 5 seconds
    
    Then I open the navigation menu
    And I choose the Sign In option
    * I wait for 1 seconds
    And I press the create account button
    * I wait for 1 seconds
    When I enter a random email
    And I enter the password
    And I enter the repeated password
    And I enter the first name
    And I enter the last name
    And I press birthday
    And I press Ok
    And I press male
    And I check Terms and Conditions
    
    And I check newsletter
        
    And I press the register button
	And I wait for 10 seconds
	Then I open the navigation menu
	And I wait for 5 seconds
	And I should see sign out button
	


	
