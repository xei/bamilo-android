package com.bamilo.modernbamilo.util.retrofit

import android.content.Context
import android.text.TextUtils
import android.util.Base64
import com.bamilo.modernbamilo.BuildConfig
import com.bamilo.modernbamilo.util.storage.getCookie
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.net.HttpCookie
import java.util.ArrayList

/**
 * This object contains the necessary configurations for Retrofit and make the developers free of
 * facing with the complexities. They can just call the method {@link makeWebApi} and all the things
 * will happen.
 *
 * Created by Hamidreza Hosseinkhani on May 22, 2018 at Bamilo.
 */
object RetrofitHelper {

    private val sLogLevel = HttpLoggingInterceptor.Level.BODY

    private const val URL_BASE = BuildConfig.URL_WEBAPI
    private val defaultHeaders = hashMapOf(
//            "app-version" to BuildConfig.VERSION_CODE.toString(),
            "locale" to "fa-ir"
    )

    private var retrofitInstance: Retrofit? = null

    /**
     * This method is responsible for creation of the web APIs via Retrofit.
     */
    @JvmStatic
    fun<T> makeWebApi(context: Context, service: Class<T>): T = getRetrofitInstance(context).create(service)

    /**
     * This method provides a single instance of Retrofit based on the base url, provided
     * OkHttpClient and ConverterFactory
     */
    private fun getRetrofitInstance(context: Context): Retrofit {
        if (retrofitInstance == null) {
            retrofitInstance = Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .client(buildOkHttpClient(context))
                    .addConverterFactory(buildConverterFactory())
                    .build()
        }

        return retrofitInstance!!
    }

    /**
     * This method builds an OkHttpClient instance with the default headers provided by
     * the method "buildDefaultHeaders"
     */
    private fun buildOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient().newBuilder()
                .addInterceptor(getInterceptor())
                .addInterceptor { chain ->
                    val modifiedRequest = chain.request().newBuilder()
                            .headers(Headers.of(defaultHeaders))
                            .build()

                    chain.proceed(modifiedRequest)
                }
                .cookieJar(getCookieJar(context))
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

    /**
     * This class is responsible for read cookie from shared preferences and use it by OkHttp requests.
     * TODO: remove this method while migrating to OAuth. And remove context param.
     */
    private fun getCookieJar(context: Context): CookieJar {
        return object : CookieJar {

            override fun saveFromResponse(httpUrl: HttpUrl, list: List<Cookie>) {

            }

            override fun loadForRequest(httpUrl: HttpUrl): List<Cookie> {
                val sharedCookie = decodeCookie(getCookie(context)!!)
                val cookie = Cookie.Builder()
                        .name(sharedCookie!!.name)
                        .value(sharedCookie.value)
                        .httpOnly()
                        .domain(httpUrl.topPrivateDomain()!!)
                        .path(httpUrl.encodedPath())
                        .build()
                val cookies = ArrayList<Cookie>()
                cookies.add(cookie)
                return cookies
            }
        }
    }

    /**
     * Create a cookie from cookie string Base64.
     *
     * @return Cookie or null
     * @author spereira
     *
     * TODO: remove this method while migrating to OAuth
     */
    private fun decodeCookie(encodedCookieString: String): HttpCookie? {
        var cookie: HttpCookie? = null
        if (!TextUtils.isEmpty(encodedCookieString)) {
            val bytes = Base64.decode(encodedCookieString, Base64.DEFAULT)
            val byteArrayInputStream = ByteArrayInputStream(bytes)
            try {
                val objectInputStream = ObjectInputStream(byteArrayInputStream)
                cookie = (objectInputStream.readObject() as AigPersistentHttpCookie).cookie
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }

        }
        return cookie
    }

}
