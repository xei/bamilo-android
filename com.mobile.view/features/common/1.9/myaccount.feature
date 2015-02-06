@Calabash_Tests @account @f_1.9
Feature: 1.9 Features - My Account

Background: 
* I call the variables

@account_email_section
Scenario: See the email notifications section
* I select the country
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I enter My Account
* I wait for 3 seconds
* I should see my account
* I wait for 2 seconds
* I should see the email notifications section

@account_email_not_logged
Scenario: See the email notifications without login
* I wait to see the home
* I wait for the overflow button
* I enter My Account
* I wait for 3 seconds
* I should see my account
* I enter email notifications
* I wait for 2 seconds
* I should wait and see login screen

@account_newsletter_page
Scenario: Email notification page
#Login
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I choose the Sign In option
* I wait for 5 seconds
* I should wait and see login screen
* I wait for 2 seconds
* I enter a valid username
* I wait for 2 seconds
* I enter the password
* I wait for 2 seconds
* I press Login Button
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I should see sign out button
* I wait for 5 seconds
* I enter My Account
* I wait for 3 seconds
* I should see my account
* I wait for 2 seconds
* I enter email notifications
* I wait for 2 seconds
* I should see the email notifications section
* I wait for 2 seconds
* I should see newsletter title
* I wait for 1 seconds
* I should see newsletter header
* I wait for 1 seconds
* I should see newsletter options

@account_newsletter_save
Scenario: Save newsletter changes
* I wait to see the home
* I wait for the overflow button
* I wait for 1 seconds
* I enter My Account
* I wait for 3 seconds
* I should see my account
* I wait for 1 seconds
* I enter email notifications
* I wait for 2 seconds
* I toggle checkbox number 1
* I wait for 1 seconds
* I enter save
* I wait for 2 seconds
* I should see the notification newsletter changes


	
