@Calabash_Tests @register
Feature: Registration feature

Background: 
Given I call the variables
    
@register_e
Scenario: I try to register with empty fields
Given I select the country
And I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
When I press the create account button
* I wait for 3 seconds
And I press the register button
Then I should see the mandatory fields error message

@register_r
Scenario: I try to register with an email that already exists
Given I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
When I press the create account button
* I wait for 3 seconds
When I enter a valid username
And I enter the password
And I enter the repeated password
And I enter the first name
And I enter the last name
And I press birthday
And I press Ok
And I press male
* I wait for 1 seconds
And I press the register button
Then I should see the email exists error message
And I press Ok

@register_dp
Scenario: I try to register with a password that is not equal
Given I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
When I press the create account button
* I wait for 3 seconds
When I enter a valid username
And I enter the password
And I enter the wrong repeated password
And I enter the first name
And I enter the last name
And I press birthday
And I press Ok
And I press male
And I check Terms and Conditions
And I press the register button
Then I should see the passwords dont match error message

@register_s
Scenario: I register an account successfully
Given I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
When I press the create account button
* I wait for 3 seconds
When I enter a random email
And I enter the password
And I enter the repeated password
And I enter the first name
And I enter the last name
And I press birthday
And I press Ok
And I press male
And I check Terms and Conditions
And I press the register button
And I wait to see the home
Then I click on the overflow button
And I wait for 5 seconds
And I should see sign out button
   