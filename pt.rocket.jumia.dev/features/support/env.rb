require 'calabash-android/cucumber'

def initvars
  @username="testcalabash@mailinator.com"
  @password="password1"
  @firstname="tester"
  @lastname="Test"
    
  @navigation="abs__home"
  @login_username="email"
  @login_password="password"
  
  @register_password2="password2"
  @register_birthday="birthday"
  @register_gender="male"
  @register_last_name="last_name"
  @register_first_name="first_name"

  @search_field="Search Field"
  @search_input="Search input"  
  @newpassword="New Password"
  @newrepeatedpassword="New Repeated Password"
  @addtocart="Add to Cart"
  @gotocart="Go to Cart"
  @proceedcheckout="Proceed to checkout"
end



def initvars_KE
  @venture_name="Kenya"
  @sign_in="Sign In"
  @wrong_username="faketester@tester.tt"
  @invalidsearch= "add" 
  @search= "nikon" 
  @myaccount="My Account"
  @myinfo="User Data"
  @categories="Categories"
  @categoryfashion="Books"
  @categoryfashionall="All for Books"
  
  @checkoutNext="Next"
  @checkout1="TestPayment"
  @checkout3="Confirm Order"

  
end


def initvars_IC
  @venture_name="Ivory Coast"
  @hint_IC="ron"
  @search_IC= "Rondônia"
  
  @restaurant_IC="Restaurante TESTE HelloFood"
  @category_IC="Pizzas Salgadas"
  @product_IC="Pizza: Atum"
  @variation_IC=""
  @choicetopping_IC=""
end



def initvars_MA
  @venture_name="Morocco"
  @hint_MA="Santiago"
  @search_MA= "Santiago, Chile"
  
  @restaurant_MA="Alcohol Center"
  @category_MA="Vodkas"
  @product_MA="Absolute Blue"
  @variation_MA="Solo"
  @choicetopping_MA=""
end

def initvars_NG
  @venture_name="Nigeria"
  @hint_NG="bo"
  @search_NG= "Bogotá, Colombia"
  
  @restaurant_NG="Mister Lee - Carrefour de la 30"
  @category_NG="Entradas"
  @product_NG="Alitas Colombia"
  @variation_NG=""
  @choicetopping_NG=""
end


