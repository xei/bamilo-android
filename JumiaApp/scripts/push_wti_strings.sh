#!/bin/bash

##############################
# values/strings.xml | 98e0f30..b7d5eff  Accepted
##############################

DIR="./JumiaApp/src/main/res"
FILE="$DIR/.wti"

##### GOTO WTI FOLDER
echo -n "> FINDING WTI FOLDER:... "
if [ ! -f $FILE ];
then echo "File not found: $FILE"; exit 1;
else cd $DIR; echo "OK";
fi

##### Push new strings to WTI
echo -n "> PUSHING NEW STRINGS TO WIT:.... "
if [ $(wti push | grep -E 'xml.*Accepted' | wc -l) -eq 0 ];
then echo "SKIPPED";
else echo "ACCEPTED";
fi

