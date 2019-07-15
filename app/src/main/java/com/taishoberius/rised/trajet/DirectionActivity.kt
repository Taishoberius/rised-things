package com.taishoberius.rised.trajet

import android.net.Uri
import android.os.Bundle
import android.util.Log
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import Json4Kotlin_Base
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.taishoberius.rised.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DirectionActivity : AppCompatActivity() {

    lateinit var depart: EditText
    lateinit var arrivee: EditText
    lateinit var valider: Button
    lateinit var trajet: TextView
    lateinit var progress: ProgressBar

    lateinit var d: String
    lateinit var a: String
    lateinit var t: String

    lateinit private var service: DirectionService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trajet_activity)

        depart = findViewById(R.id.depart)
        arrivee = findViewById(R.id.arrivee)
        valider = findViewById(R.id.valider)
        trajet = findViewById(R.id.trajet)
        progress = findViewById(R.id.progress)

        valider.setOnClickListener {

            progress.visibility = View.VISIBLE
            trajet.visibility = View.GONE

            d = depart.text.toString()
            a = arrivee.text.toString()
            t = getTrajet(d,a,"transit", object: Listener<String>{
                override fun onSuccess(data: String) {
                    progress.visibility = View.GONE
                    trajet.visibility = View.VISIBLE
                    trajet.text = "Durée du trajet : $data"
                }

                override fun onError(data: String) {
                    progress.visibility = View.GONE
                    trajet.visibility = View.VISIBLE
                    trajet.text = "$data"
                }

            })
        }
    }

    fun getTrajet(origin: String, dest: String, mode: String, listener: Listener<String>): String {
        var duree = ""
        service = Retrofit.Builder()
            .baseUrl(URLManager.getBaseURL())
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DirectionService::class.java)

        service.getDirections(origin, dest, mode).enqueue(object : Callback<Json4Kotlin_Base> {
            override fun onFailure(call: Call<Json4Kotlin_Base>, t: Throwable) {
                Log.d("COUCOU", "failure")
            }

            override fun onResponse(call: Call<Json4Kotlin_Base>, response: Response<Json4Kotlin_Base>) {
                Log.d("COUCOU", "${response.body()}")
                if (response.body()?.status != "ZERO_RESULTS") {
                    response.body()?.also {
                        duree = it.routes.get(0).legs.get(0)
                            .duration.text
                        //.steps.get(0)
                        Log.d("COUCOU", "success ${duree}")
                        listener.onSuccess(duree)
                    }
                } else {
                    listener.onError("Aucun résultat, veuillez réessayer")
                }

            }

        })

        return duree
    }

    interface Listener<T> {
        fun onSuccess(data: T)
        fun onError(data: T)
    }

    fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor {
                val request: Request = it.request()
                val url: HttpUrl = request.url()

                val urlBuilder: HttpUrl.Builder = url.newBuilder()
                    .addQueryParameter("key",URLManager.apiKey)

                val newUrl = urlBuilder.build()
                val newRequest = request.newBuilder().url(newUrl).build()
                it.proceed(newRequest)
            }.build()
    }
}
