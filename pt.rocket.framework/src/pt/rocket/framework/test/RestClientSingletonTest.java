package pt.rocket.framework.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.auth.AuthScope;
import ch.boye.httpclientandroidlib.auth.AuthenticationException;
import ch.boye.httpclientandroidlib.auth.UsernamePasswordCredentials;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.CookieStore;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpDelete;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpPut;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
import ch.boye.httpclientandroidlib.client.params.ClientPNames;
import ch.boye.httpclientandroidlib.client.protocol.ClientContext;
import ch.boye.httpclientandroidlib.conn.ConnectTimeoutException;
import ch.boye.httpclientandroidlib.conn.params.ConnRoutePNames;
import ch.boye.httpclientandroidlib.cookie.Cookie;
import ch.boye.httpclientandroidlib.impl.auth.BasicScheme;
import ch.boye.httpclientandroidlib.impl.client.BasicCookieStore;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.cookie.BasicClientCookie;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import ch.boye.httpclientandroidlib.params.BasicHttpParams;
import ch.boye.httpclientandroidlib.params.HttpConnectionParams;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.protocol.BasicHttpContext;
import ch.boye.httpclientandroidlib.protocol.HTTP;
import ch.boye.httpclientandroidlib.protocol.HttpContext;

import pt.rocket.framework.network.DarwinHttpClient;
import pt.rocket.framework.rest.RestContract;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
//import pt.rocket.framework.Config;
//import pt.rocket.framework.Log;
import android.util.Log;

/**
 * The RestClientSingleton takes care of the whole rest communication. Its a
 * Singleton thus it is possible to set the default HTTP Header and to manage
 * the current session. It is always possible to set the session id manually.
 * The client itself updates the session id everytime a new session id is
 * received. If there is a session id it will be send with each request.
 * 
 * provides methods to send REST Requests
 * 
 * GET POST PUT DELETE
 * 
 * notifies the Processor the state of the current REST Methodcall
 * 
 * @author Jacob Zschunke
 * 
 */
public class RestClientSingletonTest {

    private static final String HEADER_SESSION_ID = "Set-Cookie";

    private static boolean useProxy = true;
    private boolean circularRedirects = true;
    										//false
    private static HttpParams currentHttpParams = null;

    private static HashMap<String, String> DEFAULT_HEADER;
    static {
        DEFAULT_HEADER = new HashMap<String, String>();
        // add header here
        DEFAULT_HEADER.put("X-ROCKET-MOBAPI-VERSION", "1.0");
        DEFAULT_HEADER.put("X-ROCKET-MOBAPI-PLATFORM", "application/rocket.mobapi-v1.0+json");
        DEFAULT_HEADER.put("X-ROCKET-MOBAPI-TOKEN", "1");
    }

    private static RestClientSingletonTest instance;
    private DefaultHttpClient httpClient;
    private CookieStore cookieStore;
    private HttpContext httpContext;

    private int responseStatusCode;

    /**
     * Sets the timeout until a connection is established. A value of zero means
     * the timeout is not used. The default value is zero.
     */
    private static final int CONNECTION_TIMEOUT = 10 * 1000; // ms

    /**
     * Sets the default socket timeout (SO_TIMEOUT) in milliseconds which is the
     * timeout for waiting for data. A timeout value of zero is interpreted as
     * an infinite timeout. This value is used when no socket timeout is set in
     * the method parameters.
     */
    private static final int SOCKET_TIMEOUT = 10 * 1000;

    private RestClientSingletonTest() {

        // httpClient = new DefaultHttpClient();
        // setAuthentication(httpClient);

        httpContext = new BasicHttpContext();
        cookieStore = new BasicCookieStore();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        responseStatusCode = HttpStatus.SC_OK;
    }

    public static synchronized RestClientSingletonTest getSingleton() {
        if (instance == null) {
            instance = new RestClientSingletonTest();
        }
        return instance;
    }

    /**
     * Gets the StatusCode for the latest request done
     * 
     * @return The request status code
     */
    public int getResponseStatusCode() {
        return responseStatusCode;
    }

    private void setSessionId(HttpResponse response) {
        Header header = response.getFirstHeader(HEADER_SESSION_ID);
        if (header != null) {
            this.setSessionId(header.getValue());
            
            
        }
        
        
        
        
    }

    /**
     * Sets the session id for the RestClient. If set to null a existing session
     * will be removed. Note that the Client itself keeps track of a new
     * Session. If there is a new Session Id the old one will be replaced. If
     * there is no Session Id at all the current keeps active.
     * 
     * @param sessionId
     *            the session Id which will be used. If null, any session will
     *            be removed.
     */
    public void setSessionId(String sessionId) {
        if (TextUtils.isEmpty(sessionId)) {
            this.cookieStore = new BasicCookieStore();
            this.httpContext.setAttribute(ClientContext.COOKIE_STORE, this.cookieStore);
        } else {
            // check if the session is already set
            for (Cookie cookie : cookieStore.getCookies()) {
                if (cookie.getValue().equalsIgnoreCase(sessionId)) {
                    // session is already set. Nothing to do here.
                    return;
                }
            }
            this.cookieStore.addCookie(new BasicClientCookie(HEADER_SESSION_ID, sessionId));
        }
    }

