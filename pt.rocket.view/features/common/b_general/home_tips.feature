@Calabash_Tests @home_tips
Feature: Home Tips

Background: 
Given I call the variables

Scenario: Home first time Tip
And I select the country
And I wait for 8 seconds
Then I should see the first tip
When I swipe left moving with 15 steps
Then I should not see the tips

Scenario: Home second time tip
And I wait for 8 seconds
Then I should not see the tips