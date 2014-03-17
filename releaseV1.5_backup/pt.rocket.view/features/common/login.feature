@login
Feature: Login feature
	
	Background:
	Given I call the variables
	And I open the navigation menu
	Then I choose the Sign In option
	* I wait for 2 seconds
	
	@login_e
	Scenario: Try to login with empty fields
  	When I press Login Button
  	Then I should see the email error message
  	And I should see the password error message
  	Then I open the navigation menu
  	And I go to home
  
    @login_wu
    Scenario: Try to login with wrong username
    When I enter a wrong username
  	And I enter the password
  	And I press Login Button
  	Then I should see the login error message
  	And I press Ok
  	Then I open the navigation menu
  	And I go to home
  	
  	@login_wp
  	Scenario: Try to login with wrong pasword
  	When I enter a valid username
  	And I enter the wrong password
  	And I press Login Button
  	Then I should see the login error message
  	And I press Ok
  	Then I open the navigation menu
  	And I go to home
  
 	@login_s
 	Scenario: Login successful
    When I enter a valid username
  	And I enter the password
  	And I press Login Button
    Then I open the navigation menu
	And I should see sign out button
	Then I close the navigation menu
	