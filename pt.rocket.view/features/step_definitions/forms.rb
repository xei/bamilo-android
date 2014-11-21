#
# FORMS
#
# perform_action('enter_text_into_named_field', 'foo', 'name') is now 
# enter_text("android.widget.EditText marked:'name'", 'foo')
#
# perform_action('clear_id_field', 'my_id') is now
# clear_text("android.widget.EditText id:'my_id'")

Then /^I enter a random number into "([^\"]*)"$/ do |name|
  @unique_random_number= rand(899999999) + 100000000
  enter_text("android.widget.EditText marked:'"+name+"'", @unique_random_number.to_s)
#  performAction('enter_text_into_named_field',@unique_random_number.to_s, name)
end

Then /^I enter a random email into "([^\"]*)"$/ do |name|
  @unique_random_email= rand(899999999) + 100000000
  @email='Tester' + @unique_random_email.to_s + '@mailinator.com'
  enter_text("android.widget.EditText marked:'"+name+"'", @email.to_s)
#  performAction('enter_text_into_named_field',@email.to_s, name)
end

Then /^I enter a random address into "([^\"]*)"$/ do |name|
  @unique_random_address= rand(89) + 10
  @address='Tester Street ' + @unique_random_address.to_s 
  enter_text("android.widget.EditText marked:'"+name+"'", @address.to_s)
#  performAction('enter_text_into_named_field',@address.to_s, name)
end

Then /^I enter a wrong username$/ do 
  enter_text("android.widget.EditText marked:'"+@login_username.to_s+"'", '')
#  performAction('enter_text_into_named_field',"", @login_username.to_s)
  enter_text("android.widget.EditText marked:'"+@login_username.to_s+"'", @wrong_username.to_s)
#  performAction('enter_text_into_named_field',@wrong_username.to_s, @login_username.to_s)
end

Then /^I enter a valid username$/ do 
  enter_text("android.widget.EditText marked:'"+@login_username.to_s+"'", '')
#  performAction('enter_text_into_named_field',"", @login_username.to_s)
  enter_text("android.widget.EditText marked:'"+@login_username.to_s+"'", @username.to_s)
#  performAction('enter_text_into_named_field',@username.to_s, @login_username.to_s)
end

Then /^I enter a random email$/ do 
  @unique_random_email= rand(899999999) + 100000000
  @email='Tester' + @unique_random_email.to_s + '@mailinator.com'
  enter_text("android.widget.EditText marked:'"+@login_username.to_s+"'", '')
#  performAction('enter_text_into_named_field',"", @login_username.to_s)
  enter_text("android.widget.EditText marked:'"+@login_username.to_s+"'", @email)
#  performAction('enter_text_into_named_field',@email, @login_username.to_s)
end

Then /^I enter the password$/ do 
  enter_text("android.widget.EditText marked:'"+@login_password.to_s+"'", '')
#  performAction('enter_text_into_named_field',"", @login_password.to_s)
  enter_text("android.widget.EditText marked:'"+@login_password.to_s+"'", @password.to_s)
#  performAction('enter_text_into_named_field',@password.to_s, @login_password.to_s)
end

Then /^I enter the wrong password$/ do
  enter_text("android.widget.EditText marked:'"+@login_password.to_s+"'", '')
#  performAction('enter_text_into_named_field',"", @login_password.to_s)
  enter_text("android.widget.EditText marked:'"+@login_password.to_s+"'", @wrong_password)
#  performAction('enter_text_into_named_field',@wrong_password, @login_password.to_s)
end

Then /^I enter the repeated password$/ do 
  enter_text("android.widget.EditText marked:'"+@register_password2.to_s+"'", '')
#  performAction('enter_text_into_named_field',"", @register_password2.to_s)
  enter_text("android.widget.EditText marked:'"+@register_password2.to_s+"'", @password.to_s)
#  performAction('enter_text_into_named_field',@password.to_s, @register_password2.to_s)
end

Then /^I enter the wrong repeated password$/ do 
  enter_text("android.widget.EditText marked:'"+@register_password2.to_s+"'", '')
#  performAction('enter_text_into_named_field',"", @register_password2.to_s)
  enter_text("android.widget.EditText marked:'"+@register_password2.to_s+"'", @password.to_s+"x")
