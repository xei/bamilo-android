package pt.rocket.framework.tracking;


public class GTMEvents {
    
    public static String GTM_INSTALL_APP = "install"; 
    public static String GTM_OPEN_APP = "openApp";
    public static String GTM_OPEN_SCREEN = "openScreen";
    public static String GTM_LOGIN = "login";
    public static String GTM_LOGOUT = "logout"; 
    public static String GTM_REGISTER = "register"; 
    public static String GTM_SIGNUP = "signUp";
    public static String GTM_SEARCH_BUTTON = "searchButton";
    public static String GTM_SEARCH_SERVER = "searchServer";
    public static String GTM_CONTACT_BUTTON ="contactButton";
    public static String GTM_CONTACT_DONE ="contactDone";
    public static String GTM_TRANSACTION = "transaction";
    public static String GTM_SHARE_PRODUCT = "shareProduct";
    public static String GTM_CHANGE_COUNTRY = "changeCountry";  
    public static String GTM_CHANGE_LANGUAGE = "changeLanguage";
    public static String GTM_VIEW_PRODUCT = "viewProduct";
    public static String GTM_ADD_TO_CART = "addToCart";
    public static String GTM_REMOVE_FROM_CART = "removeFromCart";
    public static String GTM_RATE_PRODUCT = "rateProduct";
    public static String GTM_VIEW_RATING= "viewRating";
    public static String GTM_VIEW_CATALOG= "viewCatalog";
    public static String GTM_FILTER_CATALOG= "filterCatalog";
    public static String GTM_SORT_CATALOG= "sortCatalog";
    public static String GTM_ADD_TO_WL = "addToWL";
    public static String GTM_REMOVE_FROM_WL = "removeFromWL";
    public static String GTM_VIEW_CART = "viewCart";
    public static String GTM_START_CHECKOUT = "startCheckout";
    public static String GTM_CLOSE_APP = "closeApp";
    public static String GTM_PUSH_NOTIFICATION = "setupPush";
    public static String GTM_RESET_SEARCH = "ResetSearch";
    public static String GTM_CREATE_ALERT = "createAlert";
    public static String GTM_DELETE_ALERT = "deleteAlert";
    public static String GTM_VIEW_ALERTS_LIST = "viewAlertsList";
        

    public static class GTMKeys {
        public static String SOURCE = "source";
        public static String APPVERSION = "appVersion";
        public static String SHOPCOUNTRY = "shopCountry";
        public static String SHOPLANGUAGE = "shopLanguage";
        public static String SCREENNAME = "screenName";
        public static String LOGINMETHOD = "loginMethod";
        public static String LOGINLOCATION = "loginLocation";
        public static String CUSTOMERID = "customerId";
        public static String USERID = "userId";
        public static String USERAGE = "userAge";
        public static String USERGENDER = "userGender";
        public static String ACCOUNTCREATIONDATE = "accountCreationDate";
        public static String NUMBERLEADS= "numberLeads";
        public static String NUMBERPURCHASES = "numberPurchases";       
        public static String LOGOUTLOCATION = "logoutLocation";
        public static String REGISTRATIONMETHOD = "registrationMethod";
        public static String REGISTRATIONLOCATION = "registrationLocation";
        
        public static String PAYMENTMETHOD = "paymentMethod";
        public static String TRANSACTIONID = "transactionId";
        public static String PRODUCTSKU = "productSKU";
        public static String CREATIONDATE = "creationDate";
        public static String DELETATIONDATE = "deletionDate";
        public static String NUMSAVEDALERTS = "NumSavedAlerts";
        public static String PRODUCTCATEGORY = "productCategory";
        public static String PRODUCTQUANTITY = "productQuantity";
        public static String PRODUCTPRICE = "productPrice";
        public static String CARTQUANTITY = "cartQuantity";
        public static String TRANSACTIONTOTAL = "transactionTotal";
        public static String TRANSACTIONSHIPPING = "transactionShipping";
        public static String TRANSACTIONTAX = "transactionTax";
        public static String TRANSACTIONCURRENCY = "transactionCurrency";
        public static String VOUCHERAMOUNT = "voucherAmount";
        public static String PREVIOUSPURCHASES = "previousPurchases";       
        
        public static String SOCIALNETWORK = "socialNetwork";
        public static String SHARELOCATION = "shareLocation";
                
        public static String PRODUCTBRAND = "productBrand";
        public static String PRODUCTSUBCATEGORY = "productSubcategory";
        public static String CURRENCY = "currency";
        public static String DISCOUNT = "discount";
        public static String PRODUCTRATING = "productRating";       
        
        public static String AREA = "search_area";
        public static String AVERAGERATINGTOTAL = "averageRatingTotal";
        
