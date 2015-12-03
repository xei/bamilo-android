#!/bin/bash

##############################
# values/strings.xml | 98e0f30..b7d5eff  Accepted
##############################

DIR="./JumiaApp/src/main/res"
FILE="$DIR/.wti"

# $1->FROM
# $2->SUBJECT
# $3->CONTENT
# $4->RECIPIENTS
email() {
    #TEMP="$(mktemp /tmp/wti_output.XXXXXXXXXX)"
    echo "$3" | mail -r "$1" -s "$2" "$4"
}

##### GOTO WTI FOLDER
echo -n "> FINDING WTI FOLDER:... "
if [ ! -f $FILE ];
then echo "File not found: $FILE"; exit 1;
else cd $DIR; echo "OK";
fi

##### Push new strings to WTI
FROM="mobilerocketporto@gmail.com"
RECIPIENTS="sergio.pereira@africainternetgroup.com"
SUBJECT="[Jumia] - Pushed new strings to WebTranslateIt"
echo -n "> PUSHING NEW STRINGS TO WIT:.... "
RESULT="$(wti push)"
if [ $(echo "${RESULT}" | grep -E 'xml.*Accepted' | wc -l) -eq 0 ];
then echo "SKIPPED";
else echo "ACCEPTED"; email "$FROM" "$SUBJECT" "$RESULT" "$RECIPIENTS";
fi