<?php
include 'JumiaAPITests.php';
session_start();

$country_test = $argv[1];
$chosen_device = $argv[2];

switch ($chosen_device) {
	    case "ONE":
	        #echo "\ni equals ONE\n";
	        $device_one = "HT019P801493";
	        $_SESSION['chosen_device'] = $device_one;
	        break;
	    case "NEXUS":
	        #echo "\ni equals NEXUS\n";
	        $device_Nexus_5 = "02c4f6c4d0240c12";
	        $_SESSION['chosen_device'] = $device_Nexus_5;
	        break;
	    case "S4":
	        #echo "\ni equals S4\n";
	        $device_s4 = "9b9a8c25";
	        $_SESSION['chosen_device'] = $device_s4;
	        break;
	    case "EMU":
	        #echo "\ni equals EMU\n";
	        $emulator_bruno = "192.168.56.101:5555";
	        $_SESSION['chosen_device'] = $emulator_bruno;
	        break;
}

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

if ($country_test !== "UG")
{
GetCities($country_test);
}

#GetShippingMethods($country_test);
#SetShippingMethods($country_test);
#GetPaymentMethods($country_test);
#SetPaymentMethods($country_test);
#GetMyOrder($country_test);
CheckoutFinish($country_test);

#1.9
#GetCampaign($country_test);
GetSearchUndefined($country_test);
SignupNewsletter($country_test);
ManageNewsletter($country_test);
CartDataTest($country_test);
OrderSummaryTest($country_test);
GetCountriesTest($country_test);
GetCountryConfigsTest($country_test);

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
