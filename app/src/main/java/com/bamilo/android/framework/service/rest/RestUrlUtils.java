package com.bamilo.android.framework.service.rest;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bamilo.android.framework.service.Darwin;
import com.bamilo.android.framework.service.rest.configs.AigRestContract;


@SuppressWarnings("unused")
public class RestUrlUtils {

    private final static String TAG = RestUrlUtils.class.getSimpleName();

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
//        if (AigRestContract.USE_ONLY_HTTPS) {
//            if (Darwin.logDebugEnabled) {
//                Log.d(TAG, "Request type changed to https.");
//            }
            builder.scheme("https");
//        }
        /**
         * Temporary: Force http for Bamilo.
         * TODO: Remove me if Bamilo supports https.
         */
//        if (AigRestContract.USE_ONLY_HTTP) {
//            Log.i(TAG, "BAMILO REQUEST: force http.");
//            builder.scheme("http");
//        }
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
     * Get all parameters from url string query and insert them all on ContentValues.
     * Syntax example: ?category=womens-dresses&sort=price&dir=asc&brand=ax-paris::new-look
     */
    public static ContentValues getQueryParameters(@NonNull String rawQuery) {
        Uri uri = Uri.parse(rawQuery);

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
