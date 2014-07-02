@Calabash_Tests @device
Feature: Device Features

Background: 
Given I call the variables

@back
Scenario: Warning Close Pop-up
* I wait for 10 seconds
And I select the country
And I wait for 10 seconds
When I go back
#Then I should see the warning pop up message

@lock
Scenario: Lock the device
And I wait for 5 seconds
When I lock the device
And I wait for 5 seconds
And I lock the device
Then I should see the home
