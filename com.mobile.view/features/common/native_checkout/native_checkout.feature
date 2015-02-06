@native_checkout @Calabash_Tests @f_pre_1.9
Feature: Native Checkout

Background: 
* I call the variables

@native_checkout_login    
Scenario: Native Checkout Login
* I select the country
* I wait for 5 seconds
* I wait to see the home
* I wait for 2 seconds
* I click on the overflow button
* I wait for 2 seconds
* I choose the Sign In option
* I wait for 2 seconds
* I should wait and see login screen
* I enter a valid username
* I enter the password
* I wait for 2 seconds
* I press Login Button
* I wait for 2 seconds
* I wait to see the home
    
@native_checkout_add_product
Scenario: Native Checkout Add Product
* I wait for 5 seconds
* I wait to see the home
* I wait for 2 seconds
* I wait for the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I press Got it
* I press grid item number 1
* I wait for 3 seconds
* I press Got it
* I add product to cart
* I should see the item was added to shopping cart message
* I wait for 2 seconds
* I go to cart
* I wait for 2 seconds
* I should not see the no items message

@native_checkout_valid
Scenario: Valid Checkout
* I wait for 5 seconds
* I wait to see the home
* I wait for 2 seconds
* I go to cart
* I wait for 15 seconds
* I press Proceed to Checkout
* I wait for 15 seconds
* I press Next
* I wait for 7 seconds
* I press Next
* I wait for 10 seconds
* I press Cash on Delivery
* I wait for 2 seconds
* I press Next
* I wait for 10 seconds
* I wait for the confirm order
* I wait for 2 seconds
* I press Confirm Order
* I wait for 3 seconds
* I should see the thank you screen
