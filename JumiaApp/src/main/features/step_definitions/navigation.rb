#
# NAVIGATION
#
# drag

Then /^I swipe left moving with (\d+) steps$/ do |steps|
  perform_action('drag',90,10,50,50,steps)
end

Then /^I swipe left on botton moving with (\d+) steps$/ do |steps|
  perform_action('drag',90,10,80,80,steps)
end

Then /^I swipe right moving with (\d+) steps$/ do |steps|
  perform_action('drag',5,90,50,50,steps)
end

Then /^I swipe down moving with (\d+) steps$/ do |steps|
  perform_action('drag',50,50,20,90,steps)
end

Then /^I swipe up moving with (\d+) steps$/ do |steps|
  perform_action('drag',50,50,80,20,steps)
end

Then /^I click in the image$/ do
  perform_action('click_on_screen',50, 35)
end

Then /^I click on search image$/ do
  perform_action('click_on_screen',90, 17)
end