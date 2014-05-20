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
  performAction('enter_text_into_id_field',"", @search_input.to_s)
  performAction('enter_text_into_id_field',@search_p.to_s, @search_input.to_s)
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

Then /^I enter (a fake email|my email) on password recovery$/ do |email|
  case email.to_s
  when "a fake email"
    performAction('enter_text_into_named_field', @wrong_username, @login_username.to_s)  
  when "my email"
    performAction('enter_text_into_named_field', @username, @login_username.to_s)
  end
end

Then /^I enter a (valid|invalid) track order number$/ do |valid|
  case valid.to_s
  when "valid"
    performAction('enter_text_into_id_field', "", "order_nr_edittext")  
    performAction('enter_text_into_id_field', @valid_order, "order_nr_edittext")  
  when "invalid"
    performAction('enter_text_into_id_field', "", "order_nr_edittext")
    performAction('enter_text_into_id_field', "invalid", "order_nr_edittext")  
  end
end

Then /^I enter a variation search$/ do 
  performAction('enter_text_into_id_field',"", "search_component")
  performAction('enter_text_into_id_field',@search_v.to_s, "search_component")
end

Then /^I enter a rated search$/ do 
  performAction('enter_text_into_id_field',"", "search_component")
  performAction('enter_text_into_id_field',@search_r.to_s, "search_component")
end

Then /^I fill the review information$/ do

  performAction('enter_text_into_named_field', @firstname, @review_name)
  performAction('enter_text_into_named_field', @review_title_t, @review_title)
  performAction('enter_text_into_named_field', @review_comment_t, @review_comment)
end

Then /^I enter a color search$/ do 
  performAction('enter_text_into_named_field',"", @search_input.to_s)
  performAction('enter_text_into_named_field',@search_color.to_s, @search_input.to_s)
end

Then /^I write a valid result on the search bar$/ do
  performAction('clear_id_field', "search_component")
  performAction('enter_text_into_id_field',"surf", "search_component")
end

Then /^I write a invalid result on the search bar$/ do
  performAction('clear_id_field', "search_component")
  performAction('enter_text_into_id_field',"testeasdasdasdifgasldjkhalsjkdhalksjdh", "search_component")
end

Then /^I write a valid email on the newsletter subscription field$/ do
  performAction('clear_id_field', "newsletter_subscription_value")
  performAction('enter_text_into_id_field',"testeasdasdasdifgasldjkhalsjkdhalksjdh", "newsletter_subscription_value")
end
