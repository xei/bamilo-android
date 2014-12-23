@Calabash_Tests @details @f_pre_1.9
Feature: PDV - Navigate to a product with different variations

Background: 
* I call the variables
    
@product_size
Scenario: I see the variations on a product
* I select the country
* I wait to see the home
* I click on search
* I wait for 2 seconds
* I enter a variation search
* I wait for 5 seconds
* I press list item number 1
* I wait for 5 seconds 
* I press Got it
* I wait for 2 seconds
* I press grid item number 1
* I wait for 2 seconds 
* I press Got it
* I wait for 2 seconds
* I scroll down
* I should see the variations
 
Scenario: I see the related items
* I wait to see the home
* I click on search
* I enter a variation search
* I wait for 5 seconds
* I press list item number 1
* I wait for 5 seconds 
* I press grid item number 1
* I wait for 2 seconds 
* I scroll down
* I scroll down
* I should see the related items
   	
@product_spec
Scenario: I See the product details
* I wait to see the home
* I click on the search bar
* I enter a variation search
* I wait for 2 seconds 
* I press list item number 1
* I wait for 5 seconds 
* I press grid item number 1
* I wait for 3 seconds
* I scroll down
* I press product specifications
* I wait for 10 seconds
* I should see the product features
* I wait for 2 seconds
* I should see the product description

@review_overview
Scenario: I see the product rating overview
* I wait to see the home
* I click on the search bar
* I enter a variation search
* I wait for 2 seconds 
* I press list item number 1
* I wait for 5 seconds 
* I press grid item number 1
* I wait for 2 seconds 
* I scroll down
* I press Rating
* I wait for 10 seconds 
* I should see the write a review button

@review_detail
Scenario: I see the rating details
* I wait to see the home
* I click on the search bar
* I wait for 2 seconds
* I enter a rated search
* I wait for 5 seconds 
* I press list item number 1
* I wait for 5 seconds 
* I press grid item number 1
* I wait for 4 seconds
* I scroll down
* I wait for 2 seconds
* I press Rating
   	
@write_review
Scenario: I write a review
* I wait to see the home
* I click on the search bar
* I wait for 2 seconds
* I enter a variation search
* I wait for 3 seconds
* I press list item number 1
* I wait for 5 seconds 
* I press grid item number 1
* I wait for 3 seconds
* I scroll down
* I press Rating
* I wait for 10 seconds 	
* I press Write a Review
* I wait for 2 seconds
* I fill the review information
* I wait for 2 seconds
#* I press rating
#* I wait for 2 seconds
* I press Send Review
   	
@sharing
Scenario: I share a product by sms
* I wait to see the home
* I click on the search bar
* I wait for 2 seconds
* I enter a variation search
* I wait for 5 seconds 
* I press list item number 1
* I wait for 5 seconds 
* I press grid item number 1
* I wait for 2 seconds
* I press share 