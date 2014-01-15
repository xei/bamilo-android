
Feature: Login feature

  Background:
    When I verify app for NG venture
    Then I choose the Nigeria venture
    Then I open the navigation menu
    And I choose the Sign In option
    
    @login_test_NG
  Scenario: I login with user
	#wrong_empty_login
  	When I press Login Button
  	Then I should see the email error message
  	Then I should see the password error message
  
    #wrong_username_login
    Then I enter a wrong username
  	And I enter the password
  	Then I press Login Button
  	Then I should see the login error message
  	And I press Ok
  	
  	#wrong_pasword
  	Then I enter a valid username
  	And I enter the wrong password
  	Then I press Login Button
  	Then I should see the login error message
  	And I press Ok
  
 	#valid_login
    Then I enter a valid username
  	And I enter the password
  	Then I press Login Button
    Then I open the navigation menu
	And I should see sign out button
	
	#logout
	Given I press Logout Button
  	Then I open the navigation menu
  	And I should see the login button 