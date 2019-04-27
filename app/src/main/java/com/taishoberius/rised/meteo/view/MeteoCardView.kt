package com.taishoberius.rised.meteo.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.taishoberius.rised.cross.view.BaseCardView
import com.taishoberius.rised.meteo.adapter.MeteoAdapter
import com.taishoberius.rised.meteo.viewmodel.IMeteoCardViewModel
import com.taishoberius.rised.meteo.viewmodel.MeteoCardViewModel
import kotlinx.android.synthetic.main.meteo.view.*

class MeteoCardView: BaseCardView, IMeteoCardView, LifecycleOwner {

    private var model: IMeteoCardViewModel = MeteoCardViewModel()
    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private val TAG = "MeteoCardView"

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        lifecycleRegistry.markState(Lifecycle.State.INITIALIZED)

    }

    override fun onAttachedToWindow() {
        lifecycleRegistry.markState(Lifecycle.State.RESUMED)
        super.onAttachedToWindow()
        model.getCurrentWeatherLiveData().observe(this as LifecycleOwner, Observer { fc ->
            Log.d(TAG, fc.toString())
        })
    }

    override fun onDetachedFromWindow() {
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
        model.onCardViewDetached()
        super.onDetachedFromWindow()
    }

    override fun initView() {
        model.launchGetCurrentWeather()
        model.launchGetForecast()
        rcv_meteo.adapter = MeteoAdapter()
    }

    override fun initListener() {

    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}