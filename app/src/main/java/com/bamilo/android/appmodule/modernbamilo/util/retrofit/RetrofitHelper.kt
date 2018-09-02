package com.bamilo.android.appmodule.modernbamilo.util.retrofit

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.Base64
import com.bamilo.android.BuildConfig
import com.bamilo.android.appmodule.modernbamilo.util.storage.getCookie
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.net.HttpCookie
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

/**
 * This object contains the necessary configurations for Retrofit and make the developers free of
 * facing with the complexities. They can just call the method {@link makeWebApi} and all the things
 * will happen.
 *
 * Created by Hamidreza Hosseinkhani on May 22, 2018 at Bamilo.
 */
object RetrofitHelper {

    private const val URL_BASE = BuildConfig.URL_WEBAPI
    private val defaultHeaders = hashMapOf("locale" to "fa-ir")

    private val sLogLevel = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

    private var retrofitInstance: Retrofit? = null

    /**
     * This method is responsible for creation of the web APIs via Retrofit.
     */
    @JvmStatic
    fun <T> makeWebApi(context: Context, service: Class<T>): T = getRetrofitInstance(context).create(service)

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
    private fun buildOkHttpClient(context: Context) = OkHttpClient().newBuilder()
            .addInterceptor(getLogInterceptor())
            .addInterceptor { chain ->
                val modifiedRequest = chain.request().newBuilder()
                        .headers(Headers.of(defaultHeaders))
                        .build()
                chain.proceed(modifiedRequest)
            }.apply {
                setCertificates(this)
                followSslRedirects(false)
                followRedirects(true)
                setTimeOutToOkHttpClient(this)

                getCookie(context)?.takeIf { !it.isEmpty() }.apply {
                    cookieJar(getCookieJar(context))
                }

            }.build()


