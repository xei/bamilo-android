//package pt.rocket.framework.rest;
//
//import java.io.IOException;
//import java.net.SocketTimeoutException;
//import java.net.URI;
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map.Entry;
//
//import javax.net.ssl.SSLException;
//
//import pt.rocket.framework.Darwin;
//import pt.rocket.framework.DarwinMode;
//import pt.rocket.framework.ErrorCode;
//import pt.rocket.framework.interfaces.IMetaData;
//import pt.rocket.framework.network.ConfigurationConstants;
//import pt.rocket.framework.network.LazHttpClientAndroidLog;
//import pt.rocket.framework.service.RemoteService;
//import pt.rocket.framework.tracking.NewRelicTracker;
//import pt.rocket.framework.utils.Constants;
//import pt.rocket.framework.utils.EventType;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import ch.boye.httpclientandroidlib.Consts;
//import ch.boye.httpclientandroidlib.Header;
//import ch.boye.httpclientandroidlib.HeaderElement;
//import ch.boye.httpclientandroidlib.HttpEntity;
//import ch.boye.httpclientandroidlib.HttpException;
//import ch.boye.httpclientandroidlib.HttpHost;
//import ch.boye.httpclientandroidlib.HttpRequest;
//import ch.boye.httpclientandroidlib.HttpRequestInterceptor;
//import ch.boye.httpclientandroidlib.HttpResponse;
//import ch.boye.httpclientandroidlib.HttpResponseInterceptor;
//import ch.boye.httpclientandroidlib.HttpStatus;
//import ch.boye.httpclientandroidlib.NameValuePair;
//import ch.boye.httpclientandroidlib.RequestLine;
//import ch.boye.httpclientandroidlib.auth.AuthScope;
//import ch.boye.httpclientandroidlib.auth.AuthState;
//import ch.boye.httpclientandroidlib.auth.Credentials;
//import ch.boye.httpclientandroidlib.auth.UsernamePasswordCredentials;
//import ch.boye.httpclientandroidlib.client.ClientProtocolException;
//import ch.boye.httpclientandroidlib.client.CookieStore;
//import ch.boye.httpclientandroidlib.client.CredentialsProvider;
//import ch.boye.httpclientandroidlib.client.HttpClient;
//import ch.boye.httpclientandroidlib.client.HttpRequestRetryHandler;
//import ch.boye.httpclientandroidlib.client.cache.CacheResponseStatus;
//import ch.boye.httpclientandroidlib.client.cache.HeaderConstants;
//import ch.boye.httpclientandroidlib.client.cache.HttpCacheContext;
//import ch.boye.httpclientandroidlib.client.cache.HttpCacheEntry;
//import ch.boye.httpclientandroidlib.client.cache.HttpCacheStorage;
//import ch.boye.httpclientandroidlib.client.config.RequestConfig;
//import ch.boye.httpclientandroidlib.client.entity.GzipDecompressingEntity;
//import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
//import ch.boye.httpclientandroidlib.client.methods.CloseableHttpResponse;
//import ch.boye.httpclientandroidlib.client.methods.HttpGet;
//import ch.boye.httpclientandroidlib.client.methods.HttpPost;
//import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
//import ch.boye.httpclientandroidlib.client.protocol.HttpClientContext;
//import ch.boye.httpclientandroidlib.client.utils.HttpClientUtils;
//import ch.boye.httpclientandroidlib.conn.ConnectTimeoutException;
//import ch.boye.httpclientandroidlib.conn.HttpHostConnectException;
//import ch.boye.httpclientandroidlib.cookie.Cookie;
//import ch.boye.httpclientandroidlib.impl.auth.BasicScheme;
//import ch.boye.httpclientandroidlib.impl.client.BasicCredentialsProvider;
//import ch.boye.httpclientandroidlib.impl.client.CloseableHttpClient;
//import ch.boye.httpclientandroidlib.impl.client.DecompressingHttpClient;
//import ch.boye.httpclientandroidlib.impl.client.cache.CacheConfig;
//import ch.boye.httpclientandroidlib.impl.client.cache.CachingHttpClientBuilder;
//import ch.boye.httpclientandroidlib.impl.client.cache.CachingHttpClients;
//import ch.boye.httpclientandroidlib.impl.client.cache.DBHttpCacheStorage;
//import ch.boye.httpclientandroidlib.message.BasicHeaderElement;
//import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
//import ch.boye.httpclientandroidlib.message.BufferedHeader;
//import ch.boye.httpclientandroidlib.protocol.HttpContext;
//import ch.boye.httpclientandroidlib.util.CharArrayBuffer;
//import ch.boye.httpclientandroidlib.util.EntityUtils;
//import de.akquinet.android.androlog.Log;
//
///**
// * The RestClientSingleton takes care of the whole rest communication. Its a
// * Singleton thus it is possible to set the default HTTP Header and to manage
// * the current session. It is always possible to set the session id manually.
// * The client itself updates the session id everytime a new session id is
// * received. If there is a session id it will be send with each request.
// * 
// * provides methods to send REST Requests
// * 
// * GET POST PUT DELETE
// * 
// * notifies the Processor the state of the current REST Methodcall
// * 
// * @see https://code.google.com/p/httpclientandroidlib/
// * 
// * @author Jacob Zschunke
// * 
// */
//public final class NewRestClientSingleton {
//	
//	private static final String TAG = NewRestClientSingleton.class.getSimpleName();
//	
//	private static final int MAX_CACHE_OBJECT_SIZE = 131072; //???
//
//    public static NewRestClientSingleton sRestClientSingleton;
//	
//	// private DarwinHttpClient mDarwinHttpClient;
//	
//	private HttpClient mHttpClient;
//	
//	private PersistentCookieStore mCookieStore;
//	
//	private HttpClientContext mHttpContext;
//	
//	private CacheConfig mCacheConfig;
//	
//	private HttpCacheStorage mCacheStore;
//	
//	private ConnectivityManager mConnManager;
//	
//	private Context mContext;
//
//	/**
//	 * Create a singleton instance
//	 * @param context
//	 * @return RestClientSingleton
//	 * @author spereira
//	 */
//    public static synchronized NewRestClientSingleton getSingleton(Context context) {
//        // Validate the current reference
//        return sRestClientSingleton == null ? sRestClientSingleton = new NewRestClientSingleton(context) : sRestClientSingleton;
//    }
//	
//	/**
//	 * Constructor
//	 * @param context
//	 * @author spereira
//	 */
//	private NewRestClientSingleton(Context context) {
//	    Log.i(TAG, "CONSTRUCTOR");
//	    // Save context
//	    this.mContext = context;
//	    // Save connectivity manager
//	    this.mConnManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//	    // Initialize rest client
//	    this.init();
//	}
//	
//	/**
//	 * Initialize all components.
//	 * @author spereira
//	 */
//    private void init(){
//        Log.i(TAG, "ON INITIALIZE");
//
//        mCacheConfig = CacheConfig.custom()
//                .setMaxCacheEntries(100)
//                .setMaxObjectSize(MAX_CACHE_OBJECT_SIZE)
//                .setSharedCache(false)
//                .build();
//        mCacheStore = new DBHttpCacheStorage(mContext, mCacheConfig);
//
//        createHttpClient();
//    }
//    
//    private void createHttpClient() {
//        // CachingHttpClientBuilder cachingHttpClientBuilder = CachingHttpClientBuilder.create()
//        CachingHttpClientBuilder cachingHttpClientBuilder = CachingHttpClients.custom()
//                .setCacheConfig(mCacheConfig)
//                .setHttpCacheStorage(mCacheStore);
//
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setCircularRedirectsAllowed(true)
//                .build();
//        cachingHttpClientBuilder.setDefaultRequestConfig(requestConfig);
//        
//        cachingHttpClientBuilder.addInterceptorFirst(preemptiveAuth);
//        if (ConfigurationConstants.RETRY > 0) cachingHttpClientBuilder.setRetryHandler(retryHandler);
//        cachingHttpClientBuilder.addInterceptorLast(gzipInterceptor);
//        cachingHttpClientBuilder.addInterceptorLast(httpResponseInterceptor);
//
//        // Set the default or custom user agent
//        String userAgent = getHttpUserAgent();
//        if (userAgent != null) cachingHttpClientBuilder.setUserAgent(userAgent);
//
//        //mHttpClient = new DecompressingHttpClient(mHttpClient);
//        //cachingHttpClientBuilder.addInterceptorFirst(new RequestAcceptEncoding());
//        //cachingHttpClientBuilder.addInterceptorLast(new ResponseContentEncoding());
//        
//        
//        mHttpClient = cachingHttpClientBuilder.build();
//        
//        mHttpClient = new DecompressingHttpClient(mHttpClient);
//
//        /*-mHttpClient.log = new LazHttpClientAndroidLog("CachingHttpClient");
//        if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
//            mHttpClient.log.enableWarn(true);
//            mHttpClient.log.enableInfo(true);
//            mHttpClient.log.enableTrace(true);
//        }*/
//        
//        mHttpContext = HttpCacheContext.create();
//        mCookieStore = new PersistentCookieStore(mContext);
//        mHttpContext.setCookieStore(mCookieStore);
//
//        // Add credentials to mHttpContext
//        setAuthentication(mContext);
//    }
//
//    public void updateHttpClient() {
//        Log.i(TAG, "ON UPDATE");
//
//        createHttpClient();
//    }
//
//
//	
//    /*
//     * ############# HTTP PARAMS #############
//     */
//
//    /**
//     * Method used to get the user agent
//     * 
//     * @return userAgent or <code>null</code>
//     * @author sergiopereira
//     * @modified Andre Lopes
//     */
//    private String getHttpUserAgent() {
//        // CASE Default user agent
//        String userAgent = System.getProperty("http.agent");
//        Log.i(TAG, "DEFAULT USER AGENT: " + userAgent);
//        return userAgent;
//    }
//
//    /**
//     * Authenticate <code>HttpContext</code> with user credentials
//     * 
//     * @param mContext
//     */
//    private void setAuthentication(Context mContext) {
//        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        AuthScope authScope = new AuthScope(RestContract.REQUEST_HOST, AuthScope.ANY_PORT);
//        UsernamePasswordCredentials credentials = null;
//
//        if (RestContract.RUNNING_TESTS) {
//            credentials = new UsernamePasswordCredentials(RestContract.AUTHENTICATION_USER_TEST, RestContract.AUTHENTICATION_PASS_TEST);
//        }
//        if (RestContract.USE_AUTHENTICATION == null) {
//            SharedPreferences sharedPrefs = mContext.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//            String shopId = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, null);
//            if (shopId == null) {
//                throw new NullPointerException(NewRestClientSingleton.class.getName() + " Shop Id is null!! Cannot initialize!");
//            }
//
//            RestContract.init(mContext, "" + shopId);
//            Darwin.initialize(DarwinMode.DEBUG, mContext, "" + shopId, false);
//        }
//        if (RestContract.USE_AUTHENTICATION) {
//            credentials = new UsernamePasswordCredentials(RestContract.AUTHENTICATION_USER, RestContract.AUTHENTICATION_PASS);
//        }
//        credentialsProvider.setCredentials(authScope, credentials);
//        mHttpContext.setCredentialsProvider(credentialsProvider);
//    }
//
//    HttpRequestInterceptor preemptiveAuth = new HttpRequestInterceptor() {
//        public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
//            AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);
//            CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(HttpClientContext.CREDS_PROVIDER);
//            HttpHost targetHost = (HttpHost) context.getAttribute(HttpClientContext.HTTP_TARGET_HOST);
//
//            if (authState.getAuthScheme() == null) {
//                AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
//                Credentials creds = credsProvider.getCredentials(authScope);
//                if (creds != null) {
//                    authState.update(new BasicScheme(), creds);
//                    //authState.setAuthScheme(new BasicScheme());
//                    //authState.setCredentials(creds);
//                }
//            }
//        }
//    };
//
//    HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
//
//        @Override
//        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
//            // TODO Auto-generated method stub
//            Log.i(TAG, " Retry number => " + executionCount);
//            if (executionCount >= ConfigurationConstants.RETRY) { // Retry 3
//                                                                  // times
//                return false;
//            }
//            if (exception instanceof SocketTimeoutException) {
//                exception.printStackTrace();
//                return true;
//            } else if (exception instanceof ClientProtocolException) {
//                exception.printStackTrace();
//                return true;
//            } else if (exception instanceof ConnectTimeoutException) {
//                exception.printStackTrace();
//                return true;
//            }
//            return false;
//        }
//    };
//
//    HttpRequestInterceptor gzipInterceptor = new HttpRequestInterceptor() {
//
//        public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
//            Header[] allHeaders = request.getAllHeaders();
//
//            // Add header to accept gzip content
//            if (ConfigurationConstants.ACTIVATE_GZIP) {
//                if (!request.containsHeader(ConfigurationConstants.HEADER_ACCEPT_ENCODING)) {
//                    Log.i(TAG,"Adding GZIP encoding");
//                    request.addHeader(ConfigurationConstants.HEADER_ACCEPT_ENCODING, ConfigurationConstants.ENCODING_GZIP);
//                }
//            }
//            if (allHeaders != null && allHeaders.length > 0) {
//                StringBuilder sb = new StringBuilder("RequestHeader:");
//                for (Header header : allHeaders) {
//                    sb.append("\n").append(header);
//                }
//                Log.d(sb.toString());
//            }
//        }
//    };
//    
//
//    /**
//     * Intercepter of the http response. The function of the intercepter is
//     * to override the cache settings in the ???
//     * 
//     */
//    HttpResponseInterceptor httpResponseInterceptor = new HttpResponseInterceptor() {
//
//        private void appendToBuffer(CharArrayBuffer buffer, String name, String value, String delim) {
//            buffer.append(delim);
//            buffer.append(name);
//            if (!TextUtils.isEmpty(value)) {
//                buffer.append("=");
//                buffer.append(value);
//            }
//        }
//
//        @Override
//        public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
//
//            if (ConfigurationConstants.FORCE_CACHE) {
//                // TODO implement configuration uppon caching request
//                HttpRequest request = (HttpRequest) context.getAttribute("http.request");
//            }
//
//            Header[] headers = response.getHeaders(HeaderConstants.CACHE_CONTROL);
//            if (headers == null || headers.length == 0) {
//                return;
//            }
//
//            for (Header header : headers) {
//                HeaderElement[] elements = header.getElements();
//                if (elements == null || headers.length == 0) {
//                    continue;
//                }
//
//                int idx;
//                CharArrayBuffer buffer = new CharArrayBuffer(1024);
//                buffer.append(HeaderConstants.CACHE_CONTROL);
//                buffer.append(": ");
//                String delim = "";
//                boolean headerWasRewritten = false;
//                for (idx = 0; idx < elements.length; idx++) {
//                    HeaderElement element = elements[idx];
//                    // Log.d(TAG, "element: " + element + " value = " +
//                    // element.getValue());
//                    if (element == null) {
//                        continue;
//                    }
//
//                    if (!element.getName().equalsIgnoreCase(HeaderConstants.CACHE_CONTROL_MAX_AGE)) {
//                        appendToBuffer(buffer, element.getName(), element.getValue(), delim);
//                        delim = ", ";
//                        continue;
//                    }
//
//                    int maxAge = 0;
//                    try {
//                        Log.d(TAG, "header element: value = " + element.getValue());
//                        maxAge = Integer.parseInt(element.getValue());
//                    } catch (NumberFormatException e) {
//                        Log.e("max-age content could not be parsed - ignored);");
//                        String newMaxAge = calcMaxAge(context);
//                        HeaderElement newElement = new BasicHeaderElement(HeaderConstants.CACHE_CONTROL_MAX_AGE, newMaxAge);
//                        appendToBuffer(buffer, newElement.getName(), newElement.getValue(), delim);
//                        delim = ", ";
//                        continue;
//                    }
//
//                    if (maxAge > ConfigurationConstants.CACHE_CONTROL_MAX_AGE_RESTRICTION) {
//                        if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
//                            Log.d(TAG, "rewrite header: detected maxAge = " + maxAge + " above = "
//                                    + ConfigurationConstants.CACHE_CONTROL_MAX_AGE_RESTRICTION);
//                        }
//                        String newMaxAge = calcMaxAge(context);
//                        HeaderElement newElement = new BasicHeaderElement(HeaderConstants.CACHE_CONTROL_MAX_AGE, newMaxAge);
//                        appendToBuffer(buffer, newElement.getName(), newElement.getValue(), delim);
//                        delim = ", ";
//                        headerWasRewritten = true;
//                    }
//                }
//
//                if (headerWasRewritten) {
//                    BufferedHeader rewrittenHeader = new BufferedHeader(buffer);
//                    response.setHeader(rewrittenHeader);
//                    Log.d(TAG, "process: cache-control header was rewritten");
//                    Log.d(TAG, "new content: " + rewrittenHeader.toString());
//                }
//            }
//
//            if (ConfigurationConstants.ACTIVATE_GZIP) {
//                HttpEntity entity = response.getEntity();
//                Header ceheader = entity.getContentEncoding();
//                if (ceheader != null) {
//                    HeaderElement[] codecs = ceheader.getElements();
//                    for (int i = 0; i < codecs.length; i++) {
//                        if (codecs[i].getName().equalsIgnoreCase("gzip")) {
//                            response.setEntity(new GzipDecompressingEntity(response.getEntity()));
//                            return;
//                        }
//                    }
//                }
//            }
//        }
//
//        /**
//         * Calculates the max age of the request
//         * 
//         * @param context
//         * @return
//         */
//        private String calcMaxAge(HttpContext context) {
//            HttpRequest request = (HttpRequest) context.getAttribute("http.request");
//            if (request == null) {
//                return String.valueOf(ConfigurationConstants.MIN_CACHE_TIME);
//            }
//
//            RequestLine requestLine = request.getRequestLine();
//            if (TextUtils.isEmpty(requestLine.getUri())) {
//                return String.valueOf(ConfigurationConstants.MIN_CACHE_TIME);
//            }
//
//            return String.valueOf(ConfigurationConstants.MIN_CACHE_TIME);
//
//        }
//
//    };
//
//    /*
//     * ############# COOKIE #############
//     */
//    
//    /**
//     * Get the Cookie store
//     * @return CookieStore
//     * @author spereira
//     */
//    public CookieStore getCookieStore() {
//        return mCookieStore;
//    }
//    
//    /**
//     * Clear all cookies from Cookie Store.
//     * @author spereira
//     */
//    public void clearCookieStore() {
//        mCookieStore.clear();
//    }
//    
//    /**
//     * Return all cookies from CookieStore.
//     * @return list of cookies
//     * @author spereira
//     */
//    public List<Cookie> getCookies() {
//        return mCookieStore.getCookies();
//    }
//    
//    /**
//     * Persist session cookie.
//     * @author spereira
//     */
//    public void persistSessionCookie() {
//        mCookieStore.saveSessionCookie();
//    }
//	
//    /*
//     * ############# GET #############
//     */
//
//	/**
//	 * Sends a HTTP GET request to the given url and returns the response as
//	 * String (e.g. a json string).
//	 * 
//	 * @param ctx
//	 *            Used for the RestProcessor to call getContentResolver and
//	 *            store the state in the database
//	 * @param urlString
//	 *            the URL to send the HTTP request
//	 * @return the response as String e.g. a json string
//	 */
//    public String executeGetRestUrlString(Uri uri, Handler mHandler, Bundle metaData) {
//
//		if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
//			Log.d(TAG, "executeGetRestUrlString original: " + uri.toString());
//		}
//		
//		// Get event type
//		EventType eventType = (EventType) metaData.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
//		// Validate if is ventures.json
//		String url = (eventType == EventType.GET_GLOBAL_CONFIGURATIONS) ? uri.toString() : RemoteService.completeUri(uri).toString();
//		
//		if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
//			Log.d(TAG, "executeGetRestUrlString complete: " + url.toString());
//		}
//		
//		HttpGet httpRequest = new HttpGet(url.replaceAll(" ", "%20"));
//		return executeHttpRequest(httpRequest, mHandler, metaData);
//	}
//	
//	/**
//	 * Sends a HTTP POST request with formData to the given url and returns the
//	 * response as String (e.g. a json string).
//	 * 
//	 * @param ctx
//	 *            Used for the RestProcessor to call getContentResolver and
//	 *            store the state in the database
//	 * @param urlString
//	 *            the URL to send the HTTP request
//	 * @param formData
//	 *            name - value pairs of the form to send with the request
//	 * @return the response as String e.g. a json string
//	 */
//	public String executePostRestUrlString(Uri uri, ContentValues formData, Handler mHandler, Bundle metaData) {
//
//		if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
//			Log.d(TAG, "executePostRestUrlString original: " + uri.toString());
//		}
//		
//		// Get event type
//		EventType eventType = (EventType) metaData.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
//		// Validate if is ventures.json
//		String url = (eventType == EventType.GET_GLOBAL_CONFIGURATIONS) ? uri.toString() : RemoteService.completeUri(uri).toString();
//		
//		if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
//			Log.d(TAG, "executePostRestUrlString complete: " + url.toString());
//		}
//		
//		HttpPost httpRequest = new HttpPost(url.toString());
//
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		if(formData != null){
//			for (Entry<String, Object> entry : formData.valueSet()) {
//				Object value = entry.getValue();
//				if (value == null) {
//					Log.w(TAG, "entry for key " + entry.getKey() + " is null - ignoring - form request will fail");
//					continue;
//				}
//	
//				params.add(new BasicNameValuePair(entry.getKey(), value.toString()));
//				if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
//					Log.d(TAG, "post: " + entry.getKey() + "=" + entry.getValue());
//				}
//			}
//		}
//		
//		httpRequest.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
//
//		return executeHttpRequest(httpRequest, mHandler, metaData);
//	}
//
//	/**
//	 * Executes a HTTPRequest and protocols the state of that request.
//	 * 
//	 * @param httpRequest
//	 *            the request to execute
//	 * @param processor
//	 *            the logger which stores and updates the state in the database
//	 * @return the response as String e.g. json string
//	 */
//	
//	
//	private String executeHttpRequest(HttpUriRequest httpRequest, Handler mHandler, Bundle metaData) {
//		Log.i("TRACK", "ON EXECUTE HTTP REQUEST");
//		
//		String result = "";
//		String md5 = metaData.getString(Constants.BUNDLE_MD5_KEY);
//		
//		EventType eventType = (EventType) metaData.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
//		
//		Boolean priority = metaData.getBoolean(Constants.BUNDLE_PRIORITY_KEY, false);
//		
//		if(!checkConnection()){
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.NO_NETWORK, result, md5, priority));
//		}
//
//		metaData.putString(IMetaData.URI, httpRequest.getURI().toString());
//		
//		// Validate cache
//		if(RestContract.RUNNING_TESTS){
//			
//			httpRequest.addHeader(HeaderConstants.CACHE_CONTROL,HeaderConstants.CACHE_CONTROL_NO_CACHE);
//		}else{
//			if (metaData.getBoolean(IMetaData.MD_IGNORE_CACHE) || eventType.cacheTime == RestContract.NO_CACHE) {
//				Log.d(TAG, "executeHttpRequest: received ignore cache flag - bypassing cache");
//				httpRequest.addHeader(HeaderConstants.CACHE_CONTROL, HeaderConstants.CACHE_CONTROL_NO_CACHE);
//			} else {			
//				Log.d(TAG, "executeHttpRequest: received cache flag - cache time " + eventType.cacheTime);
//				String value = HeaderConstants.CACHE_CONTROL_MAX_AGE + "=" + eventType.cacheTime + "; " + HeaderConstants.CACHE_CONTROL_MUST_REVALIDATE;
//				httpRequest.addHeader(HeaderConstants.CACHE_CONTROL, value);
//			}
//		}
//		
//
//		HttpResponse response = null;
//		HttpEntity entity = null;
//		
//		// Start time 
//		long startTimeMillis = System.currentTimeMillis();
//		
//		try {
//			response = mHttpClient.execute(httpRequest, mHttpContext);
//			Log.e("TRACK", "CloseableHttpResponse " + response.hashCode() + " created!");
//			int statusCode = response.getStatusLine().getStatusCode();
//			
//			if (statusCode != HttpStatus.SC_OK) {
//				ClientProtocolException e = new ClientProtocolException();
//				if(statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE){
//					mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.SERVER_IN_MAINTENANCE, result, md5, priority));
//					trackError(mContext, e, httpRequest.getURI(), ErrorCode.SERVER_IN_MAINTENANCE, result, false, startTimeMillis);
//				} else {
//					mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.HTTP_STATUS, result, md5, priority));	
//					trackError(mContext, e, httpRequest.getURI(), ErrorCode.HTTP_STATUS, result, false, startTimeMillis);
//				}
//				
//				EntityUtils.consumeQuietly(response.getEntity());
//				Log.w(TAG, "Got bad status code for request: " + httpRequest.getURI() + " -> " + statusCode);
//				
//				return null;
//			}
//
//			if (mHttpContext instanceof HttpCacheContext) {
//			CacheResponseStatus responseStatus = ((HttpCacheContext) mHttpContext).getCacheResponseStatus();
//			if (responseStatus != null) {
//			switch (responseStatus) {
//			case CACHE_HIT:
//				Log.d(TAG, "CACHE RESPONSE STATUS: A response came from the cache with no requests sent upstream");
//				break;
//			case CACHE_MODULE_RESPONSE:
//				Log.d(TAG, "CACHE RESPONSE STATUS: The response came directly by the caching module");
//				break;
//			case CACHE_MISS:
//				Log.d(TAG, "CACHE RESPONSE STATUS: The response came from an upstream server");
//				break;
//			case VALIDATED:
//				Log.d(TAG, "CACHE RESPONSE STATUS: The response came from the cache after validating the entry with the origin server");
//				break;
//			}
//			}
//			}
//			
////			//String cacheWarning = null;
////			if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
////				Header[] headers = response.getAllHeaders();
////				if (headers != null && headers.length > 0) {
////					StringBuilder sb = new StringBuilder("ServerHeaders:");
////					for (Header header : headers) {
////						//if (HeaderConstants.WARNING.equals(header.getName())) {
////						//	cacheWarning = header.getValue();
////						//}
////						sb.append("\n").append(header.getName()).append("=").append(header.getValue());
////					}
////					Log.d(TAG, sb.toString());
////				}
////			}
//
////			// Detect redirect
////			HttpUriRequest currentReq = (HttpUriRequest) httpContext.getAttribute(ExecutionContext.HTTP_REQUEST);
////			HttpHost currentHost = (HttpHost) httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
////			String currentUrl = (currentReq.getURI().isAbsolute()) 
////								? currentReq.getURI().toString() 
////								: (currentHost.toURI() + currentReq.getURI());
////								
////			if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
////				Log.d(TAG, "currentUrl: " + currentUrl);
////			}
//			
//			// metaData.putString(IMetaData.LOCATION, currentUrl);
//
//			entity = response.getEntity();
//			if (entity == null || entity.getContentLength() == 0) {
//				mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.EMPTY_ENTITY, result, md5, priority));
//				Exception e = new Exception();
//				trackError(mContext, e, httpRequest.getURI(), ErrorCode.EMPTY_ENTITY, null, false, startTimeMillis);
//				Log.w(TAG, "Got empty entity for request: " + httpRequest.getURI() + " -> " + statusCode);
//				EntityUtils.consumeQuietly(entity);
//				return null;
//			}
//			
//            // Save the session cookie into preferences
//            persistSessionCookie();
//			
//			// FIXME - OutOfMemoryError
//			result = EntityUtils.toString(entity, Consts.UTF_8);
//            
//			Log.i(TAG, "API RESPONSE : " + result.toString());
//			
//			// Get the byte count response
//            int byteCountResponse = result.getBytes().length;
//			// closes the stream
//			EntityUtils.consumeQuietly(entity);
//			// Send success message
//			mHandler.sendMessage(buildResponseSuccessMessage(eventType, httpRequest.getURI(), Constants.SUCCESS, ErrorCode.NO_ERROR, result, md5, priority, startTimeMillis, byteCountResponse));
//			// Return the result string
//			return result;
//			
//		} catch (ClientProtocolException e) {
//			Log.d("TRACK", "ClientProtocolException");
//			Log.e(TAG, "There was a protocol error calling " + httpRequest.getURI(), e);
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.HTTP_PROTOCOL, result, md5, priority));
//			trackError(mContext, e, httpRequest.getURI(), ErrorCode.HTTP_PROTOCOL, null, false, startTimeMillis);
//		} catch (HttpHostConnectException e) {
//			Log.d("TRACK", "HttpHostConnectException");
//			Log.w(TAG, "Http host connect error calling " + httpRequest.getURI(), e);
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.CONNECT_ERROR, result, md5, priority));
//			trackError(mContext, e, httpRequest.getURI(), ErrorCode.CONNECT_ERROR, null, false, startTimeMillis);
//		} catch (ConnectTimeoutException e) {
//			Log.d("TRACK", "ConnectTimeoutException");
//			Log.w(TAG, "Connection timeout calling " + httpRequest.getURI(), e);
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.TIME_OUT, result, md5, priority));
//			trackError(mContext, e, httpRequest.getURI(), ErrorCode.TIME_OUT, null, false, startTimeMillis);
//		} catch (SocketTimeoutException e) {
//			Log.d("TRACK", "SocketTimeoutException");
//			Log.w(TAG, "Socket timeout calling " + httpRequest.getURI(), e);
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.TIME_OUT, result, md5, priority));
//			trackError(mContext, e, httpRequest.getURI(), ErrorCode.TIME_OUT, null, false, startTimeMillis);
//		} catch (UnknownHostException e) {
//			Log.d("TRACK", "UnknownHostException");
//			Log.w(TAG, "Unknown host error calling " + httpRequest.getURI(), e);
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.CONNECT_ERROR, result, md5, priority));
//			trackError(mContext, e, httpRequest.getURI(), ErrorCode.CONNECT_ERROR, null, false, startTimeMillis);
//		} catch (SSLException e) {
//			Log.d("TRACK", "SSLException");
//			Log.e(TAG, "SSL error calling " + httpRequest.getURI(), e);
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.SSL, result, md5, priority));
//			trackError(mContext, e, httpRequest.getURI(), ErrorCode.SSL, null, false, startTimeMillis);
//		} catch (IOException e) {
//			Log.d("TRACK", "IOException");
//			Log.e(TAG, "IO error calling " + httpRequest.getURI(), e);
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.IO, result, md5, priority));
//			trackError(mContext, e, httpRequest.getURI(), ErrorCode.IO, null, false, startTimeMillis);
//		} catch (OutOfMemoryError e) {
//            Log.d("TRACK", "OutOfMemoryError");
//            Log.e(TAG, "OutOfMemoryError calling " + httpRequest.getURI(), e);
//            mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.IO, result, md5, priority));
//            trackError(mContext, null, httpRequest.getURI(), ErrorCode.IO, null, false, startTimeMillis);
//		} catch (Exception e) {
//			Log.d("TRACK", "Exception");
//			e.printStackTrace();
//			Log.e(TAG, "Anormal exception " + e.getMessage(), e);
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.UNKNOWN_ERROR, result, md5, priority));
//			trackError(mContext, e, httpRequest.getURI(), ErrorCode.UNKNOWN_ERROR, null, false, startTimeMillis);
//        } finally {
//            if (response != null) {
//                Log.e("TRACK", "CloseableHttpResponse " + response.hashCode() + " closed!");
//                HttpClientUtils.closeQuietly(response);
//            } else {
//                Log.e("TRACK", "CloseableHttpResponse is null!");
//            }
//            EntityUtils.consumeQuietly(entity);
//        }
//
//		return null;
//	}
//    
//	private boolean checkConnection() {
//		NetworkInfo networkInfo = mConnManager.getActiveNetworkInfo();
//		return networkInfo != null && networkInfo.isConnected();
//	}
//
//	/*-public void removeEntryFromCache(String url) {
//
//		if (url == null) {
//			Log.w(TAG, "REMOVE ENTRY FROM CACHE: URL IS NULL !");
//			return;
//		}
//
//		Uri uri = RemoteService.completeUri(Uri.parse(url));
//		SchemeRegistry sr = mDarwinHttpClient.getConnectionManager().getSchemeRegistry();
//		Scheme s = sr.getScheme(uri.getScheme());
//		uri = uri.buildUpon().authority(uri.getAuthority() + ":" + String.valueOf(s.getDefaultPort())).build();
//		String newUrl = Uri.decode(uri.toString());
//		if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
//			Log.d(TAG, "Removing entry from cache: " + newUrl);
//		}
//		try {
//			mCacheStore.removeEntry(newUrl);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}*/
//
//	
//	/**
//	 * Method used to move an entry for other key, (TEASERS)
//	 * @param url1
//	 * @param url2
//	 * @author sergiopereira
//	 */
//	public void moveEntryInCache(String url1, String url2) {
//		try {
//			// Create complete uri
//			String uri1 = RemoteService.completeUri(Uri.parse(url1)).toString();
//			String uri2 = RemoteService.completeUri(Uri.parse(url2)).toString();
//			// Get entry from url1
//			HttpCacheEntry entry = mCacheStore.getEntry(uri1);
//			// Copy entry for url2
//			mCacheStore.putEntry(uri2, entry);
//			// Remove entry for url1
//			mCacheStore.removeEntry(uri1);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (NullPointerException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Method that builds the response for the information requested
//	 * 
//	 * @param status
//	 * @return
//	 */
//	private Message buildResponseMessage(EventType eventType, int status, ErrorCode error, String response, String md5, Boolean priority, long... values) {
//		Message msg = new Message();
//		Bundle bundle = new Bundle();
//
//		msg.what = status;
//		bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, error);
//		bundle.putString(Constants.BUNDLE_MD5_KEY, md5);
//		bundle.putString(Constants.BUNDLE_RESPONSE_KEY, response);
//		bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, priority);
//		bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, eventType);
//		// Get elapsed time
//		if (values != null && values.length > 0) bundle.putLong(Constants.BUNDLE_ELAPSED_REQUEST_TIME, values[0]);
//	
//		msg.setData(bundle);
//		
//		return msg;
//	}
//	
//	/**
//	 * Method that builds the response for the information requested
//	 * And track success response
//	 * @param eventType
//	 * @param uri
//	 * @param status
//	 * @param error
//	 * @param response
//	 * @param md5
//	 * @param priority
//	 * @param startTimeMillis
//	 * @param bytesReceived
//	 * @return Message
//	 * @author sergiopereira
//	 */
//	private Message buildResponseSuccessMessage(EventType eventType, URI uri, int status, ErrorCode error, String response, String md5, Boolean priority, long startTimeMillis, long bytesReceived) {
//		// Get the current time
//		long endTimeMillis = System.currentTimeMillis();
//		// Get the elpsed time
//		long elapsed = endTimeMillis - startTimeMillis;
//		Log.i(TAG, "executeHttpRequest took " + elapsed + "ms | " + ((uri != null) ? uri.toString() : "n.a."));
//		// Track http transaction
//		NewRelicTracker.noticeSuccessTransaction((uri != null) ? uri.toString() : "n.a.", status, startTimeMillis, endTimeMillis, bytesReceived);
//		// Create a message
//		return buildResponseMessage(eventType, status, error, response, md5, priority, elapsed);
//	}
//	
//	
//	/**
//	 * Track error
//	 * @param mContext
//	 * @param e
//	 * @param uri
//	 * @param errorCode
//	 * @param msg
//	 * @param nonFatal
//	 * @param startTimeMillis
//	 * @author sergiopereira
//	 */
//	private void trackError(Context mContext, Exception e, URI uri, ErrorCode errorCode, String msg, boolean nonFatal, long startTimeMillis) {
//		String uriString = (uri != null) ? uri.toString() : "n.a.";
//		NewRelicTracker.noticeFailureTransaction(uriString, startTimeMillis, System.currentTimeMillis());
//	}
//	
//}
