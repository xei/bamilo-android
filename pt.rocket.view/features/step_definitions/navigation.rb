#
# NAVIGATION
#
# drag

Then /^I swipe left moving with (\d+) steps$/ do |steps|
  performAction('drag',90,10,50,50,steps)
end

Then /^I swipe right moving with (\d+) steps$/ do |steps|
  performAction('drag',5,90,50,50,steps)
end

Then /^I swipe down moving with (\d+) steps$/ do |steps|
  performAction('drag',50,50,20,90,steps)
end

Then /^I swipe up moving with (\d+) steps$/ do |steps|
  performAction('drag',50,50,90,20,steps)
end

Then /^I click in the image$/ do
  performAction('click_on_screen',50, 35)
end