        public static String PROPERTY_TYPE = "Property type";
        public static String PRODUCT_CATEGORY = "productCategory";
        public static String MAXIMAL_PRICE = "MaximalPrice";
        public static String PRICE_RANGE = "search_priceRange";
        public static String PRICE = "Price";
        public static String MAXIMAL_LIVING_AREA = "MaximalLivingArea";
        public static String MAKE = "search_make";
        public static String MINIMAL_BATHROOMS = "MinimalBathrooms";
        public static String MINIMAL_BEDROOMS = "MinimalBedrooms";
        public static String MODEL = "search_model";
        public static String YEAR_RANGE = "search_yearRange";
        public static String REGION = "search_region";
        public static String CITY = "search_city";
        public static String SEARCH_VEHICLE_TYPE = "search_vehicleType";
        public static String SEARCH_CONDITION = "search_condition";

        public static String QUANTITYCART = "quantityCart";
        public static String CARTVALUE = "cartValue";       
        
        public static String RATINGPRICE = "ratingPrice";
        public static String RATINGAPPEARANCE = "ratingAppearance";
        public static String RATINGQUALITY = "ratingQuality";       
        
        public static String AVERAGERATINGPRICE = "averageRatingPrice";
        public static String AVERAGERATINGAPPEARANCE = "averageRatingAppearance";
        public static String AVERAGERATINGQUALITY = "averageRatingQuality";    
        
        public static String CATEGORY = "category";
        public static String SUBCATEGORY = "subcategory";
        public static String PAGENUMBER = "pageNumber";   

        public static String ALERT_MAKE ="alert_make";
        public static String ALERT_MODEL ="alert_model";
        public static String ALERT_YEAR ="alert_year";
        public static String ALERT_PRICE ="alert_price";
        public static String ALERT_REGION ="alert_region";
        public static String ALERT_VEHICLE_TYPE ="alert_vehicleType";
        
        public static String FILTERTYPE = "filterType";
        public static String SORTTYPE = "sortType";
        
        public static String SEARCHTERM = "searchTerm";
        public static String RESULTSNUMBER = "resultsNumber";       
        
        public static String SUBSCRIBERID = "subscriberId";
        public static String SIGNUPLOCATION = "signUpLocation";
        
        public static String PASSWORD_FORGOTTEN = "password forgotten";
        public static String CONTACT_TYPE = "ContactType";
        public static String PRODUCT_ID = "productID";
        
        public static String MEDIUM = "medium";
        public static String CAMPAIGN = "campaign";

        
        public static String PRODUCT_MAKE = "product_make";
        public static String PRODUCT_MODEL = "product_model";
        public static String PRODUCT_YEAR = "product_year";
        public static String PRODUCT_PRICE = "product_price";
        public static String PRODUCT_REGION = "product_region";
        public static String PRODUCT_CITY = "product_city";
        public static String PRODUCT_AREA = "product_area";
        public static String PRODUCT_CONDITION = "product_condition";
        public static String PRODUCT_VEHICLE_TYPE = "product_vehicleType";
        public static String PRODUCT_TRANSMISSION = "product_transmission";
        public static String PRODUCT_FUEL = "product_fuel";
        public static String PRODUCT_COLOR = "product_color";

        public static String CATALOG_MAKE = "catalog_make";
        public static String CATALOG_MODEL = "catalog_model";
        public static String CATALOG_YEAR_RANGE = "catalog_yearRange";
        public static String CATALOG_PRICE_RANGE = "catalog_priceRange";
        public static String CATALOG_REGION = "catalog_region";
        public static String CATALOG_CITY = "catalog_city";
        public static String CATALOG_AREA = "catalog_area";
        public static String CATALOG_CONDITION = "catalog_condition";
        public static String CATALOG_VEHICLE_TYPE = "catalog_vehicleType";
        
        
        
        
    }

    public static class GTMValues {
        public static String ORGANIC = "Organic";
        public static String CAMPAIGN = "Campaign";     
        
        public static String PUSH = "Push";
        public static String DIRECT = "Direct";    
        public static String EMAILAUTH = "Email Auth";
        public static String FACEBOOK = "Facebook";
        public static String CHECKOUT = "Checkout";
        public static String MYACCOUNT = "My Account";
        public static String SIDEMENU = "Side menu";
        public static String PROPERTYSELECTED = "property selected";
        
        public static String CREDIRTCARD = "Credit card";
        public static String COD = "COD";
        public static String GIFTCARD = "Giftcard";     
        
        public static String PRODUCTPAGE = "Product Page";
        public static String CARTPAGE = "Cart Page";
        public static String WISHLISTPAGE = "Wishlist Page";
        public static String REGISTERPAGE = "Register Page";
        public static String EMAIL ="Email";
        public static String PHONE = "Phone";
        
        public static String REFERRER = "referrer";
        public static String PUSH_SOURCE = "push";
        public static String PRE_INSTALL = "preinstall";
    }

            
    
}

