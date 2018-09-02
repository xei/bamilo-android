package com.bamilo.android.appmodule.bamiloapp.utils.imageloader

import android.content.Context
import com.bamilo.android.BuildConfig
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * Created by Farshid since 5/13/2018. contact farshidabazari@gmail.com
 */

@GlideModule
class CustomGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val client = UnsafeOkHttpClient.getOkHttpBuilder()
                .apply { setTimeOutToOkHttpClient(this) }
                .build()

        registry.replace(GlideUrl::class.java, InputStream::class.java,
                OkHttpUrlLoader.Factory(client))
    }

    private fun setTimeOutToOkHttpClient(okHttpClientBuilder: OkHttpClient.Builder) = okHttpClientBuilder.apply {
        readTimeout(BuildConfig.SOCKET_TIMEOUT_SECOND, TimeUnit.SECONDS)
        connectTimeout(BuildConfig.CONNECTION_TIMEOUT_SECOND, TimeUnit.SECONDS)
        writeTimeout(BuildConfig.WRITE_TIMEOUT_SECOND, TimeUnit.SECONDS)
    }
}