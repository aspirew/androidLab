package com.example.bmicalc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bmicalc.RecyclerViewAdapter.measurementsViewHolder

class RecyclerViewAdapter(
        val measurements: List<MainActivity.Measurement>,
        val context: Context
) : RecyclerView.Adapter<RecyclerViewAdapter.measurementsViewHolder>() {


    class measurementsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date = itemView.findViewById<TextView>(R.id.RVItemDate)
        val result = itemView.findViewById<TextView>(R.id.RVItemResult)
        val name = itemView.findViewById<TextView>(R.id.RVItemName)
        val mass = itemView.findViewById<TextView>(R.id.RVItemMass)
        val height = itemView.findViewById<TextView>(R.id.RVItemHeight)
        val massUnit = itemView.findViewById<TextView>(R.id.RVItemMassUnit)
        val heightUnit  = itemView.findViewById<TextView>(R.id.RVItemHeightUnit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): measurementsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.measurement_history_item, parent, false)
        return measurementsViewHolder(view)
    }

    override fun onBindViewHolder(holder: measurementsViewHolder, position: Int) {

        val metricSystem = Pair("kg", "cm")
        val imperialSystem = Pair("lb", "in")
        var activeSystem = metricSystem

        val date = measurements[position].date.substring(0, 10)
        val hour = measurements[position].date.substring(11, 19)

        holder.date.text = "$date $hour"
        holder.result.text = measurements[position].value.toString()
        holder.name.text = measurements[position].name
        holder.mass.text = measurements[position].mass.toString()
        holder.height.text = measurements[position].height.toString()

        if(measurements[position].system != 0)
            activeSystem = imperialSystem

        holder.massUnit.text = activeSystem.first
        holder.heightUnit.text = activeSystem.second

    }

    override fun getItemCount(): Int {
        return measurements.size
    }

}