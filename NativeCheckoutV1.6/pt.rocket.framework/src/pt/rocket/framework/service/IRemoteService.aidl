//IRemoteservice.java generator
package pt.rocket.framework.service;

import pt.rocket.framework.service.IRemoteServiceCallback;

interface IRemoteService{

	void sendRequest(in Bundle bundle);
	
	void registerCallback(IRemoteServiceCallback cb);
	
	void unregisterCallback(IRemoteServiceCallback cb);

}