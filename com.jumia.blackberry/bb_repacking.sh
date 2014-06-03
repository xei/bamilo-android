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
MAC_CRT_PATH="/Users/rocket/Library/Research\ In\ Motion/"

########################
##### VALIDATE ENV #####
########################
if $DEV
then
	echo "> Dev environment"
	APK_PATH="com.jumia.blackberry/bin/com.jumia.blackberry.apk"
	BAR_NAME="com.jumia.blackberry.bar"
else
	echo "> Release environment"
	APK_PATH="com.jumia.blackberry/bin/Jumia-release.apk"
	BAR_NAME="Jumia-release.bar"
	#### Reload PATH (MAC OS)
	echo "> Reload PATH"
	. ~/.bash_profile
fi

##### 
echo "1 - Find apk"
#echo Create apk
#ant debug

#######################
##### CERTIFICATE #####
#######################
echo "2 - Copy Token and Certificate"
cp -R $CERT_FOLDER/* "$MAC_CRT_PATH"

#####################
##### REPACKING #####
#####################
echo "3 - Repacking apk to bar"
blackberry-apkpackager $APK_PATH $CNF_PATH -m $MNF_PATH -t $BAR_FOLDER -r

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
