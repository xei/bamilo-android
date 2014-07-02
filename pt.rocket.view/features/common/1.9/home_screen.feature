@Calabash_Tests @home_newsletter
Feature: 1.9 Features - Home Newsletter

Background: 
Given I call the variables

@home_newsletter_section
Scenario: See the newletter subscription section
When I select the country
* I wait for 5 seconds

Then I should see newsletter subscription section

#Scenario: Subscribe with valid email
# I write a valid email on the newsletter subscription field

@home_newsletter_no_email
Scenario: Subscribe with no email
* I wait for 5 seconds
When I press the newsletter Male
Then I should see the newsletter message error


#Scenario: Subscribe with already subscribed email