#  performAction('enter_text_into_named_field',@password.to_s+"x", @register_password2.to_s)
end

Then /^I enter the first name$/ do 
  enter_text("android.widget.EditText marked:'"+@register_first_name.to_s+"'", '')
#  performAction('enter_text_into_named_field',"", @register_first_name.to_s)
  enter_text("android.widget.EditText marked:'"+@register_first_name.to_s+"'", @firstname.to_s)
#  performAction('enter_text_into_named_field',@firstname.to_s, @register_first_name.to_s)
end

Then /^I enter the last name$/ do 
  enter_text("android.widget.EditText marked:'"+@register_last_name.to_s+"'", '')
#  performAction('enter_text_into_named_field',"", @register_last_name.to_s)
  enter_text("android.widget.EditText marked:'"+@register_last_name.to_s+"'", @lastname.to_s)
#  performAction('enter_text_into_named_field',@lastname.to_s, @register_last_name.to_s)
end

Then /^I enter a valid search$/ do 
  enter_text("android.widget.EditText marked:'"+@search_input.to_s+"'", '')
#  performAction('enter_text_into_id_field',"", @search_input.to_s)
  enter_text("android.widget.EditText marked:'"+@search_input.to_s+"'", @search_p.to_s)
#  performAction('enter_text_into_id_field',@search_p.to_s, @search_input.to_s)
end

Then /^I enter a invalid search$/ do 
  clear_text("android.widget.EditText id:'abs__search_src_text'")
#  performAction('clear_id_field', "abs__search_src_text" )
  enter_text("android.widget.EditText marked:'abs__search_src_text'", @invalidsearch.to_s)
  performAction('enter_text_into_id_field',@invalidsearch.to_s, "abs__search_src_text")
end

Then /^I enter the new password$/ do 
  enter_text("android.widget.EditText marked:'"+@newpassword.to_s+"'", '')
#  performAction('enter_text_into_named_field',"", @newpassword.to_s)
  enter_text("android.widget.EditText marked:'"+@newpassword.to_s+"'", @password.to_s)
#  performAction('enter_text_into_named_field',@password.to_s, @newpassword.to_s)
end

Then /^I enter the new repeated password$/ do 
  enter_text("android.widget.EditText marked:'"+@newrepeatedpassword.to_s+"'", '')
#  performAction('enter_text_into_named_field',"", @newrepeatedpassword.to_s)
  enter_text("android.widget.EditText marked:'"+@newrepeatedpassword.to_s+"'", @password.to_s)
#  performAction('enter_text_into_named_field',@password.to_s, @newrepeatedpassword.to_s)
end

Then /^I enter a wrong repeated password$/ do 
  enter_text("android.widget.EditText marked:'"+@newrepeatedpassword.to_s+"'", '')
#  performAction('enter_text_into_named_field',"", @newrepeatedpassword.to_s)
  enter_text("android.widget.EditText marked:'"+@newrepeatedpassword.to_s+"'", @password.to_s+"x")
#  performAction('enter_text_into_named_field',@password.to_s+"x", @newrepeatedpassword.to_s)
end

Then /^I enter (a fake email|my email) on password recovery$/ do |email|
  case email.to_s
  when "a fake email"
    enter_text("android.widget.EditText marked:'"+@login_username.to_s+"'", @wrong_username)
#    performAction('enter_text_into_named_field', @wrong_username, @login_username.to_s)  
  when "my email"
    enter_text("android.widget.EditText marked:'"+@login_username.to_s+"'", @username)
#    performAction('enter_text_into_named_field', @username, @login_username.to_s)
  end
end

Then /^I enter a (valid|invalid) track order number$/ do |valid|
  case valid.to_s
  when "valid"
    enter_text("android.widget.EditText marked:'order_nr_edittext'", '')
#    performAction('enter_text_into_id_field', "", "order_nr_edittext")
    enter_text("android.widget.EditText marked:'order_nr_edittext'", @valid_order)  
#    performAction('enter_text_into_id_field', @valid_order, "order_nr_edittext")  
  when "invalid"
    enter_text("android.widget.EditText marked:'order_nr_edittext'", '')
