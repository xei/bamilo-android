#
# CLICKS AND PRESS
#
# Changes in Calabash 0.5
#
# performAction('click_on_view_by_id', "") is now tap_when_element_exists("* marked:'identifier'")
#
# performAction('click_on_text', "") is now touch("* {text CONTAINS 'my_text'}");
#
# performAction('press', "") is now touch("* marked:'my_id'")

Then /^I go to home$/ do
  touch("* {text CONTAINS '"+@home.to_s+"'}")
  #performAction('click_on_text',@home.to_s)
end

Then /^I open the navigation menu$/ do 
  tap_when_element_exists("* marked:'"+@navigation.to_s+"'")
#  performAction('click_on_view_by_id', @navigation.to_s)
end

Then /^I close the navigation menu$/ do 
  tap_when_element_exists("* marked:'"+@navigation.to_s+"'")
#  performAction('click_on_view_by_id',@navigation.to_s)
end

Then /^I press Login Button$/ do
  tap_when_element_exists("* marked:'"+@login_button_id.to_s+"'")
#  performAction('click_on_view_by_id', @login_button_id.to_s)
end

Then /^I press the create account button$/ do
  tap_when_element_exists("* marked:'"+@createaccbutton_id.to_s+"'")
#  performAction('click_on_view_by_id', @createaccbutton_id.to_s)
end

Then /^I press the register button$/ do
  touch("* {text CONTAINS '"+@register.to_s+"'}")
  #performAction('click_on_text', @register.to_s)
end

Then /^I press Submit$/ do
  tap_when_element_exists("* marked:'"+@submit.to_s+"'")
#  performAction('click_on_view_by_id', @submit.to_s)
end

Then /^I press Rating$/ do
  tap_when_element_exists("* marked:'"+@rating.to_s+"'")
#  performAction('click_on_view_by_id', @rating.to_s)
end

Then /^I go to cart$/ do 
  touch("* marked:'"+@gotocart.to_s+"'")
#  performAction('press',@gotocart.to_s)
end

Then /^I press the cart icon$/ do
  touch("* marked:'"+@gotocart.to_s+"'")
#  performAction('press', @gotocart.to_s)
end

Then /^I choose the Sign In option$/ do
  touch("* {text CONTAINS '"+@signin.to_s+"'}")
  
end

Then /^I press Logout Button$/ do
  touch("* {text CONTAINS '"+@signout.to_s+"'}")
  #performAction('click_on_text',@signout.to_s)
end

Then /^I press Ok$/ do
  touch("* {text CONTAINS '"+@ok.to_s+"'}")
  #performAction('click_on_text',@ok.to_s)
end

Then /^I enter My Account$/ do 
  touch("* {text CONTAINS '"+@myaccount.to_s+"'}")
  #performAction('click_on_text',@myaccount.to_s)
end

Then /^I enter My User Data$/ do 
  touch("* {text CONTAINS '"+@myinfo.to_s+"'}")
  #performAction('click_on_text',@myinfo.to_s)
end

Then /^I enter a valid Category$/ do 
  touch("* {text CONTAINS '"+@categoryfashion.to_s+"'}")
  #performAction('click_on_text',@categoryfashion.to_s)
end

Then /^I add product to cart$/ do 
  touch("* {text CONTAINS '"+@addtocart.to_s+"'}")
  #performAction('click_on_text',@addtocart.to_s)
end

Then /^I press Terms and Conditions$/ do
  touch("* {text CONTAINS '"+@termsandconditions.to_s+"'}")
  #performAction('click_on_text',@termsandconditions.to_s)
end

Then /^I select the country$/ do
  wait_for_elements_exist(["* marked:'"+@venture_name.to_s+"'"],:timeout => 20)
  touch("* {text CONTAINS '"+@venture_name.to_s+@dev.to_s+"'}")
  #performAction('click_on_text',@venture_name.to_s+@dev.to_s )
  #puts @venture_name.to_s+@dev.to_s
end

Then /^I click forgot password$/ do
  touch("* {text CONTAINS '"+@forgot_password.to_s+"'}")
  #performAction('click_on_text', @forgot_password.to_s)
end

Then /^I press Got it$/ do
  touch("* {text CONTAINS '"+@got_it.to_s+"'}")
  #performAction('click_on_text', @got_it.to_s)
end

Then /^I press Choose Country$/ do
  touch("* {text CONTAINS '"+@choose_country.to_s+"'}")
  #performAction('click_on_text', @choose_country.to_s)
