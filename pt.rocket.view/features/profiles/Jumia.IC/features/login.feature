@IC @login_IC @login
Feature: Login feature


  Background:
    When I verify app for IC venture
    Then I choose the Ivory Coast venture
    And I wait for 10 seconds
    Then I open the navigation menu
    And I choose the Sign In option
    
    
    @login_test_IC
  Scenario: I login with user
#  	@wrong_empty_login
  	When I press "Login Button"
  	And I wait for 5 seconds
  	Then I should see the email error message
  	Then I should see the password error message
  
#  	@wrong_username_login
  	And I wait for 5 seconds
    Then I enter a wrong username
  	And I enter the password
  	Then I press "Login Button"
  	And I wait for 5 seconds
  	Then I should see the login error message
  	And I press "Ok"
  
 # 	@valid_login
  	And I wait for 5 seconds
    Then I enter a valid username
  	And I enter the password
  	Then I press "Login Button"
  	And I wait for 10 seconds
    Then I open the navigation menu
    And I wait for 5 seconds
	And I should see sign out button
	
	
#	@logout
	Given I press Logout Button
  	Then I open the navigation menu
  	And I should see the login button 