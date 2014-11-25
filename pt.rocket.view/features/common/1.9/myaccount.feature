@Calabash_Tests @account @f_1.9
Feature: 1.9 Features - My Account

Background: 
Given I call the variables

@account_email_section
Scenario: See the email notifications section
Given I select the country
Then I wait to see the home
When I click on the overflow button
And I enter My Account
* I wait for 3 seconds
Then I should see my account
And I should see the email notifications section

@account_email_not_logged
Scenario: See the email notifications without login
Given I wait to see the home
When I click on the overflow button
And I enter My Account
* I wait for 3 seconds
Then I should see my account
When I enter email notifications
Then I should see login screen

@account_newsletter_page
Scenario: Email notification page
#Login
Given I wait to see the home
When I click on the overflow button
And I choose the Sign In option
* I wait for 5 seconds
Then I should see login screen
When I enter a valid username
And I enter the password
And I press Login Button
And  I wait to see the home
And I click on the overflow button
Then I should see sign out button
#
When I enter My Account
* I wait for 3 seconds
Then I should see my account
When I enter email notifications
* I wait for 2 seconds
Then I should see the email notifications section
And I should see newsletter title
And I should see newsletter header
And I should see newsletter options

@account_newsletter_save
Scenario: Save newsletter changes
Given I wait to see the home
When I click on the overflow button
And I enter My Account
* I wait for 3 seconds
Then I should see my account
And I enter email notifications
And I toggle checkbox number 1
And I enter save
* I wait for 4 seconds
Then I should see the notification newsletter changes


	