end

Then /^I press Yes$/ do
  touch("* {text CONTAINS '"+@yes.to_s+"'}")
  #performAction('click_on_text',@yes.to_s)
end

Then /^I press Search$/ do
  touch("* {text CONTAINS '"+@search.to_s+"'}")
  #performAction('click_on_text', @search.to_s)
end

Then /^I proceed to checkout$/ do 
  touch("* {text CONTAINS '"+@proceedcheckout.to_s+"'}")
#  performAction('press',@proceedcheckout.to_s)
end

Then /^I press male$/ do 
  touch("* marked:'"+@male.to_s+"'")
#  performAction('press',@male.to_s)
end

Then /^I press birthday$/ do
  touch("* marked:'"+@birthday.to_s+"'")
#  performAction('press',@birthday.to_s)
end

Then /^I check Terms and Conditions$/ do
  case $country.to_s
  when "teste"
    touch("* marked:'"+@termsandconditionscheck.to_s+"'")
#    performAction('press',@termsandconditionscheck.to_s)
  else
    
  end
  
  
end

Then /^I press Save$/ do
  touch("* marked:'"+@save.to_s+"'")
#  performAction('press', @save.to_s)
end

Then /^I press product specifications$/ do
  touch("* {text CONTAINS '"+@specification.to_s+"'}")
  #performAction('click_on_text', @specification.to_s)
end

Then /^I press a review$/ do
  touch("* {text CONTAINS '"+@posted_by.to_s+"'}")
  #performAction('click_on_text', @posted_by.to_s)
end

Then /^I press Write a Review$/ do
  touch("* {text CONTAINS '"+@write_review.to_s+"'}")
  #performAction('click_on_text', @write_review.to_s)
end

Then /^I press rating$/ do
  @collection=query("RatingBar")
  #puts @collection.length
  id = 77
  @collection.each do |item|
    item["id"] = id.to_s
    id+=1
    #puts "type is :"
    #puts item["rect"]["x"]
    tap_when_element_exists("* marked:'"+item["id"]+"'")
#    performAction('click_on_view_by_id', item["id"])
  end
end

Then /^I press Send Review$/ do
  touch("* {text CONTAINS '"+@send_review.to_s+"'}")
#  performAction('click_on_text', @send_review.to_s)
end

Then /^I press to reviews$/ do
  touch("* {text CONTAINS '"+@to_reviews.to_s+"'}")
#  performAction('click_on_text', @to_reviews.to_s)
end

Then /^I press share$/ do
  #@share=query("ShareActionProvider")
  #puts @share
  #performAction('click_on_view_by_id', "menu_share")
  tap_when_element_exists("* marked:'"+"abs__search_button"+"'")
#  performAction('click_on_view_by_id', "abs__search_button")
  
end

Then /^I press Messaging$/ do
  touch("* {text CONTAINS '"+@messaging.to_s+"'}")
#  performAction('click_on_text', @messaging.to_s)
end

Then /^I press delete product$/ do
  touch("* {text CONTAINS '"+@delete.to_s+"'}")
#  performAction('click_on_text', @delete.to_s)
end

Then /^I change the product quantity(| to zero)$/ do |zero|
  tap_when_element_exists("* marked:'"+"changequantity_button"+"'")
#  performAction('click_on_view_by_id', "changequantity_button")
  case zero
  when " to zero"
    touch("*0")
#    performAction('click_on_text', "0")
  else
    touch("*2")
#    performAction('click_on_text', "2")    
  end
end

#Then /^I add multiple products$/ do
#  $i= 0
#  $f= 0
#  while $i<10 do
#    performAction('click_on_view_by_id',@navigation.to_s)
#    puts "navigation"
#    touch("*"+@categories.to_s)
##    performAction('click_on_text',@categories.to_s)
#    puts "category"
#    touch("*"+@categoryfashion.to_s)
##    performAction('click_on_text',@categoryfashion.to_s)
#    puts "wait"
#    performAction('wait', 2)
#    puts "subcategory"
#    performAction('press_list_item', 1, 0)
#    puts "wait"
#    performAction('wait', 2)
#    
#    if $i>3
#      puts "swipe"
#      performAction('drag',90,10,50,50,10)
#    end
#    if $i>6
#      puts "swipe"
#      performAction('drag',90,10,50,50,10)
#    end
#    #performAction('wait', 10)
#    puts "product"
#    performAction('press_list_item', $f, 0)
#    puts "wait"
#    performAction('wait', 2)
#    if $i == 0
#      step "I press Got it"
#    end
#      
#    puts "add to cart"
#    performAction('click_on_text',@addtocart.to_s)
#    puts "wait"
#    performAction('wait', 5)
#    puts "go to cart"
#    performAction('press',@gotocart.to_s)
#    #home
#    #performAction('click_on_view_by_id',@navigation.to_s)
#    #performAction('click_on_text',@home.to_s)
#    
#    $i += 1
#    puts $i
#    $f += 1
#    puts $f
#    if $f>3
#      $f = 0
#      puts  $f
#    end
#  end
#end

