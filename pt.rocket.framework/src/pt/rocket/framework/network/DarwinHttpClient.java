package pt.rocket.framework.network;

import java.io.IOException;

import pt.rocket.framework.Darwin;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.rest.RestContract;
import android.net.Uri;
import android.text.TextUtils;
import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HeaderElement;
import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpRequestInterceptor;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpResponseInterceptor;
import ch.boye.httpclientandroidlib.RequestLine;
import ch.boye.httpclientandroidlib.auth.AuthScope;
import ch.boye.httpclientandroidlib.auth.AuthState;
import ch.boye.httpclientandroidlib.auth.Credentials;
import ch.boye.httpclientandroidlib.client.CredentialsProvider;
import ch.boye.httpclientandroidlib.client.cache.HeaderConstants;
import ch.boye.httpclientandroidlib.client.params.ClientPNames;
import ch.boye.httpclientandroidlib.client.protocol.ClientContext;
import ch.boye.httpclientandroidlib.impl.auth.BasicScheme;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.conn.PoolingClientConnectionManager;
import ch.boye.httpclientandroidlib.message.BasicHeaderElement;
import ch.boye.httpclientandroidlib.message.BufferedHeader;
import ch.boye.httpclientandroidlib.params.CoreConnectionPNames;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.protocol.ExecutionContext;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import ch.boye.httpclientandroidlib.util.CharArrayBuffer;
import de.akquinet.android.androlog.Log;

/**
 * Defines the the certificated enabled http client used to communicate with the
 * remote mobile api.
 * 
 * @author Daniel Rindt, GuilhermeSilva
 * 
 */
public class DarwinHttpClient extends DefaultHttpClient {

	private static final String TAG = DarwinHttpClient.class.getSimpleName();

	/**
	 * 
	 */
	private static final int TIMEOUT = 10000;

	// private static final int CACHE_CONTROL_MAX_AGE_RESTRICTION = 12 * 60 *
	// 60;

	private static final int CACHE_CONTROL_MAX_AGE_RESTRICTION = 24 * 60 * 60;


