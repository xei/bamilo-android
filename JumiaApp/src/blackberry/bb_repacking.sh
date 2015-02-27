#!/bin/bash

#############################################################################
# BlackBerry repacking script.												#
# Date: 17/06/2014															#
# Author: sergiopereira														#
# Description: 																#
#		- This script supports Dev enviroment to run in local machine.		#
#		- Copy the content of certicate folder for RIM HOME	(~/.rim)		#
#		- Repacking the apk to bar											#
#		- Sign the bar with respective certicate							#
#		- Upload the apk to HockeyApp with the bar inside 					#
# Requirements:																#
#		- Machine must have the Blackberry command line tools for Android	#
#		- The PATH must contain the bin folder 								#
#############################################################################


#######################
##### ENVIRONMENT #####
#######################
HOSTNAME_JENKINS="jenkinsmastermobile"

#########################
##### DEF CONSTANTS #####
#########################
STOREPASS="jumiablackberry"
PCK_NAME="com.jumia.blackberry"
BAR_FOLDER="$PCK_NAME/dist/blackberry"
CERT_FOLDER="$PCK_NAME/assets/blackberry/certificate"

#####################
##### DEF FILES #####
#####################
CNF_PATH="$PCK_NAME/assets/blackberry/android.cfg"
MNF_PATH="$PCK_NAME/assets/blackberry/MANIFEST.MF"
#ICON_PATH="$PCK_NAME/res/drawable-ldpi/ic_launcher.png"
#HOCKEY_FILE="$BAR_FOLDER/com.jumia.blackberry.zip"

########################
##### VALIDATE ENV #####
########################
if [ ! `hostname -s` == $HOSTNAME_JENKINS ]
then
	echo "> WARNING: Dev environment"
	APK_FILE="$PCK_NAME/build/outputs/apk/JumiaApp-black-jumia-release.apk"
	BAR_FILE="$BAR_FOLDER/JumiaApp-black-jumia-release.bar"
	STORE_CRT_FOLDER=~/.rim/
else
	echo "> Release environment"
	APK_FILE="$PCK_NAME/build/outputs/apk/JumiaApp-black-jumia-release.apk"
	BAR_FILE="$BAR_FOLDER/JumiaApp-black-jumia-release.bar"
	#STORE_CRT_FOLDER=~/Library/Research\ In\ Motion/
	STORE_CRT_FOLDER=~/.rim/
	#### Reload PATH (MAC OS)
	. ~/.bash_profile
fi

####################
##### FIND APK #####
#################### 
echo -n "1 - Find apk: "
if [ -f $APK_FILE ]
then echo "SUCCESS"
else echo "FAIL, File not found!"; exit 1;
fi

#######################
##### CERTIFICATE #####
#######################
echo "2 - Copy Token and Certificate"
cp $CERT_FOLDER/* "$STORE_CRT_FOLDER"

#####################
##### REPACKING #####
#####################
echo "3 - Repacking apk to bar"
#blackberry-apkpackager $APK_FILE $CNF_PATH -m $MNF_PATH -ci $ICON_PATH -t $BAR_FOLDER -r
blackberry-apkpackager $APK_FILE $CNF_PATH -m $MNF_PATH -t $BAR_FOLDER -r

################
##### SIGN #####
################
echo "4 - Sign Blackberry App"
blackberry-signer -storepass $STOREPASS $BAR_FILE

#######################
##### HOCKEY APP ######
#######################
echo "5 - Create HockeyApp file, add bar into apk file"
#zip -j $HOCKEY_FILE $APK_FILE $BAR_FILE
zip -g -j $APK_FILE $BAR_FILE

###################
##### OTHERS ######
###################

#### Generate certificate (TO CREATE A KEY)
# blackberry-keytool -genkeypair -storepass $STOREPASS -dname "cn=Jumia"
