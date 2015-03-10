package com.mobile.wear.application;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.mobile.wear.receiver.JumiaDataReceiverService;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by pcarvalho on 3/2/15.
 */
public class CustomApplication extends Application {

    public static CustomApplication INSTANCE;
    private String nodeId;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TAG", "code1wear onCreate");
        INSTANCE = this;

        JumiaDataReceiverService.getInstance();
        JumiaDataReceiverService.connectToWearable();
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeId() {
        if (this.nodeId == null)
            retrieveDeviceNode();

        return this.nodeId;
    }

    private void retrieveDeviceNode() {
        mGoogleApiClient = getGoogleApiClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mGoogleApiClient.blockingConnect(1000, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    setNodeId(nodes.get(0).getId());
                }
                //mGoogleApiClient.disconnect();
            }
        }).start();
    }

    public void setGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        if (mGoogleApiClient == null) {
            this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .build();
        } else {
            this.mGoogleApiClient = mGoogleApiClient;
        }
    }

    public GoogleApiClient getGoogleApiClient() {
        if (mGoogleApiClient == null) {
            setGoogleApiClient(null);
        }

        return mGoogleApiClient;
    }
}
