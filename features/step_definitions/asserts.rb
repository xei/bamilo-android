#
# ASSERTS
#
# performAction('assert_text', 'foo', true) is now assert_text('foo')
# performAction('assert_text', 'foo', false) is now assert_text('foo', false)
 
Then /^I should see the generated address$/ do 
  wait_for_elements_exist(["* {text CONTAINS '"+@address.to_s+"'}"],:timeout => 20)
  assert_text(@address.to_s, true)
end
 
Then /^I should see my first name$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@firstname.to_s+"'}"],:timeout => 20)
  assert_text(@firstname.to_s) 
end

Then /^I should see my email$/ do 
  wait_for_elements_exist(["* {text CONTAINS '"+@username.to_s+"'}"],:timeout => 20)
  assert_text(@username.to_s)
end

Then /^I should see the email error message$/ do 
  wait_for_elements_exist(["* {text CONTAINS '"+@emailerrormessage.to_s+"'}"],:timeout => 20)
  assert_text(@emailerrormessage.to_s)
end

Then /^I should see the password error message$/ do 
  wait_for_elements_exist(["* {text CONTAINS '"+@passerrormessage.to_s+"'}"],:timeout => 20)
  assert_text(@passerrormessage.to_s)
end

Then /^I should see the login error message$/ do 
  wait_for_elements_exist(["* marked:'"+@dialog_text.to_s+"'"],:timeout => 15)
  #assert_text(@loginerror.to_s)
end

Then /^I should see sign out button$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@signout.to_s+"'}"], :timeout => 30)
  #performAction('assert_text', @signout.to_s, true) 
end

Then /^I wait for the next$/ do 
  #wait_for_elements_exist(["* marked:'"+@next.to_s+"'"],:timeout => 20)
  wait_for_elements_exist(["* marked:'"+@next.to_s+"'"],:timeout => 20)
end

Then /^I wait for the navigation menu$/ do
  wait_for_elements_exist(["* marked:'"+@navigation.to_s+"'"],:timeout => 20)
  tap_when_element_exists("* marked:'"+@navigation.to_s+"'")
end

Then /^I wait for the confirm order$/ do 
  wait_for_elements_exist(["* marked:'"+@confirm_order.to_s+"'"],:timeout => 20)
end

Then /^I should see the mandatory fields error message$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@mandatory.to_s+"'}"],:timeout => 20)
  assert_text(@mandatory.to_s) 
#  performAction('assert_text', @mandatory.to_s, true) 
end

Then /^I should see the email exists error message$/ do 
  wait_for_elements_exist(["* {text CONTAINS '"+@sameemail.to_s+"'}"],:timeout => 20)
  assert_text(@sameemail.to_s)
#  performAction('assert_text', @sameemail.to_s, true) 
end

Then /^I should see the passwords dont match error message$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@differentpassword.to_s+"'}"],:timeout => 20)
  assert_text(@differentpassword.to_s)
#  performAction('assert_text', @differentpassword.to_s, true)
end

Then /^I should see the search message$/ do 
  wait_for_elements_exist(["* {text CONTAINS '"+@searchdefault.to_s+"'}"],:timeout => 20)
  assert_text(@searchdefault.to_s)
#  performAction('assert_text', @searchdefault.to_s, true) 
end

Then /^I should see the no suggestion message$/ do 
  wait_for_elements_exist(["* {text CONTAINS '"+@nosuggest.to_s+"'}"],:timeout => 20)
  assert_text(@nosuggest.to_s)
#  performAction('assert_text', @nosuggest.to_s, true) 
end

Then /^I should see the add to cart button$/ do 
  wait_for_elements_exist(["* {text CONTAINS '"+@addtocart.to_s+"'}"],:timeout => 20)
  assert_text(@addtocart.to_s)
#  performAction('assert_text', @addtocart.to_s, true) 
end

Then /^I should see the login button$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@sign_in.to_s+"'}"],:timeout => 20)
  assert_text(@sign_in.to_s)
#  performAction('assert_text', @sign_in.to_s, true)
end

