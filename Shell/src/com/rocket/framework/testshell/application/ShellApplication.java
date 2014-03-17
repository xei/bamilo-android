package com.rocket.framework.testshell.application;

import pt.rocket.framework.rest.RestClientSingleton;
import pt.rocket.framework.rest.RestContract;
import android.app.Application;

public class ShellApplication extends Application {
	@Override
	  public void onCreate()
	  {
	    super.onCreate();
	    
	    RestContract.RUNNING_TESTS = true;
	    // Initialize the singletons so their instances
	    // are bound to the application process.
	    RestClientSingleton.init(getApplicationContext());
	  }
}
