@login @Calabash_Tests @f_pre_1.9
Feature: Login feature
	
Background:
* I call the variables
	
@login_e
Scenario: Try to login with empty fields
* I select the country
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should see login screen
* I press Login Button
* I should see the email error message
* I should see the password error message
  
@login_wu
Scenario: Try to login with wrong username
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should see login screen
* I enter a wrong username
* I enter the password
* I press Login Button
* I wait for 4 seconds
* I should see the login error message
* I press Ok
  	
@login_wp
Scenario: Try to login with wrong pasword
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should see login screen
* I enter a valid username
* I enter the wrong password
* I press Login Button
* I wait for 4 seconds
* I should see the login error message
* I press Ok
  
@login_s
Scenario: Login successful
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should see login screen
* I enter a valid username
* I enter the password
* I press Login Button
* I wait to see the home
* I click on the overflow button
* I should see sign out button
