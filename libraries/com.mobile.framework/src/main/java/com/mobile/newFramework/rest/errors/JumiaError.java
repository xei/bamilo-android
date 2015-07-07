package com.mobile.newFramework.rest.errors;

import com.mobile.newFramework.ErrorCode;

import retrofit.RetrofitError.Kind;

/**
 * Created by pcarvalho on 5/22/15.
 */
public class JumiaError {

    private Kind mKind;
    private String mMessage;
    private ErrorCode mErrorCode;
    private int mCode;

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public ErrorCode getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.mErrorCode = errorCode;
    }

    public Kind getKind() {
        return mKind;
    }

    public void setKind(Kind kind) {
        this.mKind = kind;
    }

    public int getStatusCode() {
        return mCode;
    }

    public void setStatusCode(int mCode) {
        this.mCode = mCode;
    }


//    private static final class JsonKey {
//        static final String MESSAGE = "message";
//        static final String CODE = "code";
//        static final String ERRORS = "errors";
//        static final String LOCATION = "location";
//    }
//
//    public class Errors {
//        @Expose
//        @SerializedName(JsonKey.MESSAGE)
//        String mMessage;
//
//        @Expose
//        @SerializedName(JsonKey.LOCATION)
//        String mLocation;
//
//        public String getMessage() {
//            return this.mMessage;
//        }
//
//        public String getLocation() {
//            return this.mLocation;
//        }
//    }
//
//    @Expose
//    @SerializedName(JsonKey.MESSAGE)
//    String mMessage;
//
//    @Expose
//    @SerializedName(JsonKey.CODE)
//    String mCode;
//
//    @Expose
//    @SerializedName(JsonKey.ERRORS)
//    List<Errors> mErrorsList;
//
//    public String getMessage() {
//        return this.mMessage;
//    }
//
//    public String getCode() {
//        return this.mCode;
//    }
//
//    public List<Errors> getErrorsList() {
//        return this.mErrorsList;
//    }
//
//    public static String parseErrorsList(final List<Errors> errors) {
//        final StringBuilder builder = new StringBuilder("");
//        for (final Errors err : errors) {
//            builder.append("Location: ")
//                    .append(err.getLocation())
//                    .append(" || Error: ")
//                    .append(err.getMessage())
//                    .append("\n\n");
//        }
//        return builder.toString();
//    }



}
