package com.example.bmicalc

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.bmicalc.databinding.ActivityMainBinding
import kotlin.math.pow
import kotlin.math.round


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
    var systemOption = METRIC_SYSTEM

    data class BmiClass(val name: String, val color: String, val description: String)

    private val bmiOperation = BmiOperations()

    private val bmiClassification = mapOf(
            Pair(0.0, 16.0) to BmiClass("Starvation", "#082E79", "Don't you feel hungry sometimes? Your bmi is of starving person."),
            Pair(16.0, 17.0) to BmiClass("Emaciation", "#4169E1", "It's not a common first world problem to be too skinny, but apparently its yours for some reason."),
            Pair(17.0, 18.5) to BmiClass("Underweight", "#ACE1AF", "You're looking too skinny cj. Go and get us something to eat"),
            Pair(18.5, 25.0) to BmiClass("Normal weight", "#CDEBA7", "True neutral, perfect weight. Keep it up."),
            Pair(25.0, 30.0) to BmiClass("Overweight", "#FFFF99", "That's one burger too much."),
            Pair(30.0, 35.0) to BmiClass("Level I obesity", "#FDE456", "That's your last chance to go back, it will be very hard over this point to be healthy again"),
            Pair(35.0, 40.0) to BmiClass("Level II obesity", "#CF2929", "You're on your way to get american citizenship. I'm not sure if you should be aiming for that"),
            Pair(40.0, Double.MAX_VALUE) to BmiClass("Level III obesity", "#801818", "I know pizza is great. But do you know what is even greater? Not dying on heart attack. Or any other disease that you're very likely to get")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setMetricSystem()
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putInt(LAST_SYSTEM_OPTION, systemOption)
            putDouble(LAST_BMI_RESULT, bmiResult)
        }

        super.onSaveInstanceState(outState)
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
            if(!massOK){
                massET.error = getString(R.string.required_value_invalid)
            }
            if (!heightOK) {
                heightET.error = getString(R.string.required_value_invalid)
            }
            if(massOK && heightOK) {
                bmiResult = bmiOperation.calculateBMI(
                        massET.text.toString().toDouble(),
                        heightET.text.toString().toInt(),
                        systemOption
                )

                val bmiClass = pickCorrectBmiClass(bmiResult)

                binding.apply {
                    bmiTV.text = bmiResult.toString()
                    bmiTV.setTextColor(Color.parseColor(bmiClass.color));
                }
            }
        }
    }

    fun showDetails(view: View){
        binding.apply {
            if(bmiResult != 0.0){
                val bmiClass = pickCorrectBmiClass(bmiResult)
                val intent = Intent(this@MainActivity, BmiDetails::class.java)
                intent.putExtra("bmi", bmiResult)
                intent.putExtra("bmiDetailsName", bmiClass.name)
                intent.putExtra("bmiDetailsColor", bmiClass.color)
                intent.putExtra("bmiDetailsDescription", bmiClass.description)
                startActivity(intent)
            }
        }
    }

    private fun checkConstraints(text: Editable, minVal: Double, maxVal: Double): Boolean{
        if(!text.isBlank()){
            return text.toString().toDouble() in minVal..maxVal
        }
        return false
    }

    private fun pickCorrectBmiClass(result: Double): BmiClass {
        for((range, bmiclass) in bmiClassification){
            if(result >= range.first && result < range.second) {
                return bmiclass
            }
        }
        return BmiClass("undefined", "#FFFFFF", "none")
    }

    private fun setMetricSystem(){
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

    private fun setImperialSystem(){
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

class BmiOperations(){
    fun calculateBMI(mass: Double, height: Int, systemOption: Int): Double {
        return if(systemOption == 0) round(mass / (height.toDouble() / 100).pow(2.0) * 100) / 100
        else round(mass / height.toDouble().pow(2.0) * 703 * 100) / 100
    }
}