package com.bamilo.android.appmodule.modernbamilo.pushnotification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.webengage.sdk.android.WebEngage

class PushNotificationService : FirebaseMessagingService() {
    override fun onNewToken(newToken: String?) {
        super.onNewToken(newToken)

        WebEngage.get().setRegistrationID(newToken)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        remoteMessage?.data?.let {
            if (it.containsKey("source") && it["source"] == "webengage") {
                WebEngage.get().receive(it)
            }
        }

    }
}
