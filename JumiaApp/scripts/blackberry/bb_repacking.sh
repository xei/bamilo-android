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

##############################
##### VALIDATE ARGUMENTS #####
##############################
# Get BB flavour
if [ $# -ne 1 ];
then echo "Please indicates the Blackberry project name:\n> sh JumiaApp/scripts/blackberry/bb_repacking.sh jumiaBlackberry"; exit 1;
else ARG_1=$1
fi

#########################
##### DEF CONSTANTS #####
#########################
STOREPASS="jumiablackberry"
STUDIO_MOBILE_PACKAGE="JumiaApp"
STORE_CRT_FOLDER=~/.rim/

#####################
##### DEF FILES #####
#####################
STUDIO_BB_FLAVOUR="$STUDIO_MOBILE_PACKAGE/src/$ARG_1"
STUDIO_BB_FLAVOUR_CERT_FOLDER="$STUDIO_BB_FLAVOUR/assets/blackberry/certificate"
STUDIO_BB_FLAVOUR_CNF_PATH="$STUDIO_BB_FLAVOUR/assets/blackberry/android.cfg"
STUDIO_BB_FLAVOUR_MNF_PATH="$STUDIO_BB_FLAVOUR/assets/blackberry/MANIFEST.MF"
STUDIO_OUT_APK="$STUDIO_MOBILE_PACKAGE/build/outputs/apk"
STUDIO_OUT_BAR="$STUDIO_MOBILE_PACKAGE/build/outputs/bar"

########################
##### VALIDATE ENV #####
########################
echo -n "1 - Validate Blackberry environment: "
# GRADLE FLAVOUR
if [ ! -d $STUDIO_BB_FLAVOUR ]; then echo "Directory not found: $STUDIO_BB_FLAVOUR"; exit 1; fi
# CERTIFICATE
if [ ! -d $STUDIO_BB_FLAVOUR_CERT_FOLDER ]; then echo "Directory not found: $STUDIO_BB_FLAVOUR"; exit 1; fi
# CONFIGURATION
if [ ! -f $STUDIO_BB_FLAVOUR_CNF_PATH ]; then echo "File not found: $STUDIO_BB_FLAVOUR_CNF_PATH"; exit 1; fi
# MANIFEST
if [ ! -f $STUDIO_BB_FLAVOUR_MNF_PATH ]; then echo "File not found: $STUDIO_BB_FLAVOUR_MNF_PATH"; exit 1; fi
echo "SUCCESS"

############################
##### FIND RELEASE APK #####
############################
APK_FILE="$(ls $STUDIO_OUT_APK/*release.apk)";
echo -n "2 - Find apk: "
if [ -f $APK_FILE ]
then
    echo "SUCCESS. Apk: $APK_FILE";
    BAR_FILE="$STUDIO_OUT_BAR/$(ls $APK_FILE | xargs -n 1 basename | sed -e s/.apk/.bar/g)";
else
    echo "FAIL. File not found: $APK_FILE";
    exit 1;
fi

#######################
##### CERTIFICATE #####
#######################
echo "2 - Copy Token and Certificate"
cp $STUDIO_BB_FLAVOUR_CERT_FOLDER/* "$STORE_CRT_FOLDER"

#####################
##### REPACKING #####
#####################
echo "3 - Repacking apk to bar"
#blackberry-apkpackager $APK_FILE $CNF_PATH -m $MNF_PATH -ci $ICON_PATH -t $BAR_FOLDER -r
blackberry-apkpackager $APK_FILE $STUDIO_BB_FLAVOUR_CNF_PATH -m $STUDIO_BB_FLAVOUR_MNF_PATH -t $STUDIO_OUT_BAR -r

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
