@MA @userdetails_MA
Feature: Update User feature

  Background:
 	When I verify app for MA venture
    Then I choose the Morocco venture
    And I wait for 10 seconds
    Then I open the navigation menu
    And I choose the Sign In option
    
    
 @userdetails_update_MA
   Scenario: update user info
   And I wait for 5 seconds
    Then I enter a valid username
  	And I enter the password
  	Then I press "Login Button"
  	And I wait for 10 seconds
    Then I open the navigation menu
	And I should see sign out button
	Then I enter My Account
	Then I enter My User Data
	And I wait for 5 seconds
	And I should see my email
	And I should see my first name
	Then I enter the new password
	And I enter the new repeated password
	Then I press "Save"
	And I wait for 10 seconds
  	Then I open the navigation menu
	And I should see sign out button
  	