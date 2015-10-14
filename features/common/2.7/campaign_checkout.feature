@campaign_checkout @Calabash_Tests @f_2.7
Feature: Multistep Checkout 2.7

Background:
* I call the variables

@campaign_checkout_valid
Scenario: Native Checkout Add Product
* I select the country
* I wait to see the home
* I wait for 2 seconds
* I touch the first Teaser
* I wait for 2 seconds
* I touch the Buy Now in Campaign
* I wait for 5 seconds