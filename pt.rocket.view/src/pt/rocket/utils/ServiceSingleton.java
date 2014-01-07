package pt.rocket.utils;

import java.util.HashMap;

import pt.rocket.framework.service.IRemoteService;
import pt.rocket.interfaces.IResponseCallback;

/**
 * Service singelton in order to access the service object in the needed places
 * 
 * @author josedourado
 * 
 */
public class ServiceSingleton {

    private static ServiceSingleton serviceSingleton;
    private IRemoteService mService;
    public static HashMap<String, IResponseCallback> responseCallbacks;

    public static ServiceSingleton getInstance() {
        if (serviceSingleton == null) {
            serviceSingleton = new ServiceSingleton();
        }
        return serviceSingleton;
    }

    public void setService(IRemoteService service) {
        mService = service;
    }

    public IRemoteService getService() {
        return mService;
    }

    public void setResponseCallbacks(HashMap<String, IResponseCallback> responseMap) {
        responseCallbacks = responseMap;
    }
    
    public HashMap<String, IResponseCallback> getResponseCallbacks(){
        return responseCallbacks;
    }

}
