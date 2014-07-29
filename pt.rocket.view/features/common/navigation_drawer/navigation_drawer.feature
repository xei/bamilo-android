@Calabash_Tests @navigation_drawer @f_pre_1.9
Feature: Navigation Drawer

Background: 
Given I call the variables

@navigation_drawer_a
Scenario: Swipe to open/close
Given I select the country
And I wait to see the home
When I swipe right moving with 15 steps
Then I should see the sidebar
When I swipe left moving with 15 steps
Then I should not see the sidebar

@navigation_drawer_b
Scenario: Click on the drawer icon
Given I wait to see the home
When I open the navigation menu
And I wait for 3 seconds
Then I should see the sidebar
And I wait for 3 seconds
When I open the navigation menu
And I wait for 5 seconds
Then I should not see the sidebar

@navigation_drawer_c
Scenario: Click on the Jumia logo
Given I wait to see the home
When I press the Jumia logo
And I wait for 3 seconds
Then I should not see the sidebar
When I open the navigation menu
And I wait for 5 seconds
Then I should see the sidebar
When I press the Jumia logo
Then I should not see the sidebar