Then /^I press Proceed to Checkout$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@proceed_to_checkout.to_s+"'}"],:timeout => 20)
  touch("* {text CONTAINS '"+@proceed_to_checkout.to_s+"'}")
#  performAction('click_on_text', @proceed_to_checkout.to_s)
end

Then /^I press Next$/ do
  touch("* {text CONTAINS '"+@next.to_s+"'}")
#  performAction('click_on_text', @next.to_s)
end

Then /^I press Pay on Delivery$/ do
  touch("* {text CONTAINS '"+@pay_on_delivery.to_s+"'}")
#  performAction('click_on_text', @pay_on_delivery.to_s)
end

Then /^I press Confirm Order$/ do
  touch("* {text CONTAINS '"+@confirm_order.to_s+"'}")
#  performAction('click_on_text', @confirm_order.to_s)
end

Then /^I press Order Status$/ do
  touch("* {text CONTAINS '"+@order_status.to_s+"'}")
#  performAction('click_on_text', @order_status.to_s)
end

Then /^I press Track Order$/ do
  tap_when_element_exists("* marked:'"+"btn_track_order"+"'")
#  performAction('click_on_view_by_id', "btn_track_order")
end 
 
Then /^I click on the search bar$/ do
  tap_when_element_exists("* marked:'"+"abs__search_button"+"'")
#  performAction('click_on_view_by_id', "abs__search_button")
end

Then /^I click on search icon$/ do
  tap_when_element_exists("* marked:'"+"cucumber_click_search"+"'")
#  performAction('click_on_view_by_id', "cucumber_click_search")
  #performAction('press_button_with_text', "ic_search_bar_normal")
  #query("button index:1")
end
  
Then /^I click in Filter$/ do
  tap_when_element_exists("* marked:'"+"products_list_filter_button"+"'")
#  performAction('click_on_view_by_id', "products_list_filter_button")
end

Then /^I press Done$/ do
  touch("* {text CONTAINS '"+@done.to_s+"'}")
#  performAction('click_on_text', @done.to_s)
end

Then /^I press on clear all$/ do
  touch("* {text CONTAINS '"+@clear_all.to_s+"'}")
#  performAction('click_on_text', @clear_all.to_s)
end

Then /^I press the Jumia logo$/ do
  tap_when_element_exists("* marked:'"+"ic_logo"+"'")
#  performAction('click_on_view_by_id', "ic_logo")
end

Then /^I lock the device$/ do
  system("adb shell input keyevent 26");
end

Then /^I press on a related item$/ do
  performAction('click_on_screen',20, 80)
end

Then /^I check newsletter$/ do
  touch("* {text CONTAINS '"+@newsletter_checkbox.to_s+"'}")
#  performAction('click_on_text', @newsletter_checkbox.to_s)
end

Then /^I enter email notifications$/ do
  touch("* {text CONTAINS '"+@email_notifications.to_s+"'}")
#    performAction('click_on_text', @email_notifications.to_s)
end

Then /^I enter save$/ do
  touch("* {text CONTAINS '"+@save.to_s+"'}")
#      performAction('click_on_text', @save.to_s)
end

Then /^I check newsletter male$/ do
  touch("* {text CONTAINS '"+@newsletter_male.to_s+"'}")
#        performAction('click_on_text', @newsletter_male.to_s)
end

Then /^I press the newsletter Male$/ do
  tap_when_element_exists("* marked:'"+"newsletter_first_btn"+"'")
#  performAction('click_on_view_by_id', "newsletter_first_btn")
end

Then /^I click on register$/ do
  performAction('click_on_screen', 50, 90)
end

Then /^I click on my profile tab$/ do
  touch("* {text CONTAINS '"+@myprofile.to_s+"'}")
#  performAction('click_on_text', @myprofile.to_s)
end

