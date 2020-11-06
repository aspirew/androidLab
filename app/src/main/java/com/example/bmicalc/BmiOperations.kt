package com.example.bmicalc

import kotlin.math.pow
import kotlin.math.round

class BmiOperations(){
    fun calculateBMI(mass: Double, height: Int, systemOption: Int): Double {
        return if(systemOption == 0) round(mass / (height.toDouble() / 100).pow(2.0) * 100) / 100
        else round(mass / height.toDouble().pow(2.0) * 703 * 100) / 100
    }
}