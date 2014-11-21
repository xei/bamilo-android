# encoding: utf-8
require 'calabash-android/cucumber'

$country = ENV['country']
$branch = ENV['branch']
  
def initvars
  
  case $branch.to_s
  when "staging"
    @dev=" Staging"
  else
    @dev=""
  end
  
  #ventures names
  @venture_maroc= "Maroc"
  @venture_cotedivoire= "Côte d'Ivoire"
  @venture_nigeria= "Nigeria"
  @venture_egypt= "Egypt"
  @venture_kenya= "Kenya"
  @venture_uganda= "Uganda"
  @venture_ghana= "Ghana"
  @venture_camerron= "Cameroon"
  
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
  @search_input="search_component"  
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
  
  @differentpassword="Passwords do not match"
  
  #password recovery
  @submit="submit_button"
  @pass_rec_email=""
  
  #rating
  @rating="product_rating_container"
  @review_name="review_name"
  @review_title="review_title"
  @review_title_t="Great Product"
  @review_comment="review_comment"
  @review_comment_t="Arrived Fast"
  @share="share_button"
  
  @country_choose="Choose Country"
  
  @sound = "Sound"
  @vibrate = "Vibrate"
  
  #initializes the country variables
  case $country.to_s
    ##### Kenya  ############################################ KE ##############################################################################
    when "ke"
    @venture_name= @venture_kenya

    @sign_in="Sign"
    @wrong_username="faketester@tester.tt"
    @invalidsearch= "addddd" 
    @search_p= "nikon" 
    @search_word="samsung"
    @search="Search"
    @myaccount="My Account"
    @myinfo="User Data"
    @categories="CATEGORIES"
    @categoryfashion="Mobile Phones"
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
    @home_c="HOME"
    @new_pass_short="The new password has to have at least 6 characters"
   
    #password recovery
    @forgot_password="Forgot Password?"
    @password_recovery="Password Recovery"
    @pass_rec_empty_email="Please fill in the E-Mail"
    @pass_rec_failed="Password recovery failed"
    @pass_rec_sent="Email sent"
    
    #cart
    @item_was_added="was added to shopping cart"
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
    @please_choose="Please choose"
    @search_v="flip flops"
    @product_features="Product Features"
    @product_description="Product Description"
    @write_review="Write a Review"
    @rating_t="posted by"
    @write_review="Write a Review"
    @send_review="Send Review"
    @to_reviews="To reviews"
    @messaging="Messaging"
    
    #cart
    @delete="Delete"
    
    #native checkout
    @proceed_to_checkout="Proceed to Checkout"
    @next="Next"
    @pay_on_delivery="Cash On Delivery"
    @confirm_order="Confirm Order"
    
    @popular_categories="Popular"
    @filter="Filter"
    @filter_brand="Brand"
    @filter_size="Size"
    @filter_color_family="Color family"
    @filter_price="Price"
    
    @done="Done"
    @clear_all="CLEAR ALL"
    @new_in="NEW IN"
    @best_rating="BEST RATING"
    @back="Please press back again if you wish to leave the app"
    
    @search_r="samsung"
    
    @valid_order="300095859"
    @no_track_results="No results found"
    
    #related items
    @related_items="Related Items"
    
    @newsletter_checkbox="Newsletter"
    
    @email_notifications="Email Notifications"
    
    @newsletter="Newsletters"
    @newsletter_male="Newsletter Male"
    @newsletter_female="Newsletter Female"
    
    @login="Login"
    
    @register="Register"
    
    @notification_newsletter_changes="Your email notifications settings have been saved"
    
    @no_result_found="Unfortunately there was no match found"
    @search_tips="Search Tips"
    
    @newsletter_subscription="Newsletter Subscription"
    
    @invalid_email_message="Invalid email address"
    
    @save="Save"
    @settings = "Settings"
    @myprofile = "My Profile"
    @my_favourites = "My Favourites"
    @recente_searches = "Recent Searches"
    @recently_viewed = "Recently Viewed"
    @menu = "MENU"
    @item_added = "Item added"
    @item_removed = "Item removed"
    @shopping_cart = "Shopping Cart"
    @my_cart = "My Cart"
    @back_button= "BACK"
    @track_my_order = "Track My Order"
    @number_items="Items"
    @remember_my_email= "Remember my email"
    
    @add_new_address = "Add new address"
    @size= "Size"
    @call_to_order = "Call to order"
    @add_all_items_to_cart = "Add All Items to Cart"
    @continue_shopping = "Continue Shopping"
    @signup = "Signup"
    @female = "female"
    
    ##### Ivory Coast ####################################### IC ##############################################################################          
    when "ic"
    @venture_name= @venture_cotedivoire
    
    @sign_in="Accéder au compte"
    @wrong_username="faketester@tester.tt"
    @invalidsearch= "addddd" 
    @search_p= "caresse Sava" 
    @search_word="samsung"
    @search="Rechercher"
    @myaccount="Mon compte"
    @myinfo="Les données de"
    @categories="CATÉGORIES"
    @categoryfashion="Mobile"
    @choose_country="Choisir le pays"
    @order_status="Suivi de commande"
     
    @checkoutNext="Next"
    @checkout1="TestPayment"
    @checkout3="Confirm Order"
      
    @emailerrormessage="dans le Email";
    @passerrormessage="dans le Mot de passe";
     
    @loginerror="L'authentification a échoué"
    @signout="Se déconnecter"
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
    @home_c="ACCUEIL"
    @new_pass_short="Le nouveau mot de passe doit comprendre au moins 6 caractères"
    
    #password recovery
    @forgot_password="Mot de passe oublié ?"
    @password_recovery="Récupération de mot de passe"
    @pass_rec_empty_email="S'il vous plait écrire dans le"
    @pass_rec_failed="Le renouvellement du mot de passe a échoué"
    @pass_rec_sent="Email envoyé"
    
    #cart
    @item_was_added="a été ajouté au panier"
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
    @first_tip="vers la gauche ou la droite pour"
    @second_tip="Cliquez pour voir l'ensemble"
    @specification="Caractéristiques"
    @please_choose="Choisissez"
    @search_v="sandal"
    @product_features="du produit"
    @product_description="Description du produit"
    @write_review="Donnez-nous notre avis"
    @posted_by="Posté"
    @write_review="Donnez-nous notre avis"
    @send_review="Envoyer votre avis"
    @to_reviews="Pour revue"
    @messaging="Messaging"
    
    #cart
    @delete="Effacer"
    
    #native checkout
    @proceed_to_checkout="Valider la commande"
    @next="Suivant"
    @pay_on_delivery="Payer cash à la livraison"
    @confirm_order="Confirmer la commande"
    
    #@popular_categories="Nos Meilleures Catégories"
    @popular_categories="Meilleures ventes"
    @filter="Filtre"
    @filter_brand="Brand"
    @filter_size="Size"
    @filter_color_family="Color family"
    @filter_price="Price"
    
    @done="Valider"
    @clear_all="TOUT EFFACER"
    @new_in="NOUVEAUTÉS"
    @best_rating="MIEUX NOTÉS"
    @back="Appuyez de nouveau sur retour si vous souhaitez quitter l'app"
    @search_r="nokia"
    
    @valid_order="300028219"
    @no_track_results="Aucun résultat pour le numéro"
    
    @related_items="Produits Similaires"
    
    @newsletter_checkbox="Je m'inscris à la newsletter"
    
    @email_notifications="Préférence eMails"
    
    @login="Connectez-vous"
    
    @newsletter="Newsletters"
    @newsletter_male="Newsletter Homme"
    @newsletter_female="Newsletter Femme"
    
    @register="Enregistrer"
    
    @notification_newsletter_changes="Vos préférences eMails ont été enregistrées"
    
    @no_result_found="n'a donné aucun résultat"
    @search_tips="Conseils d'utilisation de la barre de recherche"
    
    @newsletter_subscription="Newsletter"
    
    @invalid_email_message="Email non valide"
    
    @save="Enregistrer"
    @settings = "Settings"
    @myprofile = "Mon Profil"
    @my_favourites = "Mes Favoris"
    @recente_searches = "Recherches Récentes"
    @recently_viewed = "Derniers Produits Vus"
    @menu = "MENU"
    
    @item_added = "Produit ajouté"
    @item_removed = "Produit supprimé"
    @shopping_cart = "Panier d'achat"
    @my_cart = "Mon panier" 
    @back_button= "RETOUR"
    @track_my_order = "Suivi de commande"
    @number_items="Articles"
    @remember_my_email= "Remember my email"
    
    @add_new_address = "Add new address"
    @size= "Size"
    @call_to_order = "Call to order"
    @add_all_items_to_cart = "Add All Items to Cart"
    @continue_shopping = "Continue Shopping"
    @signup = "Signup"
    @female = "female"
      
    ##### Morocco ########################################### MA ##############################################################################
    when "ma"
    @venture_name= @venture_maroc
    
    @sign_in="Accéder au compte"
    @wrong_username="faketester@tester.tt"
    @invalidsearch= "addddf" 
    @search_p= "surf" 
    @search_word="samsung"
    @search="Rechercher"
    @myaccount="Mon compte"
    @myinfo="Les données de"
    @categories="CATÉGORIES"
    @categoryfashion="Tablettes"
    @choose_country="Choisir le pays"
    @order_status="Suivre la commande"
    
    @checkoutNext="Next"
    @checkout1="TestPayment"
    @checkout3="Confirm Order"
    
    @emailerrormessage="dans le E-mail";
    @passerrormessage="dans le Mot de passe";
    
    @loginerror="L'authentification a échoué"
    @signout="Se déconnecter"
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
    @home_c="ACCUEIL"
    @new_pass_short="Le nouveau mot de passe doit comprendre au moins 6 caractères"
    
    #password recovery
    @forgot_password="Mot de passe oublié ?"
    @password_recovery="Récupération de mot de passe"
    @pass_rec_empty_email="S'il vous plait écrire dans le"
    @pass_rec_failed="Le renouvellement du mot de passe a échoué"
    @pass_rec_sent="Email envoyé"
    
    #cart
    @item_was_added="a été ajouté au panier"
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
    @first_tip="vers la gauche ou la droite pour"
    @second_tip="Cliquez pour voir l'ensemble"
    @specification="Caractéristiques"
    @please_choose="Choisissez"
    @search_v="sandal"
    @product_features="Spécificités du produit"
    @product_description="Description du produit"
    @write_review="Donnez-nous notre avis"
    @posted_by="Posté"
    @write_review="Donnez-nous notre avis"
    @send_review="Envoyer votre avis"
    @to_reviews="Pour revue"
    @messaging="Messaging"
      
    #cart
    @delete="Effacer"
    
    #native checkout
    @proceed_to_checkout="Valider la commande"
    @next="Suivant"
    @pay_on_delivery="Paiement à la livraison"
    @confirm_order="Confirmer la commande"
    
    @popular_categories="Nos Meilleures Catégories"
    @filter="Filtre"
    @filter_brand="Brand"
    @filter_size="Size"
    @filter_color_family="Color family"
    @filter_price="Price"
    
    @done="Valider"
    @clear_all="TOUT EFFACER"
    @new_in="NOUVEAUTÉS"
    @best_rating="MIEUX NOTÉS"
    @back="Appuyez de nouveau sur retour si vous souhaitez quitter l'app"
    @search_r="dane elec"
    
    @valid_order="300452452"
    @no_track_results="Aucun résultat pour le numéro"
    
    @related_items="Produits Similaires"
    
    @newsletter_checkbox="Je m'inscris à la newsletter"
    
    @email_notifications="Préférence eMails"
    
    @login="Connectez-vous"
    
    @newsletter="Newsletters"
    @newsletter_male="Newsletter Homme"
    @newsletter_female="Newsletter Femme"
    
    @register="Enregistrer"
    
    @notification_newsletter_changes="Vos préférences eMails ont été enregistrées"
    
    @no_result_found="n'y a pas de résultat"
    @search_tips="Conseils d'utilisation de la barre de recherche"
    
    @newsletter_subscription="Newsletter"
    
    @invalid_email_message="Email non valide"
    
    @save="Enregistrer"
    @settings = "Settings"
    @myprofile = "Mon Profil"
    @my_favourites = "Mes Favoris"
    @recente_searches = "Recherches Récentes"
    @recently_viewed = "Derniers Produits Vus"
    @menu = "MENU"
    @item_added = "Produit ajouté"
    @item_removed = "Produit supprimé"
    @shopping_cart = "Panier d'achat"
    @my_cart = "Mon panier" 
    @back_button= "RETOUR"
    @track_my_order = "Suivre la commande"
    @number_items="Articles"
    @remember_my_email= "Remember my email"
    
    @add_new_address = "Add new address"
    @size= "Size"
    @call_to_order = "Call to order"
    @add_all_items_to_cart = "Add All Items to Cart"
    @continue_shopping = "Continue Shopping"
    @signup = "Signup"
    @female = "female"
        
    ##### Nigeria ########################################### NG ##############################################################################
    when "ng"
    @venture_name= @venture_nigeria
    
    @sign_in="Sign In"
    @wrong_username="faketester@tester.tt"
    @invalidsearch= "adddddf" 
    @search_p= "nikon" 
    @search_word="samsung"
    @search="Search"
    @myaccount="My Account"
    @myinfo="User Data"
    @categories="CATEGORIES"
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
    @home_c="HOME"
    @new_pass_short="The new password has to have at least 6 characters"
        
    #password recovery
    @forgot_password="Forgot Password?"
    @password_recovery="Password Recovery"
    @pass_rec_empty_email="Please fill in the E-Mail"
    @pass_rec_failed="Password recovery failed"
    @pass_rec_sent="Email sent"
    
    #cart
    @item_was_added="was added to shopping cart"
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
    @please_choose="Please choose"
    @search_v="sandal"
    @product_features="Product Features"
    @product_description="Product Description"
    @write_review="Write a Review"
    @posted_by="posted by"
    @write_review="Write a Review"
    @send_review="Send Review"
    @to_reviews="To reviews"
    @messaging="Messaging"
      
    #cart
    @delete="Delete"
    
    #native checkout
    @proceed_to_checkout="Proceed to Checkout"
    @next="Next"
    @pay_on_delivery="Pay On Delivery"
    @confirm_order="Confirm Order"
    
    @popular_categories="Popular"
    @filter="Filter"
    @filter_brand="Brand"
    @filter_size="Size"
    @filter_color_family="Color family"
    @filter_price="Price"
    
    @done="Done"
    @clear_all="CLEAR ALL"
    @new_in="NEW IN"
    @best_rating="BEST RATING"
    @back="Please press back again if you wish to leave the app"
    @search_r="samsung"
    
    @valid_order="304442242"
    @no_track_results="No results found"
    
    @related_items="Related Items"
    
    @newsletter_checkbox="Newsletter"
    
    @email_notifications="Email Notifications"
    
    @login="Login"
    
    @newsletter="Newsletters"
    @newsletter_male="Male"
    @newsletter_female="Newsletter Female"
    
    @register="Register"
    
    @notification_newsletter_changes="Your email notifications settings have been saved"
    
    @no_result_found="Unfortunately there was no match found"
    @search_tips="Search Tips"
    
    @newsletter_subscription="Newsletter Subscription"
    
    @invalid_email_message="Invalid email address"
    
    @save="Save"
    @settings = "Settings"
    @myprofile = "My Profile"
    @my_favourites = "My Favourites"
    @recente_searches = "Recent Searches"
    @recently_viewed = "Recently Viewed"
    @menu = "MENU"
    @item_added = "Item added"
    @item_removed = "Item removed"
    @shopping_cart = "Shopping Cart"
    @my_cart = "My Cart"
    @back_button= "BACK"
    @track_my_order = "Track My Order"
    @number_items="Items"
    @remember_my_email= "Remember my email"
    
    @add_new_address = "Add new address"
    @size= "Size"
    @call_to_order = "Call to order"
    @add_all_items_to_cart = "Add All Items to Cart"
    @continue_shopping = "Continue Shopping"
    @signup = "Signup"
    @female = "female"
    
    ##### Egypt ############################################# EG ##############################################################################
    when "eg"
    @venture_name= @venture_egypt
    
    @sign_in="Sign In"
    @wrong_username="faketester@tester.tt"
    @invalidsearch= "adddddf" 
    @search_p= "nikon" 
    @search_word="samsung"
    @search="Search" 
    @myaccount="My Account"
    @myinfo="User Data"
    @categories="CATEGORIES"
    @categoryfashion="Mobiles"
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
    @home_c="HOME"
    @new_pass_short="The new password has to have at least 6 characters"
    
    #password recovery
    @forgot_password="Forgot Password?"
    @password_recovery="Password Recovery"
    @pass_rec_empty_email="Please fill in the E-Mail"
    @pass_rec_failed="Password recovery failed"
    @pass_rec_sent="Email sent"
    
    #cart
    @item_was_added="was added to shopping cart"
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
    @please_choose="Please choose"
    @search_v="sandal"
    @product_features="Product Features"
    @product_description="Product Description"
    @write_review="Write a Review"
    @posted_by="posted by"
    @write_review="Write a Review"
    @send_review="Send Review"
    @to_reviews="To reviews"
    @messaging="Messaging"
    
    #cart
    @delete="Delete"
    
    #native checkout
    @proceed_to_checkout="Proceed to Checkout"
    @next="Next"
    @pay_on_delivery="Cash On Delivery"
    @confirm_order="Confirm Order"
    
    @popular_categories="Popular"
    @filter="Filter"
    @filter_brand="Brand"
    @filter_size="Size"
    @filter_color_family="Color family"
    @filter_price="Price"
    
    @done="Done"
    @clear_all="CLEAR ALL"
    @new_in="NEW IN"
    @best_rating="BEST RATING"
    @back="Please press back again if you wish to leave the app"
    @search_r="samsung"
    
    @valid_order="300573169"
    @no_track_results="No results found"
    
    @related_items="Related Items"
    
    @newsletter_checkbox="Newsletter"
    
    @email_notifications="Email Notifications"
    
    @login="Login"
    
    @newsletter="Newsletters"
    @newsletter_male="Male"
    @newsletter_female="Newsletter Female"
    
    @register="Register"
    
    @notification_newsletter_changes="Your email notifications settings have been saved"
    
    @no_result_found="Unfortunately there was no match found"
    @search_tips="Search Tips"
    
    @newsletter_subscription="Newsletter Subscription"
    
    @invalid_email_message="Invalid email address"
    
    @save="Save"
    @settings = "Settings"
    @myprofile = "My Profile"
    @my_favourites = "My Favourites"
    @recente_searches = "Recent Searches"
    @recently_viewed = "Recently Viewed"
    @menu = "MENU"
    @item_added = "Item added"
    @item_removed = "Item removed"
    @shopping_cart = "Shopping Cart"
    @my_cart = "My Cart"
    @back_button= "BACK"
    @track_my_order = "Track My Order"
    @number_items="Items"
    @remember_my_email= "Remember my email"
    
    @add_new_address = "Add new address"
    @size= "Size"
    @call_to_order = "Call to order"
    @add_all_items_to_cart = "Add All Items to Cart"
    @continue_shopping = "Continue Shopping"
    @signup = "Signup"
    @female = "female"
    
    ##### Uganda  ############################################ UG ##############################################################################
    when "ug"
    @venture_name= @venture_uganda
    
    @sign_in="Sign In"
    @wrong_username="faketester@tester.tt"
    @invalidsearch= "addddd" 
    @search_p= "nikon" 
    @search_word="samsung"
    @search="Search"
    @myaccount="My Account"
    @myinfo="User Data"
    @categories="CATEGORIES"
    @categoryfashion="Mobile"
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
    @home_c="HOME"
    @new_pass_short="The new password has to have at least 6 characters"
   
    #password recovery
    @forgot_password="Forgot Password?"
    @password_recovery="Password Recovery"
    @pass_rec_empty_email="Please fill in the E-Mail"
    @pass_rec_failed="Password recovery failed"
    @pass_rec_sent="Email sent"
    
    #cart
    @item_was_added="was added to shopping cart"
    @no_items="You have no items in the cart"
    @got_it="Got it"
    @clear_cart_message="This clears the cart"
    
    #catalog
    @currency= "USH"
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
    @please_choose="Please choose"
    @search_v="sandal"
    @product_features="Product Features"
    @product_description="Product Description"
    @write_review="Write a Review"
    @rating_t="posted by"
    @write_review="Write a Review"
    @send_review="Send Review"
    @to_reviews="To reviews"
    @messaging="Messaging"
    
    #cart
    @delete="Delete"
    
    #native checkout
    @proceed_to_checkout="Proceed to Checkout"
    @next="Next"
    @pay_on_delivery="Cash on Delivery"
    @confirm_order="Confirm Order"
    
    @popular_categories="Popular"
    @filter="Filter"
    @filter_brand="Brand"
    @filter_size="Size"
    @filter_color_family="Color family"
    @filter_price="Price"
    
    @done="Done"
    @clear_all="CLEAR ALL"
    @new_in="NEW IN"
    @best_rating="BEST RATING"
    @back="Please press back again if you wish to leave the app"
    @search_r="samsung"
    
    @valid_order="400093859"
    @no_track_results="No results found"
    
    @related_items="Related Items"
    
    @newsletter_checkbox="Newsletter"
    
    @email_notifications="Email Notifications"
    
    @login="Login"
    
    @newsletter="Newsletters"
    @newsletter_male="Newsletter Male"
    @newsletter_female="Newsletter Female"
    
    @register="Register"
    
    @notification_newsletter_changes="Your email notifications settings have been saved"
    
    @no_result_found="Unfortunately there was no match found"
    @search_tips="Search Tips"
    
    @newsletter_subscription="Newsletter Subscription"
    
    @invalid_email_message="Invalid email address"
    
    @save="Save"
    @settings = "Settings"
    @myprofile = "My Profile"
    @my_favourites = "My Favourites"
    @recente_searches = "Recent Searches"
    @recently_viewed = "Recently Viewed"
    @menu = "MENU"
    @item_added = "Item added"
    @item_removed = "Item removed"
    @shopping_cart = "Shopping Cart"
    @my_cart = "My Cart"
    @back_button= "BACK"
    @track_my_order = "Track My Order"
    @number_items="Items"
    @remember_my_email= "Remember my email"
    
    @add_new_address = "Add new address"
    @size= "Size"
    @call_to_order = "Call to order"
    @add_all_items_to_cart = "Add All Items to Cart"
    @continue_shopping = "Continue Shopping"
    @signup = "Signup"
    @female = "female"
    
    ##### Ghana   ############################################ GH ##############################################################################
    when "gh"
    @venture_name= @venture_ghana
    
    @sign_in="Sign In"
    @wrong_username="faketester@tester.tt"
    @invalidsearch= "addddd" 
    @search_p= "nikon" 
    @search_word="samsung"
    @search="Search"
    @myaccount="My Account"
    @myinfo="User Data"
    @categories="CATEGORIES"
    @categoryfashion="Mobile"
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
    @home_c="Top Sellers"
    @new_pass_short="The new password has to have at least 6 characters"
   
    #password recovery
    @forgot_password="Forgot Password?"
    @password_recovery="Password Recovery"
    @pass_rec_empty_email="Please fill in the E-Mail"
    @pass_rec_failed="Password recovery failed"
    @pass_rec_sent="Email sent"
    
    #cart
    @item_was_added="was added to shopping cart"
    @no_items="You have no items in the cart"
    @got_it="Got it"
    @clear_cart_message="This clears the cart"
    
    #catalog
    @currency= "GH"
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
    @please_choose="Please choose"
    @search_v="jersey"
    @product_features="Product Features"
    @product_description="Product Description"
    @write_review="Write a Review"
    @rating_t="posted by"
    @write_review="Write a Review"
    @send_review="Send Review"
    @to_reviews="To reviews"
    @messaging="Messaging"
    
    #cart
    @delete="Delete"
    
    #native checkout
    @proceed_to_checkout="Proceed to Checkout"
    @next="Next"
    @pay_on_delivery="Cash On Delivery"
    @confirm_order="Confirm Order"
    
    @popular_categories="Popular"
    @filter="Filter"
    @filter_brand="Brand"
    @filter_size="Size"
    @filter_color_family="Color family"
    @filter_price="Price"
    
    @done="Done"
    @clear_all="CLEAR ALL"
    @new_in="NEW IN"
    @best_rating="BEST RATING"
    @back="Please press back again if you wish to leave the app"
    @search_r="samsung"
    
    @valid_order="300000376"
    @no_track_results="No results found"
    
    @related_items="Related Items"
    
    @newsletter_checkbox="Newsletter"
    
    @email_notifications="Email Notifications"
    
    @login="Login"
    
    @newsletter="Newsletters"
    @newsletter_male="Newsletter Male"
    @newsletter_female="Newsletter Female"
    
    @register="Register"
    
    @notification_newsletter_changes="Your email notifications settings have been saved"
    
    @no_result_found="Unfortunately there was no match found"
    @search_tips="Search Tips"
    
    @newsletter_subscription="Newsletter Subscription"
    
    @invalid_email_message="Invalid email address"
    
    @save="Save"
    @settings = "Settings"
    @myprofile = "My Profile"
    @my_favourites = "My Favourites"
    @recente_searches = "Recent Searches"
    @recently_viewed = "Recently Viewed"
    @menu = "MENU"
    @item_added = "Item added"
    @item_removed = "Item removed"
    @shopping_cart = "Shopping Cart"
    @my_cart = "My Cart"
    @back_button= "BACK"
    @track_my_order = "Track My Order"
    @number_items="Items"
    @remember_my_email= "Remember my email"
    
    @add_new_address = "Add new address"
    @size= "Size"
    @call_to_order = "Call to order"
    @add_all_items_to_cart = "Add All Items to Cart"
    @continue_shopping = "Continue Shopping"
    @signup = "Signup"
    @female = "female"
  end
end