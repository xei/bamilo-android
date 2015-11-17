package com.mobile.newFramework.rest.errors;

import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.utils.output.Print;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * Created by pcarvalho on 5/22/15.
 */
public class AigErrorHandler implements ErrorHandler {


    private static final String TAG = AigErrorHandler.class.getSimpleName();

    @Override
    public Throwable handleError(RetrofitError cause) {

        JumiaError  jumiaError = new JumiaError();

        int statusCode = -1;
        if(cause.getResponse() != null){
            statusCode = cause.getResponse().getStatus();
        }
        //jumiaError.setStatusCode(statusCode);
        jumiaError.setMessage(cause.getMessage());

        switch (cause.getKind()) {
            case NETWORK:
                // Validate cause
                int code = cause.getCause() instanceof NoConnectivityException ? ErrorCode.NO_NETWORK : ErrorCode.CONNECT_ERROR;
                // handle an IOException occurred while communicating to the server.
                Print.w(TAG, "NETWORK ERROR: " + cause.getMessage());
                jumiaError.setCode(code);
                jumiaError.setKind(RetrofitError.Kind.NETWORK);
                break;
            case CONVERSION:
                // An exception was thrown while (de)serializing a body.
                Print.w(TAG, "JSON CONVERSION ERROR", cause.getCause());
                jumiaError.setCode(ErrorCode.ERROR_PARSING_SERVER_DATA);
                jumiaError.setKind(RetrofitError.Kind.CONVERSION);
                break;
            case HTTP:
                // A non-200 HTTP status code was received from the server.
                if(statusCode == ErrorCode.SERVER_IN_MAINTENANCE){
                    Print.w(TAG, "HTTP SERVER IN MAINTENANCE ERROR: " + cause.getMessage());
                    jumiaError.setCode(ErrorCode.SERVER_IN_MAINTENANCE);
                } else if(statusCode == ErrorCode.SERVER_OVERLOAD){
                    Print.w(TAG, "HTTP SERVER OVERLOAD ERROR: " + cause.getMessage());
                    jumiaError.setCode(ErrorCode.SERVER_OVERLOAD);
                } else {
                    Print.w(TAG, "HTTP STATUS ERROR: " + cause.getMessage());
                    jumiaError.setCode(ErrorCode.HTTP_STATUS);
                }
                jumiaError.setKind(RetrofitError.Kind.HTTP);
                break;
            case UNEXPECTED:
                // An internal error occurred while attempting to runOnHandlerThread a request.
                Print.w(TAG, "UNEXPECTED ERROR: " + cause.getMessage());
                jumiaError.setCode(ErrorCode.UNKNOWN_ERROR);
                jumiaError.setKind(RetrofitError.Kind.UNEXPECTED);
                break;
        }

        return new AigBaseException(jumiaError);
    }
}
