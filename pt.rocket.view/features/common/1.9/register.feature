@Calabash_Tests @news_19 @f_1.9
Feature: 1.9 Features - Newsletter

Background: 
* I call the variables

@newsletter_checkbox
Scenario: See the newsletter checkbox on register
* I select the country
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should see login screen
* I press the create account button
* I wait for 1 seconds
* I should see the newsletter checkbox

@newsletter_register
Scenario: I register with newsletter on
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should see login screen
* I press the create account button
* I wait for 1 seconds
* I enter a random email
* I enter the password
* I enter the repeated password
* I enter the first name
* I enter the last name
* I press birthday

* I press male
* I check Terms and Conditions
* I check newsletter
* I scroll down 
* I press the register button
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I should see sign out button
	


	
