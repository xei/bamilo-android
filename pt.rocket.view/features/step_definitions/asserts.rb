#
# ASSERTS
#
# performAction('assert_text', 'foo', true) is now assert_text('foo')
# performAction('assert_text', 'foo', false) is now assert_text('foo', false)
 
Then /^I should see the generated address$/ do 
  assert_text(@address.to_s, true)
#  performAction('assert_text', @address.to_s, true) 
end
 
Then /^I should see my first name$/ do
  assert_text(@firstname.to_s) 
#  performAction('assert_text', @firstname.to_s, true) 
end

Then /^I should see my email$/ do 
  assert_text(@username.to_s)
#  performAction('assert_text', @username.to_s, true) 
end

Then /^I should see the email error message$/ do 
  assert_text(@emailerrormessage.to_s)
#  performAction('assert_text', @emailerrormessage.to_s, true) 
end

Then /^I should see the password error message$/ do 
  assert_text(@passerrormessage.to_s)
#  performAction('assert_text', @passerrormessage.to_s, true) 
end

Then /^I should see the login error message$/ do 
  assert_text(@loginerror.to_s)
#  performAction('assert_text', @loginerror.to_s, true) 
end

Then /^I should see sign out button$/ do
  wait_for_elements_exist(["* marked:'"+@signout.to_s+"'"],:timeout => 20) 
  #performAction('assert_text', @signout.to_s, true) 
end

Then /^I wait for the next$/ do 
  wait_for_elements_exist(["* marked:'"+@next.to_s+"'"],:timeout => 20)
  wait_for_elements_exist(["* marked:'"+@next.to_s+"'"],:timeout => 20)
end

Then /^I wait for the confirm order$/ do 
  wait_for_elements_exist(["* marked:'"+@confirm_order.to_s+"'"],:timeout => 20)
end

Then /^I should see the mandatory fields error message$/ do
  assert_text(@mandatory.to_s) 
#  performAction('assert_text', @mandatory.to_s, true) 
end

Then /^I should see the email exists error message$/ do 
  assert_text(@sameemail.to_s)
#  performAction('assert_text', @sameemail.to_s, true) 
end

Then /^I should see the passwords dont match error message$/ do
  assert_text(@differentpassword.to_s)
#  performAction('assert_text', @differentpassword.to_s, true)
end

Then /^I should see the search message$/ do 
  assert_text(@searchdefault.to_s)
#  performAction('assert_text', @searchdefault.to_s, true) 
end

Then /^I should see the no suggestion message$/ do 
  assert_text(@nosuggest.to_s)
#  performAction('assert_text', @nosuggest.to_s, true) 
end

Then /^I should see the add to cart button$/ do 
  assert_text(@addtocart.to_s)
#  performAction('assert_text', @addtocart.to_s, true) 
end

Then /^I should see the login button$/ do
  assert_text(@sign_in.to_s)
#  performAction('assert_text', @sign_in.to_s, true)
end

Then /^I should see the Terms and Conditions$/ do
  case $country.to_s
  when "teste"
    assert_text(@termsandconditionscheck.to_s)
#    performAction('assert_text', @termsandconditionscheck.to_s, true)
  else
    
  end
end

Then /^I should see the password changed with success message$/ do
  assert_text(@password_changed_message.to_s)
#  performAction('assert_text', @password_changed_message.to_s, true)
end

Then /^I should see the password is to short message$/ do
  assert_text(@new_pass_short.to_s)
#  performAction('assert_text', @new_pass_short.to_s, true)
end

Then /^I should see the forgot password link$/ do
  assert_text(@forgot_password.to_s)
#  performAction('assert_text', @forgot_password.to_s, true)
end

Then /^I should see the password recovery screen$/ do
  assert_text(@password_recovery.to_s)
#  performAction('assert_text', @password_recovery.to_s, true)
end

Then /^I should see the password recovery (empty email|email not correct|email sent) message$/ do |message|
  case message.to_s
  when "empty email"
    assert_text(@pass_rec_empty_email.to_s)
