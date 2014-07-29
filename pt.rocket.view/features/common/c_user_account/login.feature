@login @Calabash_Tests @f_pre_1.9
Feature: Login feature
	
Background:
Given I call the variables
	
@login_e
Scenario: Try to login with empty fields
Given I select the country
And I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
When I press Login Button
Then I should see the email error message
And I should see the password error message
  
@login_wu
Scenario: Try to login with wrong username
Given I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
When I enter a wrong username
And I enter the password
And I press Login Button
Then I should see the login error message
And I press Ok
  	
@login_wp
Scenario: Try to login with wrong pasword
Given I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
When I enter a valid username
And I enter the wrong password
And I press Login Button
* I wait for 2 seconds
Then I should see the login error message
And I press Ok
  
@login_s
Scenario: Login successful
Given I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
When I enter a valid username
And I enter the password
And I press Login Button
And I wait to see the home
Then I click on the overflow button
And I should see sign out button
