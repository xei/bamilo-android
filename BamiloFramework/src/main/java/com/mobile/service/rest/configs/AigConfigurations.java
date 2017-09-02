package com.mobile.service.rest.configs;

public class AigConfigurations {

    /**
     * Max cache size
     */
    public static final int CACHE_MAX_SIZE = 10 * 1024 * 1024; // 10 Megabytes of cache

//    /**
//     * Retry same request allowed - if value is set to 0 then the system does not provide the usage of the retry handler
//     */
//    public static final int RETRY = 3;

    /**
     * ---- API Auth ---- These fields are not final because they are obfuscated by dexguard and being final decreases the access performance
     */
    public static final String HEADER_API_AUTHORIZATION = "Authorization";

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
