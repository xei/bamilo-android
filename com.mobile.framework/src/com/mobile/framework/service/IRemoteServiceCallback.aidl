//IRemoteservice.java generator
package com.mobile.framework.service;

oneway interface IRemoteServiceCallback{

	void getResponse(out Bundle bundle);
	
	void getError(out Bundle bundle);

}