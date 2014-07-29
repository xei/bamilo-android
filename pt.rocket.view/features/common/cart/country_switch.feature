@Calabash_Tests @country_switch @f_pre_1.9
Feature: Cart - Country switch

Background: 
Given I call the variables

Scenario: Country Switch
Given I select the country
And I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
When I enter a valid username
And I enter the password
And I press Login Button
And I wait to see the home
When I click on the overflow button
Then I should see sign out button
And I click on the overflow button
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
* I wait for 3 seconds
And I press Got it
And I press list item number 1
* I wait for 3 seconds
Then I press Got it
And I add product to cart
* I wait for 15 seconds
Then I should see the item was added to shopping cart message
When I go to cart
When I open the navigation menu
And I press Choose Country
* I wait for 2 seconds
And I press on other country
* I wait for 1 seconds
Then I should see the clear the cart message
And I press Yes
* I wait for 5 seconds
#When I press the cart icon
#* I wait for 20 seconds
#	Then I should see the empty message
	
