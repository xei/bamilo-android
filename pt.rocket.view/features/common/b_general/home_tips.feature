@Calabash_Tests @home_tips
Feature: Home Tips

Background: 
Given I call the variables

@tip_home_first_time
Scenario: Home first time Tip
Given I select the country
And I wait to see the home
Then I should see the first tip
When I swipe left moving with 15 steps
Then I should not see the tips

@tip_home_second_time
Scenario: Home second time tip
Given I wait to see the home
Then I should not see the tips