//IRemoteservice.java generator
package pt.rocket.framework.service;

oneway interface IRemoteServiceCallback{

	void getResponse(out Bundle bundle);
	
	void getError(out Bundle bundle);

}