@user_details @Calabash_Tests
Feature: See personal data

Background: 
Given I call the variables

@user_details_c
Scenario: See the user details
Given I select the country
And I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
When I enter a valid username
And I enter the password
And I press Login Button
And I wait to see the home
When I click on the overflow button
Then I should see sign out button
When I enter My Account
* I wait for 5 seconds
And I enter My User Data
* I wait for 4 seconds
Then I should see my email
And I should see my first name
	
@user_details_e
Scenario: Try with empty fields
Given I wait to see the home
When I click on the overflow button
And I enter My Account
* I wait for 5 seconds
And I enter My User Data
* I wait for 5 seconds
When I press Save
Then I should see the password is to short message
	
@user_details_dp
Scenario: Try different passwords
Given I wait to see the home
When I click on the overflow button
And I enter My Account
* I wait for 5 seconds
And I enter My User Data
* I wait for 5 seconds
When I enter the new password
And I enter a wrong repeated password
And I press Save
Then I should see the passwords dont match error message

@user_details_s
Scenario: Change password success
Given I wait to see the home
When I click on the overflow button
And I enter My Account
* I wait for 5 seconds
And I enter My User Data
* I wait for 10 seconds
When I enter the new password
And I enter the new repeated password
And I press Save
* I wait for 1 seconds
Then I should see the password changed with success message