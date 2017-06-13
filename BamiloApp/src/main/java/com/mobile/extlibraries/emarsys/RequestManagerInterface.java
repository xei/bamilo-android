package com.mobile.extlibraries.emarsys;

import java.util.Map;

/**
 * Created by Arash Hassanpour on 4/8/2017.
 */

public interface RequestManagerInterface {


    //typedef void(^RequestCompletion)(int statusCode, id data, NSArray* errorMessages);


    //-(instancetype) initWithBaseUrl:(NSString *)baseUrl;

    void asyncGET(String path, Map<String, Object> params, RequestExecutionType type, RequestCompletion completion);
    void asyncPOST(String path, Map<String, Object> params, RequestExecutionType type, RequestCompletion completion);
    void asyncPUT(String path, Map<String, Object> params, RequestExecutionType type, RequestCompletion completion);
    void asyncDELETE(String path, Map<String, Object> params, RequestExecutionType type, RequestCompletion completion);
    void asyncRequest(HttpVerb method, String path, Map<String, Object> params, RequestExecutionType type, RequestCompletion completion);

}
