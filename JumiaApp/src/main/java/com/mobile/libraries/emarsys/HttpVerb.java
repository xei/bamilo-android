package com.mobile.libraries.emarsys;

/**
 * Created by Arash Hassanpour on 4/5/2017.
 */
public enum HttpVerb {
    HttpVerbPOST("POST"),
    HttpVerbGET("GET"),
    HttpVerbPUT("PUT"),
    HttpVerbDELETE("DELETE");

    private String value;

    public String getValue(){
        return this.value;
    }
    private HttpVerb(String action){
        this.value = action;
    }
}
