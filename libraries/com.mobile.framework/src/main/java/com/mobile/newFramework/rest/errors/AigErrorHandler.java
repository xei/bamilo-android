package com.mobile.newFramework.rest.errors;

import com.mobile.newFramework.utils.output.Print;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * AIG error handler
 * @author pcarvalho
 */
public class AigErrorHandler implements ErrorHandler {

    private static final String TAG = AigErrorHandler.class.getSimpleName();

    @Override
    public Throwable handleError(RetrofitError cause) {

        AigError aigError = new AigError();

        int statusCode = -1;
        if(cause.getResponse() != null){
            statusCode = cause.getResponse().getStatus();
        }
        //aigError.setStatusCode(statusCode);
        aigError.setMessage(cause.getMessage());

        switch (cause.getKind()) {
            case NETWORK:
                // Validate cause
                int code = cause.getCause() instanceof NoConnectivityException ? ErrorCode.NO_CONNECTIVITY : ErrorCode.CONNECT_ERROR;
                // handle an IOException occurred while communicating to the server.
                Print.w(TAG, "NETWORK ERROR: " + cause.getMessage());
                aigError.setCode(code);
                aigError.setKind(RetrofitError.Kind.NETWORK);
                break;
            case CONVERSION:
                // An exception was thrown while (de)serializing a body.
                Print.w(TAG, "JSON CONVERSION ERROR", cause.getCause());
                aigError.setCode(ErrorCode.ERROR_PARSING_SERVER_DATA);
                aigError.setKind(RetrofitError.Kind.CONVERSION);
                break;
            case HTTP:
                // A non-200 HTTP status code was received from the server.
                if(statusCode == ErrorCode.SERVER_IN_MAINTENANCE){
                    Print.w(TAG, "HTTP SERVER IN MAINTENANCE ERROR: " + cause.getMessage());
                    aigError.setCode(ErrorCode.SERVER_IN_MAINTENANCE);
                } else if(statusCode == ErrorCode.SERVER_OVERLOAD){
                    Print.w(TAG, "HTTP SERVER OVERLOAD ERROR: " + cause.getMessage());
                    aigError.setCode(ErrorCode.SERVER_OVERLOAD);
                } else {
                    Print.w(TAG, "HTTP STATUS ERROR: " + cause.getMessage());
                    aigError.setCode(ErrorCode.HTTP_STATUS);
                }
                aigError.setKind(RetrofitError.Kind.HTTP);
                break;
            case UNEXPECTED:
                // An internal error occurred while attempting to runOnHandlerThread a request.
                Print.w(TAG, "UNEXPECTED ERROR: " + cause.getMessage());
                aigError.setCode(ErrorCode.UNKNOWN_ERROR);
                aigError.setKind(RetrofitError.Kind.UNEXPECTED);
                break;
        }

        return new AigBaseException(aigError);
    }
}
