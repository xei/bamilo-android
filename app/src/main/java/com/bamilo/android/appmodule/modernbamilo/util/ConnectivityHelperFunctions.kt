package com.bamilo.android.appmodule.modernbamilo.util

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import android.util.Log
import java.net.NetworkInterface
import java.util.*


private const val TAG_DEBUG = "ConnectivityHelper"

fun getConnectionType(context: Context): Int {
    try {
        return (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo.type
    } catch (e: Exception) {
        return 0
    }

}


fun getConnectionSubType(context: Context) : String {
    try {
        return (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo.subtypeName
    } catch (e: Exception) {
        return ""
    }

}

fun getNetworkOperatorName(context: Context) :String {
    try {
        return (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperatorName
    } catch (e: Exception) {
        return ""
    }

}


fun isVpnConnected(): Boolean {
    val networkList = ArrayList<String>()
    try {
        for (networkInterface in Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (networkInterface.isUp)
                networkList.add(networkInterface.name)
        }
    } catch (ex: Exception) {
        Log.d(TAG_DEBUG, "isVpnUsing Network List didn't received")
    }

    return networkList.contains("tun0")
}

fun getIpAddress(context: Context) : String {
    try {
        val wm = context.getSystemService(WIFI_SERVICE) as WifiManager?
        return wm!!.connectionInfo.ipAddress.toString()
    } catch (e: Exception) {
        return ""
    }

}