package com.mobile.newFramework.rest;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.rest.configs.AigRestContract;

import de.akquinet.android.androlog.Log;

public class RestUrlUtils {

    private final static String TAG = RestUrlUtils.class.getSimpleName();

//    /**
//     *
//     *  Return the complete url of uri. Port is added if possible.
//     *
//     */
//    public static String completeUrlWithPort(Uri uri) {
//        String completeUrl = completeUri(uri).toString();
//        try {
//            URL url = new URL(completeUrl);
//            URIBuilder uriBuilder = new URIBuilder(completeUrl);
//            uriBuilder.setPort(url.getDefaultPort());
//            completeUrl = uriBuilder.toString();
//        } catch (MalformedURLException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//
//        if (Darwin.logDebugEnabled) {
//            Log.d(TAG, "completeUriWithPort: uri = " + completeUrl);
//        }
//
//        return completeUrl;
//    }

    public static Uri completeUri(Uri uri) {
        //
        if (Darwin.logDebugEnabled) {
            Log.d(TAG, "completeUri: host= " + AigRestContract.REQUEST_HOST + " base= " + AigRestContract.REST_BASE_PATH + " service= " + uri);
        }
        //
        Uri.Builder builder = uri.buildUpon();
        //
        if (uri.getAuthority() == null) {
            builder.authority(AigRestContract.REQUEST_HOST).path(AigRestContract.REST_BASE_PATH + uri.getPath());
            Log.w(TAG, "Url " + uri + " should include authority, authority and base path added");
        }
        //
        if (AigRestContract.USE_ONLY_HTTPS) {
            if (Darwin.logDebugEnabled) {
                Log.d(TAG, "Request type changed to https.");
            }
            builder.scheme("https");
        }
        /**
         * Temporary: Force http for Bamilo.
         * TODO: Remove me if Bamilo supports https.
         */
        if (AigRestContract.USE_ONLY_HTTP) {
            Log.i(TAG, "BAMILO REQUEST: force http.");
            builder.scheme("http");
        }
        //
        uri = builder.build();
        if (Darwin.logDebugEnabled) {
            Log.d(TAG, "Rebuilded uri: " + uri);
        }
        return uri;
    }

    /**
     * Method used to get a parameter value from url.
     * @param url - The valid url
     * @param parameter - The valid key
     * @return String
     */
    public static String getQueryValue(@NonNull String url, @NonNull String parameter) {
        return Uri.parse(url).getQueryParameter(parameter);
    }

    /**
     * Get all parameters from url query and insert them all on ContentValues.
     * Syntax example: ?category=womens-dresses&sort=price&dir=asc
     * @param uri
     */
    public static ContentValues getQueryParameters(Uri uri) {
        if (uri.isOpaque()) {
            throw new UnsupportedOperationException("This isn't a hierarchical URI.");
        }

        ContentValues queryValues = new ContentValues();

        String query = uri.getEncodedQuery();
        if (query == null) {
            return queryValues;
        }

        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = (next == -1) ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            String name = Uri.decode(query.substring(start, separator));
            queryValues.put(name, uri.getQueryParameter(name));

            // Move start to end of name.
            start = end + 1;
        } while (start < query.length());
        return queryValues;
    }
}
