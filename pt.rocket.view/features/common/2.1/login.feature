@Calabash_Tests @f_2.1 @login_checkbox
Feature: 2.1 Features - Login

Background: 
Given I call the variables

Scenario: Choose Country
Given I select the country

Scenario: New Checkbox to remember user email
Given I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
And I should see the remember user email checkbox

Scenario: Login with checkbox unchecked
Given I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
When I click on remember user email
And I enter a valid username
And I enter the password
And I press Login Button
Then I wait to see the home
When I click on the overflow button
Then I should see sign out button
When I press Logout Button
And I press Yes
Then I wait to see the home
When I click on the overflow button
Then I should see the login button 
When I choose the Sign In option
Then I should see login screen
And I should not see email

Scenario: Login with checkbox checked
Given I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
And I should see the remember user email checkbox
And I enter a valid username
And I enter the password
And I press Login Button
Then I wait to see the home
When I click on the overflow button
Then I should see sign out button
When I press Logout Button
And I press Yes
Then I wait to see the home
When I click on the overflow button
Then I should see the login button 
When I choose the Sign In option
Then I should see login screen
And I should see email

