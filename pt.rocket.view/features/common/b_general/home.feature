@f_pre_1.9 @app_initial
Feature: Initial app features 
Install app, open, choose country

Background: 
* I call the variables

@country_check @Calabash_Tests @teste_init
Scenario: Initial country checker
* I wait for 5 seconds
* I should see the countries

@splash_check @Calabash_Tests @teste_init
Scenario: Check the splash screen
#Given I start the app 
#* I sould see the splash screen

@home_check @Calabash_Tests @teste_init
Scenario: Home should appear when i open the app
* I select the country
* I wait to see the home

@sidebar_check @Calabash_Tests @teste_init
Scenario: Check the sidebar layout
* I wait to see the home
* I open the navigation menu
* I wait for 5 seconds
* I should see the sidebar

##

#@server_check
#Scenario: Check selected server
#* I select the country
#* I should see the corresponding server

#@language_check
#Scenario: Check language
#* I select the country
#* I open the navigation menu
#* I should see the login button