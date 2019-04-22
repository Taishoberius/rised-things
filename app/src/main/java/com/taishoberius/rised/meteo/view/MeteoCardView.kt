package com.taishoberius.rised.meteo.view

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.taishoberius.rised.meteo.viewmodel.IMeteoCardViewModel
import com.taishoberius.rised.meteo.viewmodel.MeteoCardViewModel

class MeteoCardView: CardView, IMeteoCardView {

    private lateinit var model: IMeteoCardViewModel


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        model = MeteoCardViewModel()
        initView()
        initListener()
    }

    override fun onDetachedFromWindow() {
        model.onCardViewDetached()
        super.onDetachedFromWindow()
    }

    private fun initView() {
        model.getCurrendWeather()
    }

    private fun initListener() {

    }
}