package com.taishoberius.rised.cross.com

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ForecastRetrofitBuilder {

    fun getFiveDaysForecastRetrofit(): Retrofit {
        return getForecastRetrofit()
            .client(getFiveDaysForecastHttpClient())
            .build()
    }

    fun getCurrentWeatherRetrofit(): Retrofit {
        return getForecastRetrofit()
            .client(getCommonHttpClientBuilder().build())
            .build()
    }

    private fun getForecastRetrofit(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(URLManager.getBaseForecastURL())
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
    }

    private fun getFiveDaysForecastHttpClient(): OkHttpClient {
        return getCommonHttpClientBuilder()
            .addInterceptor {chain ->
                val res = chain.proceed(chain.request())
                val stringRes = res.body()?.string()
                val jsonObject = JSONObject(stringRes)
                val contentType = res.body()?.contentType()
                val responseBody = ResponseBody.create(contentType, jsonObject.get("list").toString())

                res.newBuilder().body(responseBody).build()
            }
            .build()
    }

    private fun getCommonHttpClientBuilder(): OkHttpClient.Builder {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(logging)
    }
}