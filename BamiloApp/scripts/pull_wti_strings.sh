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

# GOTO WTI folder (./JumiaApp/src/main/res/.wti)
cd ./JumiaApp/src/main/res/
# Pull from WTI
echo "> PULLING STRINGS FROM WIT...."
#[ $(wti pull | grep -E 'xml.*OK' | wc -l) -eq 0 ] && { echo "STRINGS ARE UPDATED" >&2; exit 1; }
[ $(wti pull | grep -E 'xml.*Skip' | wc -l) -eq 0 ] && { echo "> STRINGS ARE UPDATED!" >&2; exit 0; }
# Push to GIT HUB
echo "> PUSHING STRINGS TO REPOSITORY..."
