@Calabash_Tests @navigation_drawer @f_pre_1.9
Feature: Navigation Drawer

Background: 
Given I call the variables

@navigation_drawer_a
Scenario: Swipe to open/close
* I select the country
* I wait to see the home
* I swipe right moving with 15 steps
* I wait for 2 seconds
* I should see the sidebar
* I swipe left moving with 15 steps
* I wait for 2 seconds
* I should not see the sidebar

@navigation_drawer_b
Scenario: Click on the drawer icon
* I wait to see the home
* I open the navigation menu
* I wait for 3 seconds
* I should see the sidebar
* I wait for 3 seconds
* I open the navigation menu
* I wait for 5 seconds
* I should not see the sidebar

#@navigation_drawer_c
#Scenario: Click on the Jumia logo
#* I wait to see the home
#* I press the Jumia logo
#* I wait for 3 seconds
#* I should not see the sidebar
#* I open the navigation menu
#* I wait for 5 seconds
#* I should see the sidebar
#* I press the Jumia logo
#* I should not see the sidebar