@Calabash_Tests @password_rec @f_pre_1.9
Feature: Password Recovery
This feature will test if the app will send an email to reset the password
The email confirmation will have to be done manually

Background: 
Given I call the variables

@password_rec_e
Scenario: Request a link with empty field
* I select the country
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should wait and see login screen
* I should see the forgot password link
* I click forgot password
* I should see the password recovery screen
* I press Submit
* I wait for 5 seconds
* I should see the password recovery empty email message

@password_rec_w
Scenario: Request a link with a non registered email
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should wait and see login screen
* I should see the forgot password link
* I click forgot password
* I wait for 5 seconds
* I should see the password recovery screen
* I wait for 5 seconds
* I enter a fake email on password recovery
* I wait for 5 seconds
* I press Submit
* I wait for 5 seconds
* I should see the password recovery email not correct message
* I wait for 10 seconds
* I press Ok

@password_rec_s
Scenario: Request a link successfully
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should wait and see login screen
* I should see the forgot password link
* I click forgot password
* I should see the password recovery screen
* I enter my email on password recovery
* I press Submit
* I wait for 5 seconds
* I should see the password recovery email sent message
* I press Ok