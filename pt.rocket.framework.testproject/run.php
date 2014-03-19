<?php
include 'JumiaAPITests.php';

startEmulator();

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
ProductListTests($countries);
ImageResolutionsTest($countries);
InitFormTest($countries);
LoginFormTests($countries);
RatingsFormTest($countries);
RatingsTest($countries);
TermsConditionsTest($countries);
RegisterFormTest($countries);
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
CallToOrder($countries);

GetSignup($countries);
GetCostumerAddress($countries);
GetPollForm($countries);
#SetPollAnswer($countries);
GetBillingAddress($countries);
SetBillingAddress($countries);
CreateAddress($countries);
GetRegions($countries);
GetCities($countries);
GetShippingMethods($countries);
SetShippingMethods($countries);
GetPaymentMethods($countries);
SetPaymentMethods($countries);
GetMyOrder($countries);
CheckoutFinish($countries);

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