Then /^I click on menu$/ do
  touch("* {text CONTAINS '"+@menu.to_s+"'}")
#  performAction('click_on_text', @menu.to_s)
end

Then /^I enter Categories$/ do 
  touch("* {text CONTAINS '"+@categories.to_s+"'}")
#  performAction('click_on_text', @categories.to_s)
end

Then /^I press the button to change the view$/ do
  tap_when_element_exists("* marked:'"+"products_switch_layout_button"+"'")
#  performAction('click_on_view_by_id', "products_switch_layout_button")
end

Then /^I click on the favorite icon$/ do
  tap_when_element_exists("* marked:'"+"product_detail_image_is_favourite"+"'")
#  performAction('click_on_view_by_id', "image_is_favourite")
end

Then /^I click on search$/ do
  tap_when_element_exists("* marked:'"+"menu_search"+"'")
#  performAction('click_on_view_by_id', "menu_search")
end

Then /^I enter recent searches$/ do
  touch("* {text CONTAINS '"+@recente_searches.to_s+"'}")
#  performAction('click_on_text', @recente_searches.to_s)
end

Then /^I enter my favorites$/ do
  touch("* {text CONTAINS '"+@my_favourites.to_s+"'}")
#  performAction('click_on_text', @my_favourites.to_s)
end

Then /^I press on other country$/ do
	case $country
	when "ug"
	  step "I press list item number 1"
	else
	  step "I press list item number 6"
	end
end

Then /^I click on cart$/ do
  touch("* {text CONTAINS '"+@shopping_cart.to_s+"'}")
#  performAction('click_on_text', @shopping_cart.to_s)
end

Then /^I click on home$/ do
  touch("* {text CONTAINS '"+@home.to_s+"'}")
#  performAction('click_on_text', @home.to_s)
end

Then /^I press the back button$/ do
  touch("* {text CONTAINS '"+@back_button.to_s+"'}")
#  performAction('click_on_text', @back_button.to_s)
end

Then /^I click on the overflow button$/ do
  tap_when_element_exists("* marked:'image_myprofile'")
#  performAction('click_on_view_by_id', 'image_myprofile')
end

Then /^I sould see the splash screen$/ do
  tap_when_element_exists("* marked:'imgView'")
#  performAction('click_on_view_by_id', "imgView")
end

Then /^I enter recently viewed$/ do
  touch("* {text CONTAINS '"+@recently_viewed.to_s+"'}")
#  performAction('click_on_text', @recently_viewed.to_s)
end

Then /^I enter track my order$/ do
  touch("* {text CONTAINS '"+@track_my_order.to_s+"'}")
#  performAction('click_on_text', @track_my_order.to_s)
end

Then /^I click on remember user email$/ do
  touch("* {text CONTAINS '"+@remember_my_email.to_s+"'}")
#  performAction('click_on_text', @remember_my_email.to_s)
end

Then /^I press gridview item number (\d+)$/ do |number|
  touch("* {text CONTAINS '"+@currency+"'}")
#  tap_when_element_exists("android.widget.GridView index:1")
end

Then /^I press Add new address$/ do
  touch("* {text CONTAINS '"+@add_new_address+"'}")
end

Then /^I click on scroll up button$/ do
  
  tap_when_element_exists("* marked:'btn_toplist'")
end

Then /^I press the image$/ do
  #tap_when_element_exists("* marked:''")
  perform_action('click_on_screen',30, 50)
end

Then /^I press the close button on image$/ do
  tap_when_element_exists("* marked:'gallery_button_close'")
end

Then /^I Press the delete button$/ do
  tap_when_element_exists("android.widget.Button marked:'Delete'")
end

Then /^I Press the call to order button$/ do
  touch("* {text CONTAINS '"+@call_to_order+"'}")
end

Then /^I press add all items to cart$/ do
  touch("* {text CONTAINS '"+@add_all_items_to_cart+"'}")
end

Then /^I press continue shopping$/ do
  touch("* {text CONTAINS '"+@continue_shopping+"'}")
end

Then /^I touch the signup area$/ do
  touch("* {id CONTAINS 'checkout_signup_toogle'}")
end

Then /^I touch the signup button$/ do
  touch("* {id CONTAINS 'checkout_signup_form_button_enter'}")
end

Then /^I touch the female button$/ do
  touch("* {text CONTAINS '"+@female+"'}")
end

Then /^I press my order history$/ do
  touch("* {text CONTAINS '"+@my_order_history+"'}")
end