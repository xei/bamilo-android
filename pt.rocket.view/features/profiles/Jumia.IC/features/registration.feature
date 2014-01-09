@IC @register_IC
Feature: Registration feature

  Background:
    Given I verify app for IC venture
    And I choose the Ivory Coast venture
    Then I open the navigation menu
    And I choose the Sign In option
      
    
  @register_test_IC
  Scenario: I register with correct form
	#wrong_empty_register
  	Given I press the create account button
    Then I press the register button
    And I should see the mandatory fields error message
    
    #wrong_repeated_register
    Then I enter a valid username
    And I enter the password
    And I enter the repeated password
    And I enter the first name
    And I enter the last name
    Then I press "Birthday"
    And I press Ok
    Then I press "male"
    Then I press "Terms and Conditions"
    Then I press "Submit Register Form"
	Then I should see the email exists error message
	Then I press Ok
    
	#different password
    And I enter the password
    And I enter the wrong repeated password
    And I press "Submit Register Form"
    Then I should see the passwords dont match error message
    
    #terms and conditions
    
    
    #valid_register
    Then I enter a random email
    And I enter the repeated password
    And I press "Submit Register Form"
	And I wait for 5 seconds
	Then I open the navigation menu
	And I should see sign out button
   