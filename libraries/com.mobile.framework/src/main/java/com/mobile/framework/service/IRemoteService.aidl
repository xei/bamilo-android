//IRemoteservice.java generator
package com.mobile.framework.service;

import com.mobile.framework.service.IRemoteServiceCallback;

interface IRemoteService{

	void sendRequest(in Bundle bundle);
	
	void registerCallback(IRemoteServiceCallback cb);
	
	void unregisterCallback(IRemoteServiceCallback cb);

}