//package com.bamilo.android.appmodule.bamiloapp.extlibraries.pushwoosh
//
//import android.os.Handler
//import android.support.annotation.MainThread
//import com.pushwoosh.notification.NotificationServiceExtension
//import com.pushwoosh.notification.PushMessage
//
//
//class PushwooshNotificationServiceExtension : NotificationServiceExtension() {
//    public override fun onMessageReceived(message: PushMessage?): Boolean {
//        if (isAppOnForeground) {
//            val mainHandler = Handler(applicationContext!!.mainLooper)
//            mainHandler.post { handlePush(message) }
//
//            return true
//        }
//
//        return false
//    }
//
//    override fun startActivityForPushMessage(message: PushMessage) {
//        super.startActivityForPushMessage(message)
//        handlePush(message)
//    }
//
//    @MainThread
//    private fun handlePush(message: PushMessage?) {
//    }
//}