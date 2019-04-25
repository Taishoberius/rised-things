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

    private lateinit var model: IMeteoCardViewModel
    private lateinit var lifecycleRegistry: LifecycleRegistry
    private val TAG = "MeteoCardView"

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        model = MeteoCardViewModel()
        model.getCurrentWeatherLiveData().observe(this as LifecycleOwner, Observer { fc ->
            Log.d(TAG, fc.toString())
        })
    }

    override fun onDetachedFromWindow() {
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