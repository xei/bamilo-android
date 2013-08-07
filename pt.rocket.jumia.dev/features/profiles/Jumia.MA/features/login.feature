@CO @login_CO
Feature: Login feature

  Background:
    When I verify app for CO venture
    Then I press "Search Button"
    When I enter a valid CO hint into "Search Box"
    Then I press "Search Button"
    Then I touch a valid search in the CO venture
    Then I press the enter button
    And I wait for 10 seconds
    Then I press "Costumer Button"
    And I wait for 3 seconds
      
    
  @login_wrong_pass_CO
  Scenario: I login with wrong password
    Then I enter a valid username into "Username"
    Then I enter "foodpanda11" into "Password"
    And I press "Login Button"
    And I wait for 3 seconds
    Then I wait for the view with id "dialog_content" to appear
    And I wait for 3 seconds
    And I press "Ok"
    And I wait for 3 seconds
    
    
    
  @login_wrong_user_CO
  Scenario: I login with wrong username
    Then I enter "gsilvqwea@rocket-internet.pt" into "Username"
    Then I enter a valid password into "Password"
    And I press "Login Button"
    And I wait for 3 seconds
    Then I wait for the view with id "dialog_content" to appear
    And I wait for 3 seconds
    And I press "Ok"
    And I wait for 3 seconds

    
  @login_empty_fields_CO
  Scenario: I login with empty fields
    Then I enter "" into "Username"
    Then I enter "" into "Password"
    And I press "Login Button"
    And I wait for 3 seconds
    Then I should not see "Ok"
  
  @login_empty_username_CO
  Scenario: I login with empty username
    Then I enter "" into "Username"
    Then I enter a valid password into "Password"
    And I press "Login Button"
    And I wait for 3 seconds
    Then I should not see "Ok"
  
   @login_empty_password_CO
   Scenario: I login with empty password
    Then I enter a valid username into "Username"
    Then I enter "" into "Password"
    And I press "Login Button"
    And I wait for 3 seconds
    Then I should not see "Ok"
 
   @login_valid_CO
   Scenario: I login valid, and logout
    Then I enter a valid username into "Username"
    Then I enter a valid password into "Password"
    And I press "Login Button"
    And I wait for 3 seconds
    And I press "Logout Button"
    And I wait for 3 seconds