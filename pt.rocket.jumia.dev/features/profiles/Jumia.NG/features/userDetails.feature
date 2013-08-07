@CI @userdetails_CI
Feature: Update User feature

  Background:
    When I verify app for CI venture
    Then I press "Search Button"
    Then I touch a valid search in the CI venture
    Then I press the enter button
    And I wait for 10 seconds
    Then I press "Costumer Button"
    And I wait for 5 seconds
    Then I enter a valid username into "Username"
    Then I enter a valid password into "Password"
    And I press "Login Button"
    And I wait for 10 seconds
    
    
 @userdetails_update_CI
   Scenario: update user info
    And I press "My Info Option"
    And I wait for 5 seconds
    Then I clear "User First Name Box"
    And I wait for 5 seconds
    #verify
    Then the view with id "profile_save_button" should have property "enabled" = "false"
    When I enter a valid first name into "User First Name Box"
    Then I clear "User Last Name Box"
    And I wait for 5 seconds
    #verify
    Then the view with id "profile_save_button" should have property "enabled" = "false"   
    When I enter a valid last name into "User Last Name Box"
    Then I clear "User Mobile Box"
    And I wait for 5 seconds
    #verify
    Then the view with id "profile_save_button" should have property "enabled" = "false"   
    When I enter a valid mobile number into "User Mobile Box"
    Then I clear "User Email Box"
    And I wait for 5 seconds
    #verify
    Then the view with id "profile_save_button" should have property "enabled" = "false"   
    When I enter a valid username into "User Email Box"
    Then I clear "User Last Name Box"
    When I enter "Test alt" into "User Last Name Box"
    And I wait for 5 seconds
    Then I clear "User Mobile Box"
    When I enter "7896325411" into "User Mobile Box"
    And I wait for 5 seconds
    And I press "Save User Form"
    And I wait for 10 seconds
    And I press "My Info Option"
    Then I should see "Test alt"    
    And I wait for 5 seconds
    Then I should see "7896325411"
    And I wait for 5 seconds   
    Then I clear "User Last Name Box"
    When I enter a valid last name into "User Last Name Box"
    Then I clear "User Mobile Box"
    When I enter a valid mobile number into "User Mobile Box"   
    And I wait for 5 seconds
    And I press "Save User Form"
    And I wait for 10 seconds
    Then I press "Logout Button"
    And I wait for 5 seconds
    