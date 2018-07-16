package com.bamilo.modernbamilo.util.logging

import android.util.Log

private const val TAG_DEFAULT = "BAMILO-APP"

/**
 * This class is responsible for Log errors and other information.
 */
class Logger {

    companion object {
        fun log(message: String, tag: String = TAG_DEFAULT, logType: LogType = LogType.INFO) {
            when (logType) {
                LogType.ERROR -> Log.e(tag, message)
                LogType.INFO -> Log.i(tag, message)
                LogType.DEBUG -> Log.d(tag, message)
                LogType.WARNING -> Log.w(tag, message)
            }
        }
    }

}