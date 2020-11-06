package com.example.bmicalc

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.bmicalc.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime


// Rafał Behrendt 246643

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var maxHeight = 0.0
    var minHeight = 0.0
    var maxMass = 0
    var minMass = 0
    var bmiResult = 0.0
    private val LAST_BMI_RESULT = "lastBmiResult"
    private val LAST_SYSTEM_OPTION = "lastSystemOption"
    private val METRIC_SYSTEM = 0
    private val IMPERIAL_SYSTEM = 1
    private val HISTORY_CAPACITY = 10
    private val SHARED_PREF_KEY = "Measurement History"

    var systemOption = METRIC_SYSTEM


    val LAUNCH_SECOND_ACTIVITY = 1

    data class BmiClass(val name: String, val color: String, val description: String, val imageSource: Int)
    data class Measurement(val date: String, val mass: Double, val height: Int, val system: Int, val value: Double, val name: String)

    private val bmiOperation = BmiOperations()

    private var sharedPref: SharedPreferences? = null

    private val measures: MutableList<Measurement> = mutableListOf()

    private val bmiClassification = mapOf(
            Pair(0.0, 16.0) to BmiClass(
                    "Starvation",
                    "#082E79",
                    "Don't you feel hungry sometimes? Your bmi is of starving person.",
                    Integer.valueOf(R.drawable.starving)
            ),
            Pair(16.0, 17.0) to BmiClass(
                    "Emaciation",
                    "#4169E1",
                    "Some people would probably envy you, but you gotta eat. You're absolutely too skinny",
                    Integer.valueOf(R.drawable.emaciation)
            ),
            Pair(17.0, 18.5) to BmiClass(
                    "Underweight",
                    "#ACE1AF",
                    "It's not a common first world problem to be too skinny, but apparently its yours for some reason.",
                    Integer.valueOf(R.drawable.underweight)
            ),
            Pair(18.5, 25.0) to BmiClass(
                    "Normal weight",
                    "#CDEBA7",
                    "True neutral, perfect weight. Keep it up.",
                    Integer.valueOf(R.drawable.perfect)
            ),
            Pair(25.0, 30.0) to BmiClass(
                    "Overweight",
                    "#FFFF99",
                    "That's one burger too much.",
                    Integer.valueOf(R.drawable.overweight)
            ),
            Pair(30.0, 35.0) to BmiClass(
                    "Level I obesity",
                    "#FDE456",
                    "That's your last chance to go back, it will be very hard over this point to be healthy again",
                    Integer.valueOf(R.drawable.obesity1)
            ),
            Pair(35.0, 40.0) to BmiClass(
                    "Level II obesity",
                    "#CF2929",
                    "You're on your way to get american citizenship. I'm not sure if you should be aiming for that",
                    Integer.valueOf(R.drawable.obesity2)
            ),
            Pair(40.0, Double.MAX_VALUE) to BmiClass(
                    "Level III obesity",
                    "#801818",
                    "I know pizza is great. But do you know what is even greater? Not dying on heart attack. Or any other disease that you're very likely to get",
                    Integer.valueOf(R.drawable.obesity3)
            )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setMetricSystem()

        sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val gson = Gson()

        val listType = object : TypeToken<List<Measurement?>?>() {}.type
        val json: String? = sharedPref?.getString(
                SHARED_PREF_KEY,
                ""
        )

        val measureHistory: List<Measurement?>? = gson.fromJson(json, listType)

        if (measureHistory != null)
            measures.addAll(gson.fromJson(json, listType))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.metric_system -> {
                setMetricSystem()
                true
            }
            R.id.imperial_system -> {
                setImperialSystem()
                true
            }
            R.id.history -> {
                val intent = Intent(this@MainActivity, MeasurementHistory::class.java)
                val gson = Gson()
                val json = gson.toJson(measures)
                intent.putExtra("measurement", json)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putInt(LAST_SYSTEM_OPTION, systemOption)
            putDouble(LAST_BMI_RESULT, bmiResult)
        }
        super.onSaveInstanceState(outState)

        val gson = Gson()

        with(sharedPref!!.edit()) {
            val json = gson.toJson(measures)
            putString(SHARED_PREF_KEY, json)
            commit()
        }

    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        savedInstanceState.run {
            systemOption = getInt(LAST_SYSTEM_OPTION)
            bmiResult = getDouble(LAST_BMI_RESULT)
        }

        when (systemOption) {
            IMPERIAL_SYSTEM -> setImperialSystem()
        }

        val bmiClass = pickCorrectBmiClass(bmiResult)

        binding.apply {
            bmiTV.text = bmiResult.toString()
            bmiTV.setTextColor(Color.parseColor(bmiClass.color))
        }
    }

    fun count(view: View) {

        binding.apply {
            val massOK = checkConstraints(massET.text, minMass.toDouble(), maxMass.toDouble())
            val heightOK = checkConstraints(heightET.text, minHeight, maxHeight)
            if (!massOK) {
                massET.error = getString(R.string.required_value_invalid)
            }
            if (!heightOK) {
                heightET.error = getString(R.string.required_value_invalid)
            }
            if (massOK && heightOK) {

                val height = heightET.text.toString().toInt()
                val mass = massET.text.toString().toDouble()

                bmiResult = bmiOperation.calculateBMI(
                        mass,
                        height,
                        systemOption
                )

                val bmiClass = pickCorrectBmiClass(bmiResult)
                bmiTV.text = bmiResult.toString()
                bmiTV.setTextColor(Color.parseColor(bmiClass.color))

                val measure = Measurement(LocalDateTime.now().toString(), mass, height, systemOption, bmiResult, bmiClass.name)

                if (measures.size >= HISTORY_CAPACITY)
                    measures.removeFirst()

                measures.add(measure)

            }
        }
    }


    fun showDetails(view: View) {
        if (bmiResult != 0.0) {
            val bmiClass = pickCorrectBmiClass(bmiResult)
            val intent = Intent(this@MainActivity, BmiDetails::class.java)
            intent.putExtra("bmi", bmiResult)
            intent.putExtra("bmiDetailsName", bmiClass.name)
            intent.putExtra("bmiDetailsColor", bmiClass.color)
            intent.putExtra("bmiDetailsDescription", bmiClass.description)
            intent.putExtra("bmiImage", bmiClass.imageSource)
            startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY)
        }
    }

    private fun checkConstraints(text: Editable, minVal: Double, maxVal: Double): Boolean {
        if (!text.isBlank()) {
            return text.toString().toDouble() in minVal..maxVal
        }
        return false
    }

    private fun pickCorrectBmiClass(result: Double): BmiClass {
        for ((range, bmiclass) in bmiClassification) {
            if (result >= range.first && result < range.second) {
                return bmiclass
            }
        }
        return BmiClass("undefined", "#FFFFFF", "none", 0)
    }

    private fun setMetricSystem() {
        binding.apply {
            massTV.setText(R.string.mass_kg)
            heightTV.setText(R.string.height_cm)
            minHeight = 100.0
            maxHeight = 250.0
            minMass = 30
            maxMass = 200
            systemOption = METRIC_SYSTEM
        }
    }

    private fun setImperialSystem() {
        binding.apply {
            massTV.setText(R.string.mass_lb)
            heightTV.setText(R.string.height_in)
            minHeight = 40.0
            maxHeight = 100.0
            minMass = 60
            maxMass = 420
            systemOption = IMPERIAL_SYSTEM
        }
    }

}