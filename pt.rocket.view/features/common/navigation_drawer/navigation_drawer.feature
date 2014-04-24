@Calabash_Tests @navigation_drawer
Feature: Navigation Drawer

Background: 
Given I call the variables


@navigation_drawer_a
Scenario: Swipe to open/close
And I select the country
And I wait for 10 seconds
When I swipe right moving with 15 steps
Then I should see the sidebar
When I swipe left moving with 15 steps
Then I should not see the sidebar

@navigation_drawer_b
Scenario: Click on the drawer icon
When I open the navigation menu
Then I should see the sidebar
When I open the navigation menu
Then I should not see the sidebar

@navigation_drawer_c
Scenario: Click on the Jumia logo
When I press the Jumia logo
Then I should not see the sidebar
When I open the navigation menu
Then I should see the sidebar
When I press the Jumia logo
Then I should not see the sidebar