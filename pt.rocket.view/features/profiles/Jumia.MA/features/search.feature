@MA @search_MA
Feature: Search feature

    
  Background:
    When I verify app for MA venture  
    Then I choose the Morocco venture
    And I wait for 10 seconds
    
  @search_test_MA
  Scenario: I search a product
   Then I press "Search Field"
   Then I should see the search message
   And I wait for 3 seconds
   Then I enter a invalid search
   Then I should see the no suggestion message
   
   Then I enter a valid search
   Then I press "Search Field"
   And I press list item number 1
   And I wait for 10 seconds 
   And I press list item number 1
   And I wait for 3 seconds
   Then I should see the add to cart button