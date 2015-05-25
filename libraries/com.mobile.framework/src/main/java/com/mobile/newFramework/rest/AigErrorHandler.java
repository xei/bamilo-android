package com.mobile.newFramework.rest;

import com.mobile.framework.ErrorCode;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * Created by pcarvalho on 5/22/15.
 */
public class AigErrorHandler implements ErrorHandler {

    @Override
    public Throwable handleError(RetrofitError cause) {
        String errorMsg = "";
        AigBaseException serverException;
        System.out.println("CAUSE KIND: " + cause.getKind());
        System.out.println("CAUSE LOCALIZED MESSAGE: " + cause.getLocalizedMessage());
        System.out.println("CAUSE MESSAGE: " + cause.getMessage());
        System.out.println("CAUSE SUCCESS TYPE: " + cause.getSuccessType());
        System.out.println("CAUSE CAUSE: " + cause.getCause());
        System.out.println("CAUSE BODY: " + cause.getBody().toString());
        System.out.println("CAUSE URL: " + cause.getResponse().getUrl());
        System.out.println("CAUSE REASON: " + cause.getResponse().getReason());
        System.out.println("CAUSE BODY MIME: " + cause.getResponse().getBody().mimeType().toString());
        System.out.println("CAUSE STATUS: " + cause.getResponse().getStatus());
        System.out.println("CAUSE HEADER: " + cause.getResponse().getHeaders());


        errorMsg = cause.getMessage();

        JumiaError  jumiaError = new JumiaError();
        int statusCode = cause.getResponse().getStatus();
        jumiaError.setStatusCode(statusCode);
        jumiaError.setMessage(cause.getMessage());
        // priority = true
        // md5 = fasdauibnasfcmbnfwiu1932swh912h238h
        // eventtask = NORMAL_TASK
        // response = ""
        // eventtype = GET_API_INFO
        // error= HTTP_STATUS

        switch (cause.getKind()) {
            case NETWORK:
                // handle an IOException occurred while communicating to the server.
                System.out.println("Network error. Cause: " + cause.getMessage());
                jumiaError.setErrorCode(ErrorCode.NO_NETWORK);
                jumiaError.setKind(RetrofitError.Kind.NETWORK);

                break;
            case CONVERSION:
                // An exception was thrown while (de)serializing a body.
                System.out.println("GSON Conversion error: " + cause.getMessage());
                jumiaError.setErrorCode(ErrorCode.ERROR_PARSING_SERVER_DATA);
                jumiaError.setKind(RetrofitError.Kind.CONVERSION);

                break;
            case HTTP:
                // A non-200 HTTP status code was received from the server.
                System.out.println("HTTP error: " + cause.getMessage());
                if(statusCode == ErrorCode.SERVER_IN_MAINTENANCE.id){
                    System.out.println("HTTP Server in Maintenance error: " + cause.getMessage());
                    jumiaError.setErrorCode(ErrorCode.SERVER_IN_MAINTENANCE);
                } else if(statusCode == ErrorCode.SERVER_OVERLOAD.id){
                    System.out.println("HTTP Server Overload error: " + cause.getMessage());
                    jumiaError.setErrorCode(ErrorCode.SERVER_OVERLOAD);
                } else {
                    System.out.println("HTTP generic error: " + cause.getMessage());
                    jumiaError.setErrorCode(ErrorCode.HTTP_STATUS);
                }
                jumiaError.setKind(RetrofitError.Kind.HTTP);
                break;
            case UNEXPECTED:
                // An internal error occurred while attempting to runOnHandlerThread a request.
                System.out.println("Unknown error with message: " + cause.getMessage());
                jumiaError.setErrorCode(ErrorCode.UNKNOWN_ERROR);
                jumiaError.setKind(RetrofitError.Kind.UNEXPECTED);
                break;
        }

        serverException = new AigBaseException(jumiaError);

        return serverException;
    }
}
