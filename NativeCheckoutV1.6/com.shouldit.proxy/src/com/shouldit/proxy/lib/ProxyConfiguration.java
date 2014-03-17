package com.shouldit.proxy.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.conn.util.InetAddressUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.webkit.URLUtil;

import com.shouldit.proxy.lib.APLConstants.CheckStatusValues;
import com.shouldit.proxy.lib.APLConstants.ProxyStatusErrors;
import com.shouldit.proxy.lib.APLConstants.ProxyStatusProperties;
import com.shouldit.proxy.lib.reflection.ReflectionUtils;
import com.shouldit.proxy.lib.reflection.android.RProxySettings;

public class ProxyConfiguration implements Comparable<ProxyConfiguration>
{
	public static final String TAG = "ProxyConfiguration";

	public Context context;
	public ProxyStatus status;
	public AccessPoint ap;
	public NetworkInfo currentNetworkInfo;
	private Proxy proxyHost;
	public String proxyDescription;
	public String proxyExclusionList;
	
	public Proxy getProxyHost()
	{
		return proxyHost;
	}

	private RProxySettings proxyToggle;
	public int deviceVersion;

	public ProxyConfiguration(Context ctx, RProxySettings proxyEnabled, Proxy proxy, String description, String exclusionList, NetworkInfo netInfo, WifiConfiguration wifiConf)
	{
		context = ctx;
				
		proxyToggle = proxyEnabled;
		proxyHost = proxy;
		proxyDescription = description;
		proxyExclusionList = exclusionList;
		currentNetworkInfo = netInfo;

		if (wifiConf != null)
			ap = new AccessPoint(wifiConf);

		deviceVersion = Build.VERSION.SDK_INT;
		status = new ProxyStatus();
	}

	@Deprecated
	@TargetApi(12)
	public void writeConfigurationToDevice()
	{
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		try
		{
			Field proxySettingsField = ap.wifiConfig.getClass().getField("proxySettings");
			proxySettingsField.set(ap.wifiConfig,(Object) proxySettingsField.getType().getEnumConstants()[proxyToggle.ordinal()]);
			Object proxySettings = proxySettingsField.get(ap.wifiConfig);
			int ordinal = ((Enum) proxySettings).ordinal();
			if (ordinal != proxyToggle.ordinal())
				throw new Exception("Cannot set proxySettings variable");
			
			Field linkPropertiesField = ap.wifiConfig.getClass().getField("linkProperties");
			Object linkProperties = linkPropertiesField.get(ap.wifiConfig);
			Field mHttpProxyField = ReflectionUtils.getField(linkProperties.getClass().getDeclaredFields(), "mHttpProxy");
			mHttpProxyField.setAccessible(true);

			
			if (proxyToggle == RProxySettings.NONE || proxyToggle == RProxySettings.UNASSIGNED)
			{				
				mHttpProxyField.set(linkProperties, null);
			}
			else if (proxyToggle == RProxySettings.STATIC)
			{							
				Class ProxyPropertiesClass = mHttpProxyField.getType();
				Constructor constr = ProxyPropertiesClass.getConstructors()[1];
				
				Object ProxyProperties = constr.newInstance(getProxyHostString(), getProxyPort() , proxyExclusionList);
				mHttpProxyField.set(linkProperties, ProxyProperties);
			}
			
			Object mHttpProxy = mHttpProxyField.get(linkProperties);
			mHttpProxy = mHttpProxyField.get(linkProperties);
		}
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoSuchFieldException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Proxy: %s\n", proxyHost.toString()));
		sb.append(String.format("Is current network: %B\n", isCurrentNetwork()));
		sb.append(String.format("Is Proxy address valid: %B\n", isProxyValidAddress()));
		sb.append(String.format("Is Proxy reachable: %B\n", isProxyReachable()));
		sb.append(String.format("Is WEB reachable: %B\n", isWebReachable(60000)));

		if (currentNetworkInfo != null)
		{
			sb.append(String.format("Network Info: %s\n", currentNetworkInfo));
		}

		if (ap != null && ap.wifiConfig != null)
			sb.append(String.format("Wi-Fi Configuration Info: %s\n", ap.wifiConfig.SSID.toString()));

		return sb.toString();
	}

	public Boolean isCurrentNetwork()
	{
		if (currentNetworkInfo != null && 
				this.getSSID() != null && 
				this.getSSID().equals(currentNetworkInfo.getExtraInfo()))
			return true;
		else
			return false;
	}

