package com.taishoberius.rised.news.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.taishoberius.rised.cross.view.BaseCardView
import com.taishoberius.rised.news.viewmodel.NewsViewModel
import com.taishoberius.rised.trajet.viewmodel.ItineraryViewModel
import kotlinx.android.synthetic.main.itinerary.view.*
import kotlinx.android.synthetic.main.news.view.*

class NewsView: BaseCardView, LifecycleOwner{
    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private val TAG = "NewsView"
    private val model = NewsViewModel()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        lifecycleRegistry.markState(Lifecycle.State.INITIALIZED)
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    override fun onAttachedToWindow() {
        lifecycleRegistry.markState(Lifecycle.State.RESUMED)
        super.onAttachedToWindow()
        model.newsLiveData.observe(this, Observer {
            first_news.text = it.articles[0].title
            second_news.text = it.articles[1].title
            third_news.text = it.articles[2].title
            fourth_news.text = it.articles[3].title
            fifth_news.text = it.articles[4].title
        })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
    }

    override fun initView() {

    }

    override fun initListener() {

    }
}