@register
Feature: Registration feature

  	Background: 
	Given I call the variables
    Then I open the navigation menu
    And I choose the Sign In option
    * I wait for 1 seconds
    And I press the create account button
    * I wait for 1 seconds
      
  	@register_e
  	Scenario: I try to register with empty fields
    When I press the register button
    Then I should see the mandatory fields error message
    Then I open the navigation menu
    And I go to home
    
    @register_t
    Scenario: I check the Terms and Conditions page
    When I press Terms and Conditions
    Then I should see the Terms and Conditions
    And I go back
    Then I open the navigation menu
    And I go to home
    
    @register_r
    Scenario: I try to register with an email that already exists
    When I enter a valid username
    And I enter the password
    And I enter the repeated password
    And I enter the first name
    And I enter the last name
    And I press birthday
    And I press Ok
    And I press male
    And I check Terms and Conditions
    And I press the register button
	Then I should see the email exists error message
	And I press Ok
	Then I open the navigation menu
	And I go to home 
    
	@register_dp
	Scenario: I try to register with a password that is not equal
	When I enter a valid username
    And I enter the password
    And I enter the wrong repeated password
    And I enter the first name
    And I enter the last name
    And I press birthday
    And I press Ok
    And I press male
    And I check Terms and Conditions
    And I press the register button
    Then I should see the passwords dont match error message
    Then I open the navigation menu
    And I go to home
    
    @register_s
    Scenario: I register an account successfully
    When I enter a random email
    And I enter the password
    And I enter the repeated password
    And I enter the first name
    And I enter the last name
    And I press birthday
    And I press Ok
    And I press male
    And I check Terms and Conditions
    And I press the register button
	And I wait for 5 seconds
	Then I open the navigation menu
	And I should see sign out button
	Then I close the navigation menu
   