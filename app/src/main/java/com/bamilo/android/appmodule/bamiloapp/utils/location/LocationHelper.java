package com.bamilo.android.appmodule.bamiloapp.utils.location;

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

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.controllers.ChooseLanguageController;
import com.bamilo.android.framework.service.database.CountriesConfigsTableHelper;
import com.bamilo.android.framework.service.objects.configs.CountryObject;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.rest.errors.ErrorCode;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.appmodule.bamiloapp.preferences.ShopPreferences;

import java.lang.ref.WeakReference;
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

    private WeakReference<Handler> callback;

    private final Handler timeoutHandle = new Handler();
    
    private boolean locationReceived = false;
    
    /**
     * Get instance of the Location Helper
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
     * @author sergiopereira
     */
    public void autoCountrySelection(Context context, Handler callback) {
        try {
            // Init
            initializeLocationHelper(context, callback);
            // From device
            TelephonyManager deviceManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            // From network
            if (getCountryFromNetwork(deviceManager)) return;
            // From sim card
            if (getCountryFromSim(deviceManager)) return;
            // From geo location
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            // From last known geo location
            if (getCountryFromLastKnownLocation(locationManager)) return;
            // From current geo location
            if (getCountryFromCurrentLocation(locationManager)) return;
            // From dialog
            sendUserInteractionMessage(null, ErrorCode.REQUIRES_USER_INTERACTION);
        } catch (NullPointerException e) {
            sendUserInteractionMessage(null, ErrorCode.REQUIRES_USER_INTERACTION);
        }
    }
    
    /*
     * ################## REQUESTS ################## 
     */

    /**
     * Get the country code from Network configurations
     */
    private boolean getCountryFromNetwork(TelephonyManager deviceManager) {
        String networkCountry = deviceManager.getNetworkCountryIso();
        if (isCountryAvailable(networkCountry)) {
            sendInitializeMessage();
            return true;
        }
        return false;
    }

    /**
     * Get the country code from SIM card
     */
    private boolean getCountryFromSim(TelephonyManager deviceManager) {
        String simCountry = deviceManager.getSimCountryIso();
        if (isCountryAvailable(simCountry)) {
            sendInitializeMessage();
            return true;
        }
        return false;
    }

    /**
     * Get the country code from the last known location using the GeoCoder api.
     */
    private boolean getCountryFromLastKnownLocation(LocationManager locationManager){
        try {
            
            String bestProvider = getBestLocationProvider(locationManager);
            if(bestProvider == null) {
                return false;
            }
            
            Location lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);
            double lat = lastKnownLocation.getLatitude();
            double lng = lastKnownLocation.getLongitude();
            String geoCountry = getCountryCodeFomGeoCoder(lat, lng);
            if(isCountryAvailable(geoCountry)) {
                sendInitializeMessage();
                return true;
            }
            return false;

        } catch (Exception e) {
            return false;
        }
        
    }
    
    /**
     * Get the country code from current location requested to location manager using the GeoCoder api.
     */
    private boolean getCountryFromCurrentLocation(LocationManager locationManager) {
        try {
            
            String bestProvider = getBestLocationProvider(locationManager);
            if(bestProvider != null) {
                locationManager.requestLocationUpdates(bestProvider, 0, 0, this);
                timeoutHandle.postDelayed(timeoutRunnable, TIMEOUT);
                return true;
            }
            return false;
            
        } catch (Exception e) {
            return false;
        }
        
    }

    /**
     * Get the best location provider GPS or Network 
     */
    private String getBestLocationProvider(LocationManager locationManager){

        // Get the best provider
        String bestProvider = null;
        // Validate if GPS is enabled
        if(checkConnection() && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            bestProvider = LocationManager.NETWORK_PROVIDER;
        // Validate if GPS disabled, connection and Network provider
        if(bestProvider == null && checkConnection() && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            bestProvider = LocationManager.GPS_PROVIDER;
        
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
     */
    public boolean isCountryAvailable(String countryCode) {
        // Filter country code 
        if (countryCode == null || countryCode.length() != 2) {
            return NO_SELECTED;
        }
        // Validate countries available
        if (BamiloApplication.INSTANCE.countriesAvailable == null || BamiloApplication.INSTANCE.countriesAvailable.size() == 0) {
            BamiloApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        }
        // Get the supported countries
        if (BamiloApplication.INSTANCE.countriesAvailable != null && BamiloApplication.INSTANCE.countriesAvailable.size() > 0) {
            for (int i = 0; i < BamiloApplication.INSTANCE.countriesAvailable.size(); i++) {
                CountryObject countryObject = BamiloApplication.INSTANCE.countriesAvailable.get(i);
                String supportedCountry = countryObject.getCountryIso();
                if (TextUtils.equalsIgnoreCase(supportedCountry, countryCode)) {
                    ChooseLanguageController.setLanguageBasedOnDevice(countryObject.getLanguages(), countryCode);
                    ShopPreferences.setShopId(context, i);
                    return SELECTED;
                }
            }
        }
        return NO_SELECTED;
    }

    @Override
    public void onLocationChanged(Location location) {
        // Set the flag
        locationReceived = true;
        if(timeoutHandle != null) timeoutHandle.removeCallbacks(timeoutRunnable);
        // Remove the listener previously added
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
        // Get location
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        String geoCountry = getCountryCodeFomGeoCoder(lat, lng);
        // Validate country
        if(isCountryAvailable(geoCountry)) {
        	sendInitializeMessage();
        } else {
        	sendUserInteractionMessage(null, ErrorCode.REQUIRES_USER_INTERACTION);
        }
    }

    @Override
    public void onProviderDisabled(String provider) { 
        // Requires user interaction
        sendUserInteractionMessage(null, ErrorCode.REQUIRES_USER_INTERACTION);
        // Remove the listener you previously added
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onProviderEnabled(String provider) { 
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { 
    }

    
    /**
     * Method used to revert the GEO location
     * @see <a href="http://stackoverflow.com/questions/11082681/get-country-from-coordinates">get-country-from-coordinates</a>
     */
    private String getCountryCodeFomGeoCoder(double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String countryCode = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address address = addresses.get(0);
            countryCode =  address.getCountryCode();
        } catch (Exception ignored) {
        }
        return countryCode;
    }

    /**
     * Send the message REQUIRES_USER_INTERACTION to callback
     */
    private void sendUserInteractionMessage(EventType eventType, @ErrorCode.Code int errorType){
        Message msg = new Message();
        msg.obj = new BaseResponse<>(eventType, errorType);
        callback.get().sendMessage(msg);
    }
    
    /**
     * Send the INITIALIZE message to BamiloApplication
     */
    public void sendInitializeMessage(){
        BamiloApplication.INSTANCE.init(callback.get());
    }

    /**
     * set location helper context and callback
     */
    public void initializeLocationHelper (Context ctx, Handler callback){
        this.context = ctx;
        this.callback = new WeakReference<>(callback);
    }
}