Then /^I should see the Terms and Conditions$/ do
  case $country.to_s
  when "teste"
    wait_for_elements_exist(["* {text CONTAINS '"+@termsandconditionscheck.to_s+"'}"],:timeout => 20)
    assert_text(@termsandconditionscheck.to_s)
#    performAction('assert_text', @termsandconditionscheck.to_s, true)
  else
    
  end
end

Then /^I should see the password changed with success message$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@password_changed_message.to_s+"'}"],:timeout => 20)
  assert_text(@password_changed_message.to_s)
#  performAction('assert_text', @password_changed_message.to_s, true)
end

Then /^I should see the password is to short message$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@new_pass_short.to_s+"'}"],:timeout => 20)
  assert_text(@new_pass_short.to_s)
#  performAction('assert_text', @new_pass_short.to_s, true)
end

Then /^I should see the forgot password link$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@forgot_password.to_s+"'}"],:timeout => 20)
  assert_text(@forgot_password.to_s)
#  performAction('assert_text', @forgot_password.to_s, true)
end

Then /^I should see the password recovery screen$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@password_recovery.to_s+"'}"],:timeout => 20)
  assert_text(@password_recovery.to_s)
#  performAction('assert_text', @password_recovery.to_s, true)
end

Then /^I should see the password recovery (empty email|email not correct|email sent) message$/ do |message|
  case message.to_s
  when "empty email"
    wait_for_elements_exist(["* {text CONTAINS '"+@pass_rec_empty_email.to_s+"'}"],:timeout => 20)
    assert_text(@pass_rec_empty_email.to_s)
#    performAction('assert_text', @pass_rec_empty_email.to_s, true)
  when "not correct"
    wait_for_elements_exist(["* {text CONTAINS '"+@pass_rec_failed.to_s+"'}"],:timeout => 20)
    assert_text(@pass_rec_failed.to_s)
#    performAction('assert_text', @pass_rec_failed.to_s, true)
  when "email sent"
    wait_for_elements_exist(["* {text CONTAINS '"+@pass_rec_sent.to_s+"'}"],:timeout => 20)
    assert_text(@pass_rec_sent.to_s)
#    performAction('assert_text', @pass_rec_sent.to_s, true)
  end
end

Then /^I should see the countries$/ do
  assert_text(@venture_maroc)
#  performAction('assert_text', @venture_maroc, true)
#  assert_text(@venture_cotedivoire)
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
  wait_for_elements_exist(["* {text CONTAINS '"+@home.upcase.to_s+"'}"],:timeout => 20)
  assert_text(@home.upcase)
#  performAction('assert_text', @home.upcase, true)
end

Then /^I should see the sidebar$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@home.to_s+"'}"],:timeout => 20)
  assert_text(@home.to_s)
#  performAction('assert_text', @home.to_s, true)
  wait_for_elements_exist(["* {text CONTAINS '"+@categories.to_s+"'}"],:timeout => 20)
  assert_text(@categories.to_s)
#  performAction('assert_text', @categories.to_s, true)
  #performAction('assert_text', @search.to_s, true)
  #performAction('assert_text', @myaccount.to_s, true)
  wait_for_elements_exist(["* {text CONTAINS '"+@choose_country.to_s+"'}"],:timeout => 20)
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
  wait_for_elements_exist(["* {text CONTAINS '"+@item_was_added.to_s+"'}"],:timeout => 20)
  assert_text(@item_was_added.to_s)
  #wait_for_elements_exist(["* marked:'"+@items_count.to_s+"'"],:timeout => 40)
  #assert_text(@item_was_added.to_s)
end

Then /^I should not see the no items message$/ do
  wait_for_elements_do_not_exist(["* marked:'"+@dialog_text.to_s+"'"],:timeout => 15)
  #assert_text(@no_items.to_s,false)
end

Then /^I should see the clear the cart message$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@clear_cart_message.to_s+"'}"],:timeout => 20)
  assert_text(@clear_cart_message.to_s)
