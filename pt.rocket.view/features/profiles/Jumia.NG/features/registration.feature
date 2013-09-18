@NG @register_NG
Feature: Registration feature

  Background:
    When I verify app for NG venture
    Then I choose the Nigeria venture
    And I wait for 10 seconds
    Then I open the navigation menu
    And I choose the Sign In option
      
    
  @register_test_MA
  Scenario: I register with correct form
  	Then I press "Register Button"
    And I wait for 5 seconds
    Then I press "Submit Register Form"
    And I wait for 5 seconds
    And I should see the mandatory fields error message
    
    Then I enter a valid username
    And I enter the password
    And I enter the repeated password
    Then I enter the first name
    Then I enter the last name
    And I press "male"
    #Then I open the birthday dialog
    Then I press "Birthday"
    Then I press "Ok"
    Then I press "Terms and Conditions"
    Then I press "Submit Register Form"
	And I wait for 10 seconds
	Then I should see the email exists error message
	Then I press "Ok"
    
  
    Then I enter a random email
    Then I press "Submit Register Form"
	And I wait for 10 seconds
	Then I open the navigation menu
	And I wait for 5 seconds
	And I should see sign out button
   