    /**
     * Sends a HTTP GET request to the given url and returns the response as
     * String (e.g. a json string).
     * 
     * @param ctx
     *            Used for the RestProcessor to call getContentResolver and
     *            store the state in the database
     * @param urlString
     *            the URL to send the HTTP request
     * @return the response as String e.g. a json string
     */
    public String executeGetRestUrlString(Context ctx, String urlString) {
        // httpClient = new DefaultHttpClient();
        circularRedirects = false;
        // httpClient = new DefaultHttpClient(getHttpParams());
        // setAuthentication(httpClient);
        httpClient = createHttpClient();

        HttpUriRequest httpRequest = null;

        httpRequest = new HttpGet(urlString);
        return executeHttpRequest(httpRequest);
    }

    /**
     * Sends a HTTP POST request with formData to the given url and returns the
     * response as String (e.g. a json string).
     * 
     * @param ctx
     *            Used for the RestProcessor to call getContentResolver and
     *            store the state in the database
     * @param urlString
     *            the URL to send the HTTP request
     * @param formData
     *            name - value pairs of the form to send with the request
     * @return the response as String e.g. a json string
     */
    public String executePostRestUrlString(Context ctx, String urlString, ContentValues contentValues) {
        circularRedirects = false;
        httpClient = createHttpClient();
        // httpClient = new DefaultHttpClient(getHttpParams());
        // setAuthentication(httpClient);

        
        HttpPost httpRequest = new HttpPost(urlString);

        Log.e("URL STIRNG", ":"+urlString);
//
//    	  HttpParams httpParams = null;
//    	  String paramString="?";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        int count=0;
        for (Entry<String, Object> entry : contentValues.valueSet()) {
        	Log.e("PARAMS", "KEY:"+entry.getKey()+" VALUE:"+ (String) entry.getValue());
//        	httpParams.setParameter(entry.getKey(), (String) entry.getValue());
//            count++;
//        	paramString= paramString +entry.getKey()+"="+(String) entry.getValue();
//        	if(count<contentValues.size()){
//        		paramString=paramString+"&";
//        	}
        	
            params.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
            

        }

        try {
        	 Log.e("EXE", "TRY");
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8 ));
      

        } catch (UnsupportedEncodingException e) {
        	Log.e("EXE", "CATCH");
        	e.printStackTrace();
        }

        return executeHttpRequest(httpRequest);
    }

    /**
     * Sends a HTTP PUT request with formData to the given url and returns the
     * response as String (e.g. a json string).
     * 
     * @param ctx
     *            Used for the RestProcessor to call getContentResolver and
     *            store the state in the database
     * @param urlString
     *            the URL to send the HTTP request
     * @param formData
     *            name - value pairs of the form to send with the request
     * @return the response as String e.g. a json string
     */
    public String executePutRestUrlString(Context ctx, String urlString, ContentValues contentValues) {
        circularRedirects = true;
        httpClient = createHttpClient();

        // httpClient = new DefaultHttpClient();
        // httpClient.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,
        // true);
        // setAuthentication(httpClient);

        HttpPut httpRequest = new HttpPut(urlString);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (Entry<String, Object> entry : contentValues.valueSet()) {
            params.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
        }

        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return executeHttpRequest(httpRequest);
    }

    /**
     * Sends a HTTP DELETE request to the given url and returns the response as
     * String (e.g. a json string).
     * 
     * @param ctx
     *            Used for the RestProcessor to call getContentResolver and
     *            store the state in the database
     * @param urlString
     *            the URL to send the HTTP request
     * @return the response as String e.g. a json string
     */
    public String executeDeleteRestUrlString(Context ctx, String urlString) {
        circularRedirects = true;
        httpClient = createHttpClient();

        // httpClient = new DefaultHttpClient();
        // httpClient.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,
        // true);
        // setAuthentication(httpClient);

        HttpDelete httpRequest = new HttpDelete(urlString);

        return executeHttpRequest(httpRequest);
    }

    /**
     * Executes a HTTPRequest and protocols the state of that request.
     * 
     * @param httpRequest
     *            the request to execute
     * @param processor
     *            the logger which stores and updates the state in the database
     * @return the response as String e.g. json string
     */
    private String executeHttpRequest(HttpUriRequest httpRequest) {
        Log.e("RestClient", "send a request for: " + httpRequest.getURI().toString());

        
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(RestContract.AUTHENTICATION_USER_TEST, RestContract.AUTHENTICATION_PASS_TEST);
        Header header;
		try {
			header = new BasicScheme().authenticate(credentials, httpRequest);
			
			  httpRequest.addHeader(header);
			
		} catch (AuthenticationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
      
        
        String result = "";

//        for (Map.Entry<String, String> headerEntry : DEFAULT_HEADER.entrySet()) {
//            httpRequest.addHeader(headerEntry.getKey(), headerEntry.getValue());
//        }

        HttpResponse response;
        boolean retry;
        int count = 0;
        do {
            result = "";
            retry = false;

            
            
            try {
                // processor.createNewEntry();
                // HttpParams cParams = httpClient.getParams();
                // Log.i(" > CNN PARMS < ", " CONNECTION_TIMEOUT: " +
                // cParams.getParameter("http.connection.timeout"));
                // Log.i(" > CNN PARMS < ", " SOCKET_TIMEOUT: " +
                // cParams.getParameter("http.socket.timeout"));
                // long startTime = System.currentTimeMillis();
            	Log.e("URL REQUESTED", ":"+ httpRequest.getURI().toString());
                response = httpClient.execute(httpRequest, httpContext);
                // Log.i(" > CNN EXEC < ", " EXECUTION TIME: " +
                // (System.currentTimeMillis() - startTime) );

                StatusLine status = response.getStatusLine();
                responseStatusCode = status.getStatusCode();
                Log.w("STATUS", ":"+responseStatusCode );
                if (status.getStatusCode() == HttpStatus.SC_OK) {
                    Log.d("http_client", "Request successful");
                    this.setSessionId(response);

                    HttpEntity entity = response.getEntity();

                    Log.d("http_client", "1");
                    if (entity != null) {

                        Log.d("http_client", "2");
                        InputStream instream = entity.getContent();
                        result = convertStreamToString(instream);

                        Log.d("http_client", "3: " + result);
                        instream.close();

                        // return result;
                    }
                } else {

                    Log.d("http_client", "Request unsuccessful: " + status.getStatusCode());

                }

            } catch (ConnectTimeoutException e) {
                Log.e("RestClient", "--- > CONNECTION TIMEOUT < --- " + e);
                httpClient.getConnectionManager().shutdown();
                responseStatusCode = HttpStatus.SC_REQUEST_TIMEOUT;
                e.printStackTrace();

            } catch (SocketTimeoutException e) {
                Log.e("RestClient", "--- > SOCKET TIMEOUT < --- " + e);
                httpClient.getConnectionManager().shutdown();
                responseStatusCode = HttpStatus.SC_REQUEST_TIMEOUT;
                e.printStackTrace();

            } catch (ClientProtocolException e) {
                Log.e("RestClient", "There was a protocol error " + e);
                httpClient.getConnectionManager().shutdown();
                httpClient = createHttpClient();
                retry = true;
                count++;

            } catch (IOException e) {
                Log.e("RestClient", "There was an IO Stream related error " + e);
                httpClient.getConnectionManager().shutdown();
                httpClient = createHttpClient();
                responseStatusCode = HttpStatus.SC_REQUEST_TIMEOUT;
                retry = true;
                count++;

            } catch (ClassCastException e) {
                Log.e("RestClient", "There is a connection error:\n" + e.getMessage() + "\n" + java.net.Proxy.class.toString() + " " + e);
                httpClient.getConnectionManager().shutdown();
                if (java.net.Proxy.class.toString().contains(e.getMessage())) {
                    useProxy = false;
                    httpClient = createHttpClient();
                    responseStatusCode = HttpStatus.SC_REQUEST_TIMEOUT;
                    retry = true;
                }
            } catch (Exception e) {
                Log.e("RestClient", "Anormal exception" + e.getMessage() + " " + e);
                httpClient.getConnectionManager().shutdown();
                responseStatusCode = HttpStatus.SC_REQUEST_TIMEOUT;
            }

        } while (retry && count < 3);

        Log.d("http_client", "4");

        return result;
    }

    private DefaultHttpClient createHttpClient() {
    	DefaultHttpClient client;
        if (!circularRedirects) {
            client = new DarwinHttpClient(getHttpParams());
        } else {
            client = new DarwinHttpClient(getHttpParams());
            client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        }

   //     setAuthentication(client);

        
        return client;
    }

    @SuppressWarnings("deprecation")
    public static HttpParams getHttpParams() {

        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIMEOUT);
        int port = android.net.Proxy.getDefaultPort();
        String host = android.net.Proxy.getDefaultHost();
        if (useProxy && port != -1 && (host != null && !host.equals(""))) {
            Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(android.net.Proxy.getDefaultHost(), android.net.Proxy.getDefaultPort()));
            httpParameters.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }

        currentHttpParams = httpParameters;

        return httpParameters;
    }

    private void setAuthentication(DefaultHttpClient httpClient) {
        if (RestContract.USE_AUTHENTICATION_TEST) {
        	Log.e("SET", "AUTHENTICATION");
            httpClient.getCredentialsProvider().setCredentials(new AuthScope(ConfigTest.API_URL, 80),
                    new UsernamePasswordCredentials(RestContract.AUTHENTICATION_USER_TEST, RestContract.AUTHENTICATION_PASS_TEST));
        }
    }

    private static final String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the
         * BufferedReader.readLine() method. We iterate until the BufferedReader
         * return null which means there's no more data to read. Each line will
         * appended to a StringBuilder and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
