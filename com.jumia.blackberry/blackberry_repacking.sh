#!/bin/sh

# Create BlackBerry Account
# user: laurent.detapol@jumia.com
# pass: RocketInternet2014

# Create apk
#echo Create apk
#ant debug

STOREPASS="sfp.1n.blackberry"
APK_PATH="com.jumia.blackberry/bin/Jumia-release.apk"
CNF_PATH="com.jumia.blackberry/assets/blackberry/android.cfg"
MNF_PATH="com.jumia.blackberry/assets/blackberry/MANIFEST.MF"
BAR_FOLDER="com.jumia.blackberry/dist/blackberry"
CERT_FOLDER="com.jumia.blackberry/assets/blackberry/certificate"
MAC_CRT_PATH="/Users/rocket/Library/Research\ In\ Motion"
BAR_NAME="Jumia-release.bar"

# Reload PATH
echo "> Reload PATH"
. ~/.bash_profile

# Generate certificate
# blackberry-keytool -genkeypair -storepass $STOREPASS -dname "cn=Jumia"

# Copy Certicate
echo "> Copy certificate"
cp $CERT_FOLDER/* '$MAC_CRT_PATH'

# Repacking apk to bar
echo "> Repacking apk to bar"
blackberry-apkpackager $APK_PATH $CNF_PATH -m $MNF_PATH -t $BAR_FOLDER -r

# Sign Blackberry App
echo "> Sign Blackberry App"
blackberry-signer -storepass $STOREPASS $BAR_FOLDER/$BAR_NAME
