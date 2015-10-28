#!/bin/bash

#############################################################################
# BlackBerry repacking script.												#
# Date: 17/06/2014															#
# Author: sergiopereira														#
# Description: 																#
#		- Copy the content of certicate folder for RIM HOME	(~/.rim)		#
#		- Repacking the apk to bar											#
#		- Sign the bar with respective certicate							#
#		- Upload the bar to HockeyApp                                       #
# Requirements:																#
#		- Machine must have the Blackberry command line tools for Android	#
#		- The PATH must contain the bin folder 								#
#############################################################################

##############################
##### VALIDATE OPTIONS #####
##############################

# HELP OUTPUT
HOW_TO_USE="### HELP ###
Script used to repacking an .apk into a .bar file.
> sh $0 [-h] [-f bb_flavor] [-t build_type]
Where:
    -h  show this help text
    -f  blackberry flavor directory name (jumiaBlackberry)
    -t  build type, release or debug (default:release)"

# VALIDATE NUMBER OF ARGUMENTS
[ $# -eq 0 ] && { echo "$HOW_TO_USE" >&2; exit 1; }

# DEFAULTS
BUILD_TYPE=release

# VALIDATE OPTIONS
while getopts ':hf:t:' option; do
    case "$option" in
        h) echo "$HOW_TO_USE"; exit; ;;
        f) FLAVOR_NAME_FOLDER=$OPTARG; ;;
        t) BUILD_TYPE=$OPTARG; ;;
        :) printf "ERROR: Missing argument for -%s\n" "$OPTARG" >&2; echo "$HOW_TO_USE" >&2; exit 1; ;;
        \?) printf "ERROR: Illegal option: -%s\n" "$OPTARG" >&2; echo "$HOW_TO_USE" >&2; exit 1; ;;
        *) echo "$HOW_TO_USE" >&2; exit 1; ;;
    esac
done

#########################
##### DEF CONSTANTS #####
#########################
STOREPASS="jumia2015"
STUDIO_MOBILE_PACKAGE="JumiaApp"
STORE_CRT_FOLDER=~/.rim/

#####################
##### DEF FILES #####
#####################
STUDIO_BB_FLAVOUR="$STUDIO_MOBILE_PACKAGE/src/$FLAVOR_NAME_FOLDER"
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
[ ! -d $STUDIO_BB_FLAVOUR ] && { echo "Blackberry flavour not found: $STUDIO_BB_FLAVOUR"; exit 1; }
# CERTIFICATE
[ ! -d $STUDIO_BB_FLAVOUR_CERT_FOLDER ] && { echo "Directory not found: $STUDIO_BB_FLAVOUR"; exit 1; }
# CONFIGURATION
[ ! -f $STUDIO_BB_FLAVOUR_CNF_PATH ] && { echo "File not found: $STUDIO_BB_FLAVOUR_CNF_PATH"; exit 1; }
# MANIFEST
[ ! -f $STUDIO_BB_FLAVOUR_MNF_PATH ] && { echo "File not found: $STUDIO_BB_FLAVOUR_MNF_PATH"; exit 1; }
echo "SUCCESS"

############################
##### FIND RELEASE APK #####
############################
APK_FILE="$(ls $STUDIO_OUT_APK/*$BUILD_TYPE.apk | grep -i $FLAVOR_NAME_FOLDER)";
echo -n "2 - Find '$APK_FILE': "
if [ ! -z $APK_FILE ] && [ -f $APK_FILE ];
then
    echo "SUCCESS";
    BAR_FILE="$STUDIO_OUT_BAR/$(ls $APK_FILE | xargs -n 1 basename | sed -e s/.apk/.bar/g)";
else
    echo "FAILED - File not found.";
    exit 1;
fi

#######################
##### CERTIFICATE #####
#######################
echo -n "3 - Copy Token and Certificate: "
test -d "$STORE_CRT_FOLDER" || mkdir -p "$STORE_CRT_FOLDER" && cp $STUDIO_BB_FLAVOUR_CERT_FOLDER/* "$STORE_CRT_FOLDER"
echo "SUCCESS";

#####################
##### REPACKING #####
#####################
echo "4 - Repacking apk to bar: "
#blackberry-apkpackager $APK_FILE $CNF_PATH -m $MNF_PATH -ci $ICON_PATH -t $BAR_FOLDER -r
blackberry-apkpackager $APK_FILE $STUDIO_BB_FLAVOUR_CNF_PATH -m $STUDIO_BB_FLAVOUR_MNF_PATH -t $STUDIO_OUT_BAR -r

################
##### SIGN #####
################
echo "5 - Sign Blackberry App: '$BAR_FILE'"
blackberry-signer -storepass $STOREPASS $BAR_FILE

#######################
##### HOCKEY APP ######
#######################
#echo "5 - Create HockeyApp file, add bar into apk file"
##zip -j $HOCKEY_FILE $APK_FILE $BAR_FILE
#zip -g -j $APK_FILE $BAR_FILE

###################
##### OTHERS ######
###################

#### Generate certificate (TO CREATE A KEY)
# blackberry-keytool -genkeypair -storepass $STOREPASS -dname "cn=Jumia"
