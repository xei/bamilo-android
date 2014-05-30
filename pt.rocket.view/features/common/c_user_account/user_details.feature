@user_details @Calabash_Tests
Feature: See personal data

 	Background: 
 	Given I call the variables
 	
 	@user_details_c
	Scenario: See the user details
	And I select the country
	And I wait for 5 seconds
#Login
	And I open the navigation menu
	* I wait for 2 seconds
	Then I choose the Sign In option
	* I wait for 5 seconds
    When I enter a valid username
  	And I enter the password
  	And I press Login Button
  	* I wait for 10 seconds
    Then I open the navigation menu
    * I wait for 10 seconds
	And I should see sign out button

#    When I open the navigation menu
    And I enter My Account
    * I wait for 5 seconds
	And I enter My User Data
	* I wait for 4 seconds
	Then I should see my email
	And I should see my first name
	Then I open the navigation menu
	And I go to home
	
	@user_details_e
	Scenario: Try with empty fields
    * I wait for 10 seconds
    When I open the navigation menu
    And I wait for 5 seconds
    And I enter My Account
    * I wait for 5 seconds
	And I enter My User Data
	* I wait for 4 seconds
	When I press Save
	Then I should see the password is to short message
	Then I open the navigation menu
	And I go to home
	
	@user_details_dp
	Scenario: Try different passwords
	* I wait for 10 seconds
    When I open the navigation menu
    And I wait for 5 seconds
    And I enter My Account
    * I wait for 5 seconds
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
	* I wait for 10 seconds
    When I open the navigation menu
    And I wait for 5 seconds
    And I enter My Account
    * I wait for 5 seconds
	And I enter My User Data
	* I wait for 10 seconds
	When I enter the new password
	And I enter the new repeated password
	And I press Save
	* I wait for 1 seconds
	Then I should see the password changed with success message
	When I open the navigation menu
  	Then I should see sign out button
  	Then I close the navigation menu