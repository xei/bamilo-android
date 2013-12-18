package pt.rocket.framework.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy.Type;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.rocket.framework.Darwin;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.IMetaData;
import pt.rocket.framework.network.DarwinHttpClient;
import pt.rocket.framework.network.LazHttpClientAndroidLog;
import pt.rocket.framework.utils.ErrorMonitoring;
import pt.rocket.framework.utils.LogTagHelper;
import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpRequest;
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
import ch.boye.httpclientandroidlib.client.cache.HttpCacheStorage;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpDelete;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpPut;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
import ch.boye.httpclientandroidlib.client.params.ClientPNames;
import ch.boye.httpclientandroidlib.client.protocol.ClientContext;
import ch.boye.httpclientandroidlib.conn.ConnectTimeoutException;
import ch.boye.httpclientandroidlib.conn.HttpHostConnectException;
import ch.boye.httpclientandroidlib.conn.routing.HttpRoute;
import ch.boye.httpclientandroidlib.conn.routing.HttpRoutePlanner;
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
import ch.boye.httpclientandroidlib.protocol.HTTP;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import ch.boye.httpclientandroidlib.util.EntityUtils;

import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxySettings;

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
public final class RestClientSingleton implements HttpRoutePlanner {

	/**
	 * 
	 */
	private static final int MAX_CACHE_OBJECT_SIZE = 131072;

	private static final String TAG = LogTagHelper
			.create(RestClientSingleton.class);
	
	private static final Pattern proxyPattern = Pattern.compile( ".*@(.*):[0-9]*$");
	
//	/** Default header values. */
//	private static final HashMap<String, String> DEFAULT_HEADER;
//	static {
//		DEFAULT_HEADER = new HashMap<String, String>();
//		// add header here
//		DEFAULT_HEADER.put("X-ROCKET-MOBAPI-VERSION", "1.0");
//		DEFAULT_HEADER.put("X-ROCKET-MOBAPI-PLATFORM",
//				"application/rocket.mobapi-v1.0+json");
//		DEFAULT_HEADER.put("X-ROCKET-MOBAPI-TOKEN", "1");
//	}
	
	private static RestClientSingleton INSTANCE;
	
	private DarwinHttpClient darwinHttpClient;
	private final HttpClient httpClient;
	private CookieStore cookieStore;
	private HttpContext httpContext;
	// handles all Database operations
	private HttpCacheStorage cache;
	
	private ConnectivityManager connManager;
	
	private static boolean updateParamsFlag;

	private Context context;

	/**
	 * Sets the timeout until a connection is established. A value of zero means
	 * the timeout is not used. The default value is zero.
	 */
	private static final int CONNECTION_TIMEOUT = 60 * 1000; // ms

	/**
	 * Sets the default socket timeout (SO_TIMEOUT) in milliseconds which is the
	 * timeout for waiting for data. A timeout value of zero is interpreted as
	 * an infinite timeout. This value is used when no socket timeout is set in
	 * the method parameters.
	 */
	private static final int SOCKET_TIMEOUT = 60 * 1000;

