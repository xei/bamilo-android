@KE @register_KE
Feature: Registration feature

  Background:
    When I verify app for KE venture
    Then I choose the Kenya venture
    And I wait for 10 seconds
    Then I open the navigation menu
    And I choose the Sign In option
      
    
  @register_test_KE
  Scenario: I register with correct form
  	Then I press "Register Button"
    And I wait for 5 seconds
    Then I press "Submit Register Form"
    And I should see "Please fill in the required(*) fields" 
    
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
	Then I should see "This email already exists."
	Then I press "Ok"
    
  
    Then I enter a random email
    Then I press "Submit Register Form"
	And I wait for 10 seconds
	Then I open the navigation menu
	And I should see "Sign Out"
   