package pt.rocket.testapp.singelton;

import pt.rocket.framework.service.IRemoteService;

/**
 * Service singelton in order to access the service object in the needed places
 * @author josedourado
 *
 */
public class ServiceSingelton {
    
    private static ServiceSingelton serviceSingelton;
    private IRemoteService mService;
    
    
    public static ServiceSingelton getInstance(){
        if(serviceSingelton == null){
            serviceSingelton = new ServiceSingelton();
        }
        return serviceSingelton;
    }
    
    public void setService(IRemoteService service){
        mService = service;
    }
    
    public IRemoteService getService(){
        return mService;
    }
    

}