	public String toShortString()
	{
		if (proxyHost == Proxy.NO_PROXY)
		{
			return Proxy.NO_PROXY.toString();
		}

		if (proxyDescription != null)
		{
			return proxyDescription;
		}
		else
		{
			return String.format("%s", proxyHost.address().toString());
		}
	}

	public String toShortIPString()
	{
		return String.format("%s:%d", getProxyIPHost(), getProxyPort());
	}

	public Proxy.Type getProxyType()
	{
		return proxyHost.type();
	}

	/**
	 * Can take a long time to execute this task. - Check if the proxy is
	 * enabled - Check if the proxy address is valid - Check if the proxy is
	 * reachable (using a PING) - Check if is possible to retrieve an URI
	 * resource using the proxy
	 * */
	public void acquireProxyStatus(int timeout)
	{
		status.clear();
		status.startchecking();
		broadCastUpdatedStatus();

		LogWrapper.d(TAG, "Checking if proxy is enabled ...");
		if (!isProxyEnabled())
		{
			LogWrapper.e(TAG, "PROXY NOT ENABLED");
			status.add(ProxyStatusProperties.PROXY_ENABLED, CheckStatusValues.CHECKED, false);
		}
		else
		{
			LogWrapper.i(TAG, "PROXY ENABLED");
			status.add(ProxyStatusProperties.PROXY_ENABLED, CheckStatusValues.CHECKED, true);
		}

		broadCastUpdatedStatus();

		LogWrapper.d(TAG, "Checking if proxy is valid address ...");
		if (!isProxyValidAddress())
		{
			LogWrapper.e(TAG, "PROXY NOT VALID ADDRESS");
			status.add(ProxyStatusProperties.PROXY_VALID_ADDRESS, CheckStatusValues.CHECKED, false);
		}
		else
		{
			LogWrapper.i(TAG, "PROXY VALID ADDRESS");
			status.add(ProxyStatusProperties.PROXY_VALID_ADDRESS, CheckStatusValues.CHECKED, true);
		}

		broadCastUpdatedStatus();

		LogWrapper.d(TAG, "Checking if proxy is reachable ...");
		if (!isProxyReachable())
		{
			LogWrapper.e(TAG, "PROXY NOT REACHABLE");
			status.add(ProxyStatusProperties.PROXY_REACHABLE, CheckStatusValues.CHECKED, false);
		}
		else
		{
			LogWrapper.i(TAG, "PROXY REACHABLE");
			status.add(ProxyStatusProperties.PROXY_REACHABLE, CheckStatusValues.CHECKED, true);
		}

		broadCastUpdatedStatus();

		LogWrapper.d(TAG, "Checking if web is reachable ...");
		if (!isWebReachable(timeout))
		{
			LogWrapper.e(TAG, "WEB NOT REACHABLE");
			status.add(ProxyStatusProperties.WEB_REACHABLE, CheckStatusValues.CHECKED, false);
		}
		else
		{
			LogWrapper.i(TAG, "WEB REACHABLE");
			status.add(ProxyStatusProperties.WEB_REACHABLE, CheckStatusValues.CHECKED, true);
		}

		broadCastUpdatedStatus();
	}

	private void broadCastUpdatedStatus()
	{
		LogWrapper.d(TAG, "Sending broadcast intent: " + APLConstants.APL_UPDATED_PROXY_STATUS_CHECK);
		Intent intent = new Intent(APLConstants.APL_UPDATED_PROXY_STATUS_CHECK);
		intent.putExtra(APLConstants.ProxyStatus, status);
		context.sendBroadcast(intent);
	}

	public ProxyStatusErrors getMostRelevantProxyStatusError()
	{
		if (!status.getEnabled().result)
			return ProxyStatusErrors.PROXY_NOT_ENABLED;

		if (status.getWeb_reachable().result)
		{
			// If the WEB is reachable, the proxy is OK!
			return ProxyStatusErrors.NO_ERRORS;
		}
		else
		{
			if (status.getProxy_reachable().result)
			{
				return ProxyStatusErrors.WEB_NOT_REACHABLE;
			}
			else
			{
				if (status.getValid_address().result)
				{
					return ProxyStatusErrors.PROXY_NOT_REACHABLE;
				}
				else
					return ProxyStatusErrors.PROXY_ADDRESS_NOT_VALID;
			}
		}
	}

