@Calabash_Tests @password_rec
Feature: Recover the password
	This feature will test if the app will send an email to reset the password
	The email confirmation will have to be done manually
	
	Background: 
	Given I call the variables
	
	@password_rec_e
	Scenario: Request a link with empty field
	And I select the country
	And I wait for 10 seconds
	When I open the navigation menu
	* I wait for 5 seconds
	And I choose the Sign In option
	Then I should see the forgot password link
	When I click forgot password
	Then I should see the password recovery screen
	When I press Submit
	Then I should see the password recovery empty email message
	
	@password_rec_w
	Scenario: Request a link with a non registered email
	* I wait for 10 seconds
	When I open the navigation menu
	* I wait for 5 seconds
	And I choose the Sign In option
	Then I should see the forgot password link
	When I click forgot password
	Then I should see the password recovery screen
	When I enter a fake email on password recovery
	And I press Submit
	Then I should see the password recovery email not correct message
	And I press Ok
	
	@password_rec_s
	Scenario: Request a link successfully
	* I wait for 10 seconds
	When I open the navigation menu
	* I wait for 5 seconds
	And I choose the Sign In option
	Then I should see the forgot password link
	When I click forgot password
	Then I should see the password recovery screen
	When I enter my email on password recovery
	And I press Submit
	Then I should see the password recovery email sent message
	And I press Ok