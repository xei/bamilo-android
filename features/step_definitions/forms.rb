#
# FORMS
#
# perform_action('enter_text_into_named_field', 'foo', 'name') is now 
# enter_text("android.widget.EditText marked:'name'", 'foo')
#
# perform_action('clear_id_field', 'my_id') is now
# clear_text("android.widget.EditText id:'my_id'")

Then /^I enter a random number into "([^\"]*)"$/ do |name|
  wait_for_elements_exist(["* marked:'"+name+"'"],:timeout => 30)
  @unique_random_number= rand(899999999) + 100000000
  enter_text("android.widget.EditText marked:'"+name+"'", @unique_random_number.to_s)
end

Then /^I enter a random email into "([^\"]*)"$/ do |name|
  wait_for_elements_exist(["* marked:'"+name+"'"],:timeout => 30)
  @unique_random_email= rand(899999999) + 100000000
  @email='Tester' + @unique_random_email.to_s + '@mailinator.com'
  enter_text("android.widget.EditText marked:'"+name+"'", @email.to_s)
end

Then /^I enter a random billingAddress into "([^\"]*)"$/ do |name|
  wait_for_elements_exist(["* marked:'"+@billingAddress.to_s+"'"],:timeout => 30)
  @unique_random_address= rand(89) + 10
  @billingAddress='Tester Street ' + @unique_random_address.to_s
  enter_text("android.widget.EditText marked:'"+name+"'", @billingAddress.to_s)
end

Then /^I enter a wrong username$/ do 
  wait_for_elements_exist(["* marked:'"+@login_username.to_s+"'"],:timeout => 30)
  enter_text("android.widget.EditText marked:'"+@login_username.to_s+"'", '')
  enter_text("android.widget.EditText marked:'"+@login_username.to_s+"'", @wrong_username.to_s)
end

Then /^I enter a valid username$/ do 
  wait_for_elements_exist(["* marked:'"+@login_username.to_s+"'"],:timeout => 30)
  enter_text("android.widget.EditText marked:'"+@login_username.to_s+"'", '')
  enter_text("android.widget.EditText marked:'"+@login_username.to_s+"'", @username.to_s)
end

Then /^I enter a random email$/ do 
  wait_for_elements_exist(["* marked:'"+@login_username.to_s+"'"],:timeout => 30)
  @unique_random_email= rand(899999999) + 100000000
  @email='Tester' + @unique_random_email.to_s + '@mailinator.com'
  enter_text("android.widget.EditText marked:'"+@login_username.to_s+"'", @email)
end

Then /^I enter the password$/ do 
  wait_for_elements_exist(["* marked:'"+@login_password.to_s+"'"],:timeout => 30)
  enter_text("android.widget.EditText marked:'"+@login_password.to_s+"'", @password.to_s)
end

Then /^I enter the wrong password$/ do
  wait_for_elements_exist(["* marked:'"+@login_password.to_s+"'"],:timeout => 30)
  enter_text("android.widget.EditText marked:'"+@login_password.to_s+"'", @wrong_password)
end

Then /^I enter the repeated password$/ do 
  wait_for_elements_exist(["* marked:'"+@register_password2.to_s+"'"],:timeout => 30)
  enter_text("android.widget.EditText marked:'"+@register_password2.to_s+"'", @password.to_s)
end

Then /^I enter the wrong repeated password$/ do 
  wait_for_elements_exist(["* marked:'"+@register_password2.to_s+"'"],:timeout => 30)
  enter_text("android.widget.EditText marked:'"+@register_password2.to_s+"'", @password.to_s+"x")
end

Then /^I enter the first name$/ do 
  wait_for_elements_exist(["* marked:'"+@register_first_name.to_s+"'"],:timeout => 30)
  enter_text("android.widget.EditText marked:'"+@register_first_name.to_s+"'", @firstname.to_s)
end

Then /^I enter the last name$/ do 
  wait_for_elements_exist(["* marked:'"+@register_last_name.to_s+"'"],:timeout => 30)
  enter_text("android.widget.EditText marked:'"+@register_last_name.to_s+"'", @lastname.to_s)
end

