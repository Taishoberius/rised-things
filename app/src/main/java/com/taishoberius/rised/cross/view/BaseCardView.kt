package com.taishoberius.rised.cross.view

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView

open class BaseCardView: CardView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initView()
        initListener()
    }

    open protected fun initView() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    open protected fun initListener() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}