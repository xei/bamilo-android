#!/bin/sh
MATERIAL_FOLDER=$HOME/Dropbox/rocket_lazada_materialsammlung

UI_ELEMENTS=$MATERIAL_FOLDER/23_UI_Elemente/Android

GIT_ROOT=${1}


if [ ! ${GIT_ROOT} ]; then
        echo "Please specify the git root for lazada as first parameter"
	exit;
fi	

TARGETDIR_VIEW_RES=${GIT_ROOT}/pt.rocket.view/res
cp $UI_ELEMENTS/01_xhdpi_320/*.png $TARGETDIR_VIEW_RES/drawable-xhdpi
cp $UI_ELEMENTS/02_hdpi_240/*.png $TARGETDIR_VIEW_RES/drawable-hdpi
cp $UI_ELEMENTS/03_mdpi_160/*.png $TARGETDIR_VIEW_RES/drawable-mdpi
