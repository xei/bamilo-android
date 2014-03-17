package pt.rocket.framework.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.http.conn.util.InetAddressUtils;

import pt.rocket.framework.Darwin;
import pt.rocket.framework.ErrorCode;
import android.text.TextUtils;

import com.bugsense.trace.BugSenseHandler;

import de.akquinet.android.androlog.Log;

public class ErrorMonitoring {
	
	private final static String TAG = ErrorMonitoring.class.getSimpleName();

	private static SortedMap<String, String> map = new TreeMap<String, String>();
	
	private static  void buildErrorMap(String uri, ErrorCode errorCode, Exception exception, String msg ) {
		map.clear();
		map.put( "Country", ShopSelector.getCountryName());
		map.put( "Uri", uri);
		if (errorCode != null && errorCode.name() != null) {
			map.put( "ErrorCode", errorCode.name());
		}
		map.put( "IPv4-Address",  getIPAddress(true));
		map.put( "IPv6-Address", getIPAddress(false));
		if (!TextUtils.isEmpty(msg)) {
			map.put( "Message",  msg );
		}		
		
		map.put( "Timestamp", SimpleDateFormat.getInstance().format(new Date()));
		map.put( "VersionCode", String.valueOf( Darwin.getVersionCode()));
		map.put( "Exception Message", exception.getMessage());
	}

	public static void sendException(Exception e, String uri, ErrorCode errorCode, String msg, String msgTwo, boolean nonFatal) {
		Log.d(TAG, "sendException: sending exception for uri = " + uri + " with errorCode = " + errorCode );
		buildErrorMap(uri, errorCode, e, msg);
		/*
		StringBuilder sb = new StringBuilder();
		for(Entry<String, String> entry: map.entrySet()) {
			sb.append( entry.getKey()).append( ": ").append(entry.getValue()).append(" ");
		}
		AnalyticsGoogle.get().sendException(sb.toString(), e, nonFatal);
		*/
		
		HashMap<String, String> hMap = new HashMap<String, String>( map );
		if ( !TextUtils.isEmpty( msgTwo )) {
			hMap.put( "Content", msgTwo);
		}
		try {
			BugSenseHandler.sendExceptionMap(hMap, e);	
		} catch (ConcurrentModificationException e2) {
			e2.printStackTrace();
		}
		
	}
	
	public static String getIPAddress(boolean useIPv4) {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase(Locale.US);
						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						if (useIPv4) {
							if (isIPv4) {
								return sAddr;
							}
						} else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%'); // drop ip6 port
																// suffix
								return delim < 0 ? sAddr : sAddr.substring(0, delim);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		} // for now eat exceptions
		return "unknown";
	}
	
}
