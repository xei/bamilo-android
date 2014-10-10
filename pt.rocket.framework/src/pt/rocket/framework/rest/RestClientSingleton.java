package pt.rocket.framework.rest;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.net.ssl.SSLException;

import org.apache.http.params.CoreProtocolPNames;

import pt.rocket.framework.Darwin;
import pt.rocket.framework.DarwinMode;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.interfaces.IMetaData;
import pt.rocket.framework.network.ConfigurationConstants;
import pt.rocket.framework.network.DarwinHttpClient;
import pt.rocket.framework.network.LazHttpClientAndroidLog;
import pt.rocket.framework.service.RemoteService;
import pt.rocket.framework.tracking.NewRelicTracker;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.ErrorMonitoring;
import pt.rocket.framework.utils.EventType;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import ch.boye.httpclientandroidlib.Consts;
import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.auth.AuthScope;
import ch.boye.httpclientandroidlib.auth.UsernamePasswordCredentials;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.CookieStore;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.cache.CacheResponseStatus;
import ch.boye.httpclientandroidlib.client.cache.HeaderConstants;
import ch.boye.httpclientandroidlib.client.cache.HttpCacheEntry;
import ch.boye.httpclientandroidlib.client.cache.HttpCacheStorage;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
import ch.boye.httpclientandroidlib.client.protocol.ClientContext;
import ch.boye.httpclientandroidlib.conn.ConnectTimeoutException;
import ch.boye.httpclientandroidlib.conn.HttpHostConnectException;
import ch.boye.httpclientandroidlib.conn.scheme.Scheme;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeRegistry;
import ch.boye.httpclientandroidlib.impl.client.BasicCookieStore;
import ch.boye.httpclientandroidlib.impl.client.DecompressingHttpClient;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.client.cache.CacheConfig;
import ch.boye.httpclientandroidlib.impl.client.cache.CachingHttpClient;
import ch.boye.httpclientandroidlib.impl.client.cache.DBHttpCacheStorage;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import ch.boye.httpclientandroidlib.params.BasicHttpParams;
import ch.boye.httpclientandroidlib.params.HttpConnectionParams;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.protocol.BasicHttpContext;
import ch.boye.httpclientandroidlib.protocol.ExecutionContext;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import ch.boye.httpclientandroidlib.util.EntityUtils;
import de.akquinet.android.androlog.Log;

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
public final class RestClientSingleton {
	
	private static final String TAG = RestClientSingleton.class.getSimpleName();
	
	private static final int MAX_CACHE_OBJECT_SIZE = 131072;
	
	//private static final Pattern proxyPattern = Pattern.compile(".*@(.*):[0-9]*$");
	
	public static RestClientSingleton INSTANCE;
	
	private DarwinHttpClient darwinHttpClient;
	
	private final HttpClient httpClient;
	
	private CookieStore cookieStore;
	
	private HttpContext httpContext;
	
	private HttpCacheStorage cache; 	// Handles all database operations
	
	private ConnectivityManager connManager;
	
	private Context context;
	
	/**
	 * 
	 * @param context
	 */
	private RestClientSingleton(Context context) {
		this.context = context;
		connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		CacheConfig cacheConfig = new CacheConfig();
		cacheConfig.setMaxCacheEntries(100);
		cacheConfig.setMaxObjectSize(MAX_CACHE_OBJECT_SIZE);
		cacheConfig.setSharedCache(false);
		cache = new DBHttpCacheStorage(context, cacheConfig);
		darwinHttpClient = new DarwinHttpClient(getHttpParams(context));
		setAuthentication(context, darwinHttpClient);

		CachingHttpClient cachingClient = new CachingHttpClient(darwinHttpClient, cache, cacheConfig);
		cachingClient.log = new LazHttpClientAndroidLog("CachingHttpClient");
		if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
			cachingClient.log.enableWarn(true);
			cachingClient.log.enableInfo(true);
			cachingClient.log.enableTrace(true);
		}

