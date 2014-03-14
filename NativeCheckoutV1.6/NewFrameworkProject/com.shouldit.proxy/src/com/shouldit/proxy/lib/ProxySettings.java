package com.shouldit.proxy.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

import com.shouldit.proxy.lib.reflection.ReflectionUtils;
import com.shouldit.proxy.lib.reflection.android.RProxySettings;

/**
 * Main class that contains utilities for getting the proxy configuration of the
 * current or the all configured networks
 * */
public class ProxySettings
{
	public static final String TAG = "ProxySettings";

	/**
	 * Main entry point to access the proxy settings
	 * */
	public static ProxyConfiguration getCurrentProxyConfiguration(Context ctx, URI uri) throws Exception
	{
		ProxyConfiguration proxyConfig;

		if (Build.VERSION.SDK_INT >= 12) // Honeycomb 3.1
		{
			proxyConfig = getProxySelectorConfiguration(ctx, uri);
		}
		else
		{
			proxyConfig = getGlobalProxy(ctx);
		}

		/**
		 * Set direct connection if no proxyConfig received
		 * */
		if (proxyConfig == null)
		{
			proxyConfig = new ProxyConfiguration(ctx, RProxySettings.NONE, Proxy.NO_PROXY, null, null, null, null);
		}

		/**
		 * Add connection details
		 * */
		ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
		proxyConfig.currentNetworkInfo = activeNetInfo;

		if (activeNetInfo != null)
		{
			switch (activeNetInfo.getType())
			{
				case ConnectivityManager.TYPE_WIFI:
					WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
					WifiInfo wifiInfo = wifiManager.getConnectionInfo();
					List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
					for (WifiConfiguration wc : wifiConfigurations)
					{
						if (wc.networkId == wifiInfo.getNetworkId())
						{
							proxyConfig.ap = new AccessPoint(wc);
							break;
						}
					}
					break;
				case ConnectivityManager.TYPE_MOBILE:
					break;
				default:
					throw new UnsupportedOperationException("Not yet implemented support for" + activeNetInfo.getTypeName() + " network type");
			}
		}

		return proxyConfig;
	}

	/**
	 * For API >= 12: Returns the current proxy configuration based on the URI,
	 * this implementation is a wrapper of the Android's ProxySelector class.
	 * Just add some other details that can be useful to the developer.
	 * */
	public static ProxyConfiguration getProxySelectorConfiguration(Context ctx, URI uri) throws Exception
	{
		ProxySelector defaultProxySelector = ProxySelector.getDefault();

		Proxy proxy = null;

		List<Proxy> proxyList = defaultProxySelector.select(uri);
		if (proxyList.size() > 0)
		{
			proxy = proxyList.get(0);
			LogWrapper.d(TAG, "Current Proxy Configuration: " + proxy.toString());
		}
		else
			throw new Exception("Not found valid proxy configuration!");

		ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
		
		ProxyConfiguration proxyConfig = null;
		if (proxy != Proxy.NO_PROXY)
			proxyConfig = new ProxyConfiguration(ctx, RProxySettings.STATIC, proxy, proxy.toString(), null, activeNetInfo, null);
		else
			proxyConfig = new ProxyConfiguration(ctx, RProxySettings.NONE, proxy, proxy.toString(), null, activeNetInfo, null);

		return proxyConfig;
	}

	/**
	 * Return the current proxy configuration for HTTP protocol
	 * */
	public static ProxyConfiguration getCurrentHttpProxyConfiguration(Context ctx) throws Exception
	{
		URI uri = new URI("http", "wwww.google.it", null, null);
		return getCurrentProxyConfiguration(ctx, uri);
	}

	/**
	 * Return the current proxy configuration for HTTPS protocol
	 * */
	public static ProxyConfiguration getCurrentHttpsProxyConfiguration(Context ctx) throws Exception
	{
		URI uri = new URI("https", "wwww.google.it", null, null);
		return getCurrentProxyConfiguration(ctx, uri);
	}

	/**
	 * Return the current proxy configuration for FTP protocol
	 * */
	public static ProxyConfiguration getCurrentFtpProxyConfiguration(Context ctx) throws Exception
	{
		URI uri = new URI("ftp", "google.it", null, null);
		return getCurrentProxyConfiguration(ctx, uri);
	}

