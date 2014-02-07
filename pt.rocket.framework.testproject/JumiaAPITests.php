<?php
$countries = array("IC", "KE", "MA", "NG", "EG", "UG");

echo "\033[01;5;34m##############################################\n";
echo "########## Running Jumia API Tests ##########\n";
echo "##############################################\033[0m\n";

restartApp();
function restartApp(){
	$uninstall ="~/Desktop/android-sdk-linux/platform-tools/adb -s  emulator-5554 uninstall com.rocket.framework.testshell.test";
	exec($uninstall);
	$install ="~/Desktop/android-sdk-linux/platform-tools/adb -s emulator-5554 install bin/pt.rocket.framework.testproject.apk";
	exec($install);
	
	}

function ApiInfoTest($countries){
		foreach($countries as $country){
			$cmd = "~/Desktop/android-sdk-linux/platform-tools/adb -s emulator-5554 shell am instrument -w -e class com.rocket.framework.testshell.test.ApiInfoTest#testGetApiInfo".$country." com.rocket.framework.testshell.test/android.test.InstrumentationTestRunner";
			exec($cmd, $result);
			echo "ApiInfoTest".$country." finished - ";
			echo checkErrors($result);
			$result = array();

		}
	}


function CallToOrder($countries){
		foreach($countries as $country){
			$cmd = "~/Desktop/android-sdk-linux/platform-tools/adb -s emulator-5554 shell am instrument -w -e class com.rocket.framework.testshell.test.CallToOrder#testCallToOrder".$country." com.rocket.framework.testshell.test/android.test.InstrumentationTestRunner";
			exec($cmd, $result);
			echo "CallToOrder".$country." finished - ";
			echo checkErrors($result);
			$result = array();

		}
	}

function RegisterFormTest($countries){
		foreach($countries as $country){
			$cmd = "~/Desktop/android-sdk-linux/platform-tools/adb -s emulator-5554 shell am instrument -w -e class com.rocket.framework.testshell.test.RegisterFormTest#testRegister".$country." com.rocket.framework.testshell.test/android.test.InstrumentationTestRunner";
			exec($cmd, $result);
			echo "RegisterFormTest".$country." finished - ";
			echo checkErrors($result);
			$result = array();

		}
	}

function RegisterTests($countries){
		foreach($countries as $country){
			$cmd = "~/Desktop/android-sdk-linux/platform-tools/adb -s emulator-5554 shell am instrument -w -e class com.rocket.framework.testshell.test.RegisterTests#testRegister".$country." com.rocket.framework.testshell.test/android.test.InstrumentationTestRunner";
			exec($cmd, $result);
			print_r($result);
			echo "RegisterTests".$country." finished - ";
			echo checkErrors($result);
			$result = array();

		}
	}
	
function CategoriesTests($countries){
		foreach($countries as $country){
			$cmd = "~/Desktop/android-sdk-linux/platform-tools/adb -s emulator-5554 shell am instrument -w -e class com.rocket.framework.testshell.test.CategoriesTests#testGetCategories".$country." com.rocket.framework.testshell.test/android.test.InstrumentationTestRunner";
			exec($cmd, $result);
			print_r($result);
			echo "CategoriesTests".$country." finished - ";
			echo checkErrors($result);
			$result = array();

		}
	}
	
function ProductDetailsTests($countries){
		foreach($countries as $country){
			$cmd = "~/Desktop/android-sdk-linux/platform-tools/adb -s emulator-5554 shell am instrument -w -e class com.rocket.framework.testshell.test.ProductDetailsTests#testProductDetails".$country." com.rocket.framework.testshell.test/android.test.InstrumentationTestRunner";
			exec($cmd, $result);
			print_r($result);
			echo "ProductDetailsTests".$country." finished - ";
			echo checkErrors($result);
			$result = array();

		}
	}

function ProductListTests($countries){
		foreach($countries as $country){
			$cmd = "~/Desktop/android-sdk-linux/platform-tools/adb -s emulator-5554 shell am instrument -w -e class com.rocket.framework.testshell.test.ProductListTests#testProductDetails".$country." com.rocket.framework.testshell.test/android.test.InstrumentationTestRunner";
			exec($cmd, $result);
			print_r($result);
			echo "ProductListTests".$country." finished - ";
			echo checkErrors($result);
			$result = array();

		}
	}

