package com.mobile.framework.network;


import ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog;
import com.mobile.framework.output.Print;


public class LazHttpClientAndroidLog extends HttpClientAndroidLog {

	private String logTag;
	private boolean debugEnabled;
	private boolean errorEnabled;
	private boolean traceEnabled;
	private boolean warnEnabled;
	private boolean infoEnabled;

	public LazHttpClientAndroidLog(Object tag) {
		super(tag);
		logTag=tag.toString();
		debugEnabled=false;
		errorEnabled=false;
		traceEnabled=false;
		warnEnabled=false;
		infoEnabled=false;
	}

	public void enableDebug(boolean enable) {
		debugEnabled=enable;
	}

	public boolean isDebugEnabled() {
		return debugEnabled;
	}

	public void debug(Object message) {
		if(isDebugEnabled()) {
			Print.d(logTag, message.toString());
		}
	}

	public void debug(Object message, Throwable t) {
		if(isDebugEnabled()) {
			Print.d(logTag, message.toString(), t);
		}
	}

	public void enableError(boolean enable) {
		errorEnabled=enable;
	}

	public boolean isErrorEnabled() {
		return errorEnabled;
	}

	public void error(Object message) {
		if(isErrorEnabled()) {
			Print.e(logTag, message.toString());
		}
	}

	public void error(Object message, Throwable t) {
		if(isErrorEnabled()) {
			Print.e(logTag, message.toString(), t);
		}
	}

	public void enableWarn(boolean enable) {
		warnEnabled=enable;
	}

	public boolean isWarnEnabled() {
		return warnEnabled;
	}

	public void warn(Object message) {
		if(isWarnEnabled()) {
			Print.w(logTag, message.toString());
		}
	}

	public void warn(Object message, Throwable t) {
		if(isWarnEnabled()) {
			Print.w(logTag, message.toString(), t);
		}
	}

	public void enableInfo(boolean enable) {
		infoEnabled=enable;
	}

	public boolean isInfoEnabled() {
		return infoEnabled;
	}

	public void info(Object message) {
		if(isInfoEnabled()) {
			Print.i(logTag, message.toString());
		}
	}

	public void info(Object message, Throwable t) {
		if(isInfoEnabled()) {
			Print.i(logTag, message.toString(), t);
		}
	}

	public void enableTrace(boolean enable) {
		traceEnabled=enable;
	}

	public boolean isTraceEnabled() {
		return traceEnabled;
	}

	public void trace(Object message) {
		if(isTraceEnabled()) {
			Print.i(logTag, message.toString());
		}
	}

	public void trace(Object message, Throwable t) {
		if(isTraceEnabled()) {
			Print.i(logTag, message.toString(), t);
		}
	}

}