#
# CLICKS AND PRESS
#
# click_on_view_by_id
# click_on_text
# press

Then /^I go to home$/ do
  performAction('click_on_text',@home.to_s)
end

Then /^I open the navigation menu$/ do 
  performAction('click_on_view_by_id',@navigation.to_s)
end

Then /^I close the navigation menu$/ do 
  performAction('click_on_view_by_id',@navigation.to_s)
end

Then /^I press Login Button$/ do
  performAction('click_on_view_by_id', @login_button_id.to_s)
end

Then /^I press the create account button$/ do
  performAction('click_on_view_by_id', @createaccbutton_id.to_s)
end

Then /^I press the register button$/ do
  performAction('click_on_view_by_id', @registerbutton_id.to_s)
end

Then /^I choose the Sign In option$/ do
  performAction('click_on_text',@sign_in.to_s)
end

Then /^I press Logout Button$/ do
  performAction('click_on_text',@signout.to_s)
  performAction('click_on_text',@yes.to_s)
end

Then /^I press Ok$/ do
  performAction('click_on_text',@ok.to_s)
end

Then /^I enter My Account$/ do 
  performAction('click_on_text',@myaccount.to_s)
end

Then /^I enter My User Data$/ do 
  performAction('click_on_text',@myinfo.to_s)
end

Then /^I enter Categories$/ do 
  performAction('click_on_text',@categories.to_s)
end

Then /^I enter a valid Category$/ do 
  performAction('click_on_text',@categoryfashion.to_s)
end

Then /^I add product to cart$/ do 
  performAction('click_on_text',@addtocart.to_s)
end

Then /^I press Terms and Conditions$/ do
  performAction('click_on_text',@termsandconditions.to_s)
end

Then /^I select the country$/ do
  performAction('click_on_text',@venture_name.to_s)
  puts @venture_name.to_s
end

Then /^I go to cart$/ do 
  performAction('press',@gotocart.to_s)
end

Then /^I proceed to checkout$/ do 
  performAction('press',@proceedcheckout.to_s)
end

Then /^I press male$/ do 
  performAction('press',@male.to_s)
end

Then /^I press birthday$/ do
  performAction('press',@birthday.to_s)
end

Then /^I check Terms and Conditions$/ do
  performAction('press',@termsandconditionscheck.to_s)
end

Then /^I press Save$/ do
  performAction('press', @save.to_s)
end