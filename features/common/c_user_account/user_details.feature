@user_details @Calabash_Tests @f_pre_1.9  @f_2.7
Feature: See personal data

Background: 
* I call the variables

@user_details_c
Scenario: See the user details
* I select the country
* I wait to see the home
* I wait for the overflow button
* I wait for 3 seconds
* I choose the Sign In option
* I wait for 2 seconds
* I should wait and see login screen
* I wait for 2 seconds
* I enter a valid username
* I wait for 2 seconds
* I enter the password
* I wait for 2 seconds
* I swipe down moving with 2 steps
* I press Login Button
* I wait to see the home
* I wait for the overflow button
* I wait for 4 seconds
* I should see sign out button
* I wait for 2 seconds
* I enter My Account
* I wait for 2 seconds
* I enter My User Data
* I wait for 2 seconds
* I should see my email
* I wait for 2 seconds
* I should see my first name
	
@user_details_e
Scenario: Try with empty fields
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I enter My Account
* I wait for 2 seconds
* I enter My User Data
* I wait for 2 seconds
* I press Save
* I wait for 2 seconds
* I should see the password is to short message
	
@user_details_dp
Scenario: Try different passwords
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I enter My Account
* I wait for 5 seconds
* I enter My User Data
* I wait for 5 seconds
* I enter the password
* I enter a wrong repeated password
* I wait for 2 seconds
* I press Save
* I wait for 2 seconds
* I should see the passwords dont match error message

@user_details_s
Scenario: Change password success
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I enter My Account
* I wait for 5 seconds
* I enter My User Data
* I wait for 10 seconds
* I enter the new password
* I wait for 1 seconds
* I enter the new repeated password
* I wait for 2 seconds
* I press Save
* I wait for 2 seconds
* I should see the password changed with success message