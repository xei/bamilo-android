//IRemoteservice.java generator
package com.mobile.wear.service;

oneway interface IDataServiceCallback{

 	void dataItemReceived(out Bundle bundle);

 	void dataMapItem(out Bundle bundle);

    void openActivity(out Intent intent);
 }