	private Boolean isProxyEnabled()
	{
		if (Build.VERSION.SDK_INT >= 12)
		{
			// On API version > Honeycomb 3.1 (HONEYCOMB_MR1)
			// Proxy is disabled by default on Mobile connection
			if (currentNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
				return false;
		}

		if (proxyHost.type() == Type.DIRECT)
		{
			return false;
		}
		else
		{
			return true; // HTTP or SOCKS proxy
		}
	}

	private boolean isProxyValidAddress()
	{
		try
		{
			String proxyHost = getProxyHostString();

			if (proxyHost != null)
			{

				if (InetAddressUtils.isIPv4Address(proxyHost) || InetAddressUtils.isIPv6Address(proxyHost) || InetAddressUtils.isIPv6HexCompressedAddress(proxyHost) || InetAddressUtils.isIPv6StdAddress(proxyHost))
				{
					return true;
				}

				if (URLUtil.isNetworkUrl(proxyHost))
				{
					return true;
				}

				if (URLUtil.isValidUrl(proxyHost))
				{
					return true;
				}

				// Test REGEX for Hostname validation
				// http://stackoverflow.com/questions/106179/regular-expression-to-match-hostname-or-ip-address
				//
				String ValidHostnameRegex = "^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\\-]*[A-Za-z0-9])$";
				Pattern pattern = Pattern.compile(ValidHostnameRegex);
				Matcher matcher = pattern.matcher(proxyHost);

				if (matcher.find())
				{
					return true;
				}
			}
			else
			{
				return false;
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Try to PING the HOST specified in the current proxy configuration
	 * */
	private Boolean isProxyReachable()
	{
		if (proxyHost != null && proxyHost.type() != Proxy.Type.DIRECT)
			return ProxyUtils.isHostReachable(proxyHost);
		else
			return false;
	}

	/**
	 * Try to download a webpage using the current proxy configuration
	 * */
	public static int DEFAULT_TIMEOUT = 60000; // 60 seconds

	private Boolean isWebReachable()
	{
		return isWebReachable(DEFAULT_TIMEOUT);
	}

	private Boolean isWebReachable(int timeout)
	{
		return ProxyUtils.isWebReachable(this, timeout);
	}

	public String getProxyHostString()
	{
		InetSocketAddress proxyAddress = (InetSocketAddress) proxyHost.address();
		if (proxyAddress != null)
		{
			return proxyAddress.getHostName();
		}
		else
		{
			// return proxy description if it's not possible to resolve the proxy name
			return this.proxyDescription;
		}
	}

	public String getProxyIPHost()
	{
		InetSocketAddress proxyAddress = (InetSocketAddress) proxyHost.address();
		if (proxyAddress != null)
		{
			InetAddress address = proxyAddress.getAddress();

			if (address != null)
			{
				return address.getHostAddress();
			}
			else
			{
				// return proxy description if it's not possible to resolve the proxy name
				return this.proxyDescription;
			}
		}
		else
			return this.proxyDescription;
	}

	public Integer getProxyPort()
	{
		InetSocketAddress proxyAddress = (InetSocketAddress) proxyHost.address();
		return proxyAddress.getPort();
	}

	public CheckStatusValues getCheckingStatus()
	{
		return status.getCheckingStatus();
	}

	public int getNetworkType()
	{
		return currentNetworkInfo.getType();
	}

	@Override
	public int compareTo(ProxyConfiguration another)
	{
		int result;

		//		if (!isNetworkAvailable)
		//		{
		//			LogWrapper.e(TAG, "Cannot compare ProxyConfigurations, network in not available!");
		//			return 0; // Cannot compare if network is not available
		//		}

		if (currentNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)
		{
			if (another.currentNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)
			{
				result = ap.compareTo(another.ap);
				if (result == 0)
				{
					if (proxyHost != another.proxyHost)
					{
						result = proxyHost.toString().compareTo(another.proxyHost.toString());
					}
				}
			}
			else
			{
				result = -1;
			}
		}
		else
		{
			if (another.currentNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)
			{
				result = +1;
			}
			else
			{
				result = 0; // Both are mobile or no connection 
			}
		}

		return result;
	}

	public String getAPDescription(Context ctx)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(ap.getSecurityString(ctx, false));
		return sb.toString();
	}

	public String getSSID()
	{
		if (ap != null && ap.wifiConfig != null && ap.wifiConfig.SSID != null)
		{
			return ap.wifiConfig.SSID;
		}
		else
			return null;
	}

	public boolean isValidConfiguration()
	{
		if (ap != null)
			return true;
		else
			return false;
	}
}