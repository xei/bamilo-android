package com.bamilo.android.framework.service.rest.errors;


import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * AIG error handler
 * @author pcarvalho
 */
public class AigErrorHandler implements ErrorHandler {

    private static final String TAG = AigErrorHandler.class.getSimpleName();

    /**
     * Error Code Normalizer is used to bypass the validation in DebugMobileApiModel Request implementation
     * that retrieves an Exception if the error code is smaller then 200
     */
    public static final int ERROR_CODE_NORMALIZER = 600;

    @Override
    public Throwable handleError(RetrofitError cause) {

        AigError aigError = new AigError();

        int statusCode = -1;
        if(cause.getResponse() != null){
            statusCode = cause.getResponse().getStatus();
        }
        aigError.setMessage(cause.getMessage());

        switch (cause.getKind()) {
            case NETWORK:
                // Validate cause
                int code = cause.getCause() instanceof NoConnectivityException ? ErrorCode.NO_CONNECTIVITY : ErrorCode.CONNECT_ERROR;
                // handle an IOException occurred while communicating to the server.
                aigError.setCode(code);
                break;
            case CONVERSION:
                // An exception was thrown while (de)serializing a body.
                aigError.setCode(ErrorCode.ERROR_PARSING_SERVER_DATA);
                break;
            case HTTP:
                // A non-200 HTTP status code was received from the server.
                if(statusCode == ErrorCode.SERVER_IN_MAINTENANCE){
                    aigError.setCode(ErrorCode.SERVER_IN_MAINTENANCE);
                } else if(statusCode == ErrorCode.SERVER_OVERLOAD){
                    aigError.setCode(ErrorCode.SERVER_OVERLOAD);
                } else if(statusCode < ERROR_CODE_NORMALIZER) {
                    aigError.setCode(ErrorCode.HTTP_STATUS);
                } else { // Case DebugMobileApiModel usage
                    aigError.setCode(statusCode - ERROR_CODE_NORMALIZER);
                }
                break;
            case UNEXPECTED:
                // An internal error occurred while attempting to runOnHandlerThread a request.
                aigError.setCode(ErrorCode.UNKNOWN_ERROR);
                break;
        }

        return new AigBaseException(aigError);
    }
}