#  performAction('assert_text', @clear_cart_message.to_s, true)
end

Then /^I should see the (empty cart|empty) message$/ do |cart|
  case cart    
  when "empty cart"
    wait_for_elements_exist(["* marked:'"+@fragment_root_empty_message.to_s+"'"],:timeout => 40)
    #assert_text(@no_items.to_s)
  when "empty"
    wait_for_elements_exist(["* marked:'"+@fragment_root_empty_message.to_s+"'"],:timeout => 40)
    #assert_text(@no_items.to_s)
#    performAction('assert_text', "You have no items in the cart", true)
  end
  
end

Then /^I should see the Catalog$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@currency.to_s+"'}"],:timeout => 20)
  assert_text(@currency.to_s)
#  performAction('assert_text', @currency.to_s, true)
end

Then /^I should see the loading items message$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@loading_items.to_s+"'}"],:timeout => 20)
  assert_text(@loading_items.to_s)
#  performAction('assert_text', @loading_items.to_s, true)
end

Then /^I should see the filter (popularity|price up|price down|name|brand|new in|best rating)$/ do |filter|
  case filter
  when "popularity"
    wait_for_elements_exist(["* {text CONTAINS '"+@popularity.to_s+"'}"],:timeout => 20)
    assert_text(@popularity.to_s)
#    performAction('assert_text', @popularity.to_s, true)
  when "price up"
    wait_for_elements_exist(["* {text CONTAINS '"+@price_up.to_s+"'}"],:timeout => 20)
    assert_text(@price_up.to_s)
#    performAction('assert_text', @price_up.to_s, true)
  when "price down"
    wait_for_elements_exist(["* {text CONTAINS '"+@price_down.to_s+"'}"],:timeout => 20)
    assert_text(@price_down.to_s)
#    performAction('assert_text', @price_down.to_s, true)
  when "name"
    wait_for_elements_exist(["* {text CONTAINS '"+@name.to_s+"'}"],:timeout => 20)
    assert_text(@name.to_s)
#    performAction('assert_text', @name.to_s, true)
  when "brand"
    wait_for_elements_exist(["* {text CONTAINS '"+@brand.to_s+"'}"],:timeout => 20)
    assert_text(@brand.to_s)
#    performAction('assert_text', @brand.to_s, true)
  when "new in"
    wait_for_elements_exist(["* {text CONTAINS '"+@new_in.to_s+"'}"],:timeout => 20)
    assert_text(@new_in.to_s)
#      performAction('assert_text', @new_in.to_s, true)
  when "best rating"
    wait_for_elements_exist(["* {text CONTAINS '"+@best_rating.to_s+"'}"],:timeout => 20)
    assert_text(@best_rating.to_s)
#      performAction('assert_text', @best_rating.to_s, true)
  end
end

Then /^I should see the (first|second) tip$/ do |tip|
  case tip
  when "first"
    wait_for_elements_exist(["* {text CONTAINS '"+@first_tip.to_s+"'}"],:timeout => 20)
    assert_text(@first_tip.to_s)
  when "second"
    wait_for_elements_exist(["* {text CONTAINS '"+@second_tip.to_s+"'}"],:timeout => 20)
    assert_text(@second_tip.to_s)
  end
end

Then /^I should see the got it button$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@got_it.to_s+"'}"],:timeout => 20)
  assert_text(@got_it.to_s)
end

Then /^I should not see the tips$/ do
  assert_text(@first_tip.to_s,false)
  assert_text(@second_tip.to_s,false)
end

Then /^I should (see|not see) the currency$/ do |value|
  case value
  when "see"
    wait_for_elements_exist(["* {text CONTAINS '"+@currency.to_s+"'}"],:timeout => 20)
    assert_text(@currency.to_s)
#    performAction('assert_text', @currency.to_s, true)
  when "not see"
    assert_text(@currency.to_s,false)
#    performAction('assert_text', @currency.to_s, false)
  end
end

Then /^I should see the specifications$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@specification.to_s+"'}"],:timeout => 20)
  assert_text(@specification.to_s)
