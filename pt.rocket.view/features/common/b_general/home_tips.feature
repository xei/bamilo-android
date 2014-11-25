@Calabash_Tests @home_tips @f_pre_1.9
Feature: Home Tips

Background: 
* I call the variables

@tip_home_first_time
Scenario: Home first time Tip
* I select the country
* I wait to see the home
* I should see the first tip
* I swipe left moving with 15 steps
* I should not see the tips

@tip_home_second_time
Scenario: Home second time tip
* I wait to see the home
* I should not see the tips