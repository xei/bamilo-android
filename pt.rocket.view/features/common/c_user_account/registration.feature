@Calabash_Tests @register @f_pre_1.9
Feature: Registration feature

Background: 
* I call the variables
    
@register_e
Scenario: I try to register with empty fields
* I select the country
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should see login screen
* I press the create account button
* I wait for 3 seconds
* I scroll down
* I press the register button
* I scroll up
* I should see the mandatory fields error message

@register_r
Scenario: I try to register with an email that already exists
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should see login screen
* I press the create account button
* I wait for 3 seconds
* I enter a valid username
* I enter the password
* I enter the repeated password
* I enter the first name
* I enter the last name
* I press birthday
* I press male
* I wait for 1 seconds
* I scroll down
* I press the register button
* I scroll up
* I wait for 4 seconds
* I should see the email exists error message
* I press Ok

@register_dp
Scenario: I try to register with a password that is not equal
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should see login screen
* I press the create account button
* I wait for 3 seconds
* I enter a valid username
* I enter the password
* I enter the wrong repeated password
* I enter the first name
* I enter the last name
* I press birthday
* I press male
* I scroll down
* I check Terms and Conditions
* I press the register button
* I scroll up
* I wait for 4 seconds
* I should see the passwords dont match error message

@register_s
Scenario: I register an account successfully
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should see login screen
* I press the create account button
* I wait for 3 seconds
* I enter a random email
* I enter the password
* I enter the repeated password
* I enter the first name
* I enter the last name
* I press birthday
* I press male
* I scroll down
* I check Terms and Conditions
* I press the register button
* I wait to see the home
* I click on the overflow button
* I wait for 5 seconds
* I should see sign out button
   