#  performAction('assert_text', @specification.to_s, true)
end

Then /^I should see the variations$/ do
  #assert_text(@please_choose.to_s)
  wait_for_elements_exist(["* {text CONTAINS '"+@size.to_s+"'}"],:timeout => 20)
  assert_text(@size.to_s)
#  performAction('assert_text', @please_choose.to_s, true)
end

Then /^I should see the product (features|description)$/ do |arg1|
  case arg1
  when "features" 
    wait_for_elements_exist(["* {text CONTAINS '"+@product_features.to_s+"'}"],:timeout => 20)
    assert_text(@product_features.to_s)
#    performAction('assert_text', @product_features.to_s, true)
  when "description"
    wait_for_elements_exist(["* {text CONTAINS '"+@product_description.to_s+"'}"],:timeout => 20)
    assert_text(@product_description.to_s)
#    performAction('assert_text', @product_description.to_s, true)
  end
end

Then /^I should see the write a review button$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@write_review.to_s+"'}"],:timeout => 20)
  assert_text(@write_review.to_s)
#  performAction('assert_text', @write_review.to_s, true)
end

Then /^I should see the search bar$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@search.to_s+"'}"],:timeout => 20)
  assert_text(@search.to_s)
#  performAction('assert_text', @search.to_s, true)
end

Then /^I check the popular categories$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@popular_categories.to_s+"'}"],:timeout => 20)
  assert_text(@popular_categories.to_s)
#  performAction('assert_text', @popular_categories.to_s, true)
end

Then /^I should see the filters$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@filter_brand.to_s+"'}"],:timeout => 20)
  assert_text(@filter_brand.to_s)
#  performAction('assert_text', @filter_brand.to_s, true)
  #performAction('assert_text', @filter_size.to_s, true)
  wait_for_elements_exist(["* {text CONTAINS '"+@filter_color_family.to_s+"'}"],:timeout => 20)
  assert_text(@filter_color_family.to_s)
#  performAction('assert_text', @filter_color_family.to_s, true)
  wait_for_elements_exist(["* {text CONTAINS '"+@filter_price.to_s+"'}"],:timeout => 20)
  assert_text(@filter_price.to_s)
#  performAction('assert_text', @filter_price.to_s, true)
end

Then /^I should not see the filter screen$/ do
  wait_for_elements_do_not_exist(["* {text CONTAINS '"+@filter_brand.to_s+"'}"],:timeout => 20)
  assert_text(@filter_brand.to_s,false)
#  performAction('assert_text', @filter_brand.to_s, false)
  wait_for_elements_do_not_exist(["* {text CONTAINS '"+@filter_color_family.to_s+"'}"],:timeout => 20)
  assert_text(@filter_color_family.to_s,false)
#  performAction('assert_text', @filter_color_family.to_s, false)
  wait_for_elements_do_not_exist(["* {text CONTAINS '"+@filter_price.to_s+"'}"],:timeout => 20)
  assert_text(@filter_price.to_s, false)
#  performAction('assert_text', @filter_price.to_s, false)
end

Then /^I should see the warning pop up message$/ do
  query("* marked:'toast_exit_message'")
  #assert_text(@back.to_s)
#  performAction('assert_text', @back.to_s, true)
end

Then /^I (should|should not) see the order$/ do |order|
  case order.to_s
  when "should"
    tap_when_element_exists("* marked:'title_status_text'")
    #tap_when_element_exists("* marked:'order_item_name'")
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
  wait_for_elements_exist(["* marked:'"+@newsletter_cat_sub+"'"],:timeout => 20)
  #assert_text(@newsletter_checkbox.to_s)
#  performAction('assert_text', @newsletter_checkbox.to_s, true)
end

Then /^I should see the email notifications section$/ do
  assert_text(@email_notifications.to_s)
#  performAction('assert_text', @email_notifications.to_s, true)
end


Then /^I should wait and see login screen$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@login.to_s+"'}"],:timeout => 20)
  assert_text(@login.to_s)
#  performAction('assert_text', @login.to_s, true)
end

