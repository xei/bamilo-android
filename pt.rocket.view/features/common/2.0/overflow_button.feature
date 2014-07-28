@Calabash_Tests @overflow_btn @f_2.0
Feature: 2.0 Features - Overflow Button

Background: 
Given I call the variables

Scenario: Choose Country
When I select the country

Scenario: Check the overflow dropdown options 
Given I wait to see the home
When I click on the overflow button
Then I should see the overflow options

Scenario: Sign in
Given I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen

Scenario: My Favourites
Given I wait to see the home
When I click on the overflow button
And I enter my favorites
Then I should see the my favorites

Scenario: Recent Searches
Given I wait to see the home
When I click on the overflow button
And I enter recent searches
Then I should see the recent searches

Scenario: Recently Viewed
Given I wait to see the home
When I click on the overflow button
And I enter recently viewed
Then I should see the recently viewed

Scenario: My Account
Given I wait to see the home
When I click on the overflow button
And I enter My Account
Then I should see my account

Scenario: Track My Order
Given I wait to see the home
When I click on the overflow button
And I enter track my order
Then I should see track my order page
