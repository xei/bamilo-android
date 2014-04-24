@login @Calabash_Tests
Feature: Login feature
	
	Background:
	Given I call the variables
	
	
	@login_e
	Scenario: Try to login with empty fields
	And I select the country
	And I wait for 5 seconds
	And I open the navigation menu
	Then I choose the Sign In option
	* I wait for 5 seconds
  	When I press Login Button
  	Then I should see the email error message
  	And I should see the password error message
  
    @login_wu
    Scenario: Try to login with wrong username
	And I wait for 5 seconds
	And I open the navigation menu
	Then I choose the Sign In option
	* I wait for 5 seconds
    When I enter a wrong username
  	And I enter the password
  	And I press Login Button
  	Then I should see the login error message
  	And I press Ok
  	
  	@login_wp
  	Scenario: Try to login with wrong pasword
	And I wait for 5 seconds
	And I open the navigation menu
	Then I choose the Sign In option
	* I wait for 5 seconds
  	When I enter a valid username
  	And I enter the wrong password
  	And I press Login Button
  	Then I should see the login error message
  	And I press Ok
  
 	@login_s
 	Scenario: Login successful
	And I wait for 5 seconds
	And I open the navigation menu
	Then I choose the Sign In option
	* I wait for 5 seconds
    When I enter a valid username
  	And I enter the password
  	And I press Login Button
  	* I wait for 5 seconds
    Then I open the navigation menu
    * I wait for 5 seconds
	And I should see sign out button