Then /^I should see login screen$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@login.to_s+"'}"],:timeout => 20)
  assert_text(@login.to_s)
#  performAction('assert_text', @login.to_s, true)
end

Then /^I should see newsletter (title|header|options)$/ do |newsletter|
  case newsletter.to_s
  when "title"
    wait_for_elements_exist(["* {text CONTAINS '"+@email_notifications.to_s+"'}"],:timeout => 20)
    assert_text(@email_notifications.to_s)
#    performAction('assert_text', @email_notifications.to_s, true)
  when "header"
    wait_for_elements_exist(["* {text CONTAINS '"+@newsletter.to_s+"'}"],:timeout => 20)
    assert_text(@newsletter.to_s)
#    performAction('assert_text', @newsletter.to_s, true)
  when "options"
    
  end
end

Then /^I should see the notification newsletter changes$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@notification_newsletter_changes.to_s+"'}"],:timeout => 20)
  assert_text(@notification_newsletter_changes.to_s)
#  performAction('assert_text', @notification_newsletter_changes.to_s, true)
end

Then /^I should see the no results text$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@no_result_found.to_s+"'}"],:timeout => 20)
  assert_text(@no_result_found.to_s)
#  performAction('assert_text', @no_result_found.to_s, true)
end

Then /^I should see the search tips$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@search_tips.to_s+"'}"],:timeout => 20)
  assert_text(@search_tips.to_s)
#  performAction('assert_text', @search_tips.to_s, true)
end

Then /^I should see newsletter subscription section$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@newsletter_subscription.to_s+"'}"],:timeout => 20)
  assert_text(@newsletter_subscription.to_s)
#  performAction('assert_text', @newsletter_subscription.to_s, true)
end

Then /^I should see the newsletter message error$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@invalidad_email_message.to_s+"'}"],:timeout => 20)
  assert_text(@invalid_email_message.to_s)
#  performAction('assert_text', @invalid_email_message.to_s, true)
end

Then /^I should see the settings tab$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@settings.to_s+"'}"],:timeout => 20)
  assert_text(@settings.to_s)
#  performAction('assert_text', @settings.to_s, true)
end

Then /^I should see the my profile tab$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@myprofile.to_s+"'}"],:timeout => 20)
  assert_text(@myprofile.to_s)
#  performAction('assert_text', @myprofile.to_s, true)
end

Then /^I should see the my profile sub tabs$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@my_favourites.to_s+"'}"],:timeout => 20)
  assert_text(@my_favourites.to_s)
#  performAction('assert_text', @my_favourites.to_s, true)
  wait_for_elements_exist(["* {text CONTAINS '"+@recente_searches.to_s+"'}"],:timeout => 20)
  assert_text(@recente_searches.to_s)
#  performAction('assert_text', @recente_searches.to_s, true)
  wait_for_elements_exist(["* {text CONTAINS '"+@recently_viewed.to_s+"'}"],:timeout => 20)
  assert_text(@recently_viewed.to_s)
#  performAction('assert_text', @recently_viewed.to_s, true)
end

Then /^I should see the menu and categories tabs$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@categories.to_s+"'}"],:timeout => 20)
  assert_text(@categories.to_s)
#  performAction('assert_text', @categories.to_s, true)
  wait_for_elements_exist(["* {text CONTAINS '"+@menu.to_s+"'}"],:timeout => 20)
  assert_text(@menu.to_s)
#  performAction('assert_text', @menu.to_s, true)
end

Then /^I should see the menu sections$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@home.to_s+"'}"],:timeout => 20)
  assert_text(@home.to_s)
#  performAction('assert_text', @home.to_s, true)
#  assert_text(@myprofile.to_s)
#  performAction('assert_text', @myprofile.to_s, true)
#  assert_text(@settings.to_s)
#  performAction('assert_text', @settings.to_s, true)
  wait_for_elements_exist(["* {text CONTAINS '"+@choose_country.to_s+"'}"],:timeout => 20)
  assert_text(@choose_country.to_s)
