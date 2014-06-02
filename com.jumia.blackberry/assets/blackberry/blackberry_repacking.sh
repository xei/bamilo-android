#!/bin/sh

# Create BlackBerry Account
# user: laurent.detapol@jumia.com
# pass: RocketInternet2014

# Create apk
#echo Create apk
#ant debug

# Reload PATH
. ~/.bash_profile

STOREPASS="sfp.1n.blackberry"
APK_PATH="./com.jumia.blackberry/bin/Jumia-release.apk"
CNF_PATH="./com.jumia.blackberry/assets/blackberry/android.cfg"
MNF_PATH="./com.jumia.blackberry/assets/blackberry/MANIFEST.MF"
BAR_FOLDER="./com.jumia.blackberry/dist/blackberry"
BAR_NAME="Jumia-release.bar"

# Repacking apk to bar
echo Repacking apk to bar
blackberry-apkpackager $APK_PATH $CNF_PATH -m $MNF_PATH -t $BAR_FOLDER -r

# Sign bar
echo Repacking apk to bar
#blackberry-signer -bbidtoken bb_id_token.csk -storepass <PASS> ./com.jumia.blackberry/dist/blackberry/com.jumia.blackberry.bar
blackberry-signer -storepass $STOREPASS $BAR_FOLDER/$BAR_NAME

# Sign App
#./batchbar-signer MyApp.bar mykey.p12 <<STOREPASS>> <<CSKPASS>>