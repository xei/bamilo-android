package com.mobile.framework.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.mobile.framework.Darwin;
import com.mobile.framework.rest.RestContract;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.worker.RequestWorker;

import de.akquinet.android.androlog.Log;

public class RemoteService extends Service {
    
    private static final String TAG=RemoteService.class.getSimpleName();
    public static RemoteCallbackList<IRemoteServiceCallback> mCallbacks = new RemoteCallbackList<IRemoteServiceCallback>();
    //Thread pool manager
    private TestThreadPoolExecutor tpe;
    private final int INIT_NUMBER_OF_THREADS = 5;
    private final int MAX_NUMBER_OF_THREADS = 6;
    private static boolean bound=false;



    @Override
    public void onCreate(){
        super.onCreate();
        tpe = new TestThreadPoolExecutor(INIT_NUMBER_OF_THREADS, MAX_NUMBER_OF_THREADS, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        //TODO decide the best place for this to be called
//        TrackerManager.googleTrackingInit(getApplicationContext());
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        super.onStartCommand(intent, flags, startId);
        
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        bound = true;
        return mBinder;
    }
    

    @Override
    public boolean onUnbind(Intent intent) {
        bound = false;
        return true; // ensures onRebind is called
    }
    
    @Override
    public void onRebind(Intent intent) {
        bound = true;
    }
    
    private final IRemoteService.Stub mBinder = new IRemoteService.Stub() {
        
        @Override
        public void sendRequest(Bundle bundle) throws RemoteException {
            // TODO Auto-generated method stub
            RequestWorker request = new RequestWorker(bundle, mHandler, getApplicationContext());
            tpe.execute(request);
            
            Log.i(TAG,"THREAD POOL "+tpe.getActiveCount());
            
            
        }

        @Override
        public void registerCallback(IRemoteServiceCallback cb) throws RemoteException {
            // TODO Auto-generated method stub
            mCallbacks.register(cb);
            
        }

        @Override
        public void unregisterCallback(IRemoteServiceCallback cb) throws RemoteException {
            // TODO Auto-generated method stub
            mCallbacks.unregister(cb);
            
        }
    };
    
    /**
     * This class creates the threadpool executor that manages all the threads
     * @author josedourado
     *
     */
    public class TestThreadPoolExecutor extends ThreadPoolExecutor {
        Set<Runnable> executions = Collections.synchronizedSet(new HashSet<Runnable>());

        public TestThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        public synchronized void execute(Runnable command) {
            if (executions.contains(command)) {
                Log.w(TAG, "Duplicate requests for: " + command);
                return;
            }
            super.execute(command);
            executions.add(command);
        }

        protected synchronized void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            executions.remove(r);
        }
    }

	public static Uri completeUri( Uri uri ) {
		if ( Darwin.logDebugEnabled) {
			Log.d( TAG, "completeUri: uri = " + uri);
		}
		Builder builder = uri.buildUpon();
		
		if (uri.getAuthority() == null) {
			builder.authority(RestContract.REQUEST_HOST).path(RestContract.REST_BASE_PATH + uri.getPath());
			Log.w(TAG, "Url " + uri + " should include authority, authority and base path added");
		}
		
		if(RestContract.USE_ONLY_HTTPS){
			if ( Darwin.logDebugEnabled) {
				Log.d(TAG, "Request type changed to https.");
			}
			builder.scheme("https");
		}
		
		/**
		 * Temporary: Force http for Bamilo.
		 * TODO: Remove me if Bamilo supports https.
		 */
		if(RestContract.USE_ONLY_HTTP) {
		    Log.i(TAG, "BAMILO REQUEST: force http.");
		    builder.scheme("http");
		}
		
		uri = builder.build();
		if ( Darwin.logDebugEnabled) {
			Log.d(TAG, "Rebuilded uri: " + uri);
		}
		return uri;
	}
    
    private static Handler mHandler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		final int N = mCallbacks.beginBroadcast();
    		switch(msg.what){
    		case Constants.SUCCESS:
    			
//    			Log.i(TAG,"Received success response from handler "+msg.getData().getString(Constants.BUNDLE_RESPONSE_KEY));			 
    		        for (int i=0; i<N; i++) {
    		            try {
    		                mCallbacks.getBroadcastItem(i).getResponse(msg.getData());
    		            } catch (RemoteException e) {
    		                // The RemoteCallbackList will take care of removing
    		                // the dead object for us.
    		            	e.printStackTrace();
    		            }
    		        }
    		        mCallbacks.finishBroadcast();
    			break;
    		case Constants.FAILURE:
//    			Log.i(TAG,"Received failed response from handler "+msg.getData().getString(Constants.BUNDLE_RESPONSE_KEY));
		        for (int i=0; i<N; i++) {
		            try {
		                mCallbacks.getBroadcastItem(i).getError(msg.getData());
		            } catch (RemoteException e) {
		                // The RemoteCallbackList will take care of removing
		                // the dead object for us.
		            	e.printStackTrace();
		            }
		        }
		        mCallbacks.finishBroadcast();
    			break;
    		}
    		
    	};
    };
    
    public static boolean isServiceBound(){
        return bound;
    }

}
