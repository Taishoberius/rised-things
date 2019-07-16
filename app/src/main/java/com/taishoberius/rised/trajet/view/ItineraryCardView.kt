package com.taishoberius.rised.trajet.view

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.taishoberius.rised.cross.view.BaseCardView
import com.taishoberius.rised.trajet.viewmodel.ItineraryViewModel
import kotlinx.android.synthetic.main.itinerary.view.*

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
        super.onAttachedToWindow()
        lifecycleRegistry.markState(Lifecycle.State.RESUMED)
        model.itineraryLiveData.observe(this, Observer {
            if (it.available) {
                itinerary_info.text = "Temps de trajet estimé: ${it.time}"
            } else {
                itinerary_info.text = "Veuillez saisir une addresse dans l'application"
            }
        })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
    }
}