@Calabash_Tests @terms @f_pre_1.9  @f_2.7
Feature: Terms and Conditions

Background: 
* I call the variables

@register_t
Scenario: I check the Terms and Conditions page.
* I select the country
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should wait and see login screen
* I press the create account button
* I wait for 5 seconds
* I check Terms and Conditions
* I should see the Terms and Conditions