		httpClient = new DecompressingHttpClient(cachingClient);
		httpContext = new BasicHttpContext();
		cookieStore = new BasicCookieStore();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		
		// Set the default or custom user agent
		setHttpUserAgent();

	}

	public static RestClientSingleton init(Context context) {
		INSTANCE = new RestClientSingleton(context);
		return INSTANCE;
	}

	public static synchronized RestClientSingleton getSingleton(Context context) {
		if (INSTANCE == null) {
			Log.w(TAG, "RestClientSingleton needs to be initialized!");
			return init(context);
		}
		INSTANCE.context = context;
		return INSTANCE;
	}
	
	/**
	 * Method used to set the user agent
	 * @author sergiopereira
	 */
	private void setHttpUserAgent(){
		// CASE Default user agent
		String defaultUserAgent = System.getProperty("http.agent");
		if(!TextUtils.isEmpty(defaultUserAgent)) {
			httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, defaultUserAgent);
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
	public String executeGetRestUrlString(Uri uri, Handler mHandler, Bundle metaData) {

		if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
			Log.d(TAG, "executeGetRestUrlString original: " + uri.toString());
		}
		
		// Get event type
		EventType eventType = (EventType) metaData.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
		// Validate if is ventures.json
		String url = (eventType == EventType.GET_GLOBAL_CONFIGURATIONS) ? uri.toString() : RemoteService.completeUri(uri).toString();
		
		if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
			Log.d(TAG, "executeGetRestUrlString complete: " + url.toString());
		}
		
		HttpGet httpRequest = new HttpGet(url.replaceAll(" ", "%20"));
		return executeHttpRequest(httpRequest, mHandler, metaData);
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
	public String executePostRestUrlString(Uri uri, ContentValues formData, Handler mHandler, Bundle metaData) {

		if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
			Log.d(TAG, "executePostRestUrlString original: " + uri.toString());
		}
		
		// Get event type
		EventType eventType = (EventType) metaData.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
		// Validate if is ventures.json
		String url = (eventType == EventType.GET_GLOBAL_CONFIGURATIONS) ? uri.toString() : RemoteService.completeUri(uri).toString();
		
		if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
			Log.d(TAG, "executePostRestUrlString complete: " + url.toString());
		}
		
		HttpPost httpRequest = new HttpPost(url.toString());

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if(formData != null){
			for (Entry<String, Object> entry : formData.valueSet()) {
				Object value = entry.getValue();
				if (value == null) {
					Log.w(TAG, "entry for key " + entry.getKey()
							+ " is null - ignoring - form request will fail");
					continue;
				}
	
				params.add(new BasicNameValuePair(entry.getKey(), value.toString()));
				if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
					Log.d(TAG, "post: " + entry.getKey() + "=" + entry.getValue());
				}
			}
		}
		
		httpRequest.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));

		return executeHttpRequest(httpRequest, mHandler, metaData);
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
	// public String executePutRestUrlString(Uri uri, ContentValues formData,
	// ResultReceiver resultReceiver, Bundle metaData) {
	// httpClient.getParams().setParameter(
	// ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
	//
	// Log.d(TAG, "put: uri = " + uri.toString());
	// HttpPut httpRequest = new HttpPut(uri.toString());
	//
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// for (Entry<String, Object> entry : formData.valueSet()) {
	// params.add(new BasicNameValuePair(entry.getKey(), (String) entry
	// .getValue()));
	// if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
	// Log.d(TAG, "put: " + entry.getKey() + "="
	// + entry.getValue().toString());
	// }
	// }
	//
	// try {
	// httpRequest.setEntity(new UrlEncodedFormEntity(params));
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// }
	//
	// return executeHttpRequest(httpRequest, resultReceiver, metaData);
	// }

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
	// public String executeDeleteRestUrlString(Uri uri,
	// ResultReceiver resultReceiver, Bundle metaData) {
	// httpClient.getParams().setParameter(
	// ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
	//
	// if (ConfigurationConstants.LOG_DEBUG_ENABLED)
	// Log.d(TAG, "delete: uri = " + uri.toString());
	// HttpDelete httpRequest = new HttpDelete(uri.toString());
	//
	// return executeHttpRequest(httpRequest, resultReceiver, metaData);
	// }

	/**
	 * Executes a HTTPRequest and protocols the state of that request.
	 * 
	 * @param httpRequest
	 *            the request to execute
	 * @param processor
	 *            the logger which stores and updates the state in the database
	 * @return the response as String e.g. json string
	 */
	private String executeHttpRequest(HttpUriRequest httpRequest, Handler mHandler, Bundle metaData) {
		Log.i("TRACK", "ON EXECUTE HTTP REQUEST");
		
		String result = "";
		String md5 = metaData.getString(Constants.BUNDLE_MD5_KEY);
		
		EventType eventType = (EventType) metaData.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
		
		Boolean priority = metaData.getBoolean(Constants.BUNDLE_PRIORITY_KEY, false);
		
		if(!checkConnection()){
			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.NO_NETWORK, result, md5, priority));
		}

		metaData.putString(IMetaData.URI, httpRequest.getURI().toString());
		
		// Validate cache
		if(RestContract.RUNNING_TESTS){
			
			httpRequest.addHeader(HeaderConstants.CACHE_CONTROL,HeaderConstants.CACHE_CONTROL_NO_CACHE);
		}else{
			if (metaData.getBoolean(IMetaData.MD_IGNORE_CACHE) || eventType.cacheTime == RestContract.NO_CACHE) {
				Log.d(TAG, "executeHttpRequest: received ignore cache flag - bypassing cache");
				httpRequest.addHeader(HeaderConstants.CACHE_CONTROL, HeaderConstants.CACHE_CONTROL_NO_CACHE);
			} else {			
				Log.d(TAG, "executeHttpRequest: received cache flag - cache time " + eventType.cacheTime);
				String value = HeaderConstants.CACHE_CONTROL_MAX_AGE + "=" + eventType.cacheTime + "; " + HeaderConstants.CACHE_CONTROL_MUST_REVALIDATE;
				httpRequest.addHeader(HeaderConstants.CACHE_CONTROL, value);
			}
		}
		

		metaData = new Bundle();
		HttpResponse response = null;
		HttpEntity entity = null;
		
		// Start time 
		long startTimeMillis = System.currentTimeMillis();
		
		try {
			response = httpClient.execute(httpRequest, httpContext);
			int statusCode = response.getStatusLine().getStatusCode();
			
			if (statusCode != HttpStatus.SC_OK) {
				ClientProtocolException e = new ClientProtocolException();
				if(statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE){
					mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.SERVER_IN_MAINTENANCE, result, md5, priority));
					trackError(context, e, httpRequest.getURI(), ErrorCode.SERVER_IN_MAINTENANCE, result, false, startTimeMillis);
				} else {
					mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.HTTP_STATUS, result, md5, priority));	
					trackError(context, e, httpRequest.getURI(), ErrorCode.HTTP_STATUS, result, false, startTimeMillis);
				}
				
				EntityUtils.consumeQuietly(response.getEntity());
				Log.w(TAG, "Got bad status code for request: " + httpRequest.getURI() + " -> " + statusCode);
				
				return null;
			}

			CacheResponseStatus responseStatus = (CacheResponseStatus) httpContext.getAttribute(CachingHttpClient.CACHE_RESPONSE_STATUS);
			switch (responseStatus) {
			case CACHE_HIT:
				Log.d(TAG, "CACHE RESPONSE STATUS: A response came from the cache with no requests sent upstream");
				break;
			case CACHE_MODULE_RESPONSE:
				Log.d(TAG, "CACHE RESPONSE STATUS: The response came directly by the caching module");
				break;
			case CACHE_MISS:
				Log.d(TAG, "CACHE RESPONSE STATUS: The response came from an upstream server");
				break;
			case VALIDATED:
				Log.d(TAG, "CACHE RESPONSE STATUS: The response came from the cache after validating the entry with the origin server");
				break;
			}
			
			//String cacheWarning = null;
			if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
				Header[] headers = response.getAllHeaders();
				if (headers != null && headers.length > 0) {
					StringBuilder sb = new StringBuilder("ServerHeaders:");
					for (Header header : headers) {
						//if (HeaderConstants.WARNING.equals(header.getName())) {
						//	cacheWarning = header.getValue();
						//}
						sb.append("\n").append(header.getName()).append("=").append(header.getValue());
					}
					Log.d(TAG, sb.toString());
				}
			}

			// Detect redirect
			HttpUriRequest currentReq = (HttpUriRequest) httpContext.getAttribute(ExecutionContext.HTTP_REQUEST);
			HttpHost currentHost = (HttpHost) httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
			String currentUrl = (currentReq.getURI().isAbsolute()) 
								? currentReq.getURI().toString() 
								: (currentHost.toURI() + currentReq.getURI());
								
			if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
				Log.d(TAG, "currentUrl: " + currentUrl);
			}
			metaData.putString(IMetaData.LOCATION, currentUrl);

			entity = response.getEntity();
			if (entity == null || entity.getContentLength() == 0) {
				mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.EMPTY_ENTITY, result, md5, priority));
				Exception e = new Exception();
				trackError(context, e, httpRequest.getURI(), ErrorCode.EMPTY_ENTITY, null, false, startTimeMillis);
				Log.w(TAG, "Got empty entity for request: " + httpRequest.getURI() + " -> " + statusCode);
				EntityUtils.consumeQuietly(entity);
				return null;
			}

			// Log.i(TAG, "USER AGENT : " + System.getProperty("http.agent"));
			
			// FIXME - OutOfMemoryError
			result = EntityUtils.toString(entity, Consts.UTF_8);
			Log.i(TAG, "code1response : "+result.toString());
			//Log.i(TAG, "code1 request response is: " + result.toString());
			//result = org.apache.commons.io.IOUtils.toString(entity.getContent());
			
			// Get the byte count response
            int byteCountResponse = result.getBytes().length;
			// closes the stream
			EntityUtils.consumeQuietly(entity);
			// Send success message
			mHandler.sendMessage(buildResponseSuccessMessage(eventType, httpRequest.getURI(), Constants.SUCCESS, ErrorCode.NO_ERROR, result, md5, priority, startTimeMillis, byteCountResponse));
			// Return the result string
			return result;
			
		} catch (ClientProtocolException e) {
			Log.d("TRACK", "ClientProtocolException");
			Log.e(TAG, "There was a protocol error calling " + httpRequest.getURI(), e);
			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.HTTP_PROTOCOL, result, md5, priority));
			trackError(context, e, httpRequest.getURI(), ErrorCode.HTTP_PROTOCOL, null, false, startTimeMillis);
		} catch (HttpHostConnectException e) {
			Log.d("TRACK", "HttpHostConnectException");
			Log.w(TAG, "Http host connect error calling " + httpRequest.getURI(), e);
			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.CONNECT_ERROR, result, md5, priority));
			trackError(context, e, httpRequest.getURI(), ErrorCode.CONNECT_ERROR, null, false, startTimeMillis);
		} catch (ConnectTimeoutException e) {
			Log.d("TRACK", "ConnectTimeoutException");
			Log.w(TAG, "Connection timeout calling " + httpRequest.getURI(), e);
			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.TIME_OUT, result, md5, priority));
			trackError(context, e, httpRequest.getURI(), ErrorCode.TIME_OUT, null, false, startTimeMillis);
		} catch (SocketTimeoutException e) {
			Log.d("TRACK", "SocketTimeoutException");
			Log.w(TAG, "Socket timeout calling " + httpRequest.getURI(), e);
			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.TIME_OUT, result, md5, priority));
			trackError(context, e, httpRequest.getURI(), ErrorCode.TIME_OUT, null, false, startTimeMillis);
		} catch (UnknownHostException e) {
			Log.d("TRACK", "UnknownHostException");
			Log.w(TAG, "Unknown host error calling " + httpRequest.getURI(), e);
			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.CONNECT_ERROR, result, md5, priority));
			trackError(context, e, httpRequest.getURI(), ErrorCode.CONNECT_ERROR, null, false, startTimeMillis);
		} catch (SSLException e) {
			Log.d("TRACK", "SSLException");
			Log.e(TAG, "SSL error calling " + httpRequest.getURI(), e);
			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.SSL, result, md5, priority));
			trackError(context, e, httpRequest.getURI(), ErrorCode.SSL, null, false, startTimeMillis);
		} catch (IOException e) {
			Log.d("TRACK", "IOException");
			Log.e(TAG, "IO error calling " + httpRequest.getURI(), e);
			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.IO, result, md5, priority));
			trackError(context, e, httpRequest.getURI(), ErrorCode.IO, null, false, startTimeMillis);
		} catch (OutOfMemoryError e) {
            Log.d("TRACK", "OutOfMemoryError");
            Log.e(TAG, "OutOfMemoryError calling " + httpRequest.getURI(), e);
            mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.IO, result, md5, priority));
            /*-// Alternative to e.getStackTrace().toString()
            // http://stackoverflow.com/questions/1149703/how-can-i-convert-a-stack-trace-to-a-string
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();*/
            trackError(context, null, httpRequest.getURI(), ErrorCode.IO, e.getStackTrace().toString(), false, startTimeMillis);
		} catch (Exception e) {
			Log.d("TRACK", "Exception");
			e.printStackTrace();
			Log.e(TAG, "Anormal exception " + e.getMessage(), e);
			mHandler.sendMessage(buildResponseMessage(eventType, Constants.FAILURE, ErrorCode.UNKNOWN_ERROR, result, md5, priority));
			trackError(context, e, httpRequest.getURI(), ErrorCode.UNKNOWN_ERROR, null, false, startTimeMillis);
		}

		EntityUtils.consumeQuietly(entity);

		return null;
	}

	public HttpParams getHttpParams(Context context) {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, ConfigurationConstants.CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, ConfigurationConstants.SOCKET_TIMEOUT);
		// setProxy( context, httpParameters );
		return httpParameters;
	}

	private static void setAuthentication(Context mContext, DefaultHttpClient httpClient) {
		if(RestContract.RUNNING_TESTS){
			httpClient.getCredentialsProvider()
			.setCredentials(
					new AuthScope(RestContract.REQUEST_HOST, AuthScope.ANY_PORT),
					new UsernamePasswordCredentials(RestContract.AUTHENTICATION_USER_TEST, RestContract.AUTHENTICATION_PASS_TEST));
			return;
		}
		
		if(RestContract.USE_AUTHENTICATION == null){
			SharedPreferences sharedPrefs = mContext.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
	        
	        /**
	         * TODO: Validate
	         * Fixed crash.
	         * If shop id isn't present in this point something is wrong, return 0 as default value
	         * @author sergiopereira 
	         */
	        //int shopId = sharedPrefs.getInt(Darwin.KEY_SELECTED_COUNTRY_ID, 0);
	        //if(shopId == -1) shopId = 0;
			// Old
	        String shopId = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, null);
	        if(shopId == null){
	        	throw new NullPointerException(RestClientSingleton.class.getName() + " Shop Id is null!! Cannot initialize!");
	        }
	        
			RestContract.init(mContext, "" + shopId);
			Darwin.initialize(DarwinMode.DEBUG, mContext, "" + shopId, false);
		}
		if (RestContract.USE_AUTHENTICATION) {
			httpClient.getCredentialsProvider()
					.setCredentials(
							new AuthScope(RestContract.REQUEST_HOST, AuthScope.ANY_PORT),
							new UsernamePasswordCredentials(RestContract.AUTHENTICATION_USER, RestContract.AUTHENTICATION_PASS));
		}
	}

	public CookieStore getCookieStore() {
		return cookieStore;
	}

	private boolean checkConnection() {
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}

	public void removeEntryFromCache(String url) {

		if (url == null) {
			Log.w(TAG, "REMOVE ENTRY FROM CACHE: URL IS NULL !");
			return;
		}

		Uri uri = RemoteService.completeUri(Uri.parse(url));
		SchemeRegistry sr = darwinHttpClient.getConnectionManager().getSchemeRegistry();
		Scheme s = sr.getScheme(uri.getScheme());
		uri = uri.buildUpon().authority(uri.getAuthority() + ":" + String.valueOf(s.getDefaultPort())).build();
		String newUrl = Uri.decode(uri.toString());
		if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
			Log.d(TAG, "Removing entry from cache: " + newUrl);
		}
		try {
			cache.removeEntry(newUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Method used to move an entry for other key
	 * @param url1
	 * @param url2
	 * @author sergiopereira
	 */
	public void moveEntryInCache(String url1, String url2) {
		try {
			// Create complete uri
			String uri1 = RemoteService.completeUri(Uri.parse(url1)).toString();
			String uri2 = RemoteService.completeUri(Uri.parse(url2)).toString();
			// Get entry from url1
			HttpCacheEntry entry = cache.getEntry(uri1);
			// Copy entry for url2
			cache.putEntry(uri2, entry);
			// Remove entry for url1
			cache.removeEntry(uri1);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method that builds the response for the information requested
	 * 
	 * @param status
	 * @return
	 */
	private Message buildResponseMessage(EventType eventType, int status, ErrorCode error, String response, String md5, Boolean priority, long... values) {
		Message msg = new Message();
		Bundle bundle = new Bundle();

		msg.what = status;
		bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, error);
		bundle.putString(Constants.BUNDLE_MD5_KEY, md5);
		bundle.putString(Constants.BUNDLE_RESPONSE_KEY, response);
		bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, priority);
		bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, eventType);
		// Get elapsed time
		if (values != null && values.length > 0) bundle.putLong(Constants.BUNDLE_ELAPSED_REQUEST_TIME, values[0]);
	
		msg.setData(bundle);
		
		return msg;
	}
	
	/**
	 * Method that builds the response for the information requested
	 * And track success response
	 * @param eventType
	 * @param uri
	 * @param status
	 * @param error
	 * @param response
	 * @param md5
	 * @param priority
	 * @param startTimeMillis
	 * @param bytesReceived
	 * @return Message
	 * @author sergiopereira
	 */
	private Message buildResponseSuccessMessage(EventType eventType, URI uri, int status, ErrorCode error, String response, String md5, Boolean priority, long startTimeMillis, long bytesReceived) {
		// Get the current time
		long endTimeMillis = System.currentTimeMillis();
		// Get the elpsed time
		long elapsed = endTimeMillis - startTimeMillis;
		Log.i(TAG, (uri != null) ? uri.toString() : "n.a." + "--------- executeHttpRequest------------: request took " + elapsed + "ms bytes: " + bytesReceived);
		// Track http transaction
		NewRelicTracker.noticeSuccessTransaction((uri != null) ? uri.toString() : "n.a.", status, startTimeMillis, endTimeMillis, bytesReceived);
		// Create a message
		return buildResponseMessage(eventType, status, error, response, md5, priority, elapsed);
	}
	
	
	/**
	 * Track error
	 * @param mContext
	 * @param e
	 * @param uri
	 * @param errorCode
	 * @param msg
	 * @param nonFatal
	 * @param startTimeMillis
	 * @author sergiopereira
	 */
	private void trackError(Context mContext, Exception e, URI uri, ErrorCode errorCode, String msg, boolean nonFatal, long startTimeMillis) {
		String uriString = (uri != null) ? uri.toString() : "n.a.";
		NewRelicTracker.noticeFailureTransaction(uriString, startTimeMillis, System.currentTimeMillis());
		// Track http failure
		// Send exception
		ErrorMonitoring.sendException(mContext, e, uriString, errorCode, msg, null, nonFatal);
	}
	
}
