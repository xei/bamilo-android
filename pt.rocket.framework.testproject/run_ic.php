<?php
include 'JumiaAPITests.php';

$country_test = "IC";

## startEmulator();

## Install App
restartApp();

## Begin Time
$begin = new DateTime(date("Y-m-d H:i:s"), new DateTimeZone('Europe/London'));

## Test

#/** Pass

RegisterTests($country_test);
RegisterFormTest($country_test);
ApiInfoTest($country_test);
CategoriesTests($country_test);
ProductDetailsTests($country_test);
ProductListTests($country_test);
ImageResolutionsTest($country_test);
InitFormTest($country_test);
LoginFormTests($country_test);
RatingsFormTest($country_test);
RatingsTest($country_test);
TermsConditionsTest($country_test);
ChangePasswordTest($country_test);
FacebookLoginTest($country_test);
ForgotPasswordFormTest($country_test);
LoginTest($country_test);
LogoutTest($country_test);
ProductReviewsTest($country_test);
SearchSuggestionTest($country_test);
TeasersTest($country_test);
TrackOrderTest($country_test);
ForgotPasswordTest($country_test);
CostumerTest($country_test);
ShoppingCartAddItemTest($country_test);
ShoppingCartChangeItemTest($country_test);
ShoppingCartItemTest($country_test);
ShoppingCartRemoveItemTest($country_test);
CallToOrder($country_test);

GetSignup($country_test);
GetCostumerAddress($country_test);
#GetBillingAddress($country_test);
#SetBillingAddress($country_test);
CreateAddress($country_test);
GetRegions($country_test);
GetCities($country_test);
#GetShippingMethods($country_test);
#SetShippingMethods($country_test);
#GetPaymentMethods($country_test);
#SetPaymentMethods($country_test);
#GetMyOrder($country_test);
CheckoutFinish($country_test);
#**/

/** Will change

FormsDatasetList($country_test);	

**/

/** Optional

PromotionsTest($country_test);

**/

## End Timer
$end = new DateTime(date("Y-m-d H:i:s"), new DateTimeZone('Europe/London'));

## Report
NumberOfErrors();
CalcDuration($begin, $end);

?>
