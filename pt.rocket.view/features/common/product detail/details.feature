@Calabash_Tests @teste5 @f_pre_1.9
Feature: PDV - Navigate to a product with different variations

Background: 
Given I call the variables
    
@product_size
Scenario: I see the variations on a product
Given I select the country
And I wait to see the home
When I click on search
When I enter a variation search
* I wait for 5 seconds
And I press list item number 1
* I wait for 5 seconds 
And I press Got it
And I press list item number 1
* I wait for 2 seconds 
And I press Got it
   	Then I should see the variations
   	
@product_spec
Scenario: I See the product details
Given I wait to see the home
When I click on the search bar
When I enter a variation search
* I wait for 2 seconds 
And I press list item number 1
* I wait for 5 seconds 
And I press list item number 1
* I wait for 3 seconds
When I press product specifications
* I wait for 10 seconds
#Then I should see the product features
And I should see the product description

@review_overview
Scenario: I see the product rating overview
Given I wait to see the home
When I click on the search bar
When I enter a variation search
* I wait for 2 seconds 
And I press list item number 1
* I wait for 5 seconds 
And I press list item number 1
* I wait for 2 seconds 
When I press Rating
* I wait for 10 seconds 
Then I should see the write a review button

@review_detail
Scenario: I see the rating details
Given I wait to see the home
When I click on the search bar
When I enter a rated search
* I wait for 5 seconds 
And I press list item number 1
* I wait for 5 seconds 
And I press list item number 1
When I press Rating
   	
@write_review
Scenario: I write a review
Given I wait to see the home
When I click on the search bar
When I enter a variation search
* I wait for 3 seconds
And I press list item number 1
* I wait for 5 seconds 
And I press list item number 1
* I wait for 3 seconds
When I press Rating
* I wait for 10 seconds 	
And I press Write a Review
* I wait for 2 seconds
And I fill the review information
* I wait for 2 seconds
And I press rating
* I wait for 2 seconds
And I press Send Review
* I wait for 2 seconds
#And I press to reviews
   	
@sharing
Scenario: I share a product by sms
Given I wait to see the home
When I click on the search bar
When I enter a variation search
* I wait for 5 seconds 
And I press list item number 1
* I wait for 5 seconds 
And I press list item number 1
When I press share 
#And I press list item number 2