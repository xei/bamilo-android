Feature: Navigate to a product with different variations

Background: 
    Given I call the variables
	When I open the navigation menu
	And I press Search 
		When I enter a variation search
   	And I press list item number 1
   	And I press list item number 1
   	* I wait for 2 seconds 
   	And I press list item number 1
   	And I press Got it

    @product_size
  	Scenario: I see the variations on a product
   	Then I should see the variations
   	
   	@product_spec
   	Scenario: I See the product details
   	When I press product specifications
   	Then I should see the product features
   	And I should see the product description
   	
   	@review_overview
   	Scenario: I see the product rating overview
   	When I press Rating
   	Then I should see the write a review button
   	
   	@review_detail
   	Scenario: I see the rating details
   	When I press Rating
   	And I press a review
   	
   	@write_review
   	Scenario: I write a review
   	When I press Rating
   	And I press Write a Review
   	* I wait for 2 seconds
  	And I fill the review information
  	And I press rating
   	And I press Send Review
   	And I press to reviews
   	
   	@sharing
   	Scenario: I share a product by sms
   	When I press share 
   	And I press list item number 2