package com.example.bmicalc


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bmicalc.databinding.ActivityBmiDetailsBinding


class BmiDetails : AppCompatActivity() {

    lateinit var binding: ActivityBmiDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi_details)
        binding = ActivityBmiDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val result = intent.getDoubleExtra("bmi", 0.0)
        val name = intent.getStringExtra("bmiDetailsName")
        val color = intent.getStringExtra("bmiDetailsColor")
        val description = intent.getStringExtra("bmiDetailsDescription")
        val image = intent.getIntExtra("bmiImage", 0)

        binding.apply {
            root.setBackgroundColor(Color.parseColor(color))
            TVResult.text = result.toString()
            TVName.text = name
            TVDescription.text = description
            imageView.setImageResource(image)

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        val returnIntent = Intent()
        setResult(RESULT_CANCELED, returnIntent)
    }

}