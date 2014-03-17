package com.shouldit.proxy.lib;

import android.content.Context;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class AccessPoint implements Comparable<AccessPoint>
{
	static final String TAG = "Settings.AccessPoint";

	public static final int INVALID_NETWORK_ID = -1;
	private static final int DISABLED_UNKNOWN_REASON = 0;
	private static final int DISABLED_DNS_FAILURE = 1;
	private static final int DISABLED_DHCP_FAILURE = 2;
	private static final int DISABLED_AUTH_FAILURE = 3;

	private static final String KEY_DETAILEDSTATE = "key_detailedstate";
	private static final String KEY_WIFIINFO = "key_wifiinfo";
	private static final String KEY_SCANRESULT = "key_scanresult";
	private static final String KEY_CONFIG = "key_config";

	public static final int[] STATE_SECURED = { R.attr.state_encrypted };
	public static final int[] STATE_NONE = {};

	/**
	 * These values are matched in string arrays -- changes must be kept in sync
	 */
	public static final int SECURITY_NONE = 0;
	public static final int SECURITY_WEP = 1;
	public static final int SECURITY_PSK = 2;
	public static final int SECURITY_EAP = 3;

	enum PskType
	{
		UNKNOWN, WPA, WPA2, WPA_WPA2
	}

	public String ssid;
	public String bssid;
	public int security;
	public int networkId;
	public boolean wpsAvailable = false;

	PskType pskType = PskType.UNKNOWN;

	public WifiConfiguration wifiConfig;
	/* package */ScanResult mScanResult;

	public int mRssi;
	private WifiInfo mInfo;
	
	public static int getSecurity(WifiConfiguration config)
	{
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK))
		{
			return SECURITY_PSK;
		}
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X))
		{
			return SECURITY_EAP;
		}
		return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
	}

	public static int getSecurity(ScanResult result)
	{
		if (result.capabilities.contains("WEP"))
		{
			return SECURITY_WEP;
		}
		else if (result.capabilities.contains("PSK"))
		{
			return SECURITY_PSK;
		}
		else if (result.capabilities.contains("EAP"))
		{
			return SECURITY_EAP;
		}
		return SECURITY_NONE;
	}

	public String getSecurityString(Context context, boolean concise)
	{
		switch (security)
		{
			case SECURITY_EAP:
				return concise ? context.getString(R.string.wifi_security_short_eap) : context.getString(R.string.wifi_security_eap);
			case SECURITY_PSK:
				switch (pskType)
				{
					case WPA:
						return concise ? context.getString(R.string.wifi_security_short_wpa) : context.getString(R.string.wifi_security_wpa);
					case WPA2:
						return concise ? context.getString(R.string.wifi_security_short_wpa2) : context.getString(R.string.wifi_security_wpa2);
					case WPA_WPA2:
						return concise ? context.getString(R.string.wifi_security_short_wpa_wpa2) : context.getString(R.string.wifi_security_wpa_wpa2);
					case UNKNOWN:
					default:
						return concise ? context.getString(R.string.wifi_security_short_psk_generic) : context.getString(R.string.wifi_security_psk_generic);
				}
			case SECURITY_WEP:
				return concise ? context.getString(R.string.wifi_security_short_wep) : context.getString(R.string.wifi_security_wep);
			case SECURITY_NONE:
			default:
				return concise ? "" : context.getString(R.string.wifi_security_none);
		}
	}

	public static PskType getPskType(ScanResult result)
	{
		boolean wpa = result.capabilities.contains("WPA-PSK");
		boolean wpa2 = result.capabilities.contains("WPA2-PSK");
		if (wpa2 && wpa)
		{
			return PskType.WPA_WPA2;
		}
		else if (wpa2)
		{
			return PskType.WPA2;
		}
		else if (wpa)
		{
			return PskType.WPA;
		}
		else
		{
			Log.w(TAG, "Received abnormal flag string: " + result.capabilities);
			return PskType.UNKNOWN;
		}
	}

	public AccessPoint(WifiConfiguration config)
	{
		loadConfig(config);
	}

	private void loadConfig(WifiConfiguration config)
	{
		ssid = (config.SSID == null ? "" : removeDoubleQuotes(config.SSID));
		bssid = config.BSSID;
		security = getSecurity(config);
		networkId = config.networkId;
		mRssi = Integer.MAX_VALUE;
		wifiConfig = config;
	}

	@Override
	public int compareTo(AccessPoint ap)
	{
		if (!(ap instanceof AccessPoint))
		{
			return 1;
		}
		
		AccessPoint other = (AccessPoint) ap;
		// Active one goes first.
		if (mInfo != other.mInfo)
		{
			return (mInfo != null) ? -1 : 1;
		}
		// Reachable one goes before unreachable one.
		if ((mRssi ^ other.mRssi) < 0)
		{
			return (mRssi != Integer.MAX_VALUE) ? -1 : 1;
		}
		// Configured one goes before unconfigured one.
		if ((networkId ^ other.networkId) < 0)
		{
			return (networkId != -1) ? -1 : 1;
		}
		// Sort by signal strength.
		int difference = WifiManager.compareSignalLevel(other.mRssi, mRssi);
		if (difference != 0)
		{
			return difference;
		}
		// Sort by ssid.
		return ssid.compareToIgnoreCase(other.ssid);
	}

	public boolean update(ScanResult result)
	{
		if (ssid.equals(result.SSID) && security == getSecurity(result))
		{
			if (WifiManager.compareSignalLevel(result.level, mRssi) > 0)
			{
				mRssi = result.level;
			}
			// This flag only comes from scans, is not easily saved in config
			if (security == SECURITY_PSK)
			{
				pskType = getPskType(result);
			}
			return true;
		}
		return false;
	}

	public int getLevel()
	{
		if (mRssi == Integer.MAX_VALUE)
		{
			return -1;
		}
		return WifiManager.calculateSignalLevel(mRssi, 4);
	}

	WifiInfo getInfo()
	{
		return mInfo;
	}

	static String removeDoubleQuotes(String string)
	{
		int length = string.length();
		if ((length > 1) && (string.charAt(0) == '"') && (string.charAt(length - 1) == '"'))
		{
			return string.substring(1, length - 1);
		}
		return string;
	}

	static String convertToQuotedString(String string)
	{
		return "\"" + string + "\"";
	}
}
