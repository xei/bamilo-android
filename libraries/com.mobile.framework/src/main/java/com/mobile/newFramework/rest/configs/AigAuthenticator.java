package com.mobile.newFramework.rest.configs;

import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.Proxy;

/**
 * This AigAuthenticator is used to set the credentials when the site redirects from one protocol to the other preventing the request from failing due to
 * authentication issues
 */
public class AigAuthenticator implements com.squareup.okhttp.Authenticator {

    String credentials;

    public AigAuthenticator() {
        this.credentials = Credentials.basic(AigRestContract.AUTHENTICATION_USER, AigRestContract.AUTHENTICATION_PASS);
    }

    @Override
    public Request authenticate(Proxy proxy, Response response) throws IOException {
        return response
                .request()
                .newBuilder()
                .header(AigConfigurations.HEADER_API_AUTHORIZATION, credentials)
                .build();
    }

    @Override
    public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
        return null;
    }
}
