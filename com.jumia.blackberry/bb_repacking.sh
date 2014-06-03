#!/bin/bash

#######################
##### Environment #####
#######################
DEV=false

#########################
##### DEF CONSTANTS #####
#########################
STOREPASS="sfp.1n.blackberry"
CNF_PATH="com.jumia.blackberry/assets/blackberry/android.cfg"
MNF_PATH="com.jumia.blackberry/assets/blackberry/MANIFEST.MF"
BAR_FOLDER="com.jumia.blackberry/dist/blackberry"
CERT_FOLDER="com.jumia.blackberry/assets/blackberry/certificate"

########################
##### VALIDATE ENV #####
########################
if $DEV
then
	echo "> Dev environment"
	APK_FILE="com.jumia.blackberry/bin/com.jumia.blackberry.apk"
	BAR_NAME="com.jumia.blackberry.bar"
	STORE_CRT_FOLDER=~/.rim/
else
	echo "> Release environment"
	APK_FILE="com.jumia.blackberry/bin/Jumia-release.apk"
	BAR_NAME="Jumia-release.bar"
	STORE_CRT_FOLDER=~/Library/Research\ In\ Motion/
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
cp $CERT_FOLDER/* $STORE_CRT_FOLDER

#####################
##### REPACKING #####
#####################
echo "3 - Repacking apk to bar"
blackberry-apkpackager $APK_FILE $CNF_PATH -m $MNF_PATH -t $BAR_FOLDER -r

################
##### SIGN #####
################
echo "4 - Sign Blackberry App"
blackberry-signer -storepass $STOREPASS $BAR_FOLDER/$BAR_NAME

###################
##### OTHERS ######
###################

#### Generate certificate (TO CREATE A KEY)
# blackberry-keytool -genkeypair -storepass $STOREPASS -dname "cn=Jumia"
