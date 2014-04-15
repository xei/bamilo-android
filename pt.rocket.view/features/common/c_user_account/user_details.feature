@user_details @Calabash_Tests
Feature: See personal data

 	Background: 
 	Given I call the variables
 	
 	@user_details_c
	Scenario: See the user details
	And I select the country
	And I wait for 5 seconds
	* I Login
    When I open the navigation menu
    And I enter My Account
    * I wait for progress
	And I enter My User Data
	* I wait for 4 seconds
	Then I should see my email
	And I should see my first name
	Then I open the navigation menu
	And I go to home
	
	@user_details_e
	Scenario: Try with empty fields
    When I open the navigation menu
    And I enter My Account
    * I wait for progress
	And I enter My User Data
	* I wait for 4 seconds
	When I press Save
	Then I should see the password is to short message
	Then I open the navigation menu
	And I go to home
	
	@user_details_dp
	Scenario: Try different passwords
    When I open the navigation menu
    And I enter My Account
    * I wait for progress
	And I enter My User Data
	* I wait for 4 seconds
	When I enter the new password
	And I enter a wrong repeated password
	And I press Save
	Then I should see the passwords dont match error message
	Then I open the navigation menu
	And I go to home
	
	@user_details_s
	Scenario: Change password success
    When I open the navigation menu
    And I enter My Account
    * I wait for progress
	And I enter My User Data
	* I wait for 4 seconds
	When I enter the new password
	And I enter the new repeated password
	And I press Save
	Then I should see the password changed with success message
	When I open the navigation menu
  	Then I should see sign out button
  	Then I close the navigation menu