	/**
	 * For API < 12: Get global proxy configuration.
	 * */
	@Deprecated
	public static ProxyConfiguration getGlobalProxy(Context ctx)
	{
		ProxyConfiguration proxyConfig = null;

		ContentResolver contentResolver = ctx.getContentResolver();
		String proxyString = Settings.Secure.getString(contentResolver, Settings.Secure.HTTP_PROXY);

		if (proxyString != null && proxyString != "" && proxyString.contains(":"))
		{
			String[] proxyParts = proxyString.split(":");
			if (proxyParts.length == 2)
			{
				String proxyAddress = proxyParts[0];
				try
				{
					Integer proxyPort = Integer.parseInt(proxyParts[1]);
					Proxy p = new Proxy(Type.HTTP, InetSocketAddress.createUnresolved(proxyAddress, proxyPort));
					proxyConfig = new ProxyConfiguration(ctx, RProxySettings.STATIC, p, proxyString, null, null, null);
					// LogWrapper.d(TAG, "ProxyHost created: " +
					// proxyConfig.toString());
				}
				catch (NumberFormatException e)
				{
					LogWrapper.d(TAG, "Port is not a number: " + proxyParts[1]);
				}
			}
		}

		return proxyConfig;
	}

	/**
	 * Get proxy configuration for Wi-Fi access point. Valid for API >= 12
	 * */
	@Deprecated
	@TargetApi(12)
	public static ProxyConfiguration getProxySdk12(Context ctx, WifiConfiguration wifiConf)
	{
		ProxyConfiguration proxyHost = null;

		ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
		
		try
		{
			Field proxySettingsField = wifiConf.getClass().getField("proxySettings");
			Object proxySettings = proxySettingsField.get(wifiConf);

			int ordinal = ((Enum) proxySettings).ordinal();

			if (ordinal == RProxySettings.NONE.ordinal() || ordinal == RProxySettings.UNASSIGNED.ordinal())
			{
				proxyHost = new ProxyConfiguration(ctx, RProxySettings.NONE, Proxy.NO_PROXY, "", "", activeNetInfo, wifiConf);
			}
			else
			{				
				Field linkPropertiesField = wifiConf.getClass().getField("linkProperties");
				Object linkProperties = linkPropertiesField.get(wifiConf);
				Field mHttpProxyField = ReflectionUtils.getField(linkProperties.getClass().getDeclaredFields(), "mHttpProxy");
				mHttpProxyField.setAccessible(true);
				Object mHttpProxy = mHttpProxyField.get(linkProperties);

				if (mHttpProxy != null)
				{
					Field mHostField = ReflectionUtils.getField(mHttpProxy.getClass().getDeclaredFields(), "mHost");
					mHostField.setAccessible(true);
					String mHost = (String) mHostField.get(mHttpProxy);

					Field mPortField = ReflectionUtils.getField(mHttpProxy.getClass().getDeclaredFields(), "mPort");
					mPortField.setAccessible(true);
					Integer mPort = (Integer) mPortField.get(mHttpProxy);

					Field mExclusionListField = ReflectionUtils.getField(mHttpProxy.getClass().getDeclaredFields(), "mExclusionList");
					mExclusionListField.setAccessible(true);
					String mExclusionList = (String) mExclusionListField.get(mHttpProxy);

					//LogWrapper.d(TAG, "Proxy configuration: " + mHost + ":" + mPort + " , Exclusion List: " + mExclusionList);

					InetSocketAddress sa = InetSocketAddress.createUnresolved(mHost, mPort);
					Proxy proxy = new Proxy(Proxy.Type.HTTP, sa);

					proxyHost = new ProxyConfiguration(ctx, RProxySettings.STATIC, proxy, proxy.toString(), mExclusionList, activeNetInfo, wifiConf);
				}
			}
		}
		catch (Exception e)
		{
			LogWrapper.e(TAG, e.getMessage());
		}

		return proxyHost;
	}

	@Deprecated
	@TargetApi(12)
	public static List<ProxyConfiguration> getProxiesConfigurations(Context ctx)
	{
		List<ProxyConfiguration> proxyHosts = new ArrayList<ProxyConfiguration>();
		WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
		
		if (configuredNetworks != null)
		{
			for (WifiConfiguration wifiConf : configuredNetworks)
			{
				ProxyConfiguration conf = getProxySdk12(ctx, wifiConf);
				proxyHosts.add(conf);
			}
		}
		
		Collections.sort(proxyHosts);

		return proxyHosts;
	}

}
