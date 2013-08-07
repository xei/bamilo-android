@common @login_common
Feature: Login feature

#  Background:
#      When I enter "049782" into "Search Box"
#      Then I press the enter button
#      And I wait for 5 seconds
      
    
  @login_wrong_pass_common
  Scenario: I login with wrong password
    Then I press "Search Button"
    When I enter "049782" into "Search Box"
    Then I press "Search Button"
    Then I press the enter button
    And I wait for 10 seconds
    Then I press "Costumer Button"
    And I wait for 3 seconds
    Then I enter "gsilva@rocket-internet.pt" into "Username"
    Then I enter "foodpanda11" into "Password"
    And I press "Login Button"
    And I wait for 3 seconds
    Then I wait for the view with id "dialog_content" to appear
    And I wait for 3 seconds
    And I press "Ok"
    And I wait for 3 seconds
    
    
    
  @login_wrong_user_common
  Scenario: I login with wrong username
    Then I press "Search Button"
    When I enter "049782" into "Search Box"
    Then I press "Search Button"    
    Then I press the enter button
    And I wait for 10 seconds
    Then I press "Costumer Button"
    And I wait for 3 seconds
    Then I enter "gsilvqwea@rocket-internet.pt" into "Username"
    Then I enter "foodpanda" into "Password"
    And I press "Login Button"
    And I wait for 3 seconds
    Then I wait for the view with id "dialog_content" to appear
    And I wait for 3 seconds
    And I press "Ok"
    And I wait for 3 seconds

    
  @login_empty_fields_common
  Scenario: I login with empty fields
    Then I press "Search Button"
    When I enter "049782" into "Search Box"
    Then I press "Search Button"
    Then I press the enter button
    And I wait for 10 seconds
    Then I press "Costumer Button"
    And I wait for 3 seconds
    Then I enter "" into "Username"
    Then I enter "" into "Password"
    And I press "Login Button"
    And I wait for 3 seconds
    Then I should not see "Ok"
  
  @login_empty_username_common
  Scenario: I login with empty username
    Then I press "Search Button"
    When I enter "049782" into "Search Box"
    Then I press "Search Button"
    Then I press the enter button
    And I wait for 10 seconds
    Then I press "Costumer Button"
    And I wait for 3 seconds
    Then I enter "" into "Username"
    Then I enter "foodpanda" into "Password"
    And I press "Login Button"
    And I wait for 3 seconds
    Then I should not see "Ok"
  
   @login_empty_password_common
   Scenario: I login with empty password
    Then I press "Search Button"
    When I enter "049782" into "Search Box"
    Then I press "Search Button"
    Then I press the enter button
    And I wait for 10 seconds
    Then I press "Costumer Button"
    And I wait for 3 seconds
    Then I enter "gsilva@rocket-internet.pt" into "Username"
    Then I enter "" into "Password"
    And I press "Login Button"
    And I wait for 3 seconds
    Then I should not see "Ok"
 
   @login_valid_common
   Scenario: I login valid, and logout
    Then I press "Search Button"
    When I enter "049782" into "Search Box"
    Then I press "Search Button"
    Then I press the enter button
    And I wait for 10 seconds
    Then I press "Costumer Button"
    And I wait for 3 seconds
    Then I enter "gsilva@rocket-internet.pt" into "Username"
    Then I enter "foodpanda" into "Password"
    And I press "Login Button"
    And I wait for 3 seconds
    And I press "Logout Button"
    And I wait for 3 seconds