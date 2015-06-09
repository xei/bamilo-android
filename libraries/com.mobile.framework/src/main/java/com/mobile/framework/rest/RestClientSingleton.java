//package com.mobile.framework.rest;
//
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
//
//import com.mobile.newFramework.Darwin;
//import com.mobile.newFramework.ErrorCode;
//import com.mobile.framework.interfaces.IMetaData;
//import com.mobile.framework.network.ConfigurationConstants;
//import com.mobile.framework.network.DarwinHttpClient;
//import com.mobile.framework.network.LazHttpClientAndroidLog;
//import com.mobile.newFramework.rest.RestUrlUtils;
//import com.mobile.newFramework.tracking.NewRelicTracker;
//import com.mobile.newFramework.utils.Constants;
//import com.mobile.newFramework.utils.EventTask;
//import com.mobile.newFramework.utils.EventType;
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
//import ch.boye.httpclientandroidlib.Consts;
//import ch.boye.httpclientandroidlib.HttpEntity;
//import ch.boye.httpclientandroidlib.HttpResponse;
//import ch.boye.httpclientandroidlib.HttpStatus;
//import ch.boye.httpclientandroidlib.NameValuePair;
//import ch.boye.httpclientandroidlib.auth.AuthScope;
//import ch.boye.httpclientandroidlib.auth.UsernamePasswordCredentials;
//import ch.boye.httpclientandroidlib.client.ClientProtocolException;
//import ch.boye.httpclientandroidlib.client.CookieStore;
//import ch.boye.httpclientandroidlib.client.HttpClient;
//import ch.boye.httpclientandroidlib.client.cache.CacheResponseStatus;
//import ch.boye.httpclientandroidlib.client.cache.HeaderConstants;
//import ch.boye.httpclientandroidlib.client.cache.HttpCacheEntry;
//import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
//import ch.boye.httpclientandroidlib.client.methods.HttpGet;
//import ch.boye.httpclientandroidlib.client.methods.HttpPost;
//import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
//import ch.boye.httpclientandroidlib.client.protocol.ClientContext;
//import ch.boye.httpclientandroidlib.conn.ConnectTimeoutException;
//import ch.boye.httpclientandroidlib.conn.HttpHostConnectException;
//import ch.boye.httpclientandroidlib.cookie.Cookie;
//import ch.boye.httpclientandroidlib.impl.client.DecompressingHttpClient;
//import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
//import ch.boye.httpclientandroidlib.impl.client.cache.CacheConfig;
//import ch.boye.httpclientandroidlib.impl.client.cache.CachingHttpClient;
//import ch.boye.httpclientandroidlib.impl.client.cache.DBHttpCacheStorage;
//import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
//import ch.boye.httpclientandroidlib.params.BasicHttpParams;
//import ch.boye.httpclientandroidlib.params.CoreProtocolPNames;
//import ch.boye.httpclientandroidlib.params.HttpConnectionParams;
//import ch.boye.httpclientandroidlib.params.HttpParams;
//import ch.boye.httpclientandroidlib.protocol.BasicHttpContext;
//import ch.boye.httpclientandroidlib.protocol.HttpContext;
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
//public final class RestClientSingleton {
//
//	private static final String TAG = RestClientSingleton.class.getSimpleName();
//
//	private static final int MAX_CACHE_OBJECT_SIZE = 131072;
//
//	private static final int SC_SERVER_OVERLOAD = 429;
//
//	public static RestClientSingleton sRestClientSingleton;
//
//	private HttpClient mHttpClient;
//
//	private PersistentCookieStore mCookieStore;
//
//	private HttpContext mHttpContext;
//
//	private DBHttpCacheStorage mCacheStore;
//
//	private ConnectivityManager mConnManager;
//
//	private Context mContext;
//
//	/**
//	 * Create a singleton instance
//	 * @return RestClientSingleton
//	 * @author spereira
//	 */
//	public static synchronized RestClientSingleton getSingleton(Context context) {
//		// Validate the current reference
//		return sRestClientSingleton == null ? sRestClientSingleton = new RestClientSingleton(context) : sRestClientSingleton;
//	}
//
//	/**
//	 * Constructor
//	 * @author spereira
//	 */
//	private RestClientSingleton(Context context) {
//		Log.i(TAG, "CONSTRUCTOR");
//		// Save context
//		this.mContext = context;
//		// Save connectivity manager
//		this.mConnManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//		// Initialize rest client
//		this.init();
//	}
//
//	/**
//	 * Initialize all components.
//	 * @author spereira
//	 */
//	public void init(){
//		Log.i(TAG, "ON INITIALIZE");
//
//		CacheConfig cacheConfig = new CacheConfig();
//		cacheConfig.setMaxCacheEntries(100);
//		cacheConfig.setMaxObjectSize(MAX_CACHE_OBJECT_SIZE);
//		cacheConfig.setSharedCache(false);
//		mCacheStore = new DBHttpCacheStorage(mContext, cacheConfig);
//		DarwinHttpClient mDarwinHttpClient = new DarwinHttpClient(getHttpParams());
//		setAuthentication(mContext, mDarwinHttpClient);
//
//		CachingHttpClient cachingClient = new CachingHttpClient(mDarwinHttpClient, mCacheStore, cacheConfig);
//		cachingClient.log = new LazHttpClientAndroidLog("CachingHttpClient");
//		if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
//			cachingClient.log.enableWarn(true);
//			cachingClient.log.enableInfo(true);
//			cachingClient.log.enableTrace(true);
//		}
//
//		mHttpClient = new DecompressingHttpClient(cachingClient);
//		mHttpContext = new BasicHttpContext();
//		mCookieStore = new PersistentCookieStore(mContext);
//		mHttpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);
//
//		// Set the default or custom user agent
//		setHttpUserAgent();
//	}
//
//
//
//    /*
//     * ############# HTTP PARAMS #############
//     */
//
//	/**
//	 * Method used to set the user agent
//	 * @author sergiopereira
//	 */
//	private void setHttpUserAgent(){
//		// CASE Default user agent
//		String defaultUserAgent = System.getProperty("http.agent");
//		Log.i(TAG, "DEFAULT USER AGENT: " + defaultUserAgent);
//		if(!TextUtils.isEmpty(defaultUserAgent)) {
//			mHttpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, defaultUserAgent);
//		}
//	}
//
//
//    /*
//     * ############# COOKIE #############
//     */
//
//	/**
//	 * Get the Cookie store
//	 * @return CookieStore
//	 * @author spereira
//	 */
//	public CookieStore getCookieStore() {
//		return mCookieStore;
//	}
//
//	/**
//	 * Clear all cookies from Cookie Store.
//	 * @author spereira
//	 */
//	public void clearCookieStore() {
//		mCookieStore.clear();
//	}
//
//	/**
//	 * Return all cookies from CookieStore.
//	 * @return list of cookies
//	 * @author spereira
//	 */
//	public List<Cookie> getCookies() {
//		return mCookieStore.getCookies();
//	}
//
//	/**
//	 * Persist session cookie.
//	 * @author spereira
//	 */
//	public void persistSessionCookie() {
//		mCookieStore.saveSessionCookie();
//	}
//
//    /*
//     * ############# GET #############
//     */
//
//	/**
//	 * Sends a HTTP GET request to the given url and returns the response as
//	 * String (e.g. a json string).
//	 *
//	 * @return the response as String e.g. a json string
//	 */
//	public String executeGetRestUrlString(Uri uri, Handler mHandler, Bundle metaData) {
//
//		if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
//			Log.d(TAG, "executeGetRestUrlString original: " + uri.toString());
//		}
//
//		// Get event type
//		EventType eventType = (EventType) metaData.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
//		// Validate if is ventures.json
//		String url = (eventType == EventType.GET_GLOBAL_CONFIGURATIONS) ? uri.toString() : RestUrlUtils.completeUri(uri).toString();
//
//		if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
//			Log.i(TAG, "executeGetRestUrlString complete: " + url);
//		}
//		try {
//			HttpGet httpRequest = new HttpGet(url.replaceAll(" ", "%20"));
//			return executeHttpRequest(httpRequest, mHandler, metaData);
//		} catch (IllegalArgumentException ex){
//			// Catch error in case of malformed url
//			Log.e(TAG, ex.getLocalizedMessage(), ex);
//			return executeHttpRequest(new HttpGet(""), mHandler,metaData);
//		}
//	}
//
//	/**
//	 * Sends a HTTP POST request with formData to the given url and returns the
//	 * response as String (e.g. a json string).
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
//		String url = (eventType == EventType.GET_GLOBAL_CONFIGURATIONS) ? uri.toString() : RestUrlUtils.completeUri(uri).toString();
//
//		if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
//			Log.i(TAG, "executePostRestUrlString complete: " + url);
//		}
//
//		try {
//			HttpPost httpRequest = new HttpPost(url);
//
//			List<NameValuePair> params = new ArrayList<>();
//			if (formData != null) {
//				for (Entry<String, Object> entry : formData.valueSet()) {
//					Object value = entry.getValue();
//					if (value == null) {
//						Log.w(TAG, "entry for key " + entry.getKey() + " is null - ignoring - form request will fail");
//						continue;
//					}
//
//					params.add(new BasicNameValuePair(entry.getKey(), value.toString()));
//					if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
//						Log.d(TAG, "post: " + entry.getKey() + "=" + entry.getValue());
//					}
//				}
//			}
//
//			httpRequest.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
//			return executeHttpRequest(httpRequest, mHandler, metaData);
//		} catch(IllegalArgumentException ex){
//			// Catch error in case of malformed url
//			Log.e(TAG, ex.getLocalizedMessage(), ex);
//			return executeHttpRequest(new HttpPost(""), mHandler,metaData);
//		}
//	}
//
//	/**
//	 * Executes a HTTPRequest and protocols the state of that request.
//	 * @return the response as String e.g. json string
//	 */
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
//		EventTask eventTask = (EventTask)metaData.getSerializable(Constants.BUNDLE_EVENT_TASK);
//
//		if(!checkConnection()){
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.NO_NETWORK, result, md5, priority, eventTask));
//		}
//
//		metaData.putString(IMetaData.URI, httpRequest.getURI().toString());
//
//		// Validate cache
//		// CASE TESTS
//		if (RestContract.RUNNING_TESTS) {
//			httpRequest.addHeader(HeaderConstants.CACHE_CONTROL,HeaderConstants.CACHE_CONTROL_NO_CACHE);
//		}
//		// CASE NORMAL
//		else{
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
//			int statusCode = response.getStatusLine().getStatusCode();
//
//			if (statusCode != HttpStatus.SC_OK) {
//				ClientProtocolException e = new ClientProtocolException();
//				if(statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE){
//					mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.SERVER_IN_MAINTENANCE, result, md5, priority,eventTask));
//					trackError(mContext, e, httpRequest.getURI(), ErrorCode.SERVER_IN_MAINTENANCE, result, false, startTimeMillis);
//				} else if(statusCode == SC_SERVER_OVERLOAD){
//					mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.SERVER_OVERLOAD, result, md5, priority,eventTask));
//					trackError(mContext, e, httpRequest.getURI(), ErrorCode.SERVER_OVERLOAD, result, false, startTimeMillis);
//				} else {
//					mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.HTTP_STATUS, result, md5, priority,eventTask));
//					trackError(mContext, e, httpRequest.getURI(), ErrorCode.HTTP_STATUS, result, false, startTimeMillis);
//				}
//
//				EntityUtils.consumeQuietly(response.getEntity());
//				Log.w(TAG, "Got bad status code for request: " + httpRequest.getURI() + " -> " + statusCode);
//
//				return null;
//			}
//
//			CacheResponseStatus responseStatus = (CacheResponseStatus) mHttpContext.getAttribute(CachingHttpClient.CACHE_RESPONSE_STATUS);
//			switch (responseStatus) {
//				case CACHE_HIT:
//					Log.d(TAG, "CACHE RESPONSE STATUS: A response came from the cache with no requests sent upstream");
//					break;
//				case CACHE_MODULE_RESPONSE:
//					Log.d(TAG, "CACHE RESPONSE STATUS: The response came directly by the caching module");
//					break;
//				case CACHE_MISS:
//					Log.d(TAG, "CACHE RESPONSE STATUS: The response came from an upstream server");
//					break;
//				case VALIDATED:
//					Log.d(TAG, "CACHE RESPONSE STATUS: The response came from the cache after validating the entry with the origin server");
//					break;
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
////			HttpUriRequest currentReq = (HttpUriRequest) mHttpContext.getAttribute(ExecutionContext.HTTP_REQUEST);
////			HttpHost currentHost = (HttpHost) mHttpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
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
//				mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.EMPTY_ENTITY, result, md5, priority,eventTask));
//				Exception e = new Exception();
//				trackError(mContext, e, httpRequest.getURI(), ErrorCode.EMPTY_ENTITY, null, false, startTimeMillis);
//				Log.w(TAG, "Got empty entity for request: " + httpRequest.getURI() + " -> " + statusCode);
//				EntityUtils.consumeQuietly(entity);
//				return null;
//			}
//
//			// Save the session cookie into preferences
//			persistSessionCookie();
//
//			result = EntityUtils.toString(entity, Consts.UTF_8);
//			Log.i(TAG, "API RESPONSE : " + result);
//
//			// Get the byte count response
//			int byteCountResponse = result.getBytes().length;
//			// closes the stream
//			EntityUtils.consumeQuietly(entity);
//			// Send success message
//			mHandler.sendMessage(buildResponseSuccessMessage(eventType, httpRequest.getURI(), Constants.SUCCESS, ErrorCode.NO_ERROR, result, md5, priority, eventTask, startTimeMillis, byteCountResponse));
//			// Return the result string
//			return result;
//
//		} catch (ClientProtocolException e) {
//			Log.d("TRACK", "ClientProtocolException");
//			Log.e(TAG, "There was a protocol error calling " + httpRequest.getURI(), e);
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.HTTP_PROTOCOL, result, md5, priority,eventTask));
//			trackError(mContext, e, httpRequest.getURI(), ErrorCode.HTTP_PROTOCOL, null, false, startTimeMillis);
//		} catch (HttpHostConnectException e) {
//			Log.d("TRACK", "HttpHostConnectException");
//			Log.w(TAG, "Http host connect error calling " + httpRequest.getURI(), e);
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.CONNECT_ERROR, result, md5, priority,eventTask));
//			trackError(mContext, e, httpRequest.getURI(), ErrorCode.CONNECT_ERROR, null, false, startTimeMillis);
//		} catch (ConnectTimeoutException e) {
//			Log.d("TRACK", "ConnectTimeoutException");
//			Log.w(TAG, "Connection timeout calling " + httpRequest.getURI(), e);
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.TIME_OUT, result, md5, priority,eventTask));
//			trackError(mContext, e, httpRequest.getURI(), ErrorCode.TIME_OUT, null, false, startTimeMillis);
//		} catch (SocketTimeoutException e) {
//			Log.d("TRACK", "SocketTimeoutException");
//			Log.w(TAG, "Socket timeout calling " + httpRequest.getURI(), e);
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.TIME_OUT, result, md5, priority,eventTask));
//			trackError(mContext, e, httpRequest.getURI(), ErrorCode.TIME_OUT, null, false, startTimeMillis);
//		} catch (UnknownHostException e) {
//			Log.d("TRACK", "UnknownHostException");
//			Log.w(TAG, "Unknown host error calling " + httpRequest.getURI(), e);
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.CONNECT_ERROR, result, md5, priority,eventTask));
//			trackError(mContext, e, httpRequest.getURI(), ErrorCode.CONNECT_ERROR, null, false, startTimeMillis);
//		} catch (SSLException e) {
//			Log.d("TRACK", "SSLException");
//			Log.e(TAG, "SSL error calling " + httpRequest.getURI(), e);
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.SSL, result, md5, priority,eventTask));
//			trackError(mContext, e, httpRequest.getURI(), ErrorCode.SSL, null, false, startTimeMillis);
//		} catch (IOException e) {
//			Log.d("TRACK", "IOException");
//			Log.e(TAG, "IO error calling " + httpRequest.getURI(), e);
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.IO, result, md5, priority,eventTask));
//			trackError(mContext, e, httpRequest.getURI(), ErrorCode.IO, null, false, startTimeMillis);
//		} catch (OutOfMemoryError e) {
//			Log.d("TRACK", "OutOfMemoryError");
//			Log.e(TAG, "OutOfMemoryError calling " + httpRequest.getURI(), e);
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.IO, result, md5, priority,eventTask));
//			trackError(mContext, null, httpRequest.getURI(), ErrorCode.IO, null, false, startTimeMillis);
//		} catch (Exception e) {
//			Log.d("TRACK", "Exception");
//			e.printStackTrace();
//			Log.e(TAG, "Anormal exception " + e.getMessage(), e);
//			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.UNKNOWN_ERROR, result, md5, priority,eventTask));
//			trackError(mContext, e, httpRequest.getURI(), ErrorCode.UNKNOWN_ERROR, null, false, startTimeMillis);
//		}
//
//		EntityUtils.consumeQuietly(entity);
//
//		return null;
//	}
//
//	public HttpParams getHttpParams() {
//		HttpParams httpParameters = new BasicHttpParams();
//		HttpConnectionParams.setConnectionTimeout(httpParameters, ConfigurationConstants.CONNECTION_TIMEOUT);
//		HttpConnectionParams.setSoTimeout(httpParameters, ConfigurationConstants.SOCKET_TIMEOUT);
//		return httpParameters;
//	}
//
//	private static void setAuthentication(Context mContext, DefaultHttpClient httpClient) {
//		if(RestContract.RUNNING_TESTS){
//			httpClient.getCredentialsProvider()
//					.setCredentials(
//							new AuthScope(RestContract.REQUEST_HOST, AuthScope.ANY_PORT),
//							new UsernamePasswordCredentials(RestContract.AUTHENTICATION_USER_TEST, RestContract.AUTHENTICATION_PASS_TEST));
//			return;
//		}
//
//		if(RestContract.USE_AUTHENTICATION == null){
//			SharedPreferences sharedPrefs = mContext.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//			String shopId = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, null);
//			if(shopId == null){
//				throw new NullPointerException(RestClientSingleton.class.getName() + " Shop Id is null!! Cannot initialize!");
//			}
//
//			RestContract.init(mContext, "" + shopId);
//			Darwin.initialize(mContext, "" + shopId);
//		}
//		if (RestContract.USE_AUTHENTICATION) {
//			httpClient.getCredentialsProvider()
//					.setCredentials(
//							new AuthScope(RestContract.REQUEST_HOST, AuthScope.ANY_PORT),
//							new UsernamePasswordCredentials(RestContract.AUTHENTICATION_USER, RestContract.AUTHENTICATION_PASS));
//		}
//	}
//
//	private boolean checkConnection() {
//		NetworkInfo networkInfo = mConnManager.getActiveNetworkInfo();
//		return networkInfo != null && networkInfo.isConnected();
//	}
//
//	/**
//	 * Method used to move an entry for other key, (TEASERS)
//	 * @author sergiopereira
//	 */
//	public void moveEntryInCache(String url1, String url2) {
//		try {
//			// Create complete uri
//			String uri1 = RestUrlUtils.completeUri(Uri.parse(url1)).toString();
//			String uri2 = RestUrlUtils.completeUri(Uri.parse(url2)).toString();
//			// Get entry from url1
//			HttpCacheEntry entry = mCacheStore.getEntry(uri1);
//			// Copy entry for url2
//			mCacheStore.putEntry(uri2, entry);
//			// Remove entry for url1
//			mCacheStore.removeEntry(uri1);
//		} catch (IOException | NullPointerException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Remove entry in cache
//	 * @author ricardosoares
//	 */
//	public void removeEntry(String url) {
//		String completeUrl = RemoteService.completeUrlWithPort(Uri.parse(url));
//		try {
//			mCacheStore.removeEntryDB(completeUrl);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Method that builds the response for the information requested
//	 */
//	private Message buildResponseMessage(EventType eventType, int status, ErrorCode error, String response, String md5, Boolean priority, EventTask eventTask, long... values) {
//		Message msg = new Message();
//		Bundle bundle = new Bundle();
//
//		msg.what = status;
//		bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, error);
//		bundle.putString(Constants.BUNDLE_MD5_KEY, md5);
//		bundle.putString(Constants.BUNDLE_RESPONSE_KEY, response);
//		bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, priority);
//		bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, eventType);
//		bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, eventTask);
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
//	 * @author sergiopereira
//	 */
//	private Message buildResponseSuccessMessage(EventType eventType, URI uri, int status, ErrorCode error, String response, String md5, Boolean priority, EventTask eventTask,long startTimeMillis, long bytesReceived) {
//		// Get the current time
//		long endTimeMillis = System.currentTimeMillis();
//		// Get the elpsed time
//		long elapsed = endTimeMillis - startTimeMillis;
//		// Log.i(TAG, (uri != null) ? uri.toString() : "n.a." + "--------- executeHttpRequest------------: request took " + elapsed + "ms bytes: " + bytesReceived);
//		Log.i(TAG, "executeHttpRequest took " + elapsed + "ms | " + ((uri != null) ? uri.toString() : "n.a."));
//		// Track http transaction
//		NewRelicTracker.noticeSuccessTransaction((uri != null) ? uri.toString() : "n.a.", status, startTimeMillis, endTimeMillis, bytesReceived);
//		// Create a message
//		return buildResponseMessage(eventType, status, error, response, md5, priority, eventTask,elapsed);
//	}
//
//
//	/**
//	 * Track error
//	 * @author sergiopereira
//	 */
//	private void trackError(Context mContext, Exception e, URI uri, ErrorCode errorCode, String msg, boolean nonFatal, long startTimeMillis) {
//		String uriString = (uri != null) ? uri.toString() : "n.a.";
//		NewRelicTracker.noticeFailureTransaction(uriString, startTimeMillis, System.currentTimeMillis());
//	}
//}
