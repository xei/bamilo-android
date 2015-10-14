@Calabash_Tests @f_2.7 @add_to_wishlist_2.7
Feature: 2.7 Features - Add and Remove from Wish list

Background:
* I call the variables

Scenario: Add to wish list in PDV
* I select the country
* I wait to see the home
* I swipe right moving with 5 steps
* I wait for 2 seconds
* I should see the Categories
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I press Got it
* I press grid item number 2
* I wait for 2 seconds
* I press Got it
* I click on the favorite icon
* I wait for the overflow button
* I enter my favorites
* I wait for 5 seconds