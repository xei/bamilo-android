package com.adjust.sdk.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;
import android.util.SparseArray;

import com.adjust.sdk.Logger;

public class MockLogger implements Logger {

    private StringBuffer logBuffer;
    private SparseArray<ArrayList<String>> logMap;

    public MockLogger() {
        logBuffer = new StringBuffer();
        logMap = new SparseArray<ArrayList<String>>(7);
        logMap.put(LogLevel.ASSERT.getAndroidLogLevel(), new ArrayList<String>());
        logMap.put(LogLevel.DEBUG.getAndroidLogLevel(), new ArrayList<String>());
        logMap.put(LogLevel.ERROR.getAndroidLogLevel(), new ArrayList<String>());
        logMap.put(LogLevel.INFO.getAndroidLogLevel(), new ArrayList<String>());
        logMap.put(LogLevel.VERBOSE.getAndroidLogLevel(), new ArrayList<String>());
        logMap.put(LogLevel.WARN.getAndroidLogLevel(), new ArrayList<String>());
        // logging test level == 1
        logMap.put(1, new ArrayList<String>());
    }

    @Override
    public String toString() {
        String logging = logBuffer.toString();
        //Log.v("TestLogger ", logging);
        return logging;
    }

    @Override
    public void setLogLevel(LogLevel logLevel) {

    }

    @Override
    public void setLogLevelString(String logLevelString) {

    }

    private void logMessage(String message, Integer iLoglevel, String messagePrefix) {
        logBuffer.append(messagePrefix + message + System.getProperty("line.separator"));
        Log.d(messagePrefix, message);

        List<String> prefixedList = logMap.get(iLoglevel);
        prefixedList.add(message);
    }

    @Override
    public void verbose(String message, Object ...parameters) {
        logMessage(String.format(message, parameters), LogLevel.VERBOSE.getAndroidLogLevel(), "v ");
    }

    @Override
    public void debug(String message, Object ...parameters) {
        logMessage(String.format(message, parameters), LogLevel.DEBUG.getAndroidLogLevel(), "d ");
    }

    @Override
    public void info(String message, Object ...parameters) {
        logMessage(String.format(message, parameters), LogLevel.INFO.getAndroidLogLevel(), "i ");
    }

    @Override
    public void warn(String message, Object ...parameters) {
        logMessage(String.format(message, parameters), LogLevel.WARN.getAndroidLogLevel(), "w ");
    }

    @Override
    public void error(String message, Object ...parameters) {
        logMessage(String.format(message, parameters), LogLevel.ERROR.getAndroidLogLevel(), "e ");
    }

    @Override
    public void Assert(String message, Object ...parameters) {
        logMessage(String.format(message, parameters), LogLevel.ASSERT.getAndroidLogLevel(), "a ");
    }

    public void test(String message) {
        logMessage(message, 1, "t ");
    }

    private Boolean mapContainsMessage(int level, String beginsWith) {
        ArrayList<String> list = logMap.get(level);
        @SuppressWarnings("unchecked")
        ArrayList<String> listCopy = (ArrayList<String>) list.clone();
        String sList = Arrays.toString(list.toArray());
        for (String log : list) {
            listCopy.remove(0);
            if (log.startsWith(beginsWith)) {
                test(log + " found");
                logMap.put(level, listCopy);
                return true;
            }
        }
        test(sList + " does not contain " + beginsWith);
        return false;
    }

    public Boolean containsMessage(LogLevel level, String beginsWith) {
        return mapContainsMessage(level.getAndroidLogLevel(), beginsWith);
    }

    public Boolean containsTestMessage(String beginsWith) {
        return mapContainsMessage(1, beginsWith);
    }
}
