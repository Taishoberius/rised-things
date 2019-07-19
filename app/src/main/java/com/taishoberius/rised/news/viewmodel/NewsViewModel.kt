package com.taishoberius.rised.news.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.news.NewsService
import com.example.news.NewsURLManager
import com.taishoberius.rised.cross.Rx.RxBus
import com.taishoberius.rised.cross.Rx.RxEvent
import com.taishoberius.rised.cross.viewmodel.IBaseCardViewModel
import com.taishoberius.rised.news.model.NewsList
import io.reactivex.disposables.Disposable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class NewsViewModel: IBaseCardViewModel {

    private var retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
    private  var newsMutableLiveData: MutableLiveData<NewsList> = MutableLiveData()
    var newsLiveData: LiveData<NewsList> = newsMutableLiveData
    private var disposable: Disposable = RxBus.listen(RxEvent.PreferenceEvent::class.java).subscribe {
        if (it.preference.news == true) {
            fetchNews()
        }
    }

    private fun fetchNews() {
        retrofit.create(NewsService::class.java)
            .getNews()
            .enqueue(object: Callback<NewsList> {
                override fun onFailure(call: Call<NewsList>, t: Throwable) {}

                override fun onResponse(call: Call<NewsList>, response: Response<NewsList>) {
                    newsMutableLiveData.value = response.body()
                }
            })
    }

    override fun onCardViewDetached() {
        disposable.dispose()
    }
}