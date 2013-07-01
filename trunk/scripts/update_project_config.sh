#!/bin/sh

if [ "`basename $PWD`" != "com.lazada.android" ]; then
    echo "Not com.lazada.android project dir - terminating"
    exit -1;
fi	

if [ ! -d ./res ]; then
    echo "No ./res dir - no project dir - terminating"
    exit -1;
fi

updateLibraryInPath() {
  android update lib-project -p $1
}

LIBS="net.hockeyapp.android com.shouldit.proxy com.actionbarsherlock com.slidingmenu com.viewpagerindicator \
      net.simonvt.widgets pt.rocket.framework pt.rocket.view"
# LIBS="net.hockeyapp.android"

for LIB in $LIBS; do
   updateLibraryInPath ../$LIB;
done

android update project --path . --subprojects --target android-16 --name Lazada

