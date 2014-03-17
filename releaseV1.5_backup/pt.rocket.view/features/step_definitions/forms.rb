#
# FORMS
#
# enter_text_into_named_field

Then /^I enter a random number into "([^\"]*)"$/ do |name|
  @unique_random_number= rand(899999999) + 100000000
  performAction('enter_text_into_named_field',@unique_random_number.to_s, name)
end

Then /^I enter a random email into "([^\"]*)"$/ do |name|
  @unique_random_email= rand(899999999) + 100000000
  @email='Tester' + @unique_random_email.to_s + '@mailinator.com'
  performAction('enter_text_into_named_field',@email.to_s, name)
end

Then /^I enter a random address into "([^\"]*)"$/ do |name|
  @unique_random_address= rand(89) + 10
  @address='Tester Street ' + @unique_random_address.to_s 
  performAction('enter_text_into_named_field',@address.to_s, name)
end

Then /^I enter a wrong username$/ do 
  performAction('enter_text_into_named_field',"", @login_username.to_s)
  performAction('enter_text_into_named_field',@wrong_username.to_s, @login_username.to_s)
end

Then /^I enter a valid username$/ do 
  performAction('enter_text_into_named_field',"", @login_username.to_s)
  performAction('enter_text_into_named_field',@username.to_s, @login_username.to_s)
end

Then /^I enter a random email$/ do 
  @unique_random_email= rand(899999999) + 100000000
  @email='Tester' + @unique_random_email.to_s + '@mailinator.com'
  performAction('enter_text_into_named_field',"", @login_username.to_s)
  performAction('enter_text_into_named_field',@email, @login_username.to_s)
end

Then /^I enter the password$/ do 
  performAction('enter_text_into_named_field',"", @login_password.to_s)
  performAction('enter_text_into_named_field',@password.to_s, @login_password.to_s)
end

Then /^I enter the wrong password$/ do
  performAction('enter_text_into_named_field',"", @login_password.to_s)
  performAction('enter_text_into_named_field',@wrong_password, @login_password.to_s)
end

Then /^I enter the repeated password$/ do 
  performAction('enter_text_into_named_field',"", @register_password2.to_s)
  performAction('enter_text_into_named_field',@password.to_s, @register_password2.to_s)
end

Then /^I enter the wrong repeated password$/ do 
  performAction('enter_text_into_named_field',"", @register_password2.to_s)
  performAction('enter_text_into_named_field',@password.to_s+"x", @register_password2.to_s)
end

Then /^I enter the first name$/ do 
  performAction('enter_text_into_named_field',"", @register_first_name.to_s)
  performAction('enter_text_into_named_field',@firstname.to_s, @register_first_name.to_s)
end

Then /^I enter the last name$/ do 
  performAction('enter_text_into_named_field',"", @register_last_name.to_s)
  performAction('enter_text_into_named_field',@lastname.to_s, @register_last_name.to_s)
end

Then /^I enter a valid search$/ do 
  performAction('enter_text_into_named_field',"", @search_input.to_s)
  performAction('enter_text_into_named_field',@search.to_s, @search_input.to_s)
end

Then /^I enter a invalid search$/ do 
  performAction('enter_text_into_named_field',"", @search_input.to_s)
  performAction('enter_text_into_named_field',@invalidsearch.to_s, @search_input.to_s)
end

Then /^I enter the new password$/ do 
  performAction('enter_text_into_named_field',"", @newpassword.to_s)
  performAction('enter_text_into_named_field',@password.to_s, @newpassword.to_s)
end

Then /^I enter the new repeated password$/ do 
  performAction('enter_text_into_named_field',"", @newrepeatedpassword.to_s)
  performAction('enter_text_into_named_field',@password.to_s, @newrepeatedpassword.to_s)
end

Then /^I enter a wrong repeated password$/ do 
  performAction('enter_text_into_named_field',"", @newrepeatedpassword.to_s)
  performAction('enter_text_into_named_field',@password.to_s+"x", @newrepeatedpassword.to_s)
end