#  performAction('assert_text', @choose_country.to_s, true)
#  assert_text(@order_status.to_s)
#  performAction('assert_text', @order_status.to_s, true)
#  assert_text(@sign_in.to_s)
#  performAction('assert_text', @sign_in.to_s, true)
end

Then /^I should see the item (added|removed) message$/ do |item|
  case item.to_s
  when "added"
    wait_for_elements_exist(["* {text CONTAINS '"+@item_added.to_s+"'}"],:timeout => 20)
    assert_text(@item_added.to_s)
#    performAction('assert_text', @item_added.to_s, true)
  when "removed"
    wait_for_elements_exist(["* {text CONTAINS '"+@item_removed.to_s+"'}"],:timeout => 20)
    assert_text(@item_removed.to_s)
#    performAction('assert_text', @item_removed.to_s, true)
  end
end

Then /^I should see the search icon$/ do
  tap_when_element_exists("* marked:'abs__search_button'")
#  performAction('click_on_view_by_id', "abs__search_button")
end

Then /^I should see the recent searches$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@recente_searches.to_s+"'}"],:timeout => 20)
  assert_text(@recente_searches.to_s)
#  performAction('assert_text', @recente_searches.to_s, true)
end

Then /^I should see the my favorites$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@my_favourites.to_s+"'}"],:timeout => 20)
  assert_text(@my_favourites.to_s)
#  performAction('assert_text', @my_favourites.to_s, true)
end

Then /^I wait to see the home$/ do
  wait_for_elements_exist(["* marked:'"+@home.to_s+"'"],:timeout => 40) 
  #wait_for_elements_exist(["* marked:'"+@home_c.to_s+"'"],:timeout => 40) 
end

Then /^I wait for the overflow button$/ do
  wait_for_elements_exist(["* marked:'image_myprofile'"],:timeout => 30) 
  tap_when_element_exists("* marked:'image_myprofile'")
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
  #assert_text(@sign_in.to_s)
#  performAction('assert_text', @sign_in.to_s, true)
  wait_for_elements_exist(["* {text CONTAINS '"+@my_favourites.to_s+"'}"],:timeout => 20)
  assert_text(@my_favourites.to_s)
#  performAction('assert_text', @my_favourites.to_s, true)
  wait_for_elements_exist(["* {text CONTAINS '"+@recente_searches.to_s+"'}"],:timeout => 20)
  assert_text(@recente_searches.to_s)
#  performAction('assert_text', @recente_searches.to_s, true)
  wait_for_elements_exist(["* {text CONTAINS '"+@recently_viewed.to_s+"'}"],:timeout => 20)
  assert_text(@recently_viewed.to_s)
#  performAction('assert_text', @recently_viewed.to_s, true)
  wait_for_elements_exist(["* {text CONTAINS '"+@my_account.to_s+"'}"],:timeout => 20)
  assert_text(@myaccount.to_s)
#  performAction('assert_text', @myaccount.to_s, true)
  wait_for_elements_exist(["* {text CONTAINS '"+@track_my_order.to_s+"'}"],:timeout => 20)
  assert_text(@track_my_order.to_s)
#  performAction('assert_text', @track_my_order.to_s, true)
end

Then /^I should see the recently viewed$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@recently_viewed.to_s+"'}"],:timeout => 20)
  assert_text(@recently_viewed.to_s)
#  performAction('assert_text', @recently_viewed.to_s, true)
end


Then /^I should see my account$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@myaccount.to_s+"'}"],:timeout => 20)
  assert_text(@myaccount.to_s)
#  performAction('assert_text', @myaccount.to_s, true)
end

Then /^I should see track my order page$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@track_my_order.to_s+"'}"],:timeout => 20)
  assert_text(@track_my_order.to_s)
#  performAction('assert_text', @track_my_order.to_s, true)
end

Then /^I should see the catalog number of items$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@number_items.to_s+"'}"],:timeout => 20)
  assert_text(@number_items.to_s)
#  performAction('assert_text', @number_items.to_s, true)
end

