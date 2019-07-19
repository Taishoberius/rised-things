package com.taishoberius.rised.notes.view

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.taishoberius.rised.cross.view.BaseCardView
import com.taishoberius.rised.news.viewmodel.NewsViewModel
import com.taishoberius.rised.notes.viewmodel.NotesViewModel
import kotlinx.android.synthetic.main.news.view.*
import kotlinx.android.synthetic.main.notes.view.*

class NotesView: BaseCardView, LifecycleOwner {
    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private val TAG = "NotesView"
    private val model = NotesViewModel()

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
        model.notesLiveData.observe(this, Observer {
            notes_text.text = it
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