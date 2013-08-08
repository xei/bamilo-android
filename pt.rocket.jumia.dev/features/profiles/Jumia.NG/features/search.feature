@KE @search_KE
Feature: Search feature

    
  Background:
    When I verify app for KE venture  
    Then I choose the Kenya venture
    And I wait for 10 seconds
    
  @search_test_KE
  Scenario: I search a product
   Then I press "Search Field"
   Then I should see "Please enter a term for suggestions!"
   And I wait for 3 seconds
   Then I enter a invalid search
   Then I should see "No suggestions for your search term!"
   
   Then I enter a valid search
   Then I press "Search Field"
   And I press list item number 1
   And I wait for 10 seconds 
   And I press list item number 1
   Then I should see "Call to order"
   Then I should see "Add to Cart"