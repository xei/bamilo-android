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
  
  
  
  #initializes the country variables
  case $country.to_s
    ##### Kenya  ############################################ KE ##############################################################################
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
    
    @popular_categories="Popular Categories"
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
    
    @newsletter_checkbox="testesteste"
    
    @email_notifications="Email Notifications"
    
    @newsletter="Newsletters"
    @newsletter_male="Newsletter Male"
    @newsletter_female="Newsletter Female"
    
    @login="Login"
    
    @register="Register"
    
    @notification_newsletter_changes="Vos préférences eMails ont été enregistrées"
    
    @no_result_found="n'a donné aucun résultat"
    @search_tips="Conseils d'utilisation de la barre de recherche"
    
    @newsletter_subscription="Newsletter Subscription"
    
    @invalid_email_message="Email non valide"
    
    @save="Save"
    
    ##### Ivory Coast ####################################### IC ##############################################################################          
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
      
    @emailerrormessage="dans le E-mail";
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
    @pass_rec_empty_email="S'il vous plait écrire dans le"
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
    @first_tip="vers la gauche ou la droite pour"
    @second_tip="Cliquez pour voir l'ensemble"
    @specification="Caractéristiques"
    @please_choose="Choisissez"
    @search_v="sandales à Clous - argent"
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
    @pay_on_delivery="Payer cash à la livraison"
    @confirm_order="Confirmer la commande"
    
    @popular_categories="Nos Meilleures Catégories"
    @filter="Filter"
    @filter_brand="Brand"
    @filter_size="Size"
    @filter_color_family="Color family"
    @filter_price="Price"
    
    @done="Done"
    @clear_all="CLEAR ALL"
    @new_in="NEW IN"
    @best_rating="BEST RATING"
    @back="Appuyez de nouveau sur retour si vous souhaitez quitter l'app"
    @search_r="nokia lumia 920 noir"
    
    @valid_order="300028219"
    @no_track_results="Aucun résultat pour le numéro"
    
    @related_items="Produits Similaires"
    
    @newsletter_checkbox="Je m'inscris à la newsletter"
    
    @email_notifications="Préférence eMails"
    
    @login="Connectez-vous"
    
    @newsletter="Newsletters"
    @newsletter_male="Newsletter Male"
    @newsletter_female="Newsletter Female"
    
    @register="Enregistrer"
    
    @notification_newsletter_changes="Vos préférences eMails ont été enregistrées"
    
    @no_result_found="n'a donné aucun résultat"
    @search_tips="Conseils d'utilisation de la barre de recherche"
    
    @newsletter_subscription="Newsletter Subscription"
    
    @invalid_email_message="Email non valide"
    
    @save="Enregistrer"
      
    ##### Morocco ########################################### MA ##############################################################################
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
    @categoryfashion="Téléphonie"
    @choose_country="Choisir le pays"
    @order_status="Suivre la commande"
    
    @checkoutNext="Next"
    @checkout1="TestPayment"
    @checkout3="Confirm Order"
    
    @emailerrormessage="dans le E-mail";
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
    @pass_rec_empty_email="S'il vous plait écrire dans le"
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
    @first_tip="vers la gauche ou la droite pour"
    @second_tip="Cliquez pour voir l'ensemble"
    @specification="Caractéristiques"
    @please_choose="Choisissez"
    @search_v="bottes à talons"
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
    
    @newsletter_checkbox="testestesteste"
    
    @email_notifications="Préférence eMails"
    
    @login="Connectez-vous"
    
    @newsletter="Newsletters"
    @newsletter_male="Newsletter Male"
    @newsletter_female="Newsletter Female"
    
    @register="Enregistrer"
    
    @notification_newsletter_changes="Vos préférences eMails ont été enregistrées"
    
    @no_result_found="n'a donné aucun résultat"
    @search_tips="Conseils d'utilisation de la barre de recherche"
    
    @newsletter_subscription="Newsletter Subscription"
    
    @invalid_email_message="Email non valide"
    
    @save="Enregistrer"
        
    ##### Nigeria ########################################### NG ##############################################################################
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
    
    @popular_categories="Popular Categories"
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
    
    @related_items="Produits Similaires"
    
    @newsletter_checkbox="testestesteste"
    
    @email_notifications="Email Notifications"
    
    @login="Connectez-vous"
    
    @newsletter="Newsletters"
    @newsletter_male="Newsletter Male"
    @newsletter_female="Newsletter Female"
    
    @register="Enregistrer"
    
    @notification_newsletter_changes="Vos préférences eMails ont été enregistrées"
    
    @no_result_found="n'a donné aucun résultat"
    @search_tips="Conseils d'utilisation de la barre de recherche"
    
    @newsletter_subscription="Newsletter Subscription"
    
    @invalid_email_message="Email non valide"
    
    @save="Save"
    
    ##### Egypt ############################################# EG ##############################################################################
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
    
    @popular_categories="Popular Categories"
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
    
    @related_items="Produits Similaires"
    
    @newsletter_checkbox="testestesteste"
    
    @email_notifications="Email Notifications"
    
    @login="Connectez-vous"
    
    @newsletter="Newsletters"
    @newsletter_male="Newsletter Male"
    @newsletter_female="Newsletter Female"
    
    @register="Enregistrer"
    
    @notification_newsletter_changes="Vos préférences eMails ont été enregistrées"
    
    @no_result_found="n'a donné aucun résultat"
    @search_tips="Conseils d'utilisation de la barre de recherche"
    
    @newsletter_subscription="Newsletter Subscription"
    
    @invalid_email_message="Email non valide"
    
    @save="Save"
    
    ##### Uganda  ############################################ UG ##############################################################################
    when "ug"
    @venture_name="Uganda"
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
    @pay_on_delivery="Pay On Delivery"
    @confirm_order="Confirm Order"
    
    @popular_categories="Popular Categories"
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
    
    @related_items="Produits Similaires"
    
    @newsletter_checkbox="testestesteste"
    
    @email_notifications="Email Notifications"
    
    @login="Connectez-vous"
    
    @newsletter="Newsletters"
    @newsletter_male="Newsletter Male"
    @newsletter_female="Newsletter Female"
    
    @register="Enregistrer"
    
    @notification_newsletter_changes="Vos préférences eMails ont été enregistrées"
    
    @no_result_found="n'a donné aucun résultat"
    @search_tips="Conseils d'utilisation de la barre de recherche"
    
    @newsletter_subscription="Newsletter Subscription"
    
    @invalid_email_message="Email non valide"
    
    @save="Save"
  end
end