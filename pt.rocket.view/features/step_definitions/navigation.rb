#
# NAVIGATION
#
# drag

Then /^I swipe right moving with (\d+) steps$/ do |steps|
  performAction('drag',90,10,50,50,steps)
end

Then /^I swipe left moving with (\d+) steps$/ do |steps|
  performAction('drag',10,90,50,50,steps)
end

Then /^I swipe up moving with (\d+) steps$/ do |steps|
  performAction('drag',50,50,20,90,steps)
end

Then /^I swipe down moving with (\d+) steps$/ do |steps|
  performAction('drag',50,50,90,20,steps)
end

#width:height
  #<-left
  #* I drag from 10:50 to 90:50 moving with 500 steps
  #right->
  #* I drag from 90:50 to 10:50 moving with 10 steps
   