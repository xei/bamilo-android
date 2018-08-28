package com.bamilo.android.appmodule.modernbamilo.util.locale

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import java.util.*

/**
 * Created by Hamidreza on June 2018.
 * Main idea came from http://stackoverflow.com/questions/40221711/android-context-getresources-updateconfiguration-deprecated/40704077#40704077
 */

class LocaleContextWrapper(base: Context) : ContextWrapper(base) {
    companion object {

        fun wrap(context: Context, language: String?): ContextWrapper {

            if (language == null) {
                return LocaleContextWrapper(context)
            }

            val config = context.resources.configuration
            val locale = Locale(language)
            Locale.setDefault(locale)

            setSystemLocale(config, locale)

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLayoutDirection(locale)
                LocaleContextWrapper(context.createConfigurationContext(config))
            } else {
                context.resources.updateConfiguration(config, context.resources.displayMetrics)
                LocaleContextWrapper(context)
            }

        }

        fun getSystemLocale(config: Configuration): Locale =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    config.locales.get(0)
                } else {
                    config.locale
                }

        private fun setSystemLocale(config: Configuration, locale: Locale) =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale)
            } else {
                config.locale = locale
            }

    }
}