	/**
	 * Darwin HttpClient param constructor.
	 * 
	 * @param keyStore
	 *            the Certificate of the remote server.
	 * @param keyStorePassword
	 *            the Vertificate key.
	 * @param params
	 *            the HttpClient params.
	 */
	public DarwinHttpClient(HttpParams params) {
		super(new PoolingClientConnectionManager(), params);
		log = new LazHttpClientAndroidLog("DefaultHttpClient");
		if (Darwin.logDebugEnabled) {
			log.enableError(true);
			log.enableWarn(false);
			log.enableInfo(false);
			log.enableDebug(false);
			log.enableTrace(false);
		}
		getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, TIMEOUT);
		addRequestInterceptor(preemptiveAuth, 0);
		addRequestInterceptor(new HttpRequestInterceptor() {

			public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
				Header[] allHeaders = request.getAllHeaders();
				if (allHeaders != null && allHeaders.length > 0) {
					StringBuilder sb = new StringBuilder("RequestHeader:");
					for (Header header : allHeaders) {
						sb.append("\n").append(header);
					}
					log.debug(sb);
				}
			}
		});

		addResponseInterceptor(new HttpResponseInterceptor() {

			private void appendToBuffer(CharArrayBuffer buffer, String name, String value, String delim) {
				buffer.append(delim);
				buffer.append(name);
				if (!TextUtils.isEmpty(value)) {
					buffer.append("=");
					buffer.append(value);
				}
			}

			@Override
			public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {

				Header[] headers = response.getHeaders(HeaderConstants.CACHE_CONTROL);
				if (headers == null || headers.length == 0) {
					return;
				}

				for (Header header : headers) {
					HeaderElement[] elements = header.getElements();
					if (elements == null || headers.length == 0) {
						continue;
					}

					int idx;
					CharArrayBuffer buffer = new CharArrayBuffer(1024);
					buffer.append(HeaderConstants.CACHE_CONTROL);
					buffer.append(": ");
					String delim = "";
					boolean headerWasRewritten = false;
					for (idx = 0; idx < elements.length; idx++) {
						HeaderElement element = elements[idx];
						// Log.d(TAG, "element: " + element + " value = " +
						// element.getValue());
						if (element == null) {
							continue;
						}

						if (!element.getName().equalsIgnoreCase(HeaderConstants.CACHE_CONTROL_MAX_AGE)) {
							appendToBuffer(buffer, element.getName(), element.getValue(), delim);
							delim = ", ";
							continue;
						}

						int maxAge = 0;
						try {
							Log.d(TAG, "header element: value = " + element.getValue());
							maxAge = Integer.parseInt(element.getValue());
						} catch (NumberFormatException e) {
							Log.e("max-age content could not be parsed - ignored);");
							String newMaxAge = calcMaxAge(context);
							HeaderElement newElement = new BasicHeaderElement(HeaderConstants.CACHE_CONTROL_MAX_AGE,
									newMaxAge);
							appendToBuffer(buffer, newElement.getName(), newElement.getValue(), delim);
							delim = ", ";
							continue;
						}

						if (maxAge > CACHE_CONTROL_MAX_AGE_RESTRICTION) {
							if ( Darwin.logDebugEnabled) {
								Log.d( TAG, "rewrite header: detected maxAge = " + maxAge + " above = " + CACHE_CONTROL_MAX_AGE_RESTRICTION );
							}
							String newMaxAge = calcMaxAge(context);
							HeaderElement newElement = new BasicHeaderElement(HeaderConstants.CACHE_CONTROL_MAX_AGE,
									newMaxAge);
							appendToBuffer(buffer, newElement.getName(), newElement.getValue(), delim);
							delim = ", ";
							headerWasRewritten = true;
						}
					}

					if ( headerWasRewritten ) {
						BufferedHeader rewrittenHeader = new BufferedHeader(buffer);
						response.setHeader(rewrittenHeader);
						Log.d( TAG, "process: cache-control header was rewritten" );
						Log.d( TAG, "new content: " + rewrittenHeader.toString());
					}
				}
			}

			private String calcMaxAge(HttpContext context) {
				HttpRequest request = (HttpRequest) context.getAttribute("http.request");
				if (request == null) {
					return String.valueOf(RestContract.MIN_CACHE_TIME);
				}

				RequestLine requestLine = request.getRequestLine();
				if (TextUtils.isEmpty(requestLine.getUri())) {
					return String.valueOf(RestContract.MIN_CACHE_TIME);
				}

				EventType[] eventList = new EventType[] { EventType.GET_SHOPPING_CART_ITEMS_EVENT,
						EventType.GET_API_INFO, EventType.GET_PRODUCT_EVENT, EventType.GET_CUSTOMER };
				for (EventType event : eventList) {
					Log.d( TAG, "calcMaxAge: action = " + event.action);
					if (event.action == null) {
						return  String.valueOf( event.cacheTime );
					}
					Uri uri = Uri.parse(event.action);
					if (requestLine.getUri().startsWith(RestContract.REST_BASE_PATH + uri.getPath())) {
						return  String.valueOf( event.cacheTime );
					}
				}
				return String.valueOf(RestContract.DEFAULT_CACHE_TIME);
			}
		});

	}

	HttpRequestInterceptor preemptiveAuth = new HttpRequestInterceptor() {
		public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
			AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
			CredentialsProvider credsProvider = (CredentialsProvider) context
					.getAttribute(ClientContext.CREDS_PROVIDER);
			HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);

			if (authState.getAuthScheme() == null) {
				AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
				Credentials creds = credsProvider.getCredentials(authScope);
				if (creds != null) {
					authState.setAuthScheme(new BasicScheme());
					authState.setCredentials(creds);
				}
			}
		}
	};
}
