@password_rec
Feature: Recover the password
	This feature will test if the app will send an email to reset the password
	The email confirmation will have to be done manually
	
	Background: 
	Given I call the variables
	When I open the navigation menu
	And I choose the Sign In option
	Then I should see the forgot password link
	When I click forgot password
	Then I should see the password recovery screen
	
	@password_rec_e
	Scenario: Request a link with empty field
	When I press Submit
	Then I should see the password recovery empty email message
	When I open the navigation menu
	And I go to home
	
	@password_rec_w
	Scenario: Request a link with a non registered email
	When I enter a fake email on password recovery
	And I press Submit
	Then I should see the password recovery email not correct message
	And I press Ok
	When I open the navigation menu
	And I go to home
	
	@password_rec_s
	Scenario: Request a link successfully
	When I enter my email on password recovery
	And I press Submit
	Then I should see the password recovery email sent message
	And I press Ok
	When I open the navigation menu
	And I go to home