@Calabash_Tests @device @f_pre_1.9
Feature: Device Features

Background: 
* I call the variables

@back
Scenario: Warning Close Pop-up
* I select the country
* I wait to see the home
* I go back
* I should see the warning pop up message

@lock
Scenario: Lock the device
* I wait to see the home
* I lock the device
* I wait for 5 seconds
* I lock the device
* I should see the home
