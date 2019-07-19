package com.example.news

object NewsURLManager {

    public val url = "https://newsapi.org/v2/"
    public val apiKey = "2dc6c5a025a74548b3b2729040e8bd54"

    fun getBaseURL(): String {
        return url
    }

}