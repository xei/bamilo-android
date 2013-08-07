@common @register_common
Feature: Registration feature

#  Background:
#      When I enter "049782" into "Search Box"
#      Then I press the enter button
#      And I wait for 5 seconds
      
    
  @register_success_common
  Scenario: I register with correct form
    Then I press "Search Button"
    When I enter "049782" into "Search Box"
    Then I press "Search Button"
    Then I press the enter button
    And I wait for 10 seconds
    Then I press "Costumer Button"
    And I press "Register Button"
    And I wait for 3 seconds
    When I enter "Tester" into "First Name Box"
    When I enter "Test" into "Last Name Box"
    Then I enter a random number into "Mobile Box"
    When I enter a random email into "Email Box"
    When I enter "password1" into "Password Box"
    When I enter "password1" into "Retype Password Box"
#   When I enter "049782" into "Reference Code Box"
    And I press "Submit Register Form"   
 #  Then I wait for the view with id "dialog_content" to appear
 #  And I wait for 3 seconds
 #  And I press "Ok"
    And I wait for 10 seconds