    private fun setCertificates(okHttpClientBuilder: OkHttpClient.Builder) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                val cf = CertificateFactory.getInstance("X.509")
                val certString = "-----BEGIN CERTIFICATE-----\n" +
                        "MIIEYjCCA0qgAwIBAgIKIQak23Abuwj69DANBgkqhkiG9w0BAQsFADBLMQswCQYD\n" +
                        "VQQGEwJOTzEdMBsGA1UECgwUQnV5cGFzcyBBUy05ODMxNjMzMjcxHTAbBgNVBAMM\n" +
                        "FEJ1eXBhc3MgQ2xhc3MgMiBDQSAyMB4XDTE3MTEyODE0MDUyMloXDTE4MTEyODIy\n" +
                        "NTkwMFowFTETMBEGA1UEAwwKYmFtaWxvLmNvbTCCASIwDQYJKoZIhvcNAQEBBQAD\n" +
                        "ggEPADCCAQoCggEBANnJ77A/bgpINMEuz4f9LpjdckYnB5N4t8DzFlIvrcqV3DpA\n" +
                        "0n0e4UpI1DO+Vx+8PEDsbzo8cdbVORxpaBR8kLcc8t3KP14F0J6jTBBzTX9/hVWl\n" +
                        "uNxOJaIjAe/1d3mMa8ZrKNYc3Ut7cReLU+QNGiPTsOoXkQ3i5hQlmkAIdx8ChDbM\n" +
                        "VU9UerjOJC+unXBWRKVyht2FwJG/bmWRb2UcuQLCHWTeMiP/T2Ino3KGo1C6IGLh\n" +
                        "gC8mYQtSocZU6xDnGlLua44qdKAVb0kUGnY06a7/+fZ5OUNoUVadoIvtlCZNpCPm\n" +
                        "ZwHTWYGh+KcDtEbDMLfBkewPru2bfLaEAKaBv4ECAwEAAaOCAXwwggF4MAkGA1Ud\n" +
                        "EwQCMAAwHwYDVR0jBBgwFoAUkq1libIAD8tRDcEj7JROj8EEP3cwHQYDVR0OBBYE\n" +
                        "FGWPQzy3EOK868sbyas29TmC1K+KMA4GA1UdDwEB/wQEAwIFoDAdBgNVHSUEFjAU\n" +
                        "BggrBgEFBQcDAQYIKwYBBQUHAwIwHwYDVR0gBBgwFjAKBghghEIBGgECBDAIBgZn\n" +
                        "gQwBAgEwOgYDVR0fBDMwMTAvoC2gK4YpaHR0cDovL2NybC5idXlwYXNzLm5vL2Ny\n" +
                        "bC9CUENsYXNzMkNBMi5jcmwwMwYDVR0RBCwwKoIKYmFtaWxvLmNvbYIMKi5iYW1p\n" +
                        "bG8uY29tgg53d3cuYmFtaWxvLmNvbTBqBggrBgEFBQcBAQReMFwwIwYIKwYBBQUH\n" +
                        "MAGGF2h0dHA6Ly9vY3NwLmJ1eXBhc3MuY29tMDUGCCsGAQUFBzAChilodHRwOi8v\n" +
                        "Y3J0LmJ1eXBhc3Mubm8vY3J0L0JQQ2xhc3MyQ0EyLmNlcjANBgkqhkiG9w0BAQsF\n" +
                        "AAOCAQEAS3xvoo4kSQNYLWb3C2UAvG+kM2SHcju4juFOEaGZ9jomEaEYZr2FTwPq\n" +
                        "RHPEnbBjtnCXezYxGR8vYr9ESivJgF69AnkOrzBeR9/wBfCYuen4eUvn1QtMhBWv\n" +
                        "Ge5fawrapB8bDgjX+pbMk+USCIh/lctLNur5e3ZwjQwhF0SnfQvFDjNNejAhsTw/\n" +
                        "hW8pKiCjbgb0rLv9Wtf6biDcc+io6scFO/gIg5QZ/daBGnU8b2njfMOWVZjr/dyW\n" +
                        "X1HvB8iQrRWiwWhddsOty8qgzcSpfCpO9aZDNbGXxFtDVHXl4Vo+kI/P+Dn8NgSC\n" +
                        "eKcCU2WVVLQP/4wB2Wftk6tZoxWL0g==\n" +
                        "-----END CERTIFICATE-----\n" +
                        "-----BEGIN CERTIFICATE-----\n" +
                        "MIIFADCCAuigAwIBAgIBGjANBgkqhkiG9w0BAQsFADBOMQswCQYDVQQGEwJOTzEd\n" +
                        "MBsGA1UECgwUQnV5cGFzcyBBUy05ODMxNjMzMjcxIDAeBgNVBAMMF0J1eXBhc3Mg\n" +
                        "Q2xhc3MgMiBSb290IENBMB4XDTE2MTIwMjEwMDAwMFoXDTMwMTAyNjA5MTYxN1ow\n" +
                        "SzELMAkGA1UEBhMCTk8xHTAbBgNVBAoMFEJ1eXBhc3MgQVMtOTgzMTYzMzI3MR0w\n" +
                        "GwYDVQQDDBRCdXlwYXNzIENsYXNzIDIgQ0EgMjCCASIwDQYJKoZIhvcNAQEBBQAD\n" +
                        "ggEPADCCAQoCggEBAJyrZ8aWSw0PkdLsyswzK/Ny/A5/uU6EqQ99c6omDMpI+yNo\n" +
                        "HjUO42ryrATs4YHla+xj+MieWyvz9HYaCnrGL0CE4oX8M7WzD+g8h6tUCS0AakJx\n" +
                        "dC5PBocUkjQGZ5ZAoF92ms6C99qfQXhHx7lBP/AZT8sCWP0chOf9/cNxCplspYVJ\n" +
                        "HkQjKN3VGa+JISavCcBqf33ihbPZ+RaLjOTxoaRaWTvlkFxHqsaZ3AsW71qSJwaE\n" +
                        "55l9/qH45vn5mPrHQJ8h5LjgQcN5KBmxUMoA2iT/VSLThgcgl+Iklbcv9rs6aaMC\n" +
                        "JH+zKbub+RyRijmyzD9YBr+ZTaowHvJs9G59uZMCAwEAAaOB6zCB6DAPBgNVHRMB\n" +
                        "Af8EBTADAQH/MB8GA1UdIwQYMBaAFMmAd+BikoL1RpzzuvdMw964o605MB0GA1Ud\n" +
                        "DgQWBBSSrWWJsgAPy1ENwSPslE6PwQQ/dzAOBgNVHQ8BAf8EBAMCAQYwEQYDVR0g\n" +
                        "BAowCDAGBgRVHSAAMD0GA1UdHwQ2MDQwMqAwoC6GLGh0dHA6Ly9jcmwuYnV5cGFz\n" +
                        "cy5uby9jcmwvQlBDbGFzczJSb290Q0EuY3JsMDMGCCsGAQUFBwEBBCcwJTAjBggr\n" +
                        "BgEFBQcwAYYXaHR0cDovL29jc3AuYnV5cGFzcy5jb20wDQYJKoZIhvcNAQELBQAD\n" +
                        "ggIBAESxBqAQmbrsyDeWL7r3QspDhkZxX+VtqHkI6A9OxR1HgahHDW4jJgGypwP4\n" +
                        "jjWkvqZ7lG4DHNv4tfAiR9bEg8wrRC9HLzF+Jm6vtUJsa/sMmkLDlmL8OKKFEZwI\n" +
                        "oBwEuwbCpDByTkD4m7ckPLI0XMzCxSXanKGgtxzFQdmnUHP3NpK9SdULGvz6E3I6\n" +
                        "QomcWJXRqOo8QOrnOLEkI5OM4Bq9lx/GVHubIOz2GZfiX3x91pcb6IxayTSIQDlL\n" +
                        "mcinv/AHInVqrVH/7hWv90yA6qG9LZc10DvnNw5/MyHEZuYsNqo8Gh14SNPqU696\n" +
                        "TSwDzEBHo4LgQsVdQlGCmmr0tF6Lu7kJlukYXFBTDFYD45pG2mPbtHgOQfBLOVjx\n" +
                        "/iNlxhu31vzcII0USKDnCYPaCOyXihnLbblHiJFTXzhNbZEstoSBH6PfChkUxORc\n" +
                        "YQ6w69TtOXs75fvfP4aVcBc6tIALQKYews3+xxqDOmXxJmL5d+B/sxzlmHruNELu\n" +
                        "Nqc5I66yJGEBrhIhNrRBUXIIz+W5w+uw2zg1T9Fo4Nq4EmEL4o1o6DyvI50D0EWZ\n" +
                        "0fkjkYNhL7htM6ktLyBoqMZaMDFsfD9S3e6ZjyNbpjHMFEvhr0vHVHWBMScYs6q2\n" +
                        "owfuqc6rT+Pco5CyOfYF33ke+z/hFe+6n6Y3oiGMDqvaTAfp\n" +
                        "-----END CERTIFICATE-----\n"
                val cert = ByteArrayInputStream(certString.toByteArray())
                val ca: Certificate
                try {
                    ca = cf.generateCertificate(cert)
                } finally {
                    cert.close()
                }

