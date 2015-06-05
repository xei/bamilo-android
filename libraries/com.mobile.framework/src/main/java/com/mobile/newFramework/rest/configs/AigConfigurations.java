package com.mobile.newFramework.rest.configs;

public class AigConfigurations {

    /**
     * Max cache size
     */
    public static final int CACHE_MAX_SIZE = 10 * 1024 * 1024; // 10 Megabytes of cache

    /**
     * Retry same request allowed - if value is set to 0 then the system does not provide the usage of the retry handler
     */
    public static final int RETRY = 3;

    /**
     * ---- API Auth ---- These fields are not final because they are obfuscated by dexguard and being final decreases the access performance
     */
    public static final String HEADER_API_AUTHORIZATION = "Authorization";

    public static final String AUTHENTICATION_USER = "rocket";
    public static final String AUTHENTICATION_PASS = "z7euN7qfRD769BP";

    /**
     * Header Domain Profile
     */
    public static final String HEADER_DOMAIN_PROFILE = "X-DOMAIN-PROFILE";


    /**
     * Header User token
     */
    public static final String HEADER_USER_TOKEN = "X-USER-TOKEN";

    /**
     * Header Accept-Language
     */
    public static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";

    /**
     * Header If-None-Match
     */
    public static final String HEADER_IF_NONE_MATCH = "If-None-Match";

    /**
     * Header ETag
     */
    public static final String HEADER_ETAG = "ETag";

    /**
     * Header Expires
     */
    public static final String HEADER_EXPIRES = "Expires";

    /**
     * Header Cache
     */
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_NO_CACHE_VALUE = "no-cache, no-store, must-revalidate";
    public static final String HEADER_CACHE_CONTROL_MAX_AGE = "max-age";
    public static final String HEADER_CACHE_CONTROL_REVALIDATE = "must-revalidate";

    /**
     * Header Content-Type
     */
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String DEFAULT_REQUEST_CONTENT_TYPE = "text/plain; charset=UTF-8";

    /**
     * the number of seconds we should wait before timing out the connection
     */
    private static final int TIME_OUT_SECONDS = 20;

    /**
     * Sets the timeout until a connection is established. A value of zero means the timeout is not used. The default value is zero.
     */
    public static final int CONNECTION_TIMEOUT = TIME_OUT_SECONDS * 1000; // ms

    /**
     * Sets the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data. A timeout value of zero is interpreted as an
     * infinite timeout. This value is used when no socket timeout is set in the method parameters.
     */
    public static final int SOCKET_TIMEOUT = TIME_OUT_SECONDS * 1000;
}
