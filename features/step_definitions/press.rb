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
  wait_for_elements_exist(["* {text CONTAINS '"+@home.to_s+"'}"],:timeout => 30)
  touch("* {text CONTAINS '"+@home.to_s+"'}")
end

Then /^I open the navigation menu$/ do 
  wait_for_elements_exist(["* marked:'"+@navigation.to_s+"'"],:timeout => 30)
  tap_when_element_exists("* marked:'"+@navigation.to_s+"'")
end

Then /^I close the navigation menu$/ do 
  wait_for_elements_exist(["* marked:'"+@navigation.to_s+"'"],:timeout => 30)
  tap_when_element_exists("* marked:'"+@navigation.to_s+"'")
end

Then /^I press Login Button$/ do
  wait_for_elements_exist(["* marked:'"+@login_button_id.to_s+"'"],:timeout => 30)
  tap_when_element_exists("* marked:'"+@login_button_id.to_s+"'")
end

Then /^I press the create account button$/ do
  wait_for_elements_exist(["* marked:'"+@createaccbutton.to_s+"'"],:timeout => 30)
  tap_when_element_exists("* marked:'"+@createaccbutton_id.to_s+"'")
end

Then /^I press the register button$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@register.to_s+"'}"],:timeout => 30)
  touch("* {text CONTAINS '"+@register.to_s+"'}")
end

Then /^I press Submit$/ do
  wait_for_elements_exist(["* marked:'"+@submit.to_s+"'"],:timeout => 30)
  tap_when_element_exists("* marked:'"+@submit.to_s+"'")
end

Then /^I press Rating$/ do
  wait_for_elements_exist(["* marked:'"+@rating.to_s+"'"],:timeout => 30)
  tap_when_element_exists("* marked:'"+@rating.to_s+"'")
end

Then /^I go to cart$/ do 
  wait_for_elements_exist(["* marked:'"+@gotocart.to_s+"'"],:timeout => 30)
  touch("* marked:'"+@gotocart.to_s+"'")
end

Then /^I press the cart icon$/ do
  wait_for_elements_exist(["* marked:'"+@gotocart.to_s+"'"],:timeout => 20)
  touch("* marked:'"+@gotocart.to_s+"'")
end

Then /^I choose the Sign In option$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@signin.to_s+"'}"],:timeout => 20)
  touch("* {text CONTAINS '"+@signin.to_s+"'}")
end

Then /^I press Logout Button$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@signout.to_s+"'}"],:timeout => 30)
  touch("* {text CONTAINS '"+@signout.to_s+"'}")
end

Then /^I press Ok$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@ok.to_s+"'}"],:timeout => 30)
  touch("* {text CONTAINS '"+@ok.to_s+"'}")
end

Then /^I enter My Account$/ do 
  wait_for_elements_exist(["* {text CONTAINS '"+@myaccount.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@myaccount.to_s+"'}")
end

Then /^I enter My User Data$/ do 
  wait_for_elements_exist(["* {text CONTAINS '"+@myinfo.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@myinfo.to_s+"'}")
end

Then /^I enter a valid Category$/ do 
  wait_for_elements_exist(["* {text CONTAINS '"+@categoryfashion.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@categoryfashion.to_s+"'}")
end

Then /^I add product to cart$/ do 
  wait_for_elements_exist(["* marked:'"+@product_detail_shop.to_s+"'"],:timeout => 30)
  touch("* marked:'"+@product_detail_shop.to_s+"'")
end

Then /^I press Terms and Conditions$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@termsandconditions.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@termsandconditions.to_s+"'}")
end

Then /^I select the country$/ do
  wait_for_elements_exist(["* marked:'"+@venture_name.to_s+"'"],:timeout => 20)
  touch("* {text CONTAINS '"+@venture_name.to_s+@dev.to_s+"'}")
end

Then /^I click forgot password$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@forgot_password.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@forgot_password.to_s+"'}")
end

Then /^I press Got it$/ do
  wait_for_elements_exist(["* marked:'"+@tips_got_it.to_s+"'"],:timeout => 20)
  touch("* marked:'"+@tips_got_it.to_s+"'")
end

Then /^I press Choose Country$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@choose_country.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@choose_country.to_s+"'}")
end

Then /^I press Yes$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@yes.to_s+"'}"],:timeout => 20)
  touch("* {text CONTAINS '"+@yes.to_s+"'}")
end

Then /^I press Search$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@search.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@search.to_s+"'}")
end

