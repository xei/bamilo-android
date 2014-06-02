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
APK_PATH="./com.jumia.blackberry/bin/com.jumia.blackberry.apk"
CNF_PATH="./com.jumia.blackberry/assets/blackberry/android.cfg"
MNF_PATH="./com.jumia.blackberry/assets/blackberry/MANIFEST.MF"
BAR_FOLDER="./com.jumia.blackberry/dist/blackberry"
BAR_NAME="com.jumia.blackberry.bar"

# Repacking apk to bar
echo Repacking apk to bar
blackberry-apkpackager $APK_PATH $CNF_PATH -m $MNF_PATH -t $BAR_FOLDER -r
# -r -> find .apk, .cfg, .mf, and .png files recursively in specified folders
# -m -> use custom manifest file(s); e.g. Helloworld.apk to use Helloworld.MF

# Sign bar
echo Repacking apk to bar
#blackberry-signer -bbidtoken bb_id_token.csk -storepass sfp.1n.blackberry ./com.jumia.blackberry/dist/blackberry/com.jumia.blackberry.bar
blackberry-signer -storepass sfp.1n.blackberry $BAR_FOLDER/$BAR_NAME

# Sign App
#./batchbar-signer MyApp.bar mykey.p12 <<STOREPASS>> <<CSKPASS>>