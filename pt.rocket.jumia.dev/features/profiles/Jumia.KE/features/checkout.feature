@BR @checkout_BR
Feature: Checkout feature

  Background:
    When I verify app for BR venture
    Then I press "Search Address"
	And I wait for 3 seconds
    When I enter a valid BR hint into "Search Box"
    Then I touch a valid search in the BR venture
    Then I press the enter button
#    Then I wait for progress
    And I wait for 10 seconds

    
        
    @checkout_success_BR
   Scenario: makes a successfully checkout
    Then I touch a valid restaurant in the BR venture
    Then I wait for progress
    And I wait for 5 seconds
    Then I touch a valid category in the BR venture
    Then I wait for progress
    And I wait for 5 seconds
   Then I touch a valid product in the BR venture
    Then I wait for progress
    And I wait for 5 seconds
    Then I press "Ok Spinner"
    And I wait for 5 seconds
    Then I press "Shopping Cart Button"
    And I wait for 5 seconds
    Then I enter a valid comment into "Comment Box"
    And I wait for 5 seconds
    Then I press "Place Order"
    And I wait for 10 seconds
    Then I enter a valid username into "Username"
    Then I enter a valid password into "Password"
    And I press "Login Button"
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
    
    
    