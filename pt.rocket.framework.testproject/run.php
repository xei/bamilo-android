<?php
include 'JumiaAPITests.php';

## startEmulator();

## Install App
restartApp();

## Begin Time
$begin = new DateTime(date("Y-m-d H:i:s"), new DateTimeZone('Europe/London'));

## Test

#/** Pass

RegisterTests($countries);
RegisterFormTest($countries);
ApiInfoTest($countries);
CategoriesTests($countries);
ProductDetailsTests($countries);
ProductListTests($countries_nc);
ImageResolutionsTest($countries);
InitFormTest($countries);
LoginFormTests($countries);
RatingsFormTest($countries);
RatingsTest($countries);
TermsConditionsTest($countries);
ChangePasswordTest($countries);
FacebookLoginTest($countries);
ForgotPasswordFormTest($countries);
LoginTest($countries);
LogoutTest($countries);
ProductReviewsTest($countries);
SearchSuggestionTest($countries);
TeasersTest($countries);
TrackOrderTest($countries);
ForgotPasswordTest($countries);
CostumerTest($countries);
ShoppingCartAddItemTest($countries);
ShoppingCartChangeItemTest($countries);
ShoppingCartItemTest($countries);
ShoppingCartRemoveItemTest($countries);
CallToOrder($countries_nc);

GetSignup($countries_nc);
GetCostumerAddress($countries_nc);
#GetBillingAddress($countries_nc);
#SetBillingAddress($countries_nc);
CreateAddress($countries_nc);
GetRegions($countries_nc);
GetCities($countries_nc);
#GetShippingMethods($countries_nc);
#SetShippingMethods($countries_nc);
#GetPaymentMethods($countries_nc);
#SetPaymentMethods($countries_nc);
#GetMyOrder($countries_nc);
CheckoutFinish($countries_nc);
#**/

/** Will change

FormsDatasetList($countries);	

**/

/** Optional

PromotionsTest($countries);

**/

## End Timer
$end = new DateTime(date("Y-m-d H:i:s"), new DateTimeZone('Europe/London'));

## Report
NumberOfErrors();
CalcDuration($begin, $end);

?>
