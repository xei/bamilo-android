Then /^I enter a random number into "([^\"]*)"$/ do |name|
  @unique_random_number= rand(899999999) + 100000000
  puts 'random number:'
    puts @unique_random_number
  performAction('enter_text_into_named_field',@unique_random_number.to_s, name)
end

Then /^I enter a random email into "([^\"]*)"$/ do |name|
  @unique_random_email= rand(899999999) + 100000000
  puts 'random email:'
    puts @unique_random_email
  @email='Tester' + @unique_random_email.to_s + '@mailinator.com'
  performAction('enter_text_into_named_field',@email.to_s, name)
end

Then /^I enter a random address into "([^\"]*)"$/ do |name|
  @unique_random_address= rand(89) + 10
  puts 'random email:'
    puts @unique_random_address
  @address='Tester Street ' + @unique_random_address.to_s 
  performAction('enter_text_into_named_field',@address.to_s, name)
end

Then /^I should see the generated address$/ do 
  puts 'random address:'
    puts @address
  performAction('assert_text', @address.to_s, true) 
end


#####################################################

Then /^I open the navigation menu$/ do 
  performAction('click_on_view_by_id',@navigation.to_s)
end


Then /^I choose the Sign In option$/ do
  
  performAction('click_on_text',@sign_in.to_s)
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
  puts 'random email:'
    puts @unique_random_email
  @email='Tester' + @unique_random_email.to_s + '@mailinator.com'
 performAction('enter_text_into_named_field',"", @login_username.to_s)
 performAction('enter_text_into_named_field',@email, @login_username.to_s)

end

Then /^I enter the password$/ do 
  
  performAction('enter_text_into_named_field',"", @login_password.to_s)
  performAction('enter_text_into_named_field',@password.to_s, @login_password.to_s)

end

Then /^I enter the repeated password$/ do 
  
  performAction('enter_text_into_named_field',"", @register_password2.to_s)
  performAction('enter_text_into_named_field',@password.to_s, @register_password2.to_s)

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

Then /^I go to cart$/ do 
  performAction('press',@gotocart.to_s)
end

Then /^I enter the new password$/ do 
  
  performAction('enter_text_into_named_field',"", @newpassword.to_s)
  performAction('enter_text_into_named_field',@password.to_s, @newpassword.to_s)

end

Then /^I enter the new repeated password$/ do 
  
  performAction('enter_text_into_named_field',"", @newrepeatedpassword.to_s)
  performAction('enter_text_into_named_field',@password.to_s, @newrepeatedpassword.to_s)

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

Then /^I should see the search message$/ do 

  performAction('assert_text', @searchdefault.to_s, true) 
end

Then /^I should see the no suggestion message$/ do 

  performAction('assert_text', @nosuggest.to_s, true) 
end

Then /^I should see the add to cart button$/ do 

  performAction('assert_text', @addtocart.to_s, true) 
end



Then /^I proceed to checkout$/ do 
  performAction('press',@proceedcheckout.to_s)
end

Then /^I scroll to next button$/ do 
#  performAction('execute_javascript',"(document.getElementById('<billingbtn>')).scrollIntoView(true)")
  performAction('scroll_down')
#  performAction('scroll_to','css','button[id=billingbtn]')
#    performAction('scroll_to','webView','button[id=billingbtn]')
#    performAction('scroll_to','webView','button[id=billingbtn]')
end





Then /^I proceed to the next step of the checkout staging$/ do 
#  touch("webview css:'//button[id=billingbtn]'")
 # touch("webview css:'//a[name=submit]'")
#  touch("webview css:'a[name=submit]'")
 # touch("webview css:'a[name=submit]'")
  #touch("webview css:'a:contains(“^Next$”)")
 # performAction("touch", 'css', 'a[text=Next]')
#  performAction('touch',@checkoutNext.to_s)
#  touch("webview css:'a[id=billingbtn]'")
#  touch("webview css:'a[id=shippingbtn]'")
#  touch("webview css:'a[id=paymentbtn]'")
#  touch("webview css:'a[id=paymentmethodbtn]'")
  
# performAction('touch','webView','button[id=billingbtn]')
#  performAction('touch','webView','button[id=billingbtn]')
  
# query("webView css:'button[id=billingbtn]'").first

  touch("webView css:'#billingbtn'")

#  touch %Q{webView css:'button[id="billingbtn"]'}
#  touch %Q{webView css:'#container button[id="card_link"]'}
#  query("webView css:'*'") 
#  performAction("dump_html")
end


Then /^I choose the test payment method$/ do 
 
  performAction('touch','webView','input[id=testpayment]')
  performAction('touch','webView','input[value=TestPayment]')
#  performAction('click_on_text',@checkout1.to_s)
  performAction('touch','webView','input[id=cashondelivery]')
  performAction('touch','webView','input[value=CashOnDelivery]')
#  performAction("dump_html")
end

Then /^I choose payment method$/ do 
  performAction("dump_html")
  performAction('touch','webView','button[id=paymentbtn]')
#  performAction('click_on_text',@checkout1.to_s)
end

Then /^I confirm the order$/ do 
  performAction("dump_html")
  performAction('touch','webView','button[id=confirmbtn]')
#  performAction('click_on_text',@checkout3.to_s)
end




#####################################################




Then /^I enter a valid username into "([^\"]*)"$/ do |name|
  
  performAction('enter_text_into_named_field',@username.to_s, name)

end

Then /^I enter a valid password into "([^\"]*)"$/ do |name|
  
  performAction('enter_text_into_named_field',@password.to_s, name)

end

Then /^I enter a valid first name into "([^\"]*)"$/ do |name|
  
  performAction('enter_text_into_named_field',@firstname.to_s, name)

end

Then /^I enter a valid last name into "([^\"]*)"$/ do |name|
  
  performAction('enter_text_into_named_field',@lastname.to_s, name)

end

Then /^I enter a valid mobile number into "([^\"]*)"$/ do |name|
  
  performAction('enter_text_into_named_field',@mobilenumber.to_s, name)

end

Then /^I enter a valid comment into "([^\"]*)"$/ do |name|
  
  performAction('enter_text_into_named_field',@comment.to_s, name)

end


Then /^I enter a valid street into "([^\"]*)"$/ do |name|
  
  performAction('enter_text_into_named_field',@street.to_s, name)

end

###################################################

Then /^I touch the first choice$/ do
  
  performAction('click_on_view_by_id',@firstchoice.to_s)
end

Then /^I touch the second choice$/ do
  
  performAction('click_on_view_by_id',@secondchoice.to_s)
end

Then /^I touch the third choice$/ do
  
  performAction('click_on_view_by_id',@thirdchoice.to_s)
end

Then /^I touch the fourth choice$/ do
  
  performAction('click_on_view_by_id',@forthchoice.to_s)
end

Then /^I touch the fifth choice$/ do
  
  performAction('click_on_view_by_id',@fifthchoice.to_s)
end





Then /^I touch the first topping$/ do
  
  performAction('click_on_view_by_id',@firsttopping.to_s)
end

Then /^I touch the second topping$/ do
  
  performAction('click_on_view_by_id',@secondtopping.to_s)
end

Then /^I touch the third topping$/ do
  
  performAction('click_on_view_by_id',@thirdtopping.to_s)
end

Then /^I touch the fourth topping$/ do
  
  performAction('click_on_view_by_id',@forthtopping.to_s)
end

Then /^I touch the fifth topping$/ do
  
  performAction('click_on_view_by_id',@fifthtopping.to_s)
end