@Calabash_Tests @account
Feature: My Accounts features

Background: 
Given I call the variables

@account_email_section
Scenario: See the email notifications section
When I select the country
And I wait for 5 seconds
#And I Login
And I open the navigation menu
And I wait for 1 seconds
And I enter My Account
Then I should see the email notifications section

@account_email_not_logged
Scenario: See the email notifications without login
When I open the navigation menu
And I wait for 1 seconds
And I enter My Account
And I enter email notifications
Then I should see login screen

@account_newsletter_page
Scenario: Email notification page
When I Login
And I open the navigation menu
And I wait for 1 seconds
And I enter My Account
And I enter email notifications
Then I should see the email notifications section
And I should see newsletter title
And I should see newsletter header
And I should see newsletter options

@account_newsletter_save
Scenario: Save newsletter changes
When I open the navigation menu
And I wait for 1 seconds
And I enter My Account
And I enter email notifications
And I check newsletter male
And I enter save
Then I should see the notification newsletter changes


	
