#
# CLICKS AND PRESS
#
# click_on_view_by_id
# click_on_text
# press

Then /^I go to home$/ do
  performAction('click_on_text',@home.to_s)
end

Then /^I open the navigation menu$/ do 
  performAction('click_on_view_by_id', @navigation.to_s)
end

Then /^I close the navigation menu$/ do 
  performAction('click_on_view_by_id',@navigation.to_s)
end

Then /^I press Login Button$/ do
  performAction('click_on_view_by_id', @login_button_id.to_s)
end

Then /^I press the create account button$/ do
  performAction('click_on_view_by_id', @createaccbutton_id.to_s)
end

Then /^I press the register button$/ do
  performAction('click_on_text', @register.to_s)
end

Then /^I press Submit$/ do
  performAction('click_on_view_by_id', @submit.to_s)
end

Then /^I press Rating$/ do
  performAction('click_on_view_by_id', @rating.to_s)
end

Then /^I go to cart$/ do 
  performAction('press',@gotocart.to_s)
end

Then /^I press the cart icon$/ do
  performAction('press', @gotocart.to_s)
end

Then /^I choose the Sign In option$/ do
  performAction('click_on_text',@sign_in.to_s)
end

Then /^I press Logout Button$/ do
  performAction('click_on_text',@signout.to_s)
  performAction('click_on_text',@yes.to_s)
end

Then /^I press Ok$/ do
  performAction('click_on_text',@ok.to_s)
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

Then /^I press Terms and Conditions$/ do
  performAction('click_on_text',@termsandconditions.to_s)
end

Then /^I select the country$/ do
  performAction('click_on_text',@venture_name.to_s+@dev.to_s )
  #puts @venture_name.to_s+@dev.to_s
end

Then /^I click forgot password$/ do
  performAction('click_on_text', @forgot_password.to_s)
end

Then /^I press Got it$/ do
  performAction('click_on_text', @got_it.to_s)
end

Then /^I press Choose Country$/ do
  performAction('click_on_text', @choose_country.to_s)
end

Then /^I press Yes$/ do
  performAction('click_on_text',@yes.to_s)
end

Then /^I press Search$/ do
  performAction('click_on_text', @search.to_s)
end

Then /^I proceed to checkout$/ do 
  performAction('press',@proceedcheckout.to_s)
end

Then /^I press male$/ do 
  performAction('press',@male.to_s)
end

Then /^I press birthday$/ do
  performAction('press',@birthday.to_s)
end

Then /^I check Terms and Conditions$/ do
  case $country.to_s
  when "teste"
    performAction('press',@termsandconditionscheck.to_s)
  else
    
  end
  
  
end

Then /^I press Save$/ do
  performAction('press', @save.to_s)
end

Then /^I press product specifications$/ do
  performAction('click_on_text', @specification.to_s)
end

Then /^I press a review$/ do
  performAction('click_on_text', @posted_by.to_s)
end

Then /^I press Write a Review$/ do
  performAction('click_on_text', @write_review.to_s)
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
    performAction('click_on_view_by_id', item["id"])
  end
end

Then /^I press Send Review$/ do
  performAction('click_on_text', @send_review.to_s)
end

Then /^I press to reviews$/ do
  performAction('click_on_text', @to_reviews.to_s)
end

Then /^I press share$/ do
  #@share=query("ShareActionProvider")
  #puts @share
  #performAction('click_on_view_by_id', "menu_share")
  performAction('press', "Share with")
  
end

Then /^I press Messaging$/ do
  performAction('click_on_text', @messaging.to_s)
end

Then /^I press delete product$/ do
  performAction('click_on_text', @delete.to_s)
end

Then /^I change the product quantity(| to zero)$/ do |zero|
  performAction('click_on_view_by_id', "changequantity_button")
  case zero
  when " to zero"
    performAction('click_on_text', "0")
  else
    performAction('click_on_text', "2")    
  end
end

Then /^I add multiple products$/ do
  $i= 0
  $f= 0
  while $i<10 do
    performAction('click_on_view_by_id',@navigation.to_s)
    puts "navigation"
    performAction('click_on_text',@categories.to_s)
    puts "category"
    performAction('click_on_text',@categoryfashion.to_s)
    puts "wait"
    performAction('wait', 2)
    puts "subcategory"
    performAction('press_list_item', 1, 0)
    puts "wait"
    performAction('wait', 2)
    
    if $i>3
      puts "swipe"
      performAction('drag',90,10,50,50,10)
    end
    if $i>6
      puts "swipe"
      performAction('drag',90,10,50,50,10)
    end
    #performAction('wait', 10)
    puts "product"
    performAction('press_list_item', $f, 0)
    puts "wait"
    performAction('wait', 2)
    if $i == 0
      step "I press Got it"
    end
      
    puts "add to cart"
    performAction('click_on_text',@addtocart.to_s)
    puts "wait"
    performAction('wait', 5)
    puts "go to cart"
    performAction('press',@gotocart.to_s)
    #home
    #performAction('click_on_view_by_id',@navigation.to_s)
    #performAction('click_on_text',@home.to_s)
    
    $i += 1
    puts $i
    $f += 1
    puts $f
    if $f>3
      $f = 0
      puts  $f
    end
  end
end

  Then /^I press Proceed to Checkout$/ do
    performAction('click_on_text', @proceed_to_checkout.to_s)
  end
  
  Then /^I press Next$/ do
    performAction('click_on_text', @next.to_s)
  end
  
  Then /^I press Pay on Delivery$/ do
    performAction('click_on_text', @pay_on_delivery.to_s)
  end
  
  Then /^I press Confirm Order$/ do
    performAction('click_on_text', @confirm_order.to_s)
  end
  
  Then /^I press Order Status$/ do
    performAction('click_on_text', @order_status.to_s)
  end
  
  Then /^I press Track Order$/ do
    performAction('click_on_view_by_id', "btn_track_order")
  end 
   
  Then /^I click on the search bar$/ do
    performAction('click_on_view_by_id', "search_component")
  end
  
  Then /^I click in Filter$/ do
    performAction('click_on_text', @filter.to_s)
  end
  
  Then /^I press Done$/ do
    performAction('click_on_text', @done.to_s)
  end
  
  Then /^I press on clear all$/ do
    performAction('click_on_text', @clear_all.to_s)
  end
  
  Then /^I press the Jumia logo$/ do
    performAction('click_on_view_by_id', "ic_logo")
  end
  
  Then /^I lock the device$/ do
    system("adb shell input keyevent 26");
  end
  
  Then /^I press on a related item$/ do
    performAction('click_on_screen',20, 80)
  end
  
  Then /^I check newsletter$/ do
    performAction('click_on_text', @newsletter_checkbox.to_s)
  end
  
  Then /^I enter email notifications$/ do
      performAction('click_on_text', @email_notifications.to_s)
  end
  
  Then /^I enter save$/ do
        performAction('click_on_text', @register.to_s)
  end
  
  Then /^I check newsletter male$/ do
          performAction('click_on_text', @newsletter_male.to_s)
  end

  Then /^I press the newsletter Male$/ do
    performAction('click_on_view_by_id', "newsletter_male_btn")
  end
  
  Then /^I click on register$/ do
    performAction('click_on_screen', 50, 90)
  end