package com.taishoberius.rised.trajet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.taishoberius.rised.cross.Rx.RxBus
import com.taishoberius.rised.cross.Rx.RxEvent
import com.taishoberius.rised.cross.viewmodel.IBaseCardViewModel
import com.taishoberius.rised.main.main.utils.AddressUtil
import com.taishoberius.rised.trajet.model.Json4Kotlin_Base
import com.taishoberius.rised.trajet.service.DirectionService
import io.reactivex.disposables.Disposable
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit.*
import retrofit2.converter.gson.GsonConverterFactory

class ItineraryViewModel: IBaseCardViewModel {

    data class ItineraryRepresentation (
        val time: String,
        val available: Boolean
    )
    private lateinit var itineraryMutableLiveData: MutableLiveData<ItineraryRepresentation>
    var itineraryLiveData: LiveData<ItineraryRepresentation> = itineraryMutableLiveData
    private var preferenceDisposable: Disposable

    init {
        preferenceDisposable = RxBus.listen(RxEvent.PreferenceEvent::class.java).subscribe {
            val preferences = it.preference
            val start = AddressUtil.getCity(preferences.address)
            val end = AddressUtil.getCity(preferences.address)
            var mode = "driving"

            preferences.transportType?.also { transport ->
                if (transport != "vehicule") {
                    mode = "transit"
                }
            }

            if (start != null && end != null) {
                fetchItinerary(start, end, mode)
            } else {
                this.itineraryMutableLiveData.value = ItineraryRepresentation("", false)
            }
        }
    }

    override fun onCardViewDetached() {
        preferenceDisposable.dispose()
    }

    private fun fetchItinerary(origin: String, dest: String, mode: String) {
        val service = Builder()
            .baseUrl(com.taishoberius.rised.trajet.service.URLManager.getBaseURL())
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DirectionService::class.java)

        service.getDirections(origin, dest, mode).enqueue(object : Callback<Json4Kotlin_Base> {
            override fun onFailure(call: Call<Json4Kotlin_Base>, t: Throwable) {
            }

            override fun onResponse(call: Call<Json4Kotlin_Base>, response: Response<Json4Kotlin_Base>) {
                if (response.body()?.status != "ZERO_RESULTS") {
                    response.body().also {
                        val time = it?.routes?.get(0)?.legs?.get(0)?.duration?.text ?: ""
                        itineraryMutableLiveData.value = ItineraryRepresentation(time, true)
                    }
                }
            }
        })
    }
    fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor {
                val request: Request = it.request()
                val url: HttpUrl = request.url()

                val urlBuilder: HttpUrl.Builder = url.newBuilder()
                    .addQueryParameter("key", com.taishoberius.rised.trajet.service.URLManager.apiKey)

                val newUrl = urlBuilder.build()
                val newRequest = request.newBuilder().url(newUrl).build()
                it.proceed(newRequest)
            }.build()
    }
}