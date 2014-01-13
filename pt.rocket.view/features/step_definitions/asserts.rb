#
# ASSERTS
#
# assert_text
 
Then /^I should see the generated address$/ do 
  performAction('assert_text', @address.to_s, true) 
end
 
Then /^I should see my first name$/ do 
  performAction('assert_text', @firstname.to_s, true) 
end

Then /^I should see my email$/ do 
  performAction('assert_text', @username.to_s, true) 
end

Then /^I should see the email error message$/ do 
  performAction('assert_text', @emailerrormessage.to_s, true) 
end

Then /^I should see the password error message$/ do 
  performAction('assert_text', @passerrormessage.to_s, true) 
end

Then /^I should see the login error message$/ do 
  performAction('assert_text', @loginerror.to_s, true) 
end

Then /^I should see sign out button$/ do 
  performAction('assert_text', @signout.to_s, true) 
end

Then /^I should see the mandatory fields error message$/ do 
  performAction('assert_text', @mandatory.to_s, true) 
end

Then /^I should see the email exists error message$/ do 
  performAction('assert_text', @sameemail.to_s, true) 
end

Then /^I should see the passwords dont match error message$/ do
  performAction('assert_text', @differentpassword.to_s, true)
end

Then /^I should see the search message$/ do 
  performAction('assert_text', @searchdefault.to_s, true) 
end

Then /^I should see the no suggestion message$/ do 
  performAction('assert_text', @nosuggest.to_s, true) 
end

Then /^I should see the add to cart button$/ do 
  performAction('assert_text', @addtocart.to_s, true) 
end

Then /^I should see the login button$/ do
  performAction('assert_text', @sign_in.to_s, true)
end

Then /^I should see the Terms and Conditions$/ do
  performAction('assert_text', @termsandconditions.to_s, true)
end

Then /^I should see the password changed with success message$/ do
  performAction('assert_text', @password_changed_message.to_s, true)
end