package com.bamilo.modernbamilo.util.retrofit

import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * This object contains the necessary configurations for Retrofit and make the developers free of
 * facing with the complexities. They can just call the method {@link makeWebApi} and all the things
 * will happen.
 *
 * Created by Hamidreza Hosseinkhani on May 22, 2018 at Bamilo.
 */
object RetrofitHelper {

    private val sLogLevel = HttpLoggingInterceptor.Level.HEADERS

    private const val URL_BASE = "http://bamilo.com/mobapi/v2.9/"
    private val defaultHeaders = hashMapOf(
//            "app-version" to BuildConfig.VERSION_CODE.toString(),
            "locale" to "fa-ir"
    )

    private var retrofitInstance: Retrofit? = null

    /**
     * This method is responsible for creation of the web APIs via Retrofit.
     */
    fun<T> makeWebApi(service: Class<T>): T = getRetrofitInstance().create(service)

    /**
     * This method provides a single instance of Retrofit based on the base url, provided
     * OkHttpClient and ConverterFactory
     */
    private fun getRetrofitInstance(): Retrofit {
        if (retrofitInstance == null) {
            retrofitInstance = Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .client(buildOkHttpClient())
                    .addConverterFactory(buildConverterFactory())
                    .build()
        }

        return retrofitInstance!!
    }

    /**
     * This method builds an OkHttpClient instance with the default headers provided by
     * the method "buildDefaultHeaders"
     */
    private fun buildOkHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder()
                .addInterceptor(getInterceptor())
//                .addInterceptor { chain ->
//                    val modifiedRequest = chain.request().newBuilder()
//                            .headers(Headers.of(defaultHeaders))
//                            .build()
//
//                    chain.proceed(modifiedRequest)
//                }
                .build()
    }

    /**
     * This method builds a converter factory (Gson, Protobuf, ...) to convert objects
     * to/from a serialization.
     */
    private fun buildConverterFactory() : Converter.Factory = GsonConverterFactory.create()

    /**
     * This method sets the log level of Logging Interceptor of OkHttp
     */
    private fun getInterceptor() = HttpLoggingInterceptor().apply {
        this.level = sLogLevel
    }

}