#    performAction('assert_text', @pass_rec_empty_email.to_s, true)
  when "not correct"
    assert_text(@pass_rec_failed.to_s)
#    performAction('assert_text', @pass_rec_failed.to_s, true)
  when "email sent"
    assert_text(@pass_rec_sent.to_s)
#    performAction('assert_text', @pass_rec_sent.to_s, true)
  end
end

Then /^I should see the countries$/ do
  assert_text(@venture_maroc)
#  performAction('assert_text', @venture_maroc, true)
  assert_text(@venture_cotedivoire)
#  performAction('assert_text', @venture_cotedivoire, true)
  assert_text(@venture_nigeria)
#  performAction('assert_text', @venture_nigeria, true)
  assert_text(@venture_egypt)
#  performAction('assert_text', @venture_egypt, true)
  assert_text(@venture_kenya)
#  performAction('assert_text', @venture_kenya, true)
  assert_text(@venture_uganda)
#  performAction('assert_text', @venture_uganda, true)
  assert_text(@venture_ghana)
#  performAction('assert_text', @venture_ghana, true)
  assert_text(@venture_camerron)

end

Then /^I should see the corresponding server$/ do
  case $country
  when "ic"
    assert_text('www.jumia.ci')
#    performAction('assert_text', "www.jumia.ci", true)
    puts "www.jumia.ci"
  when "ke"
    assert_text('www.jumia.co.ke')
#    performAction('assert_text', "www.jumia.co.ke", true)
    puts "www.jumia.co.ke"
  when "ma"
    assert_text('www.jumia.ma')
#    performAction('assert_text', "www.jumia.ma", true)
    puts "www.jumia.ma"
  when "ng"
    assert_text('www.jumia.com.ng')
#    performAction('assert_text', "www.jumia.com.ng", true)
    puts "www.jumia.co.ng"
  when "eg"
    assert_text('www.jumia.com.eg')
#    performAction('assert_text', "www.jumia.com.eg", true)
    puts "www.jumia.com.eg"
  end
end

Then /^I should see the home$/ do
  assert_text(@home.upcase)
#  performAction('assert_text', @home.upcase, true)
end

Then /^I should see the sidebar$/ do
  assert_text(@home.to_s)
#  performAction('assert_text', @home.to_s, true)
  assert_text(@categories.to_s)
#  performAction('assert_text', @categories.to_s, true)
  #performAction('assert_text', @search.to_s, true)
  #performAction('assert_text', @myaccount.to_s, true)
  assert_text(@choose_country.to_s)
#  performAction('assert_text', @choose_country.to_s, true)
  #performAction('assert_text', @order_status.to_s, true)
  #performAction('assert_text', @sign_in.to_s, true)
end

Then /^I should not see the sidebar$/ do
  assert_text(@sign_in.to_s,false)
#  performAction('assert_text', @sign_in.to_s, false)
end

Then /^I should see the item was added to shopping cart message$/ do
  assert_text(@item_was_added.to_s)
#  performAction('assert_text', @item_was_added.to_s, true)
end

Then /^I should not see the no items message$/ do
  assert_text(@no_items.to_s,false)
#  performAction('assert_text', @no_items.to_s, false)
end

Then /^I should see the clear the cart message$/ do
  assert_text(@clear_cart_message.to_s)
#  performAction('assert_text', @clear_cart_message.to_s, true)
end

Then /^I should see the (empty cart|empty) message$/ do |cart|
  case cart    
  when "empty cart"
    assert_text(@no_items.to_s)
#    performAction('assert_text', @no_items.to_s, true)
  when "empty"
    assert_text('You have no items in the cart')
#    performAction('assert_text', "You have no items in the cart", true)
  end
  
end

Then /^I should see the Catalog$/ do
  assert_text(@currency.to_s)
#  performAction('assert_text', @currency.to_s, true)
end

Then /^I should see the loading items message$/ do
  assert_text(@loading_items.to_s)
#  performAction('assert_text', @loading_items.to_s, true)
end

