@CI @reorder_CI
Feature: Update User feature

  Background:
    When I verify app for CL venture
    Then I press "Search Button"
    When I enter a valid CL hint into "Search Box"
    Then I press "Search Button"
    Then I touch a valid search in the CL venture
    Then I press the enter button
#    Then I wait for progress
    And I wait for 10 seconds
    Then I press "Costumer Button"
    And I wait for 5 seconds
    Then I enter a valid username into "Username"
    Then I enter a valid password into "Password"
    And I press "Login Button"
    And I wait for 10 seconds
    
    
 @reorder_success_CL
   Scenario: update user info
    And I press "My Orders Option"
    And I wait for 5 seconds
    Then I press "Re Order"
    Then I press "Place Order"
    And I wait for 10 seconds
    Then I press "Finalize Order"
    And I wait for 3 seconds
    And I press "Verify Later Button"
    And I wait for 10 seconds
    Then I press "Finish Button"
    And I wait for 5 seconds
    Then I press "Ok"
    And I wait for 5 seconds
    Then I press "Costumer Button"
    And I wait for 5 seconds
    Then I press "Logout Button"
    And I wait for 5 seconds