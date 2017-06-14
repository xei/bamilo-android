package com.mobile.extlibraries.emarsys;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arash Hassanpour on 4/5/2017.
 */

public enum HTTPStatusCode {

    CREATED(201),                      //Created
    SUCCESSFUL(202),                   //User data is successfully updated
    WRONG_INPUT_OR_MISSING_PARAM(400), //Wrong input or missing mandatory parameter
    UNAUTHORIZED(401),                 //Invalid HTTP Basic Authentication
    ALREADY_EXISTS(409),               //Hardware ID already exists
    DATABASE_ERROR(500),               //Database error (Everything else)
    INTERNAL_SERVER_ERROR(501)         //Internal server error
    ;

    private int value;
    private static Map map = new HashMap<>();

    public int getValue(){
        return this.value;
    }
    HTTPStatusCode(int action){
        this.value = action;
    }

    static {
        for (HTTPStatusCode pageType : HTTPStatusCode.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static HTTPStatusCode valueOf(int pageType) {
        return (HTTPStatusCode)map.get(pageType);
    }


}
