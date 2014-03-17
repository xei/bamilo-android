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
  
  #password recovery
  @submit="submit_button"
  @pass_rec_email=""
  
  
  
  #initializes the country variables
  case $country.to_s
    ##### Kenya  ############################################ KE
    when "ke"
    @venture_name="Kenya"
    @sign_in="Sign In"
    @wrong_username="faketester@tester.tt"
    @invalidsearch= "addddd" 
    @search_p= "nikon" 
    @search="Search"
    @myaccount="My Account"
    @myinfo="User Data"
    @categories="Categories"
    @categoryfashion="Books"
    @choose_country="Choose Country"
    @order_status="Order Status"
    
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
   
    #password recovery
    @forgot_password="Forgot Password?"
    @password_recovery="Password Recovery"
    @pass_rec_empty_email="Please fill in the E-Mail"
    @pass_rec_failed="Password recovery failed"
    @pass_rec_sent="Email sent"
    
    #cart
    @item_added="was added to shopping cart"
    @no_items="You have no items in the cart"
    @got_it="Got it"
    @clear_cart_message="This clears the cart"
    
    #catalog
    @currency= "KSh"
    @loading_items="Loading more items"
    @popularity="POPULARITY"
    @price_up="PRICE UP"
    @price_down="PRICE DOWN"
    @name="NAME"
    @brand="BRAND"
    
    #product detail
    @first_tip="Swipe left or right to"
    @second_tip="Tap to open the product gallery"
    @specification="Specification"
    
    ##### Ivory Coast ####################################### IC          
    when "ic"
    @venture_name="Ivory Coast"
    @sign_in="Accéder au compte"
    @wrong_username="faketester@tester.tt"
    @invalidsearch= "addddd" 
    @search_p= "caresse Sava" 
    @search="Rechercher"
    @myaccount="Mon compte"
    @myinfo="Les données de"
    @categories="Catégories"
    @categoryfashion="Informatique"
    @choose_country="Choisir le pays"
    @order_status="Suivi de commande"
     
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
    
    #password recovery
    @forgot_password="Mot de passe oublié ?"
    @password_recovery="Récupération de mot de passe"
    @pass_rec_empty_email="S'il vous plait écrire dans le Email"
    @pass_rec_failed="Le renouvellement du mot de passe a échoué"
    @pass_rec_sent="Email envoyé"
    
    #cart
    @item_added="a été ajouté au panier"
    @no_items="Vous n'avez pas d'articles dans le panier"
    @got_it="J'ai Compris"
    @clear_cart_message="Ceci efface le panier"
    
    #catalog
    @currency= "FCFA"
    @loading_items="Charger plus de produits"
    @popularity="POPULARITÉ"
    @price_up="PRIX CROISSANT"
    @price_down="PRIX DÉCROISSANT"
    @name="NOM"
    @brand="MARQUE"
    
    #product detail
    @first_tip="Balayez vers la gauche ou la droite pour"
    @second_tip="Cliquez pour voir l'ensemble"
    @specification="Caractéristiques"
      
    ##### Morocco ########################################### MA
    when "ma"
    @venture_name="Morocco"
    @sign_in="Accéder au compte"
    @wrong_username="faketester@tester.tt"
    @invalidsearch= "addddf" 
    @search_p= "Pierre Pelot" 
    @search="Rechercher"
    @myaccount="Mon compte"
    @myinfo="Les données de"
    @categories="Catégories"
    @categoryfashion="Informatique"
    @choose_country="Choisir le pays"
    @order_status="Suivre la commande"
    
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
    
    #password recovery
    @forgot_password="Mot de passe oublié ?"
    @password_recovery="Récupération de mot de passe"
    @pass_rec_empty_email="S'il vous plait écrire dans le Email"
    @pass_rec_failed="Le renouvellement du mot de passe a échoué"
    @pass_rec_sent="Email envoyé"
    
    #cart
    @item_added="a été ajouté au panier"
    @no_items="Vous n'avez pas d'articles dans le panier"
    @got_it="J'ai Compris"
    @clear_cart_message="Ceci efface le panier"
    
    #catalog
    @currency= "Dhs"
    @loading_items="Charger plus de produits"
    @popularity="POPULARITÉ"
    @price_up="PRIX CROISSANT"
    @price_down="PRIX DÉCROISSANT"
    @name="NOM"
    @brand="MARQUE"
    
    #product detail
    @first_tip="Balayez vers la gauche ou la droite pour"
    @second_tip="Cliquez pour voir l'ensemble"
    @specification="Caractéristiques"
      
    ##### Nigeria ########################################### NG
    when "ng"
    @venture_name="Nigeria"
    @sign_in="Sign In"
    @wrong_username="faketester@tester.tt"
    @invalidsearch= "adddddf" 
    @search_p= "nikon" 
    @search="Search"
    @myaccount="My Account"
    @myinfo="User Data"
    @categories="Categories"
    @categoryfashion="Computing"
    @choose_country="Choose Country"
    @order_status="Order Status"
    
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
        
    #password recovery
    @forgot_password="Forgot Password?"
    @password_recovery="Password Recovery"
    @pass_rec_empty_email="Please fill in the E-Mail"
    @pass_rec_failed="Password recovery failed"
    @pass_rec_sent="Email sent"
    
    #cart
    @item_added="was added to shopping cart"
    @no_items="You have no items in the cart"
    @got_it="Got it"
    @clear_cart_message="This clears the cart"
    
    #catalog
    @currency= "₦"
    @loading_items="Loading more items"
    @popularity="POPULARITY"
    @price_up="PRICE UP"
    @price_down="PRICE DOWN"
    @name="NAME"
    @brand="BRAND"
    
    #product detail
    @first_tip="Swipe left or right to"
    @second_tip="Tap to open the product gallery"
    @specification="Specification"
      
    ##### Egypt ############################################# EG
    when "eg"
    @venture_name="Egypt"
    @sign_in="Sign In"
    @wrong_username="faketester@tester.tt"
    @invalidsearch= "adddddf" 
    @search_p= "nikon" 
    @search="Search" 
    @myaccount="My Account"
    @myinfo="User Data"
    @categories="Categories"
    @categoryfashion="Books"
    @choose_country="Choose Country"
    @order_status="Order Status"
    
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
    
    #password recovery
    @forgot_password="Forgot Password?"
    @password_recovery="Password Recovery"
    @pass_rec_empty_email="Please fill in the E-Mail"
    @pass_rec_failed="Password recovery failed"
    @pass_rec_sent="Email sent"
    
    #cart
    @item_added="was added to shopping cart"
    @no_items="You have no items in the cart"
    @got_it="Got it"
    @clear_cart_message="This clears the cart"
    
    #catalog
    @currency= "EGP"
    @loading_items="Loading more items"
    @popularity="POPULARITY"
    @price_up="PRICE UP"
    @price_down="PRICE DOWN"
    @name="NAME"
    @brand="BRAND"
    
    #product detail
    @first_tip="Swipe left or right to"
    @second_tip="Tap to open the product gallery"
    @specification="Specification"
  end
end