Then /^I enter a valid search$/ do 
  wait_for_elements_exist(["* marked:'"+@search_input.to_s+"'"],:timeout => 30)
  enter_text("android.widget.EditText marked:'"+@search_input.to_s+"'", '')
  enter_text("android.widget.EditText marked:'"+@search_input.to_s+"'", @search_p.to_s)
end

Then /^I enter a invalid search$/ do 
  wait_for_elements_exist(["* marked:'"+@search_input.to_s+"'"],:timeout => 30)
  clear_text_in("android.widget.EditText marked:'"+@search_input.to_s+"'")
  enter_text("android.widget.EditText marked:'"+@search_input.to_s+"'", @invalidsearch.to_s)
end

Then /^I enter the new password$/ do 
  wait_for_elements_exist(["* marked:'"+@newpassword.to_s+"'"],:timeout => 30)
  clear_text_in("android.widget.EditText marked:'"+@newpassword.to_s+"'")
  enter_text("android.widget.EditText marked:'"+@newpassword.to_s+"'", @password.to_s)
end

Then /^I enter the new repeated password$/ do 
  wait_for_elements_exist(["* marked:'"+@newrepeatedpassword.to_s+"'"],:timeout => 30)
  clear_text_in("android.widget.EditText marked:'"+@newrepeatedpassword.to_s+"'")
  enter_text("android.widget.EditText marked:'"+@newrepeatedpassword.to_s+"'", @password.to_s)
end

Then /^I enter a wrong repeated password$/ do 
  wait_for_elements_exist(["* marked:'"+@newrepeatedpassword.to_s+"'"],:timeout => 30)
  clear_text_in("android.widget.EditText marked:'"+@newrepeatedpassword.to_s+"'")
  enter_text("android.widget.EditText marked:'"+@newrepeatedpassword.to_s+"'", @password.to_s+"x")
end

Then /^I enter (a fake email|my email) on password recovery$/ do |email|
  wait_for_elements_exist(["* marked:'"+@login_username.to_s+"'"],:timeout => 30)
  case email.to_s
  when "a fake email"
    enter_text("android.widget.EditText marked:'"+@login_username.to_s+"'", @wrong_username)
  when "my email"
    enter_text("android.widget.EditText marked:'"+@login_username.to_s+"'", @username)
  end
end

Then /^I enter a (valid|invalid) track order number$/ do |valid|
  wait_for_elements_exist(["* marked:'order_nr'"],:timeout => 30)
  clear_text_in("android.widget.EditText marked:'order_nr'")
  case valid.to_s
  when "valid"
    enter_text("android.widget.EditText marked:'order_nr'", @valid_order)  
  when "invalid"
    enter_text("android.widget.EditText marked:'order_nr'", 'invalid')
  end
end

Then /^I enter a variation search$/ do 
  wait_for_elements_exist(["* marked:'abs__search_src_text'"],:timeout => 30)
  clear_text_in("android.widget.EditText marked:'abs__search_src_text'")
  enter_text("android.widget.EditText marked:'abs__search_src_text'", @search_v.to_s)
end

Then /^I enter a rated search$/ do 
  wait_for_elements_exist(["* marked:'abs__search_src_text'"],:timeout => 30)
  clear_text_in("android.widget.EditText marked:'abs__search_src_text'")
  enter_text("android.widget.EditText marked:'abs__search_src_text'", @search_r.to_s)
end

Then /^I fill the review information$/ do
  wait_for_elements_exist(["* marked:'"+@review_name+"'"],:timeout => 30)
  enter_text("android.widget.EditText marked:'"+@review_name+"'", @firstname)
  enter_text("android.widget.EditText marked:'"+@review_title+"'", @review_title_t)
  enter_text("android.widget.EditText marked:'"+@review_comment+"'", @review_comment_t)
end

Then /^I enter a color search$/ do 
  wait_for_elements_exist(["* marked:'"+@search_input.to_s+"'"],:timeout => 30)
  clear_text_in("android.widget.EditText marked:'"+@search_input.to_s+"'")
  enter_text("android.widget.EditText marked:'"+@search_input.to_s+"'", @search_color.to_s)
end

