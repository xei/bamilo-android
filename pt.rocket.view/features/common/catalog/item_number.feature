@Calabash_Tests @item_number @f_pre_1.9
Feature: Catalog - Item number

Background: 
Given I call the variables

Scenario: Item number on catalog
Given I select the country
And I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
* I wait for 3 seconds
Then I should see the catalog number of items


