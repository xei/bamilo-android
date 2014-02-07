package pt.rocket.framework.testproject.utils;

public class Log {

	
	public static void i(String tag, String msg){
		android.util.Log.i(tag, msg);
	}
	
	public static void d(String tag, String msg){
		android.util.Log.d(tag, msg);
	}
	
	public static void v(String tag, String msg){
		android.util.Log.v(tag, msg);
	}
}
