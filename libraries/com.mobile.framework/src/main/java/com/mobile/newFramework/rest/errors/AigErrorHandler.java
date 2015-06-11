package com.mobile.newFramework.rest.errors;

import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.utils.output.Print;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * Created by pcarvalho on 5/22/15.
 */
public class AigErrorHandler implements ErrorHandler {

    @Override
    public Throwable handleError(RetrofitError cause) {

        AigBaseException serverException;

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
                Print.d("Network error. Cause: " + cause.getMessage());
                jumiaError.setErrorCode(code);
                jumiaError.setKind(RetrofitError.Kind.NETWORK);
                break;
            case CONVERSION:
                // An exception was thrown while (de)serializing a body.
                Print.d("JSON Conversion error: " + cause.getMessage());
                jumiaError.setErrorCode(ErrorCode.ERROR_PARSING_SERVER_DATA);
                jumiaError.setKind(RetrofitError.Kind.CONVERSION);
                break;
            case HTTP:
                // A non-200 HTTP status code was received from the server.
                Print.d("HTTP error: " + cause.getMessage());
                if(statusCode == ErrorCode.SERVER_IN_MAINTENANCE.id){
                    Print.d("HTTP Server in Maintenance error: " + cause.getMessage());
                    jumiaError.setErrorCode(ErrorCode.SERVER_IN_MAINTENANCE);
                } else if(statusCode == ErrorCode.SERVER_OVERLOAD.id){
                    Print.d("HTTP Server Overload error: " + cause.getMessage());
                    jumiaError.setErrorCode(ErrorCode.SERVER_OVERLOAD);
                } else {
                    Print.d("HTTP generic error: " + cause.getMessage());
                    jumiaError.setErrorCode(ErrorCode.HTTP_STATUS);
                }
                jumiaError.setKind(RetrofitError.Kind.HTTP);
                break;
            case UNEXPECTED:
                // An internal error occurred while attempting to runOnHandlerThread a request.
                Print.d("Unknown error with message: " + cause.getMessage());
                jumiaError.setErrorCode(ErrorCode.UNKNOWN_ERROR);
                jumiaError.setKind(RetrofitError.Kind.UNEXPECTED);
                break;
        }

        serverException = new AigBaseException(jumiaError);

//        if(cause.getStackTrace() != null && cause.getStackTrace().length > 0){
//            for (int i = 0; i < cause.getStackTrace().length; i++) {
//                Print.d("JSON Conversion stacktrace: "+cause.getStackTrace()[i].toString());
//            }
//        }
        return serverException;
    }
}
