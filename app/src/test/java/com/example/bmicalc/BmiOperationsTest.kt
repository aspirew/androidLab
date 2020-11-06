package com.example.bmicalc

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.math.pow
import kotlin.math.round

class BmiOperationsTest : FunSpec({

    val bmi = BmiOperations()
    val dummyMass = 100.0
    val dummyHeight = 180
    val metricSystemResult = round(dummyMass / (dummyHeight.toDouble() / 100).pow(2.0) * 100) / 100
    val imperialSystemResult = round(dummyMass / dummyHeight.toDouble().pow(2.0) * 703 * 100) / 100

    test("For system option 0 calculate BMI should return metric system result") {
        bmi.calculateBMI(dummyMass, dummyHeight, 0) shouldBe metricSystemResult
    }

    test("For system option other than 0 calculate BMI should return imperial system result") {
        bmi.calculateBMI(dummyMass, dummyHeight, 1) shouldBe imperialSystemResult
    }
})
