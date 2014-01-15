@KE @register_KE
Feature: Registration feature

  Background:
    Given I verify app for KE venture
    And I choose the Kenya venture
    Then I open the navigation menu
    And I choose the Sign In option
      
    
  @register_test_KE
  Scenario: I register with correct form
	#wrong_empty_register
  	Given I press the create account button
    Then I press the register button
    And I should see the mandatory fields error message
    
    #terms and conditions
    Then I press Terms and Conditions
    And I should see the Terms and Conditions
    Then I go back
    
    #wrong_repeated_register
    Then I enter a valid username
    And I enter the password
    And I enter the repeated password
    And I enter the first name
    And I enter the last name
    Then I press birthday
    And I press Ok
    Then I press male
    And I check Terms and Conditions
    And I press the register button
	Then I should see the email exists error message
	And I press Ok
    
	#different password
    Then I enter the password
    And I enter the wrong repeated password
    And I press the register button
    Then I should see the passwords dont match error message
    
    #valid_register
    Then I enter a random email
    And I enter the repeated password
    And I press the register button
	And I wait for 5 seconds
	Then I open the navigation menu
	And I should see sign out button
   