@login @Calabash_Tests @f_pre_1.9  @f_2.7
Feature: Login feature
	
Background:
* I call the variables
	
@login_e
Scenario: Try to login with empty fields
* I select the country
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I choose the Sign In option
* I wait for 2 seconds
* I should wait and see login screen
* I press Login Button
* I wait for 2 seconds
* I should see the email error message
* I should see the password error message
  
@login_wu
Scenario: Try to login with wrong username
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should wait and see login screen
* I enter a wrong username
* I enter the password
* I swipe down moving with 2 steps
* I press Login Button
* I wait for 4 seconds
* I should see the login error message
* I wait for 1 seconds
* I press Ok
  	
@login_wp
Scenario: Try to login with wrong pasword
* I wait to see the home
* I wait for the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should wait and see login screen
* I enter a valid username
* I enter the wrong password
* I swipe down moving with 2 steps
* I press Login Button
* I wait for 4 seconds
* I should see the login error message
* I press Ok
  
@login_s
Scenario: Login successful
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I choose the Sign In option
* I wait for 2 seconds
* I should wait and see login screen
* I enter a valid username
* I wait for 1 seconds
* I enter the password
* I wait for 2 seconds
* I swipe down moving with 2 steps
* I press Login Button
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I should see sign out button
