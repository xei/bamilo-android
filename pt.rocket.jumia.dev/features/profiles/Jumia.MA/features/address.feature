@CO @address_CO
Feature: Add and Remove Address feature

  Background:
    When I verify app for CO venture
    Then I press "Search Button"
    When I enter a valid CO hint into "Search Box"
    Then I press "Search Button"
    Then I touch a valid search in the CO venture
    Then I press the enter button
#    Then I wait for progress
    And I wait for 10 seconds
    Then I press "Costumer Button"
    And I wait for 5 seconds
    Then I enter a valid username into "Username"
    Then I enter a valid password into "Password"
    And I press "Login Button"
    And I wait for 10 seconds  
    
    
    @address_homepage_add_success_CO
   Scenario: add address from homepage
    When I go back
    And I wait for 5 seconds
    Then I go back
    And I wait for 5 seconds
    Then I press "Homepage Button"
    And I wait for 5 seconds
    And I press "Homepage Add Address Button"
    Then I wait for progress    
    And I wait for 5 seconds
    Then I clear "New Second Addressline1 Box"
    Then I enter "fake test street" into "New Second Addressline1 Box"
    And I press "Ok Button"
    Then I wait for progress    
    And I wait for 5 seconds
    Then I press "Costumer Button"        
    And I wait for 5 seconds   
    And I press "My Addresses Option"
    And I wait for 5 seconds   
    And I press "Remove Address Button"
    And I wait for 3 seconds
    And I press "Ok"    
    And I wait for 5 seconds
    Then I should not see "fake test street"
    And I wait for 10 seconds
    And I press "Logout Button"
    And I wait for 3 seconds 
 
 
 
   @address_add_remove_success_CO
   Scenario: add and remove address on my account
    And I press "My Addresses Option"
    And I wait for 5 seconds
    And I press "Add Address Button"
    And I wait for 5 seconds
    Then I clear "New Second Addressline1 Box"
    Then I enter "street to delete" into "New Second Addressline1 Box"
    And I press "Ok Button"
    And I wait for 5 seconds
    And I press "Remove Address Button"
    And I wait for 3 seconds
    And I press "Ok"    
    And I wait for 5 seconds
#    Then I should see "Address deleted successfully."
#    And I wait for 5 seconds
    Then I should not see "street to delete"
    And I wait for 10 seconds
    And I press "Logout Button"
    And I wait for 3 seconds