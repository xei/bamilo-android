@multistep_checkout @Calabash_Tests @f_2.7
Feature: Multistep Checkout 2.7

Background:
* I call the variables

@multistep_checkout_create_address
Scenario: Native Checkout Add Product
* I select the country
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I choose the Sign In option
* I wait for 2 seconds
* I should wait and see login screen
* I wait for 2 seconds
* I enter a valid username
* I wait for 1 seconds
* I enter the password
* I swipe down moving with 2 steps
* I press Login Button
* I wait to see the home
* I swipe right moving with 5 steps
* I should see the Categories
* I enter a valid Category
* I enter a valid Category
* I press Got it
* I press grid item number 2
* I wait for 2 seconds
* I press Got it
* I add product to cart
* I wait for 2 seconds
* I select size
* I wait for 2 seconds
* I go to cart
* I wait for 2 seconds
* I should not see the no items message
* I press Proceed to Checkout
* I wait for 5 seconds
* I should see the Address
* I press New Address
* I wait for 4 seconds
* I fill the new address form
* I wait for 4 seconds
* I press Next
* I wait for 5 seconds

@multistep_checkout_add_product
Scenario: Native Checkout Add Product
* I swipe right moving with 5 steps
* I should see the Categories
* I enter a valid Category
* I enter a valid Category
* I press grid item number 2
* I wait for 2 seconds
* I add product to cart
* I wait for 2 seconds
* I select size
* I wait for 2 seconds
* I go to cart
* I wait for 2 seconds
* I should not see the no items message


@multistep_checkout_valid_2
Scenario: Valid Checkout
* I wait to see the home
* I go to cart
* I wait for 2 seconds
* I press Proceed to Checkout
* I wait for 5 seconds
* I should see the Address
* I press Next
* I wait for 5 seconds
* I should see the Shipping Label
* I press Next
* I wait for 5 seconds
* I press Cash on Delivery
* I wait for 2 seconds
* I press Next
* I wait for 5 seconds
#* I should not see the Voucher Label
* I press Confirm Order
* I wait for 5 seconds
* I should see the thank you screen
* I wait for 2 seconds
* I press continue shopping
* I wait to see the home
* I wait for 2 seconds

@multistep_checkout_valid_voucher_cart
Scenario: Checkout with valid voucher in cart
* I wait to see the home
* I swipe right moving with 5 steps
* I should see the Categories
* I enter a valid Category
* I enter a valid Category
* I wait for 2 seconds
* I press grid item number 2
* I wait for 2 seconds
* I add product to cart
* I wait for 2 seconds
* I select size
* I wait for 2 seconds
* I go to cart
* I wait for 4 seconds
* I enter a valid Voucher
* I press Use
* I should see the Voucher Label
* I press Proceed to Checkout
* I wait for 5 seconds
* I should see the Address
* I press Next
* I wait for 5 seconds
* I should see the Shipping Label
* I press Next
* I wait for 5 seconds
* I press Cash on Delivery
* I wait for 2 seconds
* I press Next
* I wait for 5 seconds
* I should see the Voucher Label
* I press Confirm Order
* I wait for 5 seconds
* I should see the thank you screen
* I wait for 2 seconds
* I press continue shopping
* I wait to see the home
* I wait for 2 seconds

@multistep_checkout_invalid_voucher_cart
Scenario: Checkout with valid voucher in cart
* I wait to see the home
* I swipe right moving with 5 steps
* I should see the Categories
* I enter a valid Category
* I enter a valid Category
* I wait for 2 seconds
* I press grid item number 2
* I wait for 2 seconds
* I add product to cart
* I wait for 2 seconds
* I select size
* I wait for 2 seconds
* I go to cart
* I wait for 4 seconds
* I press Use
* I should see Invalid Voucher Message
* I wait for 4 seconds
* I enter a invalid Voucher
* I press Use
* I should see Invalid Voucher Message
* I wait for 2 seconds

@multistep_checkout_invalid_voucher_payment
Scenario: Checkout with valid voucher in cart
* I wait to see the home
* I go to cart
* I wait for 4 seconds
* I press Proceed to Checkout
* I wait for 5 seconds
* I should see the Address
* I press Next
* I wait for 5 seconds
* I should see the Shipping Label
* I press Next
* I wait for 5 seconds
* I press Cash on Delivery
* I press Use
* I should see Invalid Voucher Message
* I wait for 4 seconds
* I enter a invalid Voucher
* I press Use
* I should see Invalid Voucher Message
* I wait for 2 seconds

@multistep_checkout_valid_voucher_Payment
Scenario: Checkout with valid voucher in Payment
* I wait to see the home
* I go to cart
* I wait for 4 seconds
* I press Proceed to Checkout
* I wait for 5 seconds
* I should see the Address
* I press Next
* I wait for 5 seconds
* I should see the Shipping Label
* I press Next
* I wait for 5 seconds
* I press Cash on Delivery
* I enter a valid Voucher
* I press Use
* I wait for 2 seconds
* I press Next
* I wait for 5 seconds
* I should see the Voucher Label
* I press Confirm Order
* I wait for 5 seconds
* I should see the thank you screen
* I wait for 2 seconds
* I press continue shopping
* I wait to see the home
* I wait for 2 seconds

@multistep_checkout_remove_voucher_cart
Scenario: Checkout with remove voucher in cart
* I wait to see the home
* I swipe right moving with 5 steps
* I should see the Categories
* I enter a valid Category
* I enter a valid Category
* I wait for 2 seconds
* I press grid item number 2
* I wait for 2 seconds
* I add product to cart
* I wait for 2 seconds
* I select size
* I wait for 2 seconds
* I go to cart
* I wait for 4 seconds
* I enter a valid Voucher
* I press Use
* I should see the Voucher Label
* I wait for 2 seconds
* I press Remove
* I should not see the Voucher Label
* I wait for 5 seconds
* I press Proceed to Checkout
* I wait for 5 seconds
* I should see the Address
* I press Next
* I wait for 5 seconds
* I should see the Shipping Label
* I press Next
* I wait for 5 seconds
* I press Cash on Delivery
* I enter a valid Voucher
* I press Use
* I wait for 2 seconds
* I press Remove
* I wait for 2 seconds
* I press Next
* I wait for 5 seconds
#* I should not see the Voucher Label