@CL @checkout_CL
Feature: Checkout feature

  Background:
    When I verify app for CL venture
    Then I press "Search Button"
    When I enter a valid CL hint into "Search Box"
    Then I press "Search Button"
    Then I touch a valid search in the CL venture
    Then I press the enter button
#    Then I wait for progress
    And I wait for 10 seconds

    
        
    @checkout_success_CL
   Scenario: makes a successfully checkout
    Then I touch a valid restaurant in the CL venture
    And I wait for 5 seconds
    #Then I press "Info Button"
    #Then I wait for the view with id "is_open" to appear
    #And I wait for 2 seconds
    #Then I go back
    #And I wait for 5 seconds
    Then I touch a valid category in the CL venture
    Then I wait for progress
    And I wait for 5 seconds
    Then I touch a valid product in the CL venture
    Then I wait for progress
    And I wait for 5 seconds
    Then I touch a valid variation in the CL venture
  #  Then I wait for progress
    And I wait for 10 seconds
    Then I touch the first choice
    And I wait for 3 seconds
    Then I touch the "Si, Soy Mayor De 18" text
    And I press "Ok Choice"
    And I wait for 5 seconds
    Then I touch the second choice
    And I wait for 3 seconds
    Then I touch the "Si, Estoy De Acuerdo" text
    And I press "Ok Choice"
    And I wait for 5 seconds
    Then I press "Add to Cart"
    Then I wait for progress
    And I wait for 5 seconds
    Then I press "Shopping Cart Button"
    And I wait for 5 seconds
    Then I enter "TEST ORDER" into "Comment Box"
    And I wait for 5 seconds
    Then I press "Place Order"
    And I wait for 10 seconds
    Then I enter "testcalabash@mailinator.com" into "Username"
    Then I enter "password1" into "Password"
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
    
    
    
    