@Calabash_Tests @news_19 @f_1.9
Feature: 1.9 Features - Newsletter

Background: 
Given I call the variables

@newsletter_checkbox
Scenario: See the newsletter checkbox on register
Given I select the country
And I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
And I press the create account button
* I wait for 1 seconds
Then I should see the newsletter checkbox

@newsletter_register
Scenario: I register with newsletter on
Given I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
When I press the create account button
* I wait for 1 seconds
And I enter a random email
And I enter the password
And I enter the repeated password
And I enter the first name
And I enter the last name
And I press birthday
And I press Ok
And I press male
And I check Terms and Conditions
And I check newsletter
And I press the register button
And I wait to see the home
And I click on the overflow button
Then I should see sign out button
	


	
