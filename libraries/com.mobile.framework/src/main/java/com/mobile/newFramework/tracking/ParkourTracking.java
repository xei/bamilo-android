package com.mobile.newFramework.tracking;

import com.mobile.newFramework.utils.output.Print;
import com.parkourmethod.parkour.Parkour;

/**
 * @author Andre Lopes
 */
public class ParkourTracking {

    /**
     * Default interval, returns a location update between every 30 seconds and 2 minutes
     * @param activity - The Parkour activity
     */
    public static void onStartup(Parkour activity) {
        Print.i("ON START PARKOUR METHED");
        activity.parkourStart();
        //activity.parkourSetInterval(0);
        //activity.parkourStop();
    }

}
