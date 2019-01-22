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

fun getConnectionType(context: Context)
        = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo.type

fun getConnectionSubType(context: Context)
        = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo.subtypeName

fun getNetworkOperatorName(context: Context) =
        (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperatorName

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
    val wm = context.getSystemService(WIFI_SERVICE) as WifiManager?
    return wm!!.connectionInfo.ipAddress.toString()
}