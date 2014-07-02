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
  case $country.to_s
  when "teste"
    performAction('assert_text', @termsandconditionscheck.to_s, true)
  else
    
  end
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
  performAction('assert_text', @venture_maroc, true)
  performAction('assert_text', @venture_cotedivoire, true)
  performAction('assert_text', @venture_nigeria, true)
  performAction('assert_text', @venture_egypt, true)
  performAction('assert_text', @venture_kenya, true)
  performAction('assert_text', @venture_uganda, true)

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

Then /^I should not see the sidebar$/ do
  performAction('assert_text', @sign_in.to_s, false)
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

Then /^I should see the filter (popularity|price up|price down|name|brand|new in|best rating)$/ do |filter|
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
  when "new in"
      performAction('assert_text', @new_in.to_s, true)
  when "best rating"
      performAction('assert_text', @best_rating.to_s, true)
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

Then /^I should see the search bar$/ do
  performAction('assert_text', @search.to_s, true)
end

Then /^I check the popular categories$/ do
  performAction('assert_text', @popular_categories.to_s, true)
end

Then /^I should see the filters$/ do
  performAction('assert_text', @filter_brand.to_s, true)
  #performAction('assert_text', @filter_size.to_s, true)
  performAction('assert_text', @filter_color_family.to_s, true)
  performAction('assert_text', @filter_price.to_s, true)
end

Then /^I should not see the filter screen$/ do
  performAction('assert_text', @filter_brand.to_s, false)
  performAction('assert_text', @filter_color_family.to_s, false)
  performAction('assert_text', @filter_price.to_s, false)
end

Then /^I should see the warning pop up message$/ do
  performAction('assert_text', @back.to_s, true)
end

Then /^I (should|should not) see the order$/ do |order|
  case order.to_s
  when "should"
    performAction('click_on_view_by_id', "order_item_name", true)
  when "should not"
    performAction('assert_text', @no_track_results, true)
  end
end

Then /^I should see the thank you screen$/ do
  performAction('click_on_view_by_id', "btn_checkout_continue", true)
end

Then /^I (should|should not) see the related items$/ do |related|
  case related.to_s
  when "should"
    performAction('assert_text', @related_items.to_s, true)
  when "should not"
    performAction('assert_text', @related_items.to_s, false)
  end
  
end

Then /^I should see the price$/ do
  performAction('assert_text', @currency.to_s, true)
end

Then /^I should see the newsletter checkbox$/ do
  performAction('assert_text', @newsletter_checkbox.to_s, true)
end

Then /^I should see the email notifications section$/ do
  performAction('assert_text', @email_notifications.to_s, true)
end

Then /^I should see login screen$/ do
  performAction('assert_text', @login.to_s, true)
end

Then /^I should see newsletter (title|header|options)$/ do |newsletter|
  case newsletter.to_s
  when "title"
    performAction('assert_text', @email_notifications.to_s, true)
  when "header"
    performAction('assert_text', @newsletter.to_s, true)
  when "options"
    
  end
end

Then /^I should see the notification newsletter changes$/ do
  performAction('assert_text', @notification_newsletter_changes.to_s, true)
end

Then /^I should see the no results text$/ do
  performAction('assert_text', @no_result_found.to_s, true)
end

Then /^I should see the search tips$/ do
  performAction('assert_text', @search_tips.to_s, true)
end

Then /^I should see newsletter subscription section$/ do
  performAction('assert_text', @newsletter_subscription.to_s, true)
end

Then /^I should see the newsletter message error$/ do
  performAction('assert_text', @invalid_email_message.to_s, true)
end

Then /^I should see the settings tab$/ do
  performAction('assert_text', @settings.to_s, true)
end

Then /^I should see the my profile tab$/ do
  performAction('assert_text', @myprofile.to_s, true)
end

Then /^I should see the my profile sub tabs$/ do
  performAction('assert_text', @my_favourites.to_s, true)
  performAction('assert_text', @recente_searches.to_s, true)
  performAction('assert_text', @recently_viewed.to_s, true)
end

Then /^I should see the menu and categories tabs$/ do
  performAction('assert_text', @categories.to_s, true)
  performAction('assert_text', @menu.to_s, true)
end

Then /^I should see the menu sections$/ do
  performAction('assert_text', @home.to_s, true)
  performAction('assert_text', @myprofile.to_s, true)
  performAction('assert_text', @settings.to_s, true)
  performAction('assert_text', @choose_country.to_s, true)
  performAction('assert_text', @order_status.to_s, true)
  performAction('assert_text', @sign_in.to_s, true)
end

Then /^I should see the item (added|removed) message$/ do |item|
  case item.to_s
  when "added"
    performAction('assert_text', @item_added.to_s, true)
  when "removed"
    performAction('assert_text', @item_removed.to_s, true)
  end
end

Then /^I should see the search icon$/ do
  performAction('click_on_view_by_id', "menu_search")
end

Then /^I should see the recent searches$/ do
  performAction('assert_text', @recente_searches.to_s, true)
end

Then /^I should see the my favorites$/ do
  performAction('assert_text', @my_favourites.to_s, true)
end