	private RestClientSingleton(Context context) {
		this.context = context;
		connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		CacheConfig cacheConfig = new CacheConfig();  
		cacheConfig.setMaxCacheEntries(100);
		cacheConfig.setMaxObjectSize(MAX_CACHE_OBJECT_SIZE);
		cacheConfig.setSharedCache(false);
		cache = new DBHttpCacheStorage(context, cacheConfig);
		darwinHttpClient = new DarwinHttpClient(getHttpParams(context));
		setAuthentication(darwinHttpClient);
		darwinHttpClient.setRoutePlanner(this);
		
		CachingHttpClient cachingClient = new CachingHttpClient(
				darwinHttpClient, cache, cacheConfig);
		cachingClient.log = new LazHttpClientAndroidLog("CachingHttpClient");
		if ( Darwin.logDebugEnabled ) {
			cachingClient.log.enableWarn(true);
			cachingClient.log.enableInfo(true);
			cachingClient.log.enableTrace(true);
		}
				
		httpClient = new DecompressingHttpClient(
				cachingClient);
		httpContext = new BasicHttpContext();
		cookieStore = new BasicCookieStore();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	public static RestClientSingleton init(Context context) {
		INSTANCE = new RestClientSingleton(context);
		return INSTANCE;
	}

	public static synchronized RestClientSingleton getSingleton() {
		if (INSTANCE == null) {
			throw new RuntimeException(
					"RestClientSingleton needs to be initialized!");
		}
		return INSTANCE;
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
	public String executeGetRestUrlString(Uri uri,
			ResultReceiver resultReceiver, Bundle metaData) {
		// databaseHelper.getReadableDatabase().quer
		if ( Darwin.logDebugEnabled) {
			Log.d(TAG, "get: " + uri.toString());
		}
		
		
		HttpGet httpRequest = null;
		try {
			httpRequest = new HttpGet(uri.toString().replaceAll(" ", "%20"));	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return executeHttpRequest(httpRequest, resultReceiver, metaData);
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
	public String executePostRestUrlString(Uri uri, ContentValues formData,
			ResultReceiver resultReceiver, Bundle metaData) {

		if (Darwin.logDebugEnabled) {
			Log.d(TAG, "post: " + uri.toString());
		}
		HttpPost httpRequest = new HttpPost(uri.toString());

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Object value;
		for (Entry<String, Object> entry : formData.valueSet()) {
			value = entry.getValue();
			if ( value == null) {
				Log.w( TAG, "entry for key " + entry.getKey() + " is null - ignoring - form request will fail" );
				continue;
			}
			
			params.add( new BasicNameValuePair(entry.getKey(), value.toString()));
			if ( Darwin.logDebugEnabled ) {
				Log.d(TAG, "post: " + entry.getKey() + "=" + entry.getValue());
			}
		}

		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		value = null;
		params = null;
		return executeHttpRequest(httpRequest, resultReceiver, metaData);
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
	public String executePutRestUrlString(Uri uri, ContentValues formData,
			ResultReceiver resultReceiver, Bundle metaData) {
		httpClient.getParams().setParameter(
				ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		if (Darwin.logDebugEnabled) {
			Log.d(TAG, "put: uri = " + uri.toString());
		}
		HttpPut httpRequest = new HttpPut(uri.toString());

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (Entry<String, Object> entry : formData.valueSet()) {
			params.add(new BasicNameValuePair(entry.getKey(), (String) entry
					.getValue()));
			if (Darwin.logDebugEnabled) {
				Log.d(TAG, "put: " + entry.getKey() + "="
					+ entry.getValue().toString());
			}
		}

		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		params = null;
		return executeHttpRequest(httpRequest, resultReceiver, metaData);
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
	public String executeDeleteRestUrlString(Uri uri,
			ResultReceiver resultReceiver, Bundle metaData) {
		httpClient.getParams().setParameter(
				ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

		if ( Darwin.logDebugEnabled)
			Log.d(TAG, "delete: uri = " + uri.toString());
		HttpDelete httpRequest = new HttpDelete(uri.toString());

		return executeHttpRequest(httpRequest, resultReceiver, metaData);
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
	private String executeHttpRequest(HttpUriRequest httpRequest,
			ResultReceiver resultReceiver, Bundle metaData) {
		String result = "";
		//		for (Map.Entry<String, String> headerEntry : DEFAULT_HEADER.entrySet()) {
		//			httpRequest.addHeader(headerEntry.getKey(), headerEntry.getValue());
		//		}
		
		
		if(httpRequest == null || httpRequest.getURI() == null || httpRequest.getURI().toString() == null){
			metaData.putString( IMetaData.URI, "MISSING PARAMETERS FROM API");
			ResponseReceiver.sendError(resultReceiver, ErrorCode.UNKNOWN_ERROR, metaData);
			return result;
		}
		
		metaData.putString( IMetaData.URI, httpRequest.getURI().toString() );
		if ( metaData.getBoolean( IMetaData.MD_IGNORE_CACHE) ) {
			Log.d( TAG, "executeHttpRequest: receveid ignore cache flag - bypassing cache" );
			String value = HeaderConstants.CACHE_CONTROL_MAX_AGE + "=0; " + HeaderConstants.CACHE_CONTROL_MUST_REVALIDATE;
			httpRequest.addHeader(HeaderConstants.CACHE_CONTROL, value);
		}
		
		metaData = new Bundle();
		HttpResponse response = null;
		HttpEntity entity = null;
		try {
			long now = System.currentTimeMillis();
			response = httpClient.execute(httpRequest, httpContext);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
//				ResponseReceiver.sendHttpError(resultReceiver, statusCode);
				Log.w(TAG,
						"Got bad status code for request: "
								+ httpRequest.getURI() + " -> " + statusCode);
//				return null;
			}		
			
			CacheResponseStatus responseStatus = (CacheResponseStatus) httpContext
					.getAttribute(CachingHttpClient.CACHE_RESPONSE_STATUS);
			switch (responseStatus) {
			case CACHE_HIT:
				Log.d(TAG,
						"A response was generated from the cache with no requests "
								+ "sent upstream");
				break;
			case CACHE_MODULE_RESPONSE:
				Log.d(TAG,
						"The response was generated directly by the caching module");
				break;
			case CACHE_MISS:
				Log.d(TAG, "The response came from an upstream server");
				break;
			case VALIDATED:
				Log.d(TAG,
						"The response was generated from the cache after validating "
								+ "the entry with the origin server");
				break;
			}
			String cacheWarning = null;
			
			if (Darwin.logDebugEnabled) {
				Header[] headers = response.getAllHeaders();
				if (headers != null && headers.length > 0) {
					StringBuilder sb = new StringBuilder("ServerHeaders:");
					for (Header header : headers) {
						if (HeaderConstants.WARNING.equals(header.getName())) {
							cacheWarning = header.getValue();
						}
						sb.append("\n").append(header.getName()).append("=").append(header.getValue());
					}
					Log.d(TAG, sb.toString());
				}
			}
			
			// Detect redirect
			HttpUriRequest currentReq = (HttpUriRequest) httpContext.getAttribute( 
	                ExecutionContext.HTTP_REQUEST);
	        HttpHost currentHost = (HttpHost)  httpContext.getAttribute( 
	                ExecutionContext.HTTP_TARGET_HOST);
	        String currentUrl = (currentReq.getURI().isAbsolute()) ? currentReq.getURI().toString() 
	        		: (currentHost.toURI() + currentReq.getURI());
	        if ( Darwin.logDebugEnabled) {
	        	Log.d( TAG, "currentUrl: " + currentUrl );
	        }
	        metaData.putString(IMetaData.LOCATION, currentUrl);
			
			entity = response.getEntity();
			if (entity == null || entity.getContentLength() == 0) {
				ResponseReceiver.sendError(resultReceiver, ErrorCode.EMPTY_ENTITY, metaData);
				Log.w(TAG,
						"Got empty entity for request: " + httpRequest.getURI()
								+ " -> " + statusCode);
				return null;
			}
			result = org.apache.commons.io.IOUtils.toString(entity.getContent());// EntityUtils.toString(entity);
			EntityUtils.consume(entity);

			long elapsed = System.currentTimeMillis() - now;
			Log.d(TAG, "executeHttpRequest: request took " + elapsed + "ms");
			ResponseReceiver.sendResult(resultReceiver, result, cacheWarning, metaData);
			return result;

		} catch (OutOfMemoryError e) {
			Log.e(TAG, "HTTP response to big " + httpRequest.getURI(), e);
			ResponseReceiver.sendError(resultReceiver, ErrorCode.HTTP_PROTOCOL, metaData);
			EntityUtils.consumeQuietly(entity);
			entity = null;
			result = null;
			System.gc();
			return null;
		} catch (ClientProtocolException e) {
			Log.e(TAG, "There was a protocol error calling " + httpRequest.getURI(), e);
			ResponseReceiver.sendError(resultReceiver, ErrorCode.HTTP_PROTOCOL, metaData);
			trackError( e, httpRequest.getURI(), ErrorCode.HTTP_PROTOCOL, null, false );
		} catch (HttpHostConnectException e) {
			Log.w(TAG, "Http host connect error calling " + httpRequest.getURI(),  e);
			ResponseReceiver.sendError(resultReceiver, ErrorCode.CONNECT_ERROR, metaData);
			trackError( e, httpRequest.getURI(), ErrorCode.CONNECT_ERROR, null, false );
		} catch (ConnectTimeoutException e) {
			Log.w(TAG, "Connection timeout calling " + httpRequest.getURI(),  e);
			ResponseReceiver.sendError(resultReceiver, ErrorCode.TIME_OUT, metaData);
			trackError( e, httpRequest.getURI(), ErrorCode.TIME_OUT, null, false );
		} catch (SocketTimeoutException e) {
			Log.w(TAG, "Socket timeout calling " + httpRequest.getURI() , e);
			ResponseReceiver.sendError(resultReceiver, ErrorCode.TIME_OUT, metaData);
			trackError( e, httpRequest.getURI(), ErrorCode.TIME_OUT, null, false );
		} catch (UnknownHostException e) {
			Log.w(TAG, "Unknown host error calling " + httpRequest.getURI(),  e);
			ResponseReceiver.sendError(resultReceiver, ErrorCode.CONNECT_ERROR, metaData);
			trackError( e, httpRequest.getURI(), ErrorCode.CONNECT_ERROR, null, false );
		} catch (IOException e) {
			Log.e(TAG, "IO error calling " + httpRequest.getURI(), e);
			ResponseReceiver.sendError(resultReceiver, ErrorCode.IO, metaData);
			trackError( e, httpRequest.getURI(), ErrorCode.IO, null, false );
		} catch (Exception e) {
			Log.e(TAG, "Anormal exception " + e.getMessage(), e);
			ResponseReceiver.sendError(resultReceiver, ErrorCode.UNKNOWN_ERROR, metaData);
			trackError( e, httpRequest.getURI(), ErrorCode.UNKNOWN_ERROR, null, false);
		}
		
		EntityUtils.consumeQuietly(entity);
			
		return result;
	}
	
	private void trackError(Exception e, URI uri, ErrorCode errorCode,
			String msg, boolean nonFatal) {
		ErrorMonitoring.sendException(e, uri.toString(), errorCode, msg, null, nonFatal);
	}

	public HttpParams getHttpParams( Context context) {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIMEOUT);
		// setProxy( context, httpParameters );
		return httpParameters;
	}

	private static void setAuthentication(DefaultHttpClient httpClient) {
		if (RestContract.USE_AUTHENTICATION) {
			httpClient.getCredentialsProvider()
					.setCredentials(
							new AuthScope(RestContract.REQUEST_HOST,
									AuthScope.ANY_PORT),
							new UsernamePasswordCredentials(
									RestContract.AUTHENTICATION_USER,
									RestContract.AUTHENTICATION_PASS));
		}
	}

	public CookieStore getCookieStore() {
		return cookieStore;
	}

	private boolean checkConnection() {
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}
	
	public void removeEntryFromCache( String url ) {
		
		if(url == null) {
			Log.w(TAG, "REMOVE ENTRY FROM CACHE: URL IS NULL !");
			return;
		}	
		
		Uri uri = RestService.completeUri(Uri.parse(url));
		SchemeRegistry sr = darwinHttpClient.getConnectionManager().getSchemeRegistry();
		Scheme s = sr.getScheme(uri.getScheme());
		uri = uri.buildUpon().authority(uri.getAuthority() + ":" + String.valueOf( s.getDefaultPort())).build();
		
		String newUrl = Uri.decode(uri.toString());
		if ( Darwin.logDebugEnabled) {
			Log.d( TAG, "Removing entry from cache: " + newUrl);
		}
		try {
			cache.removeEntry(newUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	@Override
	public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext httpContext)
				throws HttpException {
				
		boolean secure = "https".equalsIgnoreCase(target.getSchemeName());

		URI uri;
		try {
			uri = new URI(target.getSchemeName() + "://" + target.getHostName());
		} catch (URISyntaxException e) {
			return new HttpRoute(target, null, secure);
		}
		ProxyConfiguration proxConf;
		try {
			proxConf = ProxySettings.getCurrentProxyConfiguration(context, uri);
		} catch (Exception e) {
			return new HttpRoute(target, null, secure);
		}
		
		if ( proxConf.getProxyType() == Type.DIRECT ) {
			Log.d( TAG, "determineRoute: no proxy - using direct connection" );
			return new HttpRoute(target, null, secure);
		}

		String hostIp;
		Matcher m = proxyPattern.matcher(proxConf.getProxyHost().toString());
		if (m.find()) {
			hostIp = m.group(1);
		} else {
			hostIp = proxConf.getProxyIPHost();
		}
		int port = proxConf.getProxyPort();
		if ( TextUtils.isEmpty(hostIp) || port <= 0) {
			return new HttpRoute(target, null, secure);
		}
		
		Log.d(TAG,
				"determineRoute: proxy hostIp = " + hostIp + " port = " + port + " ip = "
						+ proxConf.getProxyIPHost());
		return new HttpRoute(target, null, new HttpHost(hostIp, port), secure);

	}
		
}
