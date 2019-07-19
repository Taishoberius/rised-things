package com.example.news

import com.taishoberius.rised.news.model.NewsList
import retrofit2.Call
import retrofit2.http.GET

public interface NewsService {

    @GET("getPreferences")
    fun getNews(): Call<NewsList>
}