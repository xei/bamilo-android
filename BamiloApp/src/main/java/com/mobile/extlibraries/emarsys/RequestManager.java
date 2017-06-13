package com.mobile.extlibraries.emarsys;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.mobile.view.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Arash Hassanpour on 4/8/2017.
 */

public class RequestManager implements RequestManagerInterface {

    private int _pendingRequestsNumber;
    public String baseUrl = "";
    public Context context;
    private static final int TIME_OUT_SECONDS = 20;

    public static final int CONNECTION_TIMEOUT = TIME_OUT_SECONDS * 1000; // ms

    public static RequestManager initWithBaseUrl(Context context, String _baseUrl) {
        RequestManager requestManager = new RequestManager();
        requestManager.baseUrl = _baseUrl;
        requestManager.context = context;
        return requestManager;
    }

    @Override
    public void asyncGET(String path, Map<String, Object> params, RequestExecutionType type, RequestCompletion completion) {
        asyncRequest(HttpVerb.HttpVerbGET, path, params, type, completion);
    }

    @Override
    public void asyncPOST(String path, Map<String, Object> params, RequestExecutionType type, RequestCompletion completion) {
        asyncRequest(HttpVerb.HttpVerbPOST, path, params, type, completion);

    }

    @Override
    public void asyncPUT(String path, Map<String, Object> params, RequestExecutionType type, RequestCompletion completion) {
        asyncRequest(HttpVerb.HttpVerbPUT, path, params, type, completion);
    }

    @Override
    public void asyncDELETE(String path, Map<String, Object> params, RequestExecutionType type, RequestCompletion completion) {
        asyncRequest(HttpVerb.HttpVerbDELETE, path, params, type, completion);

    }

    @Override
    public void asyncRequest(HttpVerb method, String path, Map<String, Object> params, RequestExecutionType type, RequestCompletion completion) {

        Log.d("Emarsys:", path);
        RunRequest runRequest = new RunRequest(method, path, params, type, completion);
        runRequest.execute();


    }

    private ArrayList<String> getPerfectErrorMessages(JSONObject errorJsonObject) {
        return null;
    }

    String getAuthorizationHeaderValue(Context context) {

        String emarsysApplicationCode = context.getResources().getString(R.string.Emarsys_ApplicationID);
        String emarsysApplicationPassword = context.getResources().getString(R.string.Emarsys_ApplicationPassword);

        String str = String.format("%s:%s", emarsysApplicationCode, emarsysApplicationPassword);
        byte[]   bytesEncoded = Base64.encode(str.getBytes(), Base64.DEFAULT);

        return String.format("Basic %s", new String(bytesEncoded));
    }

    private String getQuery(Map<String, Object> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry pair : params.entrySet())
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getKey().toString(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue().toString(), "UTF-8"));
        }

        return result.toString();
    }


    class RunRequest extends AsyncTask<String, Void, Void> {

        private Exception exception;

        HttpVerb method;
        String path;
        Map<String, Object> params;
        RequestExecutionType type;
        RequestCompletion completion;

        public RunRequest (HttpVerb method, String path, Map<String, Object> params, RequestExecutionType type, RequestCompletion completion)
        {
            this.method = method;
            this.path = path;
            this.params = params;
            this.type = type;
            this.completion = completion;
        }

        protected Void doInBackground(String... urls) {
            URL url = null;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(String.format("%s%s", baseUrl, path));


                urlConnection = (HttpURLConnection) url.openConnection();

                //urlConnection.setRequestProperty("Content-Type", "text/plain");
                urlConnection.setRequestProperty("Content-Type", "x-www-form-urlencoded");
                urlConnection.setRequestProperty("Authorization", getAuthorizationHeaderValue(context));


                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                urlConnection.setRequestMethod(method.getValue());
                urlConnection.setUseCaches(false);
                urlConnection.setAllowUserInteraction(false);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                os.close();

                urlConnection.connect();
                int status = urlConnection.getResponseCode();

                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                        br.close();
                        JSONObject jsonObject = null;

                        jsonObject = new JSONObject(sb.toString());

                        Object metadata = jsonObject.get("metadata");
                        if (metadata != null || jsonObject.getBoolean("success")){
                            completion.completion(status, metadata, null);
                        }else{
                            completion.completion(status, null, null);
                        }
                        break;

                    default:
                        BufferedReader errorBuf = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                        sb = new StringBuilder();
                        while ((line = errorBuf.readLine()) != null) {
                            sb.append(line);
                        }
                        errorBuf.close();
                        JSONObject errorJsonObject = null;

                        errorJsonObject = new JSONObject(sb.toString());
                        if (errorJsonObject != null && errorJsonObject.length()>0) {
                            completion.completion(status, null, getPerfectErrorMessages(errorJsonObject));
                            //} else if (errorObject) {
                            //    NSArray * errorArray =[NSArray arrayWithObject:[errorObject localizedDescription]];
                            //    completion.completion(status, null, errorArray);
                        } else {
                            completion.completion(status, null, null);
                        }
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return null;
        }

        protected void onPostExecute(Void feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }
}