@Calabash_Tests @country_switch @f_pre_1.9
Feature: Cart - Country switch

Background: 
* I call the variables

Scenario: Country Switch
* I select the country
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I choose the Sign In option
* I wait for 5 seconds
* I should wait and see login screen
* I wait for 2 seconds
* I enter a valid username
* I wait for 2 seconds
* I enter the password
* I wait for 2 seconds
* I press Login Button
* I wait for 3 seconds
* I wait to see the home
* I wait for the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I enter a valid Category
* I wait for 3 seconds
* I press Got it
* I wait for 2 seconds
* I press grid item number 1
* I wait for 3 seconds
* I press Got it
* I add product to cart
* I should see the item was added to shopping cart message
* I wait for 2 seconds
* I go to cart
* I wait for the navigation menu
* I wait for 2 seconds
* I press Choose Country
* I wait for 2 seconds
* I press on other country
* I wait for 2 seconds
* I should see the clear the cart message
#* I wait for 2 seconds
#* I press Yes
#* I press the cart icon
#* I should see the empty message
	
