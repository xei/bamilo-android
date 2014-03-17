require 'calabash-android/management/adb'
require 'calabash-android/operations'


Then /^I install the app$/ do
  reinstall_apps()
end

Then /^I start the app$/ do
  start_test_server_in_background
end

Then /^I shutdown the app$/ do
  shutdown_test_server
end

Then /^I call the variables$/ do  
  initvars
  #puts $country
end