Then /^I should see the remember user email checkbox$/ do
  wait_for_elements_exist("* marked:'login_remember_user_email'")
  #assert_text(@remember_my_email.to_s)
#  performAction('assert_text', @remember_my_email.to_s, true)
end

Then /^I (should not|should) see email$/ do |item|
  case item.to_s
    when "should"
    wait_for_elements_exist(["* {text CONTAINS '"+@username.to_s+"'}"],:timeout => 20)
    assert_text(@username.to_s)
#      performAction('assert_text', @username.to_s, true)
    when "should not"
    wait_for_elements_do_not_exist(["* {text CONTAINS '"+@username.to_s+"'}"],:timeout => 20)
    assert_text(@username.to_s,false)
#      performAction('assert_text', @username.to_s, false)
    end
end

Then /^I should see size$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@size.to_s+"'}"],:timeout => 20)
  assert_text(@size.to_s)
end

Then /^I should not see the (sound|vibrate) option$/ do |option|
  case option.to_s
  when "sound"
    wait_for_elements_do_not_exist(["* {text CONTAINS '"+@sound.to_s+"'}"],:timeout => 20)
    assert_text(@sound.to_s,false)
  when "vibrate"
    wait_for_elements_do_not_exist(["* {text CONTAINS '"+@vibrate.to_s+"'}"],:timeout => 20)
    assert_text(@vibrate.to_s,false)
  end
end

Then /^I should see my order history$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@my_order_history_2.to_s+"'}"],:timeout => 20)
  assert_text(@my_order_history_2.to_s)
end

Then /^I should see other addresses$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@otheradsresses.to_s+"'}"],:timeout => 20)
  assert_text(@otheradsresses.to_s)
end

Then /^I should see the mandatory First Name error message$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@error_first_name.to_s+"'}"],:timeout => 20)
  assert_text(@error_first_name.to_s)
end

Then /^I should see the mandatory Last Name error message$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@error_last_name.to_s+"'}"],:timeout => 20)
  assert_text(@error_last_name.to_s)
end

Then /^I should see the mandatory Address error message$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@error_address.to_s+"'}"],:timeout => 20)
  assert_text(@error_address.to_s)
end

Then /^I should see the Categories$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@categories.to_s+"'}"],:timeout => 20)
  assert_text(@categories.to_s)
end

Then /^I should see the no items message$/ do
  wait_for_elements_do_not_exist(["* marked:'"+@dialog_text.to_s+"'"],:timeout => 15)
  #assert_text(@no_items.to_s)
end

Then /^I should see the Address$/ do
  wait_for_elements_exist(["* marked:'test tester'"],:timeout => 15)
  #assert_text(@no_items.to_s)
end

Then /^I should see the Shipping Label$/ do
  wait_for_elements_exist(["* marked:'"+@shipping.to_s+"'"],:timeout => 15)
  #assert_text(@no_items.to_s)
end

Then /^I wait to see the Teaser$/ do
  wait_for_elements_exist(["* marked:'home_teaser_campaign_title'"],:timeout => 40)
end

Then /^I should see the Address Edited Message$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@edited_address_msg.to_s+"'}"],:timeout => 20)
  assert_text(@edited_address_msg.to_s)
end

Then /^I (should not|should) see the Voucher Label$/ do |item|
 case item.to_s
    when "should"
      wait_for_elements_exist(["* {text CONTAINS '"+@voucher_label.to_s+"'}"],:timeout => 20)
      assert_text(@voucher_label.to_s)
    when "should not"
      wait_for_elements_do_not_exist(["* {text CONTAINS '"+@voucher_label.to_s+"'}"],:timeout => 20)
      assert_text(@voucher_label.to_s,false)
    end
end

Then /^I should see Invalid Voucher Message$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@invalid_voucher_msg.to_s+"'}"],:timeout => 20)
  assert_text(@invalid_voucher_msg.to_s)
end

Then /^I should see the product with query$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@search_p.to_s+"'}"],:timeout => 20)
  assert_text(@search_p.to_s)
end