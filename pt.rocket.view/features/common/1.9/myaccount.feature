@Calabash_Tests @account @f_1.9
Feature: 1.9 Features - My Account

Background: 
* I call the variables

@account_email_section
Scenario: See the email notifications section
* I select the country
* I wait to see the home
* I click on the overflow button
* I enter My Account
* I wait for 3 seconds
* I should see my account
* I should see the email notifications section

@account_email_not_logged
Scenario: See the email notifications without login
* I wait to see the home
* I click on the overflow button
* I enter My Account
* I wait for 3 seconds
* I should see my account
* I enter email notifications
* I should see login screen

@account_newsletter_page
Scenario: Email notification page
#Login
* I wait to see the home
* I click on the overflow button
* I choose the Sign In option
* I wait for 5 seconds
* I should see login screen
* I enter a valid username
* I enter the password
* I press Login Button
* I wait to see the home
* I click on the overflow button
* I should see sign out button
#
* I enter My Account
* I wait for 3 seconds
* I should see my account
* I enter email notifications
* I wait for 2 seconds
* I should see the email notifications section
* I should see newsletter title
* I should see newsletter header
* I should see newsletter options

@account_newsletter_save
Scenario: Save newsletter changes
* I wait to see the home
* I click on the overflow button
* I enter My Account
* I wait for 3 seconds
* I should see my account
* I enter email notifications
* I wait for 2 seconds
* I toggle checkbox number 1
* I enter save
* I wait for 4 seconds
* I should see the notification newsletter changes


	
