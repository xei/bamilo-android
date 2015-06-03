package com.mobile.newFramework.rest;

import android.net.Uri;

import com.mobile.framework.Darwin;
import com.mobile.framework.rest.RestContract;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import ch.boye.httpclientandroidlib.client.utils.URIBuilder;
import de.akquinet.android.androlog.Log;

public class RestUrlUtils {

    private final static String TAG = RestUrlUtils.class.getSimpleName();

    /**
     *
     *  Return the complete url of uri. Port is added if possible.
     *
     */
    public static String completeUrlWithPort(Uri uri) {
        String completeUrl = completeUri(uri).toString();
        try {
            URL url = new URL(completeUrl);
            URIBuilder uriBuilder = new URIBuilder(completeUrl);
            uriBuilder.setPort(url.getDefaultPort());
            completeUrl = uriBuilder.toString();
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }

        if (Darwin.logDebugEnabled) {
            Log.d(TAG, "completeUriWithPort: uri = " + completeUrl);
        }

        return completeUrl;
    }

    public static Uri completeUri(Uri uri) {
        //
        if (Darwin.logDebugEnabled) {
            Log.d(TAG, "completeUri: host= " + RestContract.REQUEST_HOST + " base= " + RestContract.REST_BASE_PATH + " service= " + uri);
        }
        //
        Uri.Builder builder = uri.buildUpon();
        //
        if (uri.getAuthority() == null) {
            builder.authority(RestContract.REQUEST_HOST).path(RestContract.REST_BASE_PATH + uri.getPath());
            Log.w(TAG, "Url " + uri + " should include authority, authority and base path added");
        }
        //
        if(RestContract.USE_ONLY_HTTPS){
            if ( Darwin.logDebugEnabled) {
                Log.d(TAG, "Request type changed to https.");
            }
            builder.scheme("https");
        }
        /**
         * Temporary: Force http for Bamilo.
         * TODO: Remove me if Bamilo supports https.
         */
        if(RestContract.USE_ONLY_HTTP) {
            Log.i(TAG, "BAMILO REQUEST: force http.");
            builder.scheme("http");
        }
        //
        uri = builder.build();
        if ( Darwin.logDebugEnabled) {
            Log.d(TAG, "Rebuilded uri: " + uri);
        }
        return uri;
    }


}