Then /^I proceed to checkout$/ do 
  wait_for_elements_exist(["* {text CONTAINS '"+@proceedcheckout.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@proceedcheckout.to_s+"'}")
end

Then /^I press male$/ do 
  wait_for_elements_exist(["* marked:'"+@male.to_s+"'"],:timeout => 20)
  touch("* marked:'"+@male.to_s+"'")
end

Then /^I press birthday$/ do
  wait_for_elements_exist(["button marked:'birthday'"],:timeout => 20)
  touch("* marked:'birthday'")
  wait_for_elements_exist(["datePicker"],:timeout => 20)
  query("datePicker",:method_name =>'updateDate',:arguments =>[1987,00,01])
  #wait_for_elements_exist(["button marked:'button2'"],:timeout => 20)
  step "I wait for 5 seconds"
  touch("* marked:'OK'")
end

Then /^I check Terms and Conditions$/ do
  case $country.to_s
  when "teste"
    wait_for_elements_exist(["* marked:'"+@termsandconditionscheck.to_s+"'"],:timeout => 20)
    touch("* marked:'"+@termsandconditionscheck.to_s+"'")
  else
    
  end
  
  
end

Then /^I press Save$/ do
  wait_for_elements_exist(["* marked:'"+@save.to_s+"'"],:timeout => 20)
  touch("* marked:'"+@save.to_s+"'")
end

Then /^I press product specifications$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@specification.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@specification.to_s+"'}")
end

Then /^I press a review$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@posted_by.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@posted_by.to_s+"'}")
end

Then /^I press Write a Review$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@write_review.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@write_review.to_s+"'}")
end

#Then /^I press rating$/ do
 # @collection=query("RatingBar")
  #puts @collection.length
  #id = 77
  #@collection.each do |item|
   # item["id"] = id.to_s
    #id+=1
    #puts "type is :"
    #puts item["rect"]["x"]
    #tap_when_element_exists("* marked:'"+item["id"]+"'")
#    performAction('click_on_view_by_id', item["id"])
  #end
#end

Then /^I press Send Review$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@send_review.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@send_review.to_s+"'}")
end

Then /^I press to reviews$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@to_reviews.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@to_reviews.to_s+"'}")
end

Then /^I press share$/ do
  tap_when_element_exists("* marked:'"+"abs__search_button"+"'")  
end

Then /^I press Messaging$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@messaging.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@messaging.to_s+"'}")
end

Then /^I press delete product$/ do
  wait_for_elements_exist(["* {contentDescription CONTAINS '"+@delete.to_s+"'}"],:timeout => 40)
  touch("* {contentDescription CONTAINS '"+@delete.to_s+"'}")
end

Then /^I reset the product quantity$/ do
  tap_when_element_exists("* marked:'"+"changequantity_button"+"'")
  sleep(3)
  scroll_up
  sleep(3)
  wait_for_elements_exist(["TextView marked:'item_text' index:0"],:timeout => 40)
  touch("TextView marked:'item_text' index:0")
end

#Then /^I change the product quantity(| to zero)$/ do |zero|
Then /^I change the product quantity$/ do
  tap_when_element_exists("* marked:'"+"changequantity_button"+"'")
  touch("*2")
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
end

Then /^I press Next$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@next.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@next.to_s+"'}")
end

Then /^I press Cash on Delivery$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@cod.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@cod.to_s+"'}")
end

Then /^I press Confirm Order$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@confirm_order.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@confirm_order.to_s+"'}")
end

Then /^I press Order Status$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@order_status.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@order_status.to_s+"'}")
end

Then /^I press Track Order$/ do
  tap_when_element_exists("* marked:'"+"btn_track_order"+"'")
end 
 
Then /^I click on the search bar$/ do
  tap_when_element_exists("* marked:'"+"abs__search_button"+"'")
end

Then /^I click on search icon$/ do
  tap_when_element_exists("* marked:'"+"cucumber_click_search"+"'")
end
  
Then /^I click in Filter$/ do
  tap_when_element_exists("* marked:'"+"products_list_filter_button"+"'")
end

Then /^I press Done$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@done.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@done.to_s+"'}")
end

Then /^I press on clear all$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@clear_all.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@clear_all.to_s+"'}")
end

Then /^I press the Jumia logo$/ do
  tap_when_element_exists("* marked:'"+"ic_logo"+"'")
end

Then /^I lock the device$/ do
  system("adb shell input keyevent 26");
end

Then /^I press on a related item$/ do
  performAction('click_on_screen',20, 80)
end

Then /^I check newsletter$/ do
  wait_for_elements_exist(["* marked:'"+@newsletter_cat_sub+"'"],:timeout => 20)
  touch("* marked:'"+@newsletter_cat_sub+"'")
end

Then /^I check receive newsletter$/ do
  wait_for_elements_exist(["* marked:'"+@newsletter_receive+"'"],:timeout => 20)
  touch("* marked:'"+@newsletter_receive+"'")
end

