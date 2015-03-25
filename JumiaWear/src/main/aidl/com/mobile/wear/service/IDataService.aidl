//IRemoteservice.java generator
package com.mobile.wear.service;

import com.mobile.wear.service.IDataServiceCallback;

interface IDataService{

    void registerCallback(IDataServiceCallback cb);

    void unregisterCallback(IDataServiceCallback cb);
}