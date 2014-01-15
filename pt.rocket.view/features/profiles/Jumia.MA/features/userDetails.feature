@MA @userdetails_MA
Feature: Update User feature

  Background:
 	Given I verify app for MA venture
    And I choose the Morocco venture
    When I open the navigation menu
    Then I choose the Sign In option
    
 
    @userdetails_update_MA
    Scenario: update user info
   	* I wait for 1 seconds
    Given I enter a valid username
  	And I enter the password
  	And I press Login Button
  	* I wait for 1 seconds
    When I open the navigation menu
	Then I should see sign out button
	When I enter My Account
	And I enter My User Data
	
	#Scenario: See the user details
	Then I should see my email
	And I should see my first name
	
	#Scenario: Try different passwords
	When I enter the new password
	And I enter a wrong repeated password
	And I press Save
	Then I should see the passwords dont match error message
	
	#Scenario: change password success
	When I enter the new password
	And I enter the new repeated password
	And I press Save
	Then I should see the password changed with success message
	When I open the navigation menu
  	Then I should see sign out button