Then /^I enter email notifications$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@email_notifications.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@email_notifications.to_s+"'}")
end

Then /^I enter save$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@save.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@save.to_s+"'}")
end

Then /^I check newsletter male$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@newsletter_male.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@newsletter_male.to_s+"'}")
end

Then /^I press the newsletter Male$/ do
  tap_when_element_exists("* marked:'"+"newsletter_first_btn"+"'")
end

Then /^I click on register$/ do
  performAction('click_on_screen', 50, 90)
end

Then /^I click on my profile tab$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@myprofile.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@myprofile.to_s+"'}")
end

Then /^I click on menu$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@menu.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@menu.to_s+"'}")
end

Then /^I enter Categories$/ do 
  wait_for_elements_exist(["* {text CONTAINS '"+@categories.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@categories.to_s+"'}")
end

Then /^I press the button to change the view$/ do
  tap_when_element_exists("* marked:'"+"products_switch_layout_button"+"'")
end

Then /^I click on the favorite icon$/ do
  tap_when_element_exists("* marked:'"+"product_detail_image_is_favourite"+"'")
end

Then /^I click on search$/ do
  tap_when_element_exists("* marked:'"+"menu_search"+"'")
end

Then /^I enter recent searches$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@recente_searches.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@recente_searches.to_s+"'}")
end

Then /^I enter my favorites$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@my_favourites.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@my_favourites.to_s+"'}")
end

Then /^I press on other country$/ do
	case $country
	when "ug"
	  step "I press list item number 1"
	when "ng"
	  step "I press list item number 8"
	else
	  step "I press list item number 6"
	end
end

Then /^I click on cart$/ do
  wait_for_elements_exist(["* marked:'"+@nav_basket_title.to_s+"'"],:timeout => 40)
  touch("* marked:'"+@nav_basket_title.to_s+"'")
end

Then /^I click on home$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@home.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@home.to_s+"'}")
end

Then /^I press the back button$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@back_button.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@back_button.to_s+"'}")
end

Then /^I click on the overflow button$/ do
  tap_when_element_exists("* marked:'image_myprofile'")
end

Then /^I sould see the splash screen$/ do
  tap_when_element_exists("* marked:'imgView'")
end

Then /^I enter recently viewed$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@recently_viewed.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@recently_viewed.to_s+"'}")
end

Then /^I enter track my order$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@track_my_order.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@track_my_order.to_s+"'}")
end

Then /^I click on remember user email$/ do
  wait_for_elements_exist("* marked:'login_remember_user_email'")
  touch("* marked:'login_remember_user_email'")
end

Then /^I press gridview item number (\d+)$/ do |number|
  wait_for_elements_exist(["* {text CONTAINS '"+@currency.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@currency.to_s+"'}")
#  tap_when_element_exists("android.widget.GridView index:1")
end

Then /^I press grid item number (\d+)$/ do |number|
  #touch("* {text CONTAINS '"+@currency+"'}")
  #tap_when_element_exists("android.widget.GridView index:1")
  wait_for_elements_exist(["GridView"],:timeout => 40)
  touch("GridView")
end

Then /^I press Add new address$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@add_new_address.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@add_new_address.to_s+"'}")
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
  tap_when_element_exists("* marked:'button_delete'")
end

Then /^I Press the call to order button$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@call_to_order.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@call_to_order+"'}")
end

Then /^I press add all items to cart$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@add_all_items_to_cart.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@add_all_items_to_cart.to_s+"'}")
end

Then /^I press continue shopping$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@continue_shopping.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@continue_shopping.to_s+"'}")
end

Then /^I touch the signup area$/ do
  wait_for_elements_exist(["* {id CONTAINS 'checkout_signup_toogle'}"],:timeout => 40)
  touch("* {id CONTAINS 'checkout_signup_toogle'}")
end

Then /^I touch the signup button$/ do
  wait_for_elements_exist(["* {id CONTAINS 'checkout_signup_form_button_enter'}"],:timeout => 40)
  touch("* {id CONTAINS 'checkout_signup_form_button_enter'}")
end

Then /^I touch the female button$/ do
  wait_for_elements_exist(["* {text CONTAINS '"+@female.to_s+"'}"],:timeout => 40)
  touch("* {text CONTAINS '"+@female.to_s+"'}")
end

Then /^I press my order history$/ do
  touch(nil, :offset => {:x => 850, :y => 250})
  #touch("* {text CONTAINS '"+@my_order_history+"'}")
  #touch("* marked:'"+@orders_container_title+"'")
end

Then /^I press Facebook Login Button$/ do
  wait_for_elements_exist(["* marked:'"+@facebook_text_view.to_s+"'"],:timeout => 30)
  tap_when_element_exists("* marked:'"+@facebook_text_view.to_s+"'")
end