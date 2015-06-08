package com.mobile.newFramework.rest.errors;

import com.mobile.newFramework.ErrorCode;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * Created by pcarvalho on 5/22/15.
 */
public class AigErrorHandler implements ErrorHandler {

    @Override
    public Throwable handleError(RetrofitError cause) {
//        String errorMsg = "";
        AigBaseException serverException;
//        try {
////            System.out.println("CAUSE KIND: " + cause.getKind());
////            System.out.println("CAUSE LOCALIZED MESSAGE: " + cause.getLocalizedMessage());
////            System.out.println("CAUSE MESSAGE: " + cause.getMessage());
////            System.out.println("CAUSE SUCCESS TYPE: " + cause.getSuccessType());
////            System.out.println("CAUSE CAUSE: " + cause.getCause());
////            System.out.println("CAUSE BODY: " + cause.getBody().toString());
////            System.out.println("CAUSE URL: " + cause.getResponse().getUrl());
////            System.out.println("CAUSE REASON: " + cause.getResponse().getReason());
////            System.out.println("CAUSE BODY MIME: " + cause.getResponse().getBody().mimeType().toString());
////            System.out.println("CAUSE STATUS: " + cause.getResponse().getStatus());
////            System.out.println("CAUSE HEADER: " + cause.getResponse().getHeaders());
//        } catch (Exception e){
//            e.printStackTrace();
//        }


        //errorMsg = cause.getMessage();

        JumiaError  jumiaError = new JumiaError();
        int statusCode = -1;
        if(cause.getResponse() != null){
            statusCode = cause.getResponse().getStatus();
        }
        jumiaError.setStatusCode(statusCode);
        jumiaError.setMessage(cause.getMessage());

        switch (cause.getKind()) {
            case NETWORK:
                // Validate cause
                ErrorCode code = cause.getCause() instanceof NoConnectivityException ? ErrorCode.NO_NETWORK : ErrorCode.CONNECT_ERROR;
                // handle an IOException occurred while communicating to the server.
                System.out.println("Network error. Cause: " + cause.getMessage());
                jumiaError.setErrorCode(code);
                jumiaError.setKind(RetrofitError.Kind.NETWORK);
                break;
            case CONVERSION:
                // An exception was thrown while (de)serializing a body.
                System.out.println("JSON Conversion error: " + cause.getMessage());
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

        //TODO temporary for better debugging
        if(cause.getStackTrace() != null && cause.getStackTrace().length > 0){
            for (int i = 0; i < cause.getStackTrace().length; i++) {
                System.out.println("JSON Conversion stacktrace: "+cause.getStackTrace()[i].toString());
            }
        }
        return serverException;
    }
}
