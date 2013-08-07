@CO @search_CO
Feature: Search feature

   
  Background:
    When I verify app for CO venture   
    
  @search_wrong_CO
  Scenario: I search with wrong location
    Then I press "Search Button"
    When I enter "fssdfs" into "Search Box"
    Then I wait for the view with id "noResultsTextView" to appear
   #Then I should see "No Results Found"
    And I wait for 3 seconds

    
    
    
  @search_success_CO
  Scenario: I search with valid location
    Then I press "Search Button"
    When I enter a valid CO hint into "Search Box"
    Then I press "Search Button"
    Then I touch a valid search in the CO venture
    Then I press the enter button
    Then I wait for the view with id "restaurant_list_container" to appear
    And I wait for 3 seconds
    
   