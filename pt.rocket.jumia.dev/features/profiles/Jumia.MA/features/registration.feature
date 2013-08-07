@CO @register_CO
Feature: Registration feature

  Background:
    When I verify app for CO venture
    Then I press "Search Button"
    When I enter a valid CO hint into "Search Box"
    Then I press "Search Button"
    Then I touch a valid search in the CO venture
    Then I press the enter button
    And I wait for 10 seconds
    Then I press "Costumer Button"
    And I press "Register Button"
    And I wait for 3 seconds
      
    
  @register_empty_number_CO
  Scenario: I register with correct form
    When I enter a valid first name into "First Name Box"
    When I enter a valid last name into "Last Name Box"
    When I enter a random email into "Email Box"
    When I enter a valid password into "Password Box"
    When I enter a valid password into "Retype Password Box"
    Then I enter a random number into "Reference Code Box"
    And I press "Submit Register Form"   
    Then I wait for the view with id "dialog_content" to appear
    And I wait for 3 seconds
    And I press "Ok"
    And I wait for 5 seconds
   
    
    
  @register_empty_email_CO
  Scenario: I register with correct form
    When I enter a valid first name into "First Name Box"
    When I enter a valid last name into "Last Name Box"
    Then I enter a random number into "Mobile Box"
    When I enter a valid password into "Password Box"
    When I enter a valid password into "Retype Password Box"
    Then I enter a random number into "Reference Code Box"
    And I press "Submit Register Form"   
    Then I wait for the view with id "dialog_content" to appear
    And I wait for 3 seconds
    And I press "Ok"
    And I wait for 5 seconds
    
  @register_repeated_number_CO
  Scenario: I register with correct form
    When I enter a valid first name into "First Name Box"
    When I enter a valid last name into "Last Name Box"
    Then I enter a valid mobile number into "Mobile Box"
    When I enter a random email into "Email Box"
    When I enter a valid password into "Password Box"
    When I enter a valid password into "Retype Password Box"
    Then I enter a random number into "Reference Code Box"
    And I press "Submit Register Form"   
    Then I wait for the view with id "dialog_content" to appear
    And I wait for 3 seconds
    And I press "Ok"
    And I wait for 5 seconds
    
 @register_repeated_reference_CO
  Scenario: I register with correct form
    When I enter a valid first name into "First Name Box"
    When I enter a valid last name into "Last Name Box"
    Then I enter a valid mobile number into "Mobile Box"
    When I enter a random email into "Email Box"
    When I enter a valid password into "Password Box"
    When I enter a valid password into "Retype Password Box"
    When I enter "789632541" into "Reference Code Box"
    And I press "Submit Register Form"   
    Then I wait for the view with id "dialog_content" to appear
    And I wait for 3 seconds
    And I press "Ok"
    And I wait for 5 seconds
    
  @register_repeated_email_CO
  Scenario: I register with correct form
    When I enter a valid first name into "First Name Box"
    When I enter a valid last name into "Last Name Box"
    When I enter a valid username into "Email Box"
    Then I enter a random number into "Mobile Box"
    When I enter a valid password into "Password Box"
    When I enter a valid password into "Retype Password Box"
    Then I enter a random number into "Reference Code Box"
    And I press "Submit Register Form"   
    Then I wait for the view with id "dialog_content" to appear
    And I wait for 3 seconds
    And I press "Ok"
    And I wait for 5 seconds
    
    
  @register_success_CO
  Scenario: I register with correct form
    When I enter a valid first name into "First Name Box"
    When I enter a valid last name into "Last Name Box"
    Then I enter a random number into "Mobile Box"
    When I enter a random email into "Email Box"
    When I enter a valid password into "Password Box"
    When I enter a valid password into "Retype Password Box"
    Then I enter a random number into "Reference Code Box"
    And I press "Submit Register Form"   
 #  Then I wait for the view with id "dialog_content" to appear
 #  And I wait for 3 seconds
 #  And I press "Ok"
    And I wait for 5 seconds
 #   When I enter a valid street into "AddressLine1 Box"
    Then I press "Submit Second Register Form"
    And I wait for 5 seconds
    Then I press "Accept Terms and Conditions Button"
    Then I press "Verify Later Button"
    And I wait for 5 seconds
    Then I press "Logout Button"
    And I wait for 5 seconds
   
    