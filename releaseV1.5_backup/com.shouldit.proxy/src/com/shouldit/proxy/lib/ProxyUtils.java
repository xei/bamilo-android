package com.shouldit.proxy.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.http.HttpHost;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class ProxyUtils
{
	public static final String TAG = "ProxyUtils";

	public static Intent getProxyIntent()
	{
		if (Build.VERSION.SDK_INT >= 12) // Honeycomb 3.1
		{
			return getAPProxyIntent();
		}
		else
		{
			return getGlobalProxyIntent();
		}
	}

	/**
	 * For API < 12
	 * */
	private static Intent getGlobalProxyIntent()
	{
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.ProxySelector"));

		return intent;
	}

	/**
	 * For API >= 12
	 * */
	private static Intent getAPProxyIntent()
	{
		Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);

		return intent;
	}

	// public static Intent getWebViewWithProxy(Context context, URI uri)
	// {
	// Intent intent = new Intent(context, );
	// intent.putExtra("URI", uri);
	//
	// return intent;
	// }

	public static boolean isHostReachable(Proxy proxy)
	{
		int exitValue;
		Runtime runtime = Runtime.getRuntime();
		Process proc;

		String cmdline = null;

		try
		{
			InetSocketAddress proxySocketAddress = (InetSocketAddress) proxy.address();
			String proxyAddress = proxySocketAddress.getAddress().getHostAddress();
			cmdline = "ping -c 1   " + proxyAddress;
		}
		catch (Exception e)
		{
			return false;
		}

		try
		{
			proc = runtime.exec(cmdline);
			proc.waitFor();
			exitValue = proc.exitValue();

			LogWrapper.d(TAG, "Ping exit value: " + exitValue);

			if (exitValue == 0)
				return true;
			else
				return false;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	public static int testHTTPConnection(URI uri, ProxyConfiguration proxyConfiguration, int timeout)
	{
		int step = 0;
		while (step < 5)
		{
			try
			{
				URL url = uri.toURL();

				if (proxyConfiguration.getProxyType() == Type.HTTP)
				{
					System.setProperty("http.proxyHost", proxyConfiguration.getProxyIPHost());
					System.setProperty("http.proxyPort", proxyConfiguration.getProxyPort().toString());
				}
				else
				{
					System.setProperty("http.proxyHost", "");
					System.setProperty("http.proxyPort", "");
				}

				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

				httpURLConnection.setReadTimeout(timeout);
				httpURLConnection.setConnectTimeout(timeout);

				return httpURLConnection.getResponseCode();
			}
			catch (MalformedURLException e)
			{
				LogWrapper.e(TAG, "ProxyUtils.testHTTPConnection() MalformedURLException : " + e.toString() );
			}
			catch (UnknownHostException e)
			{
				LogWrapper.e(TAG, "ProxyUtils.testHTTPConnection() UnknownHostException : " + e.toString() );
			}
			catch (SocketTimeoutException e)
			{
				LogWrapper.e(TAG, "ProxyUtils.testHTTPConnection() timed out after: " + timeout + " msec");
			}
			catch (SocketException e)
			{
				LogWrapper.e(TAG, "ProxyUtils.testHTTPConnection() SocketException : " + e.toString() );
			}
			catch (IOException e)
			{
				LogWrapper.e(TAG, "ProxyUtils.testHTTPConnection() IOException : " + e.toString() );
			}
			
			step++;
			
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				LogWrapper.e(TAG, "Exception during waiting for next try of testHTTPConnection: " + e.toString());
				return -1;
			}
		}

		return -1;
	}

	public static String getURI(URI uri, Proxy proxy, int timeout)
	{
		try
		{
			URL url = uri.toURL();
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(proxy);

			httpURLConnection.setReadTimeout(timeout);
			httpURLConnection.setConnectTimeout(timeout);

			int response = httpURLConnection.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK)
			{
				// Response successful
				InputStream inputStream = httpURLConnection.getInputStream();

				// Parse it line by line
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				String temp;
				StringBuilder sb = new StringBuilder();
				while ((temp = bufferedReader.readLine()) != null)
				{
					// LogWrapper.d(TAG, temp);
					sb.append(temp);
				}

				return sb.toString();
			}
			else
			{
				LogWrapper.e(TAG, "INCORRECT RETURN CODE: " + response);
				return null;
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (SocketTimeoutException e)
		{
			LogWrapper.e(TAG, "ProxyUtils.getURI() timed out after: " + timeout + " msec");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static boolean isWebReachable(ProxyConfiguration proxyConfiguration, int timeout)
	{
		try
		{
			int result = testHTTPConnection(new URI("http://www.un.org/"), proxyConfiguration, timeout);

			switch (result)
			{
				case HttpURLConnection.HTTP_OK:
				case HttpURLConnection.HTTP_CREATED:
				case HttpURLConnection.HTTP_NO_CONTENT:
				case HttpURLConnection.HTTP_NOT_AUTHORITATIVE:
				case HttpURLConnection.HTTP_ACCEPTED:
				case HttpURLConnection.HTTP_PARTIAL:
				case HttpURLConnection.HTTP_RESET:
					return true;

				default:
					return false;
			}
		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public static void setWebViewProxy(Context context, ProxyConfiguration proxyConf)
	{
		try
		{
			if (proxyConf != null && proxyConf.getProxyType() == Type.HTTP && proxyConf.deviceVersion < 12)
			{
				setProxy(context, proxyConf.getProxyIPHost(), proxyConf.getProxyPort());
			}
			else
			{
				resetProxy(context);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void resetProxy(Context ctx) throws Exception
	{
		Object requestQueueObject = getRequestQueue(ctx);
		if (requestQueueObject != null)
		{
			setDeclaredField(requestQueueObject, "mProxyHost", null);
		}
	}

	private static boolean setProxy(Context ctx, String host, int port)
	{
		boolean ret = false;
		try
		{
			Object requestQueueObject = getRequestQueue(ctx);
			if (requestQueueObject != null)
			{
				// Create Proxy config object and set it into request Q
				HttpHost httpHost = new HttpHost(host, port, "http");
				setDeclaredField(requestQueueObject, "mProxyHost", httpHost);
				// LogWrapper.d("Webkit Setted Proxy to: " + host + ":" + port);
				ret = true;
			}
		}
		catch (Exception e)
		{
			LogWrapper.e("ProxySettings", "Exception setting WebKit proxy settings: " + e.toString());
		}
		return ret;
	}

	@SuppressWarnings("rawtypes")
	private static Object GetNetworkInstance(Context ctx) throws ClassNotFoundException
	{
		Class networkClass = Class.forName("android.webkit.Network");
		return networkClass;
	}

	private static Object getRequestQueue(Context ctx) throws Exception
	{
		Object ret = null;
		Object networkClass = GetNetworkInstance(ctx);
		if (networkClass != null)
		{
			Object networkObj = invokeMethod(networkClass, "getInstance", new Object[] { ctx }, Context.class);
			if (networkObj != null)
			{
				ret = getDeclaredField(networkObj, "mRequestQueue");
			}
		}
		return ret;
	}

	private static Object getDeclaredField(Object obj, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
	{
		Field f = obj.getClass().getDeclaredField(name);
		f.setAccessible(true);
		Object out = f.get(obj);
		return out;
	}

	private static void setDeclaredField(Object obj, String name, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
	{
		Field f = obj.getClass().getDeclaredField(name);
		f.setAccessible(true);
		f.set(obj, value);
	}

	@SuppressWarnings("rawtypes")
	private static Object invokeMethod(Object object, String methodName, Object[] params, Class... types) throws Exception
	{
		Object out = null;
		Class c = object instanceof Class ? (Class) object : object.getClass();

		if (types != null)
		{
			Method method = c.getMethod(methodName, types);
			out = method.invoke(object, params);
		}
		else
		{
			Method method = c.getMethod(methodName);
			out = method.invoke(object);
		}
		return out;
	}

}
