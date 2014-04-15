#
# Multisteps
#
# 
 
Then /^I Login$/ do 
  step "I open the navigation menu"
  step "I choose the Sign In option"
  step "I wait for 5 seconds"
  step "I enter a valid username"
  step "I enter the password"
  step "I press Login Button"
  step "I wait for 5 seconds"
  step "I open the navigation menu"
  step "I wait for 5 seconds"
  step "I should see sign out button"
  step "I close the navigation menu"
end

Then /^I add a product$/ do
  step "I open the navigation menu"
  step "I enter Categories"
  step "I enter a valid Category"
  step "I press list item number 1"
  step "I wait for 1 seconds" 
  step "I press list item number 4"
  step "I wait for 3 seconds"
  step "I press Got it"
  step "I add product to cart"
  step "I wait for 5 seconds"    
  step "I should see the item was added to shopping cart message"
  step "I go to cart"
  step "I open the navigation menu"
  step "I go to home"
end
 
