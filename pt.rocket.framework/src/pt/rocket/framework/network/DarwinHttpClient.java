package pt.rocket.framework.network;

import java.io.IOException;
import java.net.SocketTimeoutException;

import android.text.TextUtils;
import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HeaderElement;
import ch.boye.httpclientandroidlib.HttpEntity;
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
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.CredentialsProvider;
import ch.boye.httpclientandroidlib.client.HttpRequestRetryHandler;
import ch.boye.httpclientandroidlib.client.cache.HeaderConstants;
import ch.boye.httpclientandroidlib.client.entity.GzipDecompressingEntity;
import ch.boye.httpclientandroidlib.client.params.ClientPNames;
import ch.boye.httpclientandroidlib.client.protocol.ClientContext;
import ch.boye.httpclientandroidlib.conn.ConnectTimeoutException;
import ch.boye.httpclientandroidlib.impl.auth.BasicScheme;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.conn.PoolingClientConnectionManager;
import ch.boye.httpclientandroidlib.message.BasicHeaderElement;
import ch.boye.httpclientandroidlib.message.BufferedHeader;
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
        if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
            log.enableError(true);
            log.enableWarn(false);
            log.enableInfo(false);
            log.enableDebug(false);
            log.enableTrace(false);
        }
        getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

        addRequestInterceptor(preemptiveAuth, 0);
        if (ConfigurationConstants.RETRY > 0)
            setHttpRequestRetryHandler(retryHandler);
        addRequestInterceptor(new HttpRequestInterceptor() {

            public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
                Header[] allHeaders = request.getAllHeaders();

                // Add header to accept gzip content
                if (ConfigurationConstants.ACTIVATE_GZIP) {
                    if (!request.containsHeader(ConfigurationConstants.HEADER_ACCEPT_ENCODING)) {
                        Log.i(TAG,"Adding GZIP encoding");
                        request.addHeader(ConfigurationConstants.HEADER_ACCEPT_ENCODING, ConfigurationConstants.ENCODING_GZIP);
                    }
                }
                if (allHeaders != null && allHeaders.length > 0) {
                    StringBuilder sb = new StringBuilder("RequestHeader:");
                    for (Header header : allHeaders) {
                        sb.append("\n").append(header);
                    }
                    log.debug(sb);
                }
            }
        });

        addResponseInterceptor(
        /**
         * Intercepter of the http response. The function of the intercepter is
         * to override the cache settings in the
         * 
         */
        new HttpResponseInterceptor() {

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

                if (ConfigurationConstants.FORCE_CACHE) {
                    // TODO implement configuration uppon caching request
                    HttpRequest request = (HttpRequest) context.getAttribute("http.request");
                }

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
                            HeaderElement newElement = new BasicHeaderElement(HeaderConstants.CACHE_CONTROL_MAX_AGE, newMaxAge);
                            appendToBuffer(buffer, newElement.getName(), newElement.getValue(), delim);
                            delim = ", ";
                            continue;
                        }

                        if (maxAge > ConfigurationConstants.CACHE_CONTROL_MAX_AGE_RESTRICTION) {
                            if (ConfigurationConstants.LOG_DEBUG_ENABLED) {
                                Log.d(TAG, "rewrite header: detected maxAge = " + maxAge + " above = "
                                        + ConfigurationConstants.CACHE_CONTROL_MAX_AGE_RESTRICTION);
                            }
                            String newMaxAge = calcMaxAge(context);
                            HeaderElement newElement = new BasicHeaderElement(HeaderConstants.CACHE_CONTROL_MAX_AGE, newMaxAge);
                            appendToBuffer(buffer, newElement.getName(), newElement.getValue(), delim);
                            delim = ", ";
                            headerWasRewritten = true;
                        }
                    }

                    if (headerWasRewritten) {
                        BufferedHeader rewrittenHeader = new BufferedHeader(buffer);
                        response.setHeader(rewrittenHeader);
                        Log.d(TAG, "process: cache-control header was rewritten");
                        Log.d(TAG, "new content: " + rewrittenHeader.toString());
                    }
                }

                if (ConfigurationConstants.ACTIVATE_GZIP) {
                    HttpEntity entity = response.getEntity();
                    Header ceheader = entity.getContentEncoding();
                    if (ceheader != null) {
                        HeaderElement[] codecs = ceheader.getElements();
                        for (int i = 0; i < codecs.length; i++) {
                            if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                                response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                                return;
                            }
                        }
                    }
                }
            }

            /**
             * Calculates the max age of the request
             * 
             * @param context
             * @return
             */
            private String calcMaxAge(HttpContext context) {
                HttpRequest request = (HttpRequest) context.getAttribute("http.request");
                if (request == null) {
                    return String.valueOf(ConfigurationConstants.MIN_CACHE_TIME);
                }

                RequestLine requestLine = request.getRequestLine();
                if (TextUtils.isEmpty(requestLine.getUri())) {
                    return String.valueOf(ConfigurationConstants.MIN_CACHE_TIME);
                }

                return String.valueOf(ConfigurationConstants.MIN_CACHE_TIME);

            }

        });

    }

    HttpRequestInterceptor preemptiveAuth = new HttpRequestInterceptor() {
        public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
            AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
            CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
            HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);

            if (authState.getAuthScheme() == null) {
                AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
                Credentials creds = credsProvider.getCredentials(authScope);
                if (creds != null) {
                	authState.update(new BasicScheme(), creds);
                    //authState.setAuthScheme(new BasicScheme());
                    //authState.setCredentials(creds);
                }
            }
        }
    };

    HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {

        @Override
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            // TODO Auto-generated method stub
            Log.i(TAG, " Retry number => " + executionCount);
            if (executionCount >= ConfigurationConstants.RETRY) { // Retry 3
                                                                  // times
                return false;
            }
            if (exception instanceof SocketTimeoutException) {
                exception.printStackTrace();
                return true;
            } else if (exception instanceof ClientProtocolException) {
                exception.printStackTrace();
                return true;
            } else if (exception instanceof ConnectTimeoutException) {
                exception.printStackTrace();
                return true;
            }
            return false;
        }
    };
    
//    @Deprecated
//    private SSLSocketFactory newSslSocketFactory(Context context) {
//        try {
//            // Get an instance of the Bouncy Castle KeyStore format
//            KeyStore trusted = KeyStore.getInstance("BKS");
//            InputStream in = null;
//            // Get the raw resource, which contains the keystore with
//            // your trusted certificates (root and any intermediate certs)
//            //TODO restore previous implementation
////            InputStream in = context.getResources().openRawResource(R.raw.mykeystore);
//            try {
//                // Initialize the keystore with the provided trusted certificates
//                // Also provide the password of the keystore
//                trusted.load(in, "my_password".toCharArray());
//            } finally {
//                in.close();
//            }
//            // Pass the keystore to the SSLSocketFactory. The factory is responsible
//            // for the verification of the server certificate.
//            SSLSocketFactory sf = new SSLSocketFactory(trusted);
//            // Hostname verification from certificate
//            // http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
//            sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
//            return sf;
//        } catch (Exception e) {
//            throw new AssertionError(e);
//        }
//    }
}
