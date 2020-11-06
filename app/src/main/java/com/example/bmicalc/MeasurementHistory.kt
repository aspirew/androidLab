package com.example.bmicalc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MeasurementHistory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurement_history)

        val gson = Gson()
        val intent = intent
        val fetchedMeasurements = intent.getStringExtra("measurement")

        val listType = object : TypeToken<List<MainActivity.Measurement?>?>() {}.type
        val measurements = gson.fromJson<List<MainActivity.Measurement>>(fetchedMeasurements, listType)

        if(measurements.isEmpty())
            findViewById<TextView>(R.id.emptyHistory).visibility = VISIBLE

        initRecyclerView(measurements)

    }

    private fun initRecyclerView(measurements: List<MainActivity.Measurement>){
        val recyclerView = findViewById<RecyclerView>(R.id.RVMeasureHistory)
        val adapter = RecyclerViewAdapter(measurements, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, LinearLayoutManager(this).orientation))
    }
}