Then /^I write a valid result on the search bar$/ do
  wait_for_elements_exist(["* marked:'abs__search_src_text'"],:timeout => 30)
  clear_text_in("android.widget.EditText id:'abs__search_src_text'")
  enter_text("android.widget.EditText marked:'abs__search_src_text'", @search_word)
end

Then /^I write a invalid result on the search bar$/ do
  wait_for_elements_exist(["* marked:'abs__search_src_text'"],:timeout => 30)
  clear_text_in("android.widget.EditText id:'abs__search_src_text'")
  enter_text("android.widget.EditText marked:'abs__search_src_text'", 'testeasdasdasdifgasldjkhalsjkdhalksjdh')
end

Then /^I write a valid email on the newsletter subscription field$/ do
  wait_for_elements_exist(["* marked:'abs__search_src_text'"],:timeout => 30)
  clear_text_in("android.widget.EditText id:'abs__search_src_text'")
  enter_text("android.widget.EditText marked:'newsletter_subscription_value'", 'testeasdasdasdifgasldjkhalsjkdhalksjdh')
end

Then /^I enter a valid search on the text field$/ do
  enter_text("android.widget.EditText marked:'abs__search_src_text'", @search_p)
end

Then /^I fill the new billingAddress form$/ do
  wait_for_elements_exist(["* marked:'first_name'"],:timeout => 30)
  enter_text("android.widget.EditText marked:'first_name'", 'test')
  step "I swipe down moving with 2 steps"
  enter_text("android.widget.EditText marked:'last_name'", 'test')
  step "I swipe down moving with 2 steps"
  enter_text("android.widget.EditText marked:'address1'", 'test')
  step "I swipe down moving with 2 steps"
  enter_text("android.widget.EditText marked:'address2'", 'testing')
  step "I swipe down moving with 2 steps"
  enter_text("android.widget.EditText marked:'phone'", @phone_nr.to_s)
  #enter_text("android.widget.EditText marked:'city'", 'test')
end

Then /^I enter the a new email$/ do
  wait_for_elements_exist(["* {contentDescription CONTAINS 'email'}"],:timeout => 30)
  number = rand(10000)
  enter_text("android.widget.EditText {contentDescription CONTAINS 'email'}", 'testcalabash'+number.to_s+'@mailinator.com')
end

Then /^I clean the Edit Address Form$/ do
  wait_for_elements_exist(["* marked:'first_name'"],:timeout => 30)
  clear_text_in("android.widget.EditText marked:'first_name'")
  step "I swipe down moving with 2 steps"
  clear_text_in("android.widget.EditText marked:'last_name'")
  step "I swipe down moving with 2 steps"
  clear_text_in("android.widget.EditText marked:'address1'")
  step "I swipe down moving with 2 steps"
  clear_text_in("android.widget.EditText marked:'address2'")
  step "I swipe down moving with 2 steps"
  clear_text_in("android.widget.EditText marked:'phone'")
  #enter_text("android.widget.EditText marked:'city'", '')
end

Then /^I enter a new value in Address$/ do
  wait_for_elements_exist(["android.widget.EditText marked:'address1'"],:timeout => 30)
  if query("EditText marked:'address1'",:text).first == "test"
    clear_text_in("android.widget.EditText marked:'address1'")
    enter_text("android.widget.EditText marked:'address1'", 'testing')
  else
    clear_text_in("android.widget.EditText marked:'address1'")
    enter_text("android.widget.EditText marked:'address1'", 'test')
  end

end

Then /^I enter a valid Voucher$/ do
  wait_for_elements_exist(["android.widget.EditText marked:'voucher_name'"],:timeout => 50)
  enter_text("android.widget.EditText marked:'voucher_name'", @voucher.to_s)
end


Then /^I enter a invalid Voucher$/ do
  wait_for_elements_exist(["android.widget.EditText marked:'voucher_name'"],:timeout => 50)
  enter_text("android.widget.EditText marked:'voucher_name'", @invalid_voucher.to_s)
end

Then /^I enter a the search a$/ do
  wait_for_elements_exist(["* marked:'"+@search_input.to_s+"'"],:timeout => 30)
  enter_text("android.widget.EditText marked:'"+@search_input.to_s+"'", '')
  enter_text("android.widget.EditText marked:'"+@search_input.to_s+"'", 'a')
  press_user_action_button
end