function ImageResolutionsTest($countries){
		foreach($countries as $country){
			$cmd = "~/Desktop/android-sdk-linux/platform-tools/adb -s emulator-5554 shell am instrument -w -e class com.rocket.framework.testshell.test.ImageResolutionsTest#testImageResolutions".$country." com.rocket.framework.testshell.test/android.test.InstrumentationTestRunner";
			exec($cmd, $result);
			print_r($result);
			echo "ImageResolutionsTest".$country." finished - ";
			echo checkErrors($result);
			$result = array();

		}
	}
	
function InitFormTest($countries){
		foreach($countries as $country){
			$cmd = "~/Desktop/android-sdk-linux/platform-tools/adb -s emulator-5554 shell am instrument -w -e class com.rocket.framework.testshell.test.InitFormTest#testInitForm".$country." com.rocket.framework.testshell.test/android.test.InstrumentationTestRunner";
			exec($cmd, $result);
			print_r($result);
			echo "InitFormTest".$country." finished - ";
			echo checkErrors($result);
			$result = array();

		}
	}
	

function LoginFormTests($countries){
		foreach($countries as $country){
			$cmd = "~/Desktop/android-sdk-linux/platform-tools/adb -s emulator-5554 shell am instrument -w -e class com.rocket.framework.testshell.test.LoginFormTests#testGetLoginForm".$country." com.rocket.framework.testshell.test/android.test.InstrumentationTestRunner";
			exec($cmd, $result);
			print_r($result);
			echo "LoginFormTests".$country." finished - ";
			echo checkErrors($result);
			$result = array();

		}
	}
	
function PromotionsTest($countries){
		foreach($countries as $country){
			$cmd = "~/Desktop/android-sdk-linux/platform-tools/adb -s emulator-5554 shell am instrument -w -e class com.rocket.framework.testshell.test.PromotionsTest#testGetPromotions".$country." com.rocket.framework.testshell.test/android.test.InstrumentationTestRunner";
			exec($cmd, $result);
			print_r($result);
			echo "PromotionsTest".$country." finished - ";
			echo checkErrors($result);
			$result = array();

		}
	}


function RatingsFormTest($countries){
		foreach($countries as $country){
			$cmd = "~/Desktop/android-sdk-linux/platform-tools/adb -s emulator-5554 shell am instrument -w -e class com.rocket.framework.testshell.test.RatingsFormTest#testGetRatingsForm".$country." com.rocket.framework.testshell.test/android.test.InstrumentationTestRunner";
			exec($cmd, $result);
			print_r($result);
			echo "RatingsFormTest".$country." finished - ";
			echo checkErrors($result);
			$result = array();

		}
	}
	
	
function RatingsTest($countries){
		foreach($countries as $country){
			$cmd = "~/Desktop/android-sdk-linux/platform-tools/adb -s emulator-5554 shell am instrument -w -e class com.rocket.framework.testshell.test.RatingsTest#testGetRatings".$country." com.rocket.framework.testshell.test/android.test.InstrumentationTestRunner";
			exec($cmd, $result);
			print_r($result);
			echo "RatingsTest".$country." finished - ";
			echo checkErrors($result);
			$result = array();

		}
	}
	
function TermsConditionsTest($countries){
		foreach($countries as $country){
			$cmd = "~/Desktop/android-sdk-linux/platform-tools/adb -s emulator-5554 shell am instrument -w -e class com.rocket.framework.testshell.test.TermsConditionsTest#testGetTermsConditions".$country." com.rocket.framework.testshell.test/android.test.InstrumentationTestRunner";
			exec($cmd, $result);
			print_r($result);
			echo "TermsConditionsTest".$country." finished - ";
			echo checkErrors($result);
			$result = array();

		}
	}
	

RegisterTests($countries);
/**
RegisterFormTest($countries);	
ApiInfoTest($countries);
CallToOrder($countries);
CategoriesTests($countries);
ProductDetailsTests($countries);
ProductListTests($countries);
ImageResolutionsTest($countries);
InitFormTest($countries);
LoginFormTests($countries);
PromotionsTest($countries);
RatingsFormTest($countries);
RatingsTest($countries);
TermsConditionsTest($countries);
RegisterFormTest($countries);
**/


function checkErrors($result){
	foreach ($result as $value){
		if (strpos($value,'Error') !== false) {
			echo "\033[01;5;31mFAILED\033[0m\n";
			$val = split("<<",$result[2]);
			echo "\033[01;5;31m".$val[1]."\033[0m\n";
			return;
			}
		}
		echo "\033[01;32mOK\033[0m\n";
	}
	

?>