                val keyStoreType = KeyStore.getDefaultType()
                val keyStore = KeyStore.getInstance(keyStoreType)
                keyStore.load(null, null)
                keyStore.setCertificateEntry("ca", ca)

                // creating a TrustManager that trusts the CAs in our KeyStore
                val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
                val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
                tmf.init(keyStore)

                // creating an SSLSocketFactory that uses our TrustManager
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, tmf.trustManagers, null)

                okHttpClientBuilder.sslSocketFactory(sslContext.socketFactory)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setTimeOutToOkHttpClient(okHttpClientBuilder: OkHttpClient.Builder) = okHttpClientBuilder.apply {
        readTimeout(BuildConfig.SOCKET_TIMEOUT_SECOND, TimeUnit.SECONDS)
        connectTimeout(BuildConfig.CONNECTION_TIMEOUT_SECOND, TimeUnit.SECONDS)
        writeTimeout(BuildConfig.WRITE_TIMEOUT_SECOND, TimeUnit.SECONDS)
    }

    /**
     * This method builds a converter factory (Gson, Protobuf, ...) to convert objects
     * to/from a serialization.
     */
    private fun buildConverterFactory(): Converter.Factory = GsonConverterFactory.create()

    /**
     * This method sets the log level of Logging Interceptor of OkHttp
     */
    private fun getLogInterceptor() = HttpLoggingInterceptor().apply { level = sLogLevel }

    /**
     * This class is responsible for read cookie from shared preferences and use it by OkHttp requests.
     * TODO: remove this method while migrating to OAuth. And remove context param.
     */
    private fun getCookieJar(context: Context): CookieJar {
        return object : CookieJar {

            override fun saveFromResponse(httpUrl: HttpUrl, list: List<Cookie>) {

            }

            override fun loadForRequest(httpUrl: HttpUrl): List<Cookie>? {
                val cookies = ArrayList<Cookie>()

                val sharedCookie = decodeCookie(getCookie(context))

                val cookieBuilder = Cookie.Builder()
                cookieBuilder.httpOnly()
                        .domain(httpUrl.topPrivateDomain()!!)
                        .path(httpUrl.encodedPath())

                if (sharedCookie == null) {
                    cookieBuilder
                            .name("")
                            .value("")
                } else {
                    cookieBuilder
                            .name(sharedCookie.name)
                            .value(sharedCookie.value)
                }

                cookies.add(cookieBuilder.build())
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
