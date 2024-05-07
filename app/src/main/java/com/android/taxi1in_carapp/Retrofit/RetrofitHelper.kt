package com.android.taxi1in_carapp.Retrofit

import android.accounts.NetworkErrorException
import android.util.Log
import com.chaos.view.BuildConfig
import com.google.gson.GsonBuilder
//import com.android.taxi1in_carapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.ConnectException
import java.net.UnknownHostException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.text.ParseException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitHelper {

    private val SERVER_URL = "https://taxi1.com.au/taxinewcar/api/"
    private var gsonAPI: API? = null
    private var connectionCallBack: ConnectionCallBack? = null

    fun getAPI(): API {
        //if (retrofit == null) {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.HEADERS
        logging.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setDateFormat("yyyy-MM-dd")
        gsonBuilder.disableHtmlEscaping()
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
            .build()
        //        }
        return retrofit.create(API::class.java)
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient? {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val TIMEOUT = 2 * 60
        return try { // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            )
            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            /**
             * log request & response only when app is in debug mode
             */
            if (BuildConfig.DEBUG) {
                builder.addInterceptor(interceptor)
            }
            builder.sslSocketFactory(sslSocketFactory,trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier(HostnameVerifier { hostname, session -> true })
            builder.connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS).readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS).writeTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS).build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun callApi(call: Call<ResponseBody>?, callBack: ConnectionCallBack?) {
        connectionCallBack = callBack
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) { //                Utils.appendLog(response.raw().request().url() + "  receive response: " + response.raw().receivedResponseAtMillis() + " send= " + response.raw().sentRequestAtMillis());
//                Utils.appendLog(response.raw().request().url() + "  diff= " + (response.raw().receivedResponseAtMillis() - response.raw().sentRequestAtMillis()));
                if (response.code() == 200) {
                    Log.i("TAG", "onResponse success-"+call.request().url.toString().replace(":"," "))
                    if (connectionCallBack != null) connectionCallBack!!.onSuccess(response)
                } else {
                    try {
                        val res = response.errorBody()!!.string()
                        if (connectionCallBack != null) connectionCallBack!!.onError(response.code(), res)
                    } catch (e: IOException) {
                        Log.i("TAG", "onResponse:"+call.request().url)
                        e.printStackTrace()
                        if (connectionCallBack != null) connectionCallBack!!.onError(response.code(), e.message)
                    } catch (e: NullPointerException) {
                        Log.i("TAG", "onResponse:"+call.request().url)
                        e.printStackTrace()
                        if (connectionCallBack != null) connectionCallBack!!.onError(response.code(), e.message)
                    } catch (e: Exception){
                        Log.i("TAG", "onResponse:"+call.request().url)
                        e.printStackTrace()
                        if (connectionCallBack != null) connectionCallBack!!.onError(response.code(), e.message)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, error: Throwable) {
                var message: String? = null
                if (error is NetworkErrorException || error is ConnectException) {
                    message = "Please check your internet connection"
                } else if (error is ParseException) {
                    message = "Parsing error! Please try again after some time!!"
                } else if (error is TimeoutException) {
                    message = "Connection TimeOut! Please check your internet connection."
                } else if (error is UnknownHostException) {
                    message = "Please check your internet connection and try later"
                } else if (error is Exception) {
                    message = error.message
                }
                if (connectionCallBack != null) connectionCallBack!!.onError(-1, message)
            }
        })
    }


    fun api(): API? {
        return gsonAPI
    }

    interface ConnectionCallBack {
        fun onSuccess(body: Response<ResponseBody?>?)
        fun onError(code: Int, error: String?)
    }
}