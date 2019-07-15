package com.taishoberius.rised.meteo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taishoberius.rised.cross.utils.MeteoUtils
import com.taishoberius.rised.cross.utils.MyDateUtils
import com.taishoberius.rised.meteo.model.Forecast
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
        holder.itemView.imv_day.setImageResource(MeteoUtils.getIcon(forecastList?.get(position)?.weather?.get(0)?.id ?: 0))
        var temp = forecastList?.get(position)?.main?.temp
        if (temp == null) {
            temp = 200.0
        }
        holder.itemView.txv_temp.text = MeteoUtils.kelvinToCelsius(temp)
    }
}