Then /^I should see the filter (popularity|price up|price down|name|brand|new in|best rating)$/ do |filter|
  case filter
  when "popularity"
    assert_text(@popularity.to_s)
#    performAction('assert_text', @popularity.to_s, true)
  when "price up"
    assert_text(@price_up.to_s)
#    performAction('assert_text', @price_up.to_s, true)
  when "price down"
    assert_text(@price_down.to_s)
#    performAction('assert_text', @price_down.to_s, true)
  when "name"
    assert_text(@name.to_s)
#    performAction('assert_text', @name.to_s, true)
  when "brand"
    assert_text(@brand.to_s)
#    performAction('assert_text', @brand.to_s, true)
  when "new in"
    assert_text(@new_in.to_s)
#      performAction('assert_text', @new_in.to_s, true)
  when "best rating"
    assert_text(@best_rating.to_s)
#      performAction('assert_text', @best_rating.to_s, true)
  end
end

Then /^I should see the (first|second) tip$/ do |tip|
  case tip
  when "first"
    assert_text(@first_tip.to_s)
#    performAction('assert_text', @first_tip.to_s, true)
  when "second"
    assert_text(@second_tip.to_s)
#    performAction('assert_text', @second_tip.to_s, true)
  end
end

Then /^I should see the got it button$/ do
  assert_text(@got_it.to_s)
#  performAction('assert_text', @got_it.to_s, true)
end

Then /^I should not see the tips$/ do
  assert_text(@first_tip.to_s,false)
#  performAction('assert_text', @first_tip.to_s, false)
  assert_text(@second_tip.to_s,false)
#  performAction('assert_text', @second_tip.to_s, false)
end

Then /^I should (see|not see) the currency$/ do |value|
  case value
  when "see"
    assert_text(@currency.to_s)
#    performAction('assert_text', @currency.to_s, true)
  when "not see"
    assert_text(@currency.to_s,false)
#    performAction('assert_text', @currency.to_s, false)
  end
end

Then /^I should see the specifications$/ do
  assert_text(@specification.to_s)
#  performAction('assert_text', @specification.to_s, true)
end

Then /^I should see the variations$/ do
  assert_text(@please_choose.to_s)
#  performAction('assert_text', @please_choose.to_s, true)
end

Then /^I should see the product (features|description)$/ do |arg1|
  case arg1
  when "features" 
    assert_text(@product_features.to_s)
#    performAction('assert_text', @product_features.to_s, true)
  when "description"
    assert_text(@product_description.to_s)
#    performAction('assert_text', @product_description.to_s, true)
  end
end

Then /^I should see the write a review button$/ do
  assert_text(@write_review.to_s)
#  performAction('assert_text', @write_review.to_s, true)
end

Then /^I should see the search bar$/ do
  assert_text(@search.to_s)
#  performAction('assert_text', @search.to_s, true)
end

Then /^I check the popular categories$/ do
  assert_text(@popular_categories.to_s)
#  performAction('assert_text', @popular_categories.to_s, true)
end

Then /^I should see the filters$/ do
  assert_text(@filter_brand.to_s)
#  performAction('assert_text', @filter_brand.to_s, true)
  #performAction('assert_text', @filter_size.to_s, true)
  assert_text(@filter_color_family.to_s)
#  performAction('assert_text', @filter_color_family.to_s, true)
  assert_text(@filter_price.to_s)
#  performAction('assert_text', @filter_price.to_s, true)
end

Then /^I should not see the filter screen$/ do
  assert_text(@filter_brand.to_s,false)
#  performAction('assert_text', @filter_brand.to_s, false)
  assert_text(@filter_color_family.to_s,false)
#  performAction('assert_text', @filter_color_family.to_s, false)
  assert_text(@filter_price.to_s)
#  performAction('assert_text', @filter_price.to_s, false)
end

Then /^I should see the warning pop up message$/ do
  assert_text(@back.to_s)
#  performAction('assert_text', @back.to_s, true)
end

Then /^I (should|should not) see the order$/ do |order|
  case order.to_s
  when "should"
    tap_when_element_exists("* marked:'order_item_name'")
#    performAction('click_on_view_by_id', "order_item_name", true)
  when "should not"
    assert_text(@no_track_results.to_s)
#    performAction('assert_text', @no_track_results, true)
  end
end

Then /^I should see the thank you screen$/ do
  wait_for_elements_exist(["* marked:'"+"btn_checkout_continue"+"'"],:timeout => 20)
end

Then /^I (should|should not) see the related items$/ do |related|
  case related.to_s
  when "should"
    assert_text(@related_items.to_s)
#    performAction('assert_text', @related_items.to_s, true)
  when "should not"
    assert_text(@related_items.to_s,false)
#    performAction('assert_text', @related_items.to_s, false)
  end
  
end

Then /^I should see the price$/ do
  assert_text(@currency.to_s)
#  performAction('assert_text', @currency.to_s, true)
end

Then /^I should see the newsletter checkbox$/ do
  assert_text(@newsletter_checkbox.to_s)
#  performAction('assert_text', @newsletter_checkbox.to_s, true)
end

Then /^I should see the email notifications section$/ do
  assert_text(@email_notifications.to_s)
#  performAction('assert_text', @email_notifications.to_s, true)
end

Then /^I should see login screen$/ do
  assert_text(@login.to_s)
#  performAction('assert_text', @login.to_s, true)
end

Then /^I should see newsletter (title|header|options)$/ do |newsletter|
  case newsletter.to_s
  when "title"
    assert_text(@email_notifications.to_s)
#    performAction('assert_text', @email_notifications.to_s, true)
  when "header"
    assert_text(@newsletter.to_s)
#    performAction('assert_text', @newsletter.to_s, true)
  when "options"
    
  end
end

Then /^I should see the notification newsletter changes$/ do
  assert_text(@notification_newsletter_changes.to_s)
#  performAction('assert_text', @notification_newsletter_changes.to_s, true)
end

Then /^I should see the no results text$/ do
  assert_text(@no_result_found.to_s)
#  performAction('assert_text', @no_result_found.to_s, true)
end

Then /^I should see the search tips$/ do
  assert_text(@search_tips.to_s)
#  performAction('assert_text', @search_tips.to_s, true)
end

Then /^I should see newsletter subscription section$/ do
  assert_text(@newsletter_subscription.to_s)
#  performAction('assert_text', @newsletter_subscription.to_s, true)
end

Then /^I should see the newsletter message error$/ do
  assert_text(@invalid_email_message.to_s)
#  performAction('assert_text', @invalid_email_message.to_s, true)
end

Then /^I should see the settings tab$/ do
  assert_text(@settings.to_s)
#  performAction('assert_text', @settings.to_s, true)
end

Then /^I should see the my profile tab$/ do
  assert_text(@myprofile.to_s)
#  performAction('assert_text', @myprofile.to_s, true)
end

Then /^I should see the my profile sub tabs$/ do
  assert_text(@my_favourites.to_s)
#  performAction('assert_text', @my_favourites.to_s, true)
  assert_text(@recente_searches.to_s)
#  performAction('assert_text', @recente_searches.to_s, true)
  assert_text(@recently_viewed.to_s)
#  performAction('assert_text', @recently_viewed.to_s, true)
end

Then /^I should see the menu and categories tabs$/ do
  assert_text(@categories.to_s)
#  performAction('assert_text', @categories.to_s, true)
  assert_text(@menu.to_s)
#  performAction('assert_text', @menu.to_s, true)
end

Then /^I should see the menu sections$/ do
  assert_text(@home.to_s)
#  performAction('assert_text', @home.to_s, true)
  assert_text(@myprofile.to_s)
#  performAction('assert_text', @myprofile.to_s, true)
  assert_text(@settings.to_s)
#  performAction('assert_text', @settings.to_s, true)
  assert_text(@choose_country.to_s)
#  performAction('assert_text', @choose_country.to_s, true)
  assert_text(@order_status.to_s)
#  performAction('assert_text', @order_status.to_s, true)
  assert_text(@sign_in.to_s)
#  performAction('assert_text', @sign_in.to_s, true)
end

Then /^I should see the item (added|removed) message$/ do |item|
  case item.to_s
  when "added"
    assert_text(@item_added.to_s)
#    performAction('assert_text', @item_added.to_s, true)
  when "removed"
    assert_text(@item_removed.to_s)
#    performAction('assert_text', @item_removed.to_s, true)
  end
end

Then /^I should see the search icon$/ do
  tap_when_element_exists("* marked:'abs__search_button'")
#  performAction('click_on_view_by_id', "abs__search_button")
end

Then /^I should see the recent searches$/ do
  assert_text(@recente_searches.to_s)
#  performAction('assert_text', @recente_searches.to_s, true)
end

Then /^I should see the my favorites$/ do
  assert_text(@my_favourites.to_s)
#  performAction('assert_text', @my_favourites.to_s, true)
end

Then /^I wait to see the home$/ do
  wait_for_elements_exist(["* marked:'"+@home_c.to_s+"'"],:timeout => 20) 
end

Then /^I should see only one column$/ do
  wait_for_elements_exist(["* marked:'"+@home.to_s+"'"],:timeout => 20)
  wait_for_elements_exist(["* marked:'"+@choose_country.to_s+"'"],:timeout => 20)
  wait_for_elements_exist(["* marked:'"+@categories.to_s+"'"],:timeout => 20)
end

Then /^I wait to see the cart$/ do
  wait_for_elements_exist(["* marked:'"+@categories.to_s+"'"],:timeout => 20)
end

Then /^I should see the choose country page$/ do
  wait_for_elements_exist(["* marked:'"+@choose_country.to_s+"'"],:timeout => 20)
end

Then /^I should see the back button$/ do
  wait_for_elements_exist(["* marked:'"+@back_button.to_s+"'"],:timeout => 20)
end

Then /^I should see the overflow options$/ do
  assert_text(@sign_in.to_s)
#  performAction('assert_text', @sign_in.to_s, true)
  assert_text(@my_favourites.to_s)
#  performAction('assert_text', @my_favourites.to_s, true)
  assert_text(@recente_searches.to_s)
#  performAction('assert_text', @recente_searches.to_s, true)
  assert_text(@recently_viewed.to_s)
#  performAction('assert_text', @recently_viewed.to_s, true)
  assert_text(@myaccount.to_s)
#  performAction('assert_text', @myaccount.to_s, true)
  assert_text(@track_my_order.to_s)
#  performAction('assert_text', @track_my_order.to_s, true)
end

Then /^I should see the recently viewed$/ do
  assert_text(@recently_viewed.to_s)
#  performAction('assert_text', @recently_viewed.to_s, true)
end


Then /^I should see my account$/ do
  assert_text(@myaccount.to_s)
#  performAction('assert_text', @myaccount.to_s, true)
end

Then /^I should see track my order page$/ do
  assert_text(@track_my_order.to_s)
#  performAction('assert_text', @track_my_order.to_s, true)
end

Then /^I should see the catalog number of items$/ do
  assert_text(@number_items.to_s)
#  performAction('assert_text', @number_items.to_s, true)
end

Then /^I should see the remember user email checkbox$/ do
  assert_text(@remember_my_email.to_s)
#  performAction('assert_text', @remember_my_email.to_s, true)
end

Then /^I (should not|should) see email$/ do |item|
  case item.to_s
    when "should"
    assert_text(@username.to_s)
#      performAction('assert_text', @username.to_s, true)
    when "should not"
    assert_text(@username.to_s,false)
#      performAction('assert_text', @username.to_s, false)
    end
end

Then /^I should see size$/ do
  assert_text(@size.to_s)
end

Then /^I should not see the (sound|vibrate) option$/ do |option|
  case option.to_s
  when "sound"
    assert_text(@sound.to_s,false)
  when "vibrate"
    assert_text(@vibrate.to_s,false)
  end
end