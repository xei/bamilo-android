@Calabash_Tests @device @f_pre_1.9
Feature: Device Features

Background: 
Given I call the variables

@back
Scenario: Warning Close Pop-up
Given I select the country
And I wait to see the home
When I go back
Then I should see the warning pop up message

@lock
Scenario: Lock the device
Given I wait to see the home
When I lock the device
And I wait for 5 seconds
And I lock the device
Then I should see the home
