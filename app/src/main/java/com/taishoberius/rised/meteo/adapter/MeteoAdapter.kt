package com.taishoberius.rised.meteo.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taishoberius.rised.meteo.model.Forecast
import android.view.LayoutInflater
import com.taishoberius.rised.R
import com.taishoberius.rised.cross.utils.MyDateUtils
import kotlinx.android.synthetic.main.meteo_item.view.*


class MeteoAdapter() : RecyclerView.Adapter<MeteoViewHolder>() {

    var forecastList: List<Forecast>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeteoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.meteo_item, parent, false)
        return MeteoViewHolder(view)

    }

    override fun getItemCount(): Int {
        return forecastList?.count() ?: 0
    }

    override fun onBindViewHolder(holder: MeteoViewHolder, position: Int) {
        holder.itemView.txv_day.text = MyDateUtils.getToday(position)
    }
}