package com.taishoberius.rised.trajet.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.taishoberius.rised.cross.view.BaseCardView
import com.taishoberius.rised.meteo.adapter.MeteoAdapter
import com.taishoberius.rised.trajet.viewmodel.ItineraryViewModel
import kotlinx.android.synthetic.main.itinerary.view.*
import kotlinx.android.synthetic.main.meteo.view.*

class ItineraryCardView: BaseCardView, LifecycleOwner {
    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private val TAG = "ItineraryCardView"
    private val model = ItineraryViewModel()

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
        model.itineraryLiveData.observe(this, Observer {
            Log.w(TAG, "itinerary live data received")
            if (it.available && it.time.isNotEmpty()) {
                itinerary_icon.visibility = View.VISIBLE
                itinerary_info.text = "${it.time}"
            } else {
                itinerary_icon.visibility = View.INVISIBLE
                itinerary_info.text = "${it.time}"
            }
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