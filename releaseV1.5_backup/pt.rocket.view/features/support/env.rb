# encoding: utf-8
require 'calabash-android/cucumber'

$country = ENV['country']
  
def initvars
  @username="testcalabash@mailinator.com"
  @password="password1"
  @firstname="tester"
  @lastname="Test"
    
  @navigation="abs__home"
  @login_username="email"
  @login_password="password"
  @wrong_password="fakepassword"
  @login_button_id="middle_login_button_signin"
  
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
  @continueshopping="Continue Shopping"
  
  @shippingbtn="shippingbtn"
  @billingbtn="billingbtn"
  @finish="finishbtn"
  @payment="paymentbtn"
  @paymentmethod="paymentmethodbtn"
  
  @yes="Yes"
  @ok="OK"
  @createaccbutton_id="middle_login_link_register"
  @registerbutton_id="register_button_submit"
  @male="male"
  @birthday="birthday"
  @termsandconditionscheck="Terms and Conditions"
  @termsandconditions="Terms and Conditions"
  @password_changed_message="Password changed with success"
  @save="Save"
  @differentpassword="Passwords do not match"
  
  
  #initializes the country variables
  case $country
    when "ke"
      @venture_name="Kenya"
      @sign_in="Sign In"
      @wrong_username="faketester@tester.tt"
      @invalidsearch= "addddd" 
      @search= "nikon" 
      @myaccount="My Account"
      @myinfo="User Data"
      @categories="Categories"
      @categoryfashion="Books"
      
      @checkoutNext="Next"
      @checkout1="TestPayment"
      @checkout3="Confirm Order"
      
      @emailerrormessage="Please fill in";
      @passerrormessage="Please fill in the Password";
      
      @loginerror="Login failed"
      @signout="Sign Out"
      @mandatory="Please fill in the required(*) fields"
      @sameemail="This email already exists."
      @searchdefault="Please enter a term for suggestions!"
      @nosuggest="No suggestions for your search term!"
       
      @addtocart="Add to Cart"
      @home="Home"
      @new_pass_short="The new password has to have at least 6 characters"
      
      
    when "ic"
      @venture_name="Ivory Coast"
      @sign_in="Accéder au compte"
      @wrong_username="faketester@tester.tt"
      @invalidsearch= "addddd" 
      @search= "caresse Sava" 
      @myaccount="Mon compte"
      @myinfo="Les données de"
      @categories="Catégories"
      @categoryfashion="Informatique"
       
      @checkoutNext="Next"
      @checkout1="TestPayment"
      @checkout3="Confirm Order"
        
      @emailerrormessage="dans le Email";
      @passerrormessage="dans le Mot de passe";
       
      @loginerror="L'authentification a échoué"
      @signout="se déconnecter"
      @mandatory="Veillez remplir les champs obligatoires"
      @sameemail="Cet email existe déjà."
      @searchdefault="Entrer votre recherche ici!"
      @nosuggest="Aucune suggestion pour votre recherche!"
      @differentpassword="Le mot de passe ne correspond pas"
        
      @addtocart="Ajouter au panier"
       
      @yes="Oui"
      @termsandconditions="Termes et conditions"
      @password_changed_message="Le mot de passe a été modifié avec succès"
      @home="Accueil"
      @new_pass_short="Le nouveau mot de passe doit comprendre au moins 6 caractères"
      
    when "ma"
      @venture_name="Morocco"
      @sign_in="Accéder au compte"
      @wrong_username="faketester@tester.tt"
      @invalidsearch= "addddf" 
      @search= "Pierre Pelot" 
      @myaccount="Mon compte"
      @myinfo="Les données de"
      @categories="Catégories"
      @categoryfashion="Informatique"
      
      @checkoutNext="Next"
      @checkout1="TestPayment"
      @checkout3="Confirm Order"
      
      @emailerrormessage="dans le Email";
      @passerrormessage="dans le Mot de passe";
      
      @loginerror="L'authentification a échoué"
      @signout="se déconnecter"
      @mandatory="Veillez remplir les champs obligatoires"
      @sameemail="Cet email existe déjà."
      @searchdefault="Entrer votre recherche ici!"
      @nosuggest="Aucune suggestion pour votre recherche!"
      
      @addtocart="Ajouter au panier"
      
      @yes="Oui"
      @termsandconditions="Termes et conditions"
      @password_changed_message="Le mot de passe a été modifié avec succès"
      @differentpassword="Le mot de passe ne correspond pas"
      @home="Accueil"
      @new_pass_short="Le nouveau mot de passe doit comprendre au moins 6 caractères"
      
    when "ng"
      @venture_name="Nigeria"
      @sign_in="Sign In"
      @wrong_username="faketester@tester.tt"
      @invalidsearch= "adddddf" 
      @search= "nikon" 
      @myaccount="My Account"
      @myinfo="User Data"
      @categories="Categories"
      @categoryfashion="Computing"
      
      @checkoutNext="Next"
      @checkout1="TestPayment"
      @checkout3="Confirm Order"
      
      @emailerrormessage="Please fill in the E-Mail";
      @passerrormessage="Please fill in the Password";
      
      @loginerror="Login failed"
      @signout="Sign Out"
      @mandatory="Please fill in the required(*) fields"
      @sameemail="This email already exists."
      @searchdefault="Please enter a term for suggestions!"
      @nosuggest="No suggestions for your search term!"
      
      @addtocart="Add to Cart"
      @home="Home"
      @new_pass_short="The new password has to have at least 6 characters"
      
    when "eg"
      @venture_name="Egypt"
      @sign_in="Sign In"
      @wrong_username="faketester@tester.tt"
      @invalidsearch= "adddddf" 
      @search= "nikon" 
      @myaccount="My Account"
      @myinfo="User Data"
      @categories="Categories"
      @categoryfashion="Computing"
      
      @checkoutNext="Next"
      @checkout1="TestPayment"
      @checkout3="Confirm Order"
      
      @emailerrormessage="Please fill in the E-Mail";
      @passerrormessage="Please fill in the Password";
      
      @loginerror="Login failed"
      @signout="Sign Out"
      @mandatory="Please fill in the required(*) fields"
      @sameemail="This email already exists."
      @searchdefault="Please enter a term for suggestions!"
      @nosuggest="No suggestions for your search term!"
      
      @addtocart="Add to Cart"
      @home="Home"
      @new_pass_short="The new password has to have at least 6 characters"
  end
end