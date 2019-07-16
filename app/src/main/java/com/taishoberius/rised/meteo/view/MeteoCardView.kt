package com.taishoberius.rised.meteo.view

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.taishoberius.rised.cross.view.BaseCardView
import com.taishoberius.rised.meteo.adapter.MeteoAdapter
import com.taishoberius.rised.meteo.viewmodel.IMeteoCardViewModel
import com.taishoberius.rised.meteo.viewmodel.MeteoCardViewModel
import kotlinx.android.synthetic.main.meteo.view.*

class MeteoCardView: BaseCardView, LifecycleOwner {

    //================================================================================
    // Attributes
    //================================================================================

    private var model: IMeteoCardViewModel = MeteoCardViewModel()
    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private val TAG = "MeteoCardView"
    private lateinit var meteoAdapter: MeteoAdapter

    //================================================================================
    // Constructors
    //================================================================================

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        lifecycleRegistry.markState(Lifecycle.State.INITIALIZED)

    }

    //================================================================================
    // LifeCycle
    //================================================================================

    override fun onAttachedToWindow() {
        lifecycleRegistry.markState(Lifecycle.State.RESUMED)
        super.onAttachedToWindow()
        model.getFiveDaysForecastLiveData().observe(this, Observer { fcList ->
            meteoAdapter.forecastList = fcList
            meteoAdapter.notifyDataSetChanged()
        })
    }

    override fun onDetachedFromWindow() {
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
        model.getFiveDaysForecastLiveData().removeObservers(this)
        model.onCardViewDetached()
        super.onDetachedFromWindow()
    }

    //================================================================================
    // Business method
    //================================================================================

    override fun initView() {
        model.launchGetWeather()
        meteoAdapter = MeteoAdapter()
        val lm = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rcv_meteo.apply {
            adapter = meteoAdapter
            layoutManager = lm
        }
    }

    override fun initListener() {

    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}