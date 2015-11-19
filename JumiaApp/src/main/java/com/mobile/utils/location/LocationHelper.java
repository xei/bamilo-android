package com.mobile.utils.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.mobile.app.JumiaApplication;
import com.mobile.controllers.ChooseLanguageController;
import com.mobile.newFramework.database.CountriesConfigsTableHelper;
import com.mobile.newFramework.objects.configs.CountryObject;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.preferences.ShopPreferences;

import java.util.List;
import java.util.Locale;

/**
 * Class used to perform the automatic country selection
 * @author sergiopereira
 */
public class LocationHelper implements LocationListener {

    private static final String TAG = LocationHelper.class.getSimpleName();

    private static LocationHelper locationHelper;
    
    public static final boolean SELECTED = true;
    
    public static final boolean NO_SELECTED = false;
    
    public static final int TIMEOUT = 5000;
    
    private Context context;

    private Handler callback;

    private Handler timeoutHandle = new Handler();
    
    private boolean locationReceived = false;
    
    /**
     * Get instance of the Location Helper
     * @return
     */
    public static LocationHelper getInstance(){
        if(locationHelper == null) locationHelper = new LocationHelper();
        return locationHelper;
    }
    
    /**
     * Method used to perform the auto selection using the next points
     * <p>- Network configuration
     * <p>- SIM card
     * <p>- Last known location from location manager
     * <p>- Current location from location manager (GPS or Network)
     * @param context
     * @param callback
     * @author sergiopereira
     */
    public void autoCountrySelection(Context context, Handler callback){

        initializeLocationHelper(context, callback);

    	// From device
        TelephonyManager deviceManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // From network
        if(getCountryFromNetwork(deviceManager)) return;
        // From sim card
        if(getCountryFromSim(deviceManager)) return;
        
        // From geo location
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // From last known geo location
        if(getContryFromLastKnownLocation(locationManager)) return;
        // From current geo location
        if(getCountryFromCurrentLocation(locationManager)) return;

        // From dialog
        sendUserInteractionMessage(null, ErrorCode.REQUIRES_USER_INTERACTION);
    }
    
    /**
     * ################## REQUESTS ################## 
     */

    /**
     * Get the country code from Network configurations
     * @param deviceManager
     * @return true or false
     * @author sergiopereira
     */
    private boolean getCountryFromNetwork(TelephonyManager deviceManager){
        String networkCountry = deviceManager.getNetworkCountryIso();
        if(isCountryAvailable(networkCountry)){
            Print.i(TAG, "MATCH COUNTRY FROM NETWORK: " + networkCountry);
            sendInitializeMessage();
            return true;
        }
        Print.i(TAG, "NO MATCH COUNTRY FROM NETWORK: " + networkCountry);
        return false;
    }
    
    /**
     * Get the country code from SIM card
     * @param deviceManager
     * @return true or false
     * @author sergiopereira
     */
    private boolean getCountryFromSim(TelephonyManager deviceManager) {
        String simCountry = deviceManager.getSimCountryIso();
        if(isCountryAvailable(simCountry)) {
            Print.i(TAG, "MATCH COUNTRY FROM SIM: " + simCountry);
            sendInitializeMessage();
            return true;
        }
        Print.i(TAG, "NO MATCH COUNTRY FROM SIM: " + simCountry);
        return false;
    }
    
    
    /**
     * Get the country code from the last known location using the GeoCoder api.
     * @param locationManager
     * @return true or false
     * @author sergiopereira
     */
    private boolean getContryFromLastKnownLocation(LocationManager locationManager){
        try {
            
            String bestProvider = getBestLocationProvider(locationManager);
            if(bestProvider == null) {
                Print.i(TAG, "NO MATCH COUNTRY FROM LASTLOCATION: BEST PROVIDER IS NULL");
                return false;
            }
            
            Location lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);
            double lat = lastKnownLocation.getLatitude();
            double lng = lastKnownLocation.getLongitude();
            String geoCountry = getCountryCodeFomGeoCoder(lat, lng);
            if(isCountryAvailable(geoCountry)) {
                Print.i(TAG, "MATCH COUNTRY FROM LASTLOCATION: " + geoCountry + " (" + lat + "/" + lng + ")");
                sendInitializeMessage();
                return true;
            }
            Print.i(TAG, "NO MATCH COUNTRY FROM LASTLOCATION: " + geoCountry + " (" + lat + "/" + lng + ")");
            return false;

        } catch (Exception e) {
            Print.w(TAG, "NO MATCH COUNTRY FROM LASTLOCATION: LAST KNOWN LOCATION IS NULL " + e.getMessage());
            return false;
        }
        
    }
    
    /**
     * Get the country code from current location requested to location manager using the GeoCoder api.
     * @param locationManager
     * @return true or false
     * @author sergiopereira
     */
    private boolean getCountryFromCurrentLocation(LocationManager locationManager) {
        try {
            
            Print.i(TAG, "GET COUNTRY FROM CURRENT GEOLOCATION");
            String bestProvider = getBestLocationProvider(locationManager);
            if(bestProvider != null) {
                locationManager.requestLocationUpdates(bestProvider, 0, 0, this);
                timeoutHandle.postDelayed(timeoutRunnable, TIMEOUT);
                return true;
            }
            Print.i(TAG, "NO MATCH COUNTRY FROM CURRENT LOCATION");
            return false;
            
        } catch (Exception e) {
            Print.w(TAG, "NO MATCH COUNTRY FROM CURRENT LOCATION", e);
            return false;
        }
        
    }
    
    
    /**
     * Get the best location provider GPS or Network 
     * @param locationManager
     * @return String
     * @author sergiopereira
     */
    private String getBestLocationProvider(LocationManager locationManager){
//        Criteria criteria = new Criteria();
//        // criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//        //String bestProvider = (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) ? LocationManager.NETWORK_PROVIDER : LocationManager.GPS_PROVIDER;
//        String bestProvider = locationManager.getBestProvider(criteria, false);
//        Log.i(TAG, "BEST PROVIDER: " + bestProvider);
//        return (checkConnection() && locationManager.isProviderEnabled(bestProvider)) ? bestProvider : null;

        // Get the best provider
        String bestProvider = null;
        // Validate if GPS is enabled
        if(checkConnection() && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            bestProvider = LocationManager.NETWORK_PROVIDER;
        // Validate if GPS disabled, connection and Network provider
        if(bestProvider == null && checkConnection() && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            bestProvider = LocationManager.GPS_PROVIDER;
        
        // Return provider
        Print.i(TAG, "SELECTED PROVIDER: " + bestProvider);
        return bestProvider;
    }
    
    /**
     * Check the connection
     * @return true or false
     * @author sergiopereira
     */
    private boolean checkConnection() {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    
    /**
     * Timeout runnable executed after TIMEOUT delay.
     * @author sergiopereira
     */
    Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            Print.i(TAG, "ON TIMEOUT RUNNABLE: " + locationReceived);
            // Validate flag
            if(!locationReceived) {
                // Remove the listener previously added
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                locationManager.removeUpdates(LocationHelper.this);
                sendUserInteractionMessage(null, ErrorCode.REQUIRES_USER_INTERACTION);
            }
        }
    };
    
    /**
     * ################## VALIDATE COUNTRY ################## 
     */
    
    /**
     * Method used to select the shop id validating the received country code is supported by application.
     * @param countryCode
     * @return true or false
     */
    public boolean isCountryAvailable(String countryCode) {
        // Filter country code 
        if(countryCode == null || countryCode.length() != 2) return NO_SELECTED;

        // Valdiate countries available
        if(JumiaApplication.INSTANCE.countriesAvailable == null || JumiaApplication.INSTANCE.countriesAvailable.size() == 0 )
            JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        
        // Get the supported countries
        if(JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0 ){
            for (int i = 0; i < JumiaApplication.INSTANCE.countriesAvailable.size(); i++) {
                CountryObject countryObject =JumiaApplication.INSTANCE.countriesAvailable.get(i);
                String supportedCountry = countryObject.getCountryIso();
                //Log.d(TAG, "SUPPORTED COUNTRY: " + supportedCountry);
                if (supportedCountry.equalsIgnoreCase(countryCode.toLowerCase())){
                    Print.d(TAG, "MATCH: SHOP ID " + i);
                    ChooseLanguageController.setLanguageBasedOnDevice(countryObject.getLanguages(), countryCode);
                    ShopPreferences.setShopId(context, i);
                    return SELECTED;
                }
            }
        }
        return NO_SELECTED;
    }

    /**
     * ################## LOCATION LISTENER ################## 
     */
    
    /*
     * (non-Javadoc)
     * @see android.location.LocationListener#onLocationChanged(android.location.Location)
     */
    @Override
    public void onLocationChanged(Location location) {
        Print.d(TAG, "ON LOCATION CHANGED");
                
        // Set the flag
        locationReceived = true;
        if(timeoutHandle != null) timeoutHandle.removeCallbacks(timeoutRunnable);
        
        // Remove the listener previously added
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
        
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        String geoCountry = getCountryCodeFomGeoCoder(lat, lng);
//        String geoCountry = "CM";
        if(isCountryAvailable(geoCountry)) {
        	Print.i(TAG, "MATCH COUNTRY FROM GEOLOCATION: " + geoCountry + " (" + lat + "/" + lng + ")");
        	sendInitializeMessage();
        } else {
        	Print.i(TAG, "NO MATCH COUNTRY FROM GEOLOCATION: " + geoCountry + " (" + lat + "/" + lng + ")");
        	sendUserInteractionMessage(null, ErrorCode.REQUIRES_USER_INTERACTION);
        }
        
    }

    /*
     * (non-Javadoc)
     * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
     */
    @Override
    public void onProviderDisabled(String provider) { 
        Print.d(TAG, "ON PROVIDER DISABLED: " + provider);
        
        // Requires user interaction
        sendUserInteractionMessage(null, ErrorCode.REQUIRES_USER_INTERACTION);
        
        // Remove the listener you previously added
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
    }

    /*
     * (non-Javadoc)
     * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
     */
    @Override
    public void onProviderEnabled(String provider) { 
        Print.d(TAG, "ON PROVIDER ENABLED: " + provider);
    }

    /*
     * (non-Javadoc)
     * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { 
        Print.d(TAG, "ON STATUS CHANGED");
    }
    
    /**
     * ################## GEO CODER ################## 
     */
    
    /**
     * Method used to revert the GEO location
     * @param lat
     * @param lng
     * @return true or false
     * @author sergiopereira
     * @see http://stackoverflow.com/questions/11082681/get-country-from-coordinates
     */
    private String getCountryCodeFomGeoCoder(double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String countryCode = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address address = addresses.get(0);
            countryCode =  address.getCountryCode();
            Print.d(TAG, "GET COUNTRY FROM GEOCODER: " + countryCode);
        } catch (Exception e) {
            Print.w(TAG, "GET ADDRESS EXCEPTION: " + e.getMessage());
        }
        return countryCode;
    }

    
    /**
     * ################## MESSAGE ################## 
     */
    
    /**
     * Send the message REQUIRES_USER_INTERACTION to callback
     * @author sergiopereira
     */
    private void sendUserInteractionMessage(EventType eventType, @ErrorCode.Code int errorType){
        Print.d(TAG, "SEND MESSAGE: " + eventType + " " + errorType);
        Message msg = new Message();
        msg.obj = new BaseResponse<>(eventType, errorType);
        callback.sendMessage(msg);
    }
    
    /**
     * Send the INITIALIZE message to JumiaApplication
     * @author sergiopereira
     */
    public void sendInitializeMessage(){
        Print.d(TAG, "SEND MESSAGE: INITIALIZE");
        JumiaApplication.INSTANCE.init(callback);
    }

    /**
     * set location helper context and callback
     * @param ctx
     */
    public void initializeLocationHelper (Context ctx, Handler callback){
        this.context = ctx;
        this.callback = callback;
    }
}
