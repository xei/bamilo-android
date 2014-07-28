@Calabash_Tests @terms
Feature: Terms and Conditions

Background: 
Given I call the variables

@register_t
Scenario: I check the Terms and Conditions page.
Given I select the country
And I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
And I press the create account button
* I wait for 5 seconds
When I check Terms and Conditions
Then I should see the Terms and Conditions