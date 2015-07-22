@Calabash_Tests @item_number @f_pre_1.9
Feature: Catalog - Item number

Background: 
* I call the variables

Scenario: Item number on catalog
* I select the country
* I wait for 1 seconds
* I wait to see the home
* I open the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I should see the catalog number of items