#    performAction('enter_text_into_id_field', "", "order_nr_edittext")
    enter_text("android.widget.EditText marked:'order_nr_edittext'", 'invalid')
#    performAction('enter_text_into_id_field', "invalid", "order_nr_edittext")  
  end
end

Then /^I enter a variation search$/ do 
  enter_text("android.widget.EditText marked:'abs__search_src_text'", '')
#  performAction('enter_text_into_id_field',"", "abs__search_src_text")
  enter_text("android.widget.EditText marked:'abs__search_src_text'", @search_v.to_s)
#  performAction('enter_text_into_id_field',@search_v.to_s, "abs__search_src_text")
end

Then /^I enter a rated search$/ do 
  enter_text("android.widget.EditText marked:'abs__search_src_text'", '')
#  performAction('enter_text_into_id_field',"", "abs__search_src_text")
  enter_text("android.widget.EditText marked:'abs__search_src_text'", @search_r.to_s)
#  performAction('enter_text_into_id_field',@search_r.to_s, "abs__search_src_text")
end

Then /^I fill the review information$/ do
  enter_text("android.widget.EditText marked:'"+@review_name+"'", @firstname)
#  performAction('enter_text_into_named_field', @firstname, @review_name)
  enter_text("android.widget.EditText marked:'"+@review_title+"'", @review_title_t)
#  performAction('enter_text_into_named_field', @review_title_t, @review_title)
  enter_text("android.widget.EditText marked:'"+@review_comment+"'", @review_comment_t)
#  performAction('enter_text_into_named_field', @review_comment_t, @review_comment)
end

Then /^I enter a color search$/ do 
  enter_text("android.widget.EditText marked:'"+@search_input.to_s+"'", '')
#  performAction('enter_text_into_named_field',"", @search_input.to_s)
  enter_text("android.widget.EditText marked:'"+@search_input.to_s+"'", @search_color.to_s)
#  performAction('enter_text_into_named_field',@search_color.to_s, @search_input.to_s)
end

Then /^I write a valid result on the search bar$/ do
  clear_text("android.widget.EditText id:'abs__search_src_text'")
#  performAction('clear_id_field', "abs__search_src_text")
  enter_text("android.widget.EditText marked:'abs__search_src_text'", @search_word)
#  performAction('enter_text_into_id_field',@search_word, "abs__search_src_text")
end

Then /^I write a invalid result on the search bar$/ do
  clear_text("android.widget.EditText id:'abs__search_src_text'")
#  performAction('clear_id_field', "abs__search_src_text")
  enter_text("android.widget.EditText marked:'abs__search_src_text'", 'testeasdasdasdifgasldjkhalsjkdhalksjdh')
#  performAction('enter_text_into_id_field',"testeasdasdasdifgasldjkhalsjkdhalksjdh", "abs__search_src_text")
end

Then /^I write a valid email on the newsletter subscription field$/ do
  clear_text("android.widget.EditText id:'abs__search_src_text'")
#  performAction('clear_id_field', "newsletter_subscription_value")
  enter_text("android.widget.EditText marked:'newsletter_subscription_value'", 'testeasdasdasdifgasldjkhalsjkdhalksjdh')
#  performAction('enter_text_into_id_field',"testeasdasdasdifgasldjkhalsjkdhalksjdh", "newsletter_subscription_value")
end

Then /^I enter a valid search on the text field$/ do
  enter_text("android.widget.EditText marked:'abs__search_src_text'", @search_p)
#  performAction('enter_text_into_id_field', @search_p, "abs__search_src_text")
end

Then /^I fill the new address form$/ do
  enter_text("android.widget.EditText marked:'first_name'", 'test')
  enter_text("android.widget.EditText marked:'last_name'", 'test')
  enter_text("android.widget.EditText marked:'address1'", 'test')
  enter_text("android.widget.EditText marked:'address2'", 'test')
  enter_text("android.widget.EditText marked:'phone'", '912345678')
  enter_text("android.widget.EditText marked:'city'", 'test')
  
end

Then /^I enter the a new email$/ do
  number = rand(10000)
  enter_text("android.widget.EditText {contentDescription CONTAINS 'email'}", 'testcalabash'+number.to_s+'@mailinator.com')
end