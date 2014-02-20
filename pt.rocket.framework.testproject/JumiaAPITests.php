<?php
include 'Color.php';
$color = new Colors();

$countries = array("IC", "KE", "MA", "NG", "EG", "UG");
#$country = array("EG");
$test = "";
$function = "";
$count_error = 0;
$count_passed = 0;
$failed_tests = "";
$adb_path_bruno = "/android/sdk/platform-tools/adb";
$adb_path_jenkins = "/android-sdk-macosx/platform-tools/adb";
$adb_path = $adb_path_jenkins;
$emulator_executable_path = "android-sdk-macosx/tools";
$emulator_bruno = "192.168.56.101:5555";
$emulator_jenkins = "emulator-5554";
$emulator= $emulator_jenkins;
$content  = "";
echo $color->getColoredString("##############################################", "white", "blue"). "\n";
echo $color->getColoredString("########## Running Jumia API Tests ###########", "white", "blue"). "\n";
echo $color->getColoredString("##############################################", "white", "blue"). "\n";

## Tests #### Tests #### Tests #### Tests #### Tests #### Tests #### Tests #### Tests #### Tests #### Tests #### Tests #### Tests #### Tests #### Tests #### Tests #### Tests ##

function ApiInfoTest($countries){
	$test = "ApiInfoTest";
	$function = "testGetApiInfo";
	Test($countries, $test, $function);
}

function RegisterFormTest($countries){
	$test = "RegisterFormTest";
	$function = "testRegisterForm";
	Test($countries, $test, $function);
}
	
function RegisterTests($countries){
	$test = "RegisterTests";
	$function = "testRegister";
	Test($countries, $test, $function);
}
	
function CategoriesTests($countries){
	$test = "CategoriesTests";
	$function = "testGetCategories";
	Test($countries, $test, $function);
}

function ProductDetailsTests($countries){
	$test = "ProductDetailsTests";
	$function = "testProductDetails";
	Test($countries, $test, $function);
}

function ProductListTests($countries){
	$test = "ProductListTests";
	$function = "testProductList";
	Test($countries, $test, $function);
}
	
function ImageResolutionsTest($countries){
	$test = "ImageResolutionsTest";
	$function = "testImageResolutions";
	Test($countries, $test, $function);
}

function InitFormTest($countries){
	$test = "InitFormTest";
	$function = "testInitForm";
	Test($countries, $test, $function);
}
	
function LoginFormTests($countries){
	$test = "LoginFormTests";
	$function = "testGetLoginForm";
	Test($countries, $test, $function);
}

function PromotionsTest($countries){
	$test = "PromotionsTest";
	$function = "testGetPromotions";
	Test($countries, $test, $function);
}

function RatingsFormTest($countries){
	$test = "RatingsFormTest";
	$function = "testGetRatingsForm";
	Test($countries, $test, $function);
}
	
function RatingsTest($countries){
	$test = "RatingsTest";
	$function = "testGetRatings";
	Test($countries, $test, $function);
}

function TermsConditionsTest($countries){
	$test = "TermsConditionsTest";
	$function = "testGetTermsConditions";
	Test($countries, $test, $function);
}

function ChangePasswordTest($countries){
	$test = "ChangePasswordTest";
	$function = "testGetChangePassword";
	Test($countries, $test, $function);
}


function FacebookLoginTest($countries){
	$test = "FacebookLoginTest";
	$function = "testGetFacebookLogin";
	Test($countries, $test, $function);
}

function CallToOrder($countries){
	$test = "CallToOrder";
	$function = "testCallToOrder";
	Test($countries, $test, $function);
}

function ForgotPasswordFormTest($countries){
	$test = "ForgotPasswordFormTest";
	$function = "testForgotPasswordForm";
	Test($countries, $test, $function);
}

function ForgotPasswordTest($countries){
	$test = "ForgotPasswordTest";
	$function = "testGetForgotPassword";
	Test($countries, $test, $function);
}

function LoginTest($countries){
	$test = "LoginTest";
	$function = "testGetLogin";
	Test($countries, $test, $function);
}

function LogoutTest($countries){
	$test = "LogoutTest";
	$function = "testGetLogout";
	Test($countries, $test, $function);
}

function ProductReviewsTest($countries){
	$test = "ProductReviewsTest";
	$function = "testProductReviews";
	Test($countries, $test, $function);
}

function SearchSuggestionTest($countries){
	$test = "SearchSuggestionTest";
	$function = "testSearchSuggestion";
	Test($countries, $test, $function);
}

function TeasersTest($countries){
	$test = "HomeTeasersTests";
	$function = "testGetHomeTeasers";
	Test($countries, $test, $function);
}

function TrackOrderTest($countries){
	$test = "TrackOrderTest";
	$function = "testGetTrackOrder";
	Test($countries, $test, $function);
}

function CostumerTest($countries){
	$test = "CostumerTest";
	$function = "testGetCostumer";
	Test($countries, $test, $function);
}

function ShoppingCartAddItemTest($countries){
	$test = "ShoppingCartAddItemTest";
	$function = "testShoppingCartAddItem";
	Test($countries, $test, $function);
}

function ShoppingCartChangeItemTest($countries){
	$test = "ShoppingCartChangetemQuantityTest";
	$function = "testShoppingCartChangeItemQuantity";
	Test($countries, $test, $function);
}

function ShoppingCartItemTest($countries){
	$test = "ShoppingCartItemsTest";
	$function = "testShoppingCartItems";
	Test($countries, $test, $function);
}

function ShoppingCartRemoveItemTest($countries){
	$test = "ShoppingCartRemoveItemTest";
	$function = "testShoppingCartRemoveItem";
	Test($countries, $test, $function);
}

## Functions #### Functions #### Functions #### Functions #### Functions #### Functions #### Functions #### Functions #### Functions #### Functions #### Functions #### Functions ##

function startEmulator(){
	global $adb_path;
	global $emulator_executable_path;
	$startEmulator = "~/".$emulator_executable_path."/emulator @jumia_tests_emulator";
	shell_exec($startEmulator."> /dev/null 2>/dev/null &");
	echo "Giving some time for the emulator to settle in before executing the tests.\n";
	for($i = 60; $i > 0; $i--)
	{
 		echo "Waiting for the emulator to start - ".$i;
  		sleep(1);
		echo "\n";	 
	}	
}

function restartApp(){
	global $adb_path;
	global $emulator;
	
	echo($adb_path);
	$currentPath = getcwd();
	echo "\nCurrent Directory:\n";
	echo $currentPath . "\n";
	echo "----------";
	
	$install ="~".$adb_path." -s ".$emulator." install -r ".$currentPath."/pt.rocket.framework.testproject/bin/pt.rocket.framework.testproject-debug.apk";
	exec($install);
	$install ="~".$adb_path." -s ".$emulator." install -r ".$currentPath."/pt.rocket.framework.testproject/../Shell/bin/MainActivity-debug.apk";
	exec($install);

	echo "Installing the apps now.";
}

function Test($countries, $test, $function){
	global $adb_path;
	global $emulator;
	foreach($countries as $country){
		$cmd = "~".$adb_path." -s  ".$emulator." shell am instrument -w -e class com.rocket.framework.testshell.test.".$test."#".$function.$country." com.rocket.framework.testshell.test/android.test.InstrumentationTestRunner";
		#echo $cmd;
		exec($cmd, $result);
		print_r($result);
		echo $test.$country." finished - ";
		echo checkErrors($test, $result, $country);
		$result = array();
	}
}

function checkErrors($test, $result, $country){
	global $count_error;
	global $count_passed;	
	global $failed_tests;
	foreach ($result as $value){
		if (strpos($value,'Error') !== false) {
			$count_error=  $count_error + 1;
			$failed_tests =$failed_tests.$test.$country."\n";
			echo "\033[01;5;31mFAILED\033[0m\n";
			#echo "\033[01;5;31m".$country."\033[0m\n";
			return;
		}
	}
	echo "\033[01;32mOK\033[0m\n";
	$count_passed=  $count_passed + 1;
}

function NumberOfErrors(){
	global $count_error;
	global $count_passed;
	global $color;	
	global $failed_tests;
	global $content;
	$content.= "\n"."Test Report" . "\n\n";
	echo "\n"."Test Report" . "\n\n";
	if($count_passed > 0){
		echo $count_passed. " Passed" . "\n\n";
		$content.= $count_passed. " Passed" . "\n\n";
	}
	if($count_error > 0){
		echo $count_error. " Failed". "\n";
		$content.= $count_error. " Failed". "\n";
		echo $failed_tests . "\n";
		$content.= $failed_tests . "\n";
	}

	
}

function CalcDuration($begin, $end){
	global $color;
	$time_one = $begin;
	$time_two = $end;
	$difference = $time_one->diff($time_two);
	echo $difference->format('%h Hours %i Minutes %s Seconds')."\n\n";
	global $content;
	$content.=$difference->format('%h Hours %i Minutes %s Seconds')."\n\n";
	$file_name = "./test_results/result".$time_one->format('Y-m-d H:i:s').".txt";
	$fp = fopen($file_name,"w");
	fwrite($fp,$content);
	fclose($fp);
}

?>
