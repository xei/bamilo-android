package com.mobile.libraries.emarsys;

import android.annotation.TargetApi;
import android.content.Context;

import com.mobile.view.R;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Map;

import static android.R.attr.id;

/**
 * Created by Arash Hassanpour on 4/4/2017.
 */

public class EmarsysRequestManager extends RequestManager {

    private String kApplicationCode = "Application_CODE";
    private String kApplicationPassword = "Application_PASSWORD";

    private int _pendingRequestsNumber;
    public String baseUrl = "";
    public Context context;
    private static final int TIME_OUT_SECONDS = 20;

    public static final int CONNECTION_TIMEOUT = TIME_OUT_SECONDS * 1000; // ms

    public static EmarsysRequestManager initWithBaseUrl(Context context, String _baseUrl) {
        EmarsysRequestManager requestManager = new EmarsysRequestManager();
        requestManager.baseUrl = _baseUrl;
        requestManager.context = context;
        return requestManager;
    }

   // @Override
    public void asyncRequest(HttpVerb method, String path, Map<String, String> params, RequestExecutionType type,
                      RequestCompletion completion) {

        RunRequest runRequest = new RunRequest(method, path, params, type, completion);
        runRequest.execute();


    }

    void handleEmarsysMobileEngageResponse(int operation, Object responseObject, InputStream error, RequestCompletion completion)
     {
         HTTPStatusCode statusCode = HTTPStatusCode.valueOf(operation);

        if(responseObject != null) {
            switch (statusCode) {
                case SUCCESSFUL:
                case CREATED:
                    completion.completion(statusCode.getValue(), responseObject, null);

                    break;

                case WRONG_INPUT_OR_MISSING_PARAM:
                case UNAUTHORIZED:
                case INTERNAL_SERVER_ERROR:
                    completion.completion(statusCode.getValue(), null, extractErrors((JSONObject)responseObject));
                    break;
            }
        } else {
            BufferedReader errorBuf = new BufferedReader(new InputStreamReader(error));
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                while ((line = errorBuf.readLine()) != null) {
                    sb.append(line);
                }

            errorBuf.close();
            JSONObject errorJsonObject = null;

            errorJsonObject = new JSONObject(sb.toString());
            completion.completion(statusCode.getValue(), null, extractErrors(errorJsonObject));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

       // [[LoadingManager sharedInstance] hideLoading];
    }

    ArrayList<String> extractErrors(JSONObject responseObject) {
        ArrayList<String> errorMessages = new ArrayList<String>();

        try {

            JSONArray errors = responseObject.getJSONArray("errors");
            for (int i = 0; i < errors.length(); i++) {
                JSONObject error = errors.getJSONObject(i);
                String message = error.getString("message");
                errorMessages.add(message);
            }
        }
        catch (JSONException ex)
        {
            return null;
        }
        return errorMessages;
    }

    String getAuthorizationHeaderValue(Context context) {

        String emarsysApplicationCode = context.getResources().getString(R.string.Emarsys_ApplicationID);
        String emarsysApplicationPassword = context.getResources().getString(R.string.Emarsys_ApplicationPassword);

        String str = String.format("%s:%s", emarsysApplicationCode, emarsysApplicationPassword);
        byte[]   bytesEncoded = Base64.encode(str.getBytes(), Base64.DEFAULT);

        return String.format("Basic %s", new String(bytesEncoded));
    }
    private String getQuery(Map<String, String> params) throws UnsupportedEncodingException {
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
        Map<String, String> params;
        RequestExecutionType type;
        RequestCompletion completion;

        public RunRequest (HttpVerb method, String path, Map<String, String> params, RequestExecutionType type, RequestCompletion completion)
        {
            this.method = method;
            this.path = path;
            this.params = params;
            this.type = type;
            this.completion = completion;
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        protected Void doInBackground(String... urls) {
            URL url = null;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(String.format("%s%s", baseUrl, path));


                urlConnection = (HttpURLConnection) url.openConnection();

                //urlConnection.setRequestProperty("Content-Type", "text/plain");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Authorization", getAuthorizationHeaderValue(context));


                switch (method) {
                    case HttpVerbPOST: {
                        urlConnection.setRequestMethod("POST");
                        /*OutputStream os = urlConnection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                        writer.write(getQuery(params));
                        writer.flush();
                        writer.close();
                        os.close();*/
                        byte[] postData = getQuery(params).getBytes( StandardCharsets.UTF_8 );
                        int postDataLength = postData.length;
                        urlConnection.setRequestProperty("charset", "utf-8");
                        urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
                        urlConnection.setUseCaches(false);
                        try(DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
                            wr.write( postData );
                        }

                        urlConnection.connect();
                        int status = urlConnection.getResponseCode();


                        switch (status) {
                            case 201:
                            case 202:
                                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                                StringBuilder sb = new StringBuilder();
                                String line;
                                while ((line = br.readLine()) != null) {
                                    sb.append(line);
                                }
                                br.close();
                                //JSONObject jsonObject = null;

                                //jsonObject = new JSONObject(sb.toString());
                                handleEmarsysMobileEngageResponse(status, urlConnection.getInputStream(), null, completion);
                                break;
                            default:
                                handleEmarsysMobileEngageResponse(status, null, urlConnection.getErrorStream(), completion);
                                break;
                        }

                    }
                    break;

                    default:
                        break;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }

}
