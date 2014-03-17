package pt.rocket.framework.utils;


public class LogTagHelper {
	
	public static String create( Class<? extends Object> clazz ) {
		return clazz.getSimpleName();
	}
}
