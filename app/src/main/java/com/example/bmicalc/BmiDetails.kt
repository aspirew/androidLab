package com.example.bmicalc

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bmicalc.databinding.ActivityBmiDetailsBinding

class BmiDetails : AppCompatActivity() {

    lateinit var binding: ActivityBmiDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_bmi_details)
        binding = ActivityBmiDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val result = intent.getDoubleExtra("bmi", 0.0)
        val name = intent.getStringExtra("bmiDetailsName")
        val color = intent.getStringExtra("bmiDetailsColor")
        val description = intent.getStringExtra("bmiDetailsDescription")

        binding.apply {
            root.setBackgroundColor(Color.parseColor(color))
            TVResult.text = result.toString()
            //TVName.text = name
            TVDescription.text = description
        }


    }
}