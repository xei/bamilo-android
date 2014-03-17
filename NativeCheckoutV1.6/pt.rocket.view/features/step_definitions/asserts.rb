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

Then /^I should see the password is to short message$/ do
  performAction('assert_text', @new_pass_short.to_s, true)
end

Then /^I should see the forgot password link$/ do
  performAction('assert_text', @forgot_password.to_s, true)
end

Then /^I should see the password recovery screen$/ do
  performAction('assert_text', @password_recovery.to_s, true)
end

Then /^I should see the password recovery (empty email|email not correct|email sent) message$/ do |message|
  case message.to_s
  when "empty email"
    performAction('assert_text', @pass_rec_empty_email.to_s, true)
  when "not correct"
    performAction('assert_text', @pass_rec_failed.to_s, true)
  when "email sent"
    performAction('assert_text', @pass_rec_sent.to_s, true)
  end
end

Then /^I should see the countries$/ do
  performAction('assert_text', "Nigeria", true)
  performAction('assert_text', "Kenya", true)
  performAction('assert_text', "Morocco", true)
  performAction('assert_text', "Egypt", true)
  performAction('assert_text', "Ivory Coast", true)
  performAction('assert_text', "Uganda", true)
end

Then /^I should see the corresponding server$/ do
  case $country
  when "ic"
    performAction('assert_text', "www.jumia.ci", true)
    puts "www.jumia.ci"
  when "ke"
    performAction('assert_text', "www.jumia.co.ke", true)
    puts "www.jumia.co.ke"
  when "ma"
    performAction('assert_text', "www.jumia.ma", true)
    puts "www.jumia.ma"
  when "ng"
    performAction('assert_text', "www.jumia.com.ng", true)
    puts "www.jumia.co.ng"
  when "eg"
    performAction('assert_text', "www.jumia.com.eg", true)
    puts "www.jumia.com.eg"
  end
end

Then /^I sould see the splash screen$/ do
  performAction('click_on_view_by_id', "imgView")
end

Then /^I should see the home$/ do
  performAction('assert_text', @home.upcase, true)
end

Then /^I should see the sidebar$/ do
  performAction('assert_text', @home.to_s, true)
  performAction('assert_text', @categories.to_s, true)
  performAction('assert_text', @search.to_s, true)
  performAction('assert_text', @myaccount.to_s, true)
  performAction('assert_text', @choose_country.to_s, true)
  performAction('assert_text', @order_status.to_s, true)
  performAction('assert_text', @sign_in.to_s, true)
end

Then /^I should see the item was added to shopping cart message$/ do
  performAction('assert_text', @item_added.to_s, true)
end

Then /^I should not see the no items message$/ do
  performAction('assert_text', @no_items.to_s, false)
end

Then /^I should see the clear the cart message$/ do
  performAction('assert_text', @clear_cart_message.to_s, true)
end

Then /^I should see the (empty cart|empty) message$/ do |cart|
  case cart    
  when "empty cart"
    performAction('assert_text', @no_items.to_s, true)
  when "empty"
    performAction('assert_text', "You have no items in the cart", true)
  end
  
end

Then /^I should see the Catalog$/ do
  performAction('assert_text', @currency.to_s, true)
end

Then /^I should see the loading items message$/ do
  performAction('assert_text', @loading_items.to_s, true)
end

Then /^I should see the filter (popularity|price up|price down|name|brand)$/ do |filter|
  case filter
  when "popularity"
    performAction('assert_text', @popularity.to_s, true)
  when "price up"
    performAction('assert_text', @price_up.to_s, true)
  when "price down"
    performAction('assert_text', @price_down.to_s, true)
  when "name"
    performAction('assert_text', @name.to_s, true)
  when "brand"
    performAction('assert_text', @brand.to_s, true)
  end
end

Then /^I should see the (first|second) tip$/ do |tip|
  case tip
  when "first"
    performAction('assert_text', @first_tip.to_s, true)
  when "second"
    performAction('assert_text', @second_tip.to_s, true)
  end
end

Then /^I should see the got it button$/ do
  performAction('assert_text', @got_it.to_s, true)
end

Then /^I should not see the tips$/ do
  performAction('assert_text', @first_tip.to_s, false)
  performAction('assert_text', @second_tip.to_s, false)
end

Then /^I should (see|not see) the currency$/ do |value|
  case value
  when "see"
    performAction('assert_text', @currency.to_s, true)
  when "not see"
    performAction('assert_text', @currency.to_s, false)
  end
end

Then /^I should see the specifications$/ do
  performAction('assert_text', @specification.to_s, true)
end

Then /^I should see the variations$/ do
  performAction('assert_text', @please_choose.to_s, true)
end

Then /^I should see the product (features|description)$/ do |arg1|
  case arg1
  when "features" 
    performAction('assert_text', @product_features.to_s, true)
  when "description"
    performAction('assert_text', @product_description.to_s, true)
  end
end

Then /^I should see the write a review button$/ do
  performAction('assert_text', @write_review.to_s, true)
end