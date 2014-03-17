package com.rocket.framework.testshell.application;

import pt.rocket.framework.rest.RestClientSingleton;
import android.app.Application;

public class ShellApplication extends Application {
	@Override
	  public void onCreate()
	  {
	    super.onCreate();
	     
	    // Initialize the singletons so their instances
	    // are bound to the application process.
	    RestClientSingleton.init(getApplicationContext());
	  }
}
