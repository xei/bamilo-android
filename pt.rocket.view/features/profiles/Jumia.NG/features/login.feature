@NG @login_NG
Feature: Login feature


  Background:
    When I verify app for NG venture
    Then I choose the Nigeria venture
    And I wait for 10 seconds
    Then I open the navigation menu
    And I choose the Sign In option
    
    
    @login_test_NG
  Scenario: I login with user
  
  	When I press "Login Button"
  	Then I should see the email error message
  	Then I should see the password error message
  
  	And I wait for 5 seconds
    Then I enter a wrong username
  	And I enter the password
  	Then I press "Login Button"
  	And I wait for 5 seconds
  	Then I should see the login error message
  	And I press "Ok"
  
  	And I wait for 5 seconds
    Then I enter a valid username
  	And I enter the password
  	Then I press "Login Button"
  	And I wait for 10 seconds
    Then I open the navigation menu
	And I should see sign out button
  	