package com.example.calchub.domain.logic.trading
import kotlin.math.pow
/**
 * Calculator logic for Compound Annual Growth Rate (CAGR).
 * CAGR is the mean annual growth rate of an investment over a specified time period longer than one year.
 */
object CagrCalculator {
    /**
     * Calculates CAGR.
     *
     * @param initialValue The initial investment value.
     * @param finalValue The final investment value.
     * @param years The time period in years.
     * @return A [Triple] containing:
     *  - First: CAGR Percentage
     *  - Second: Total Return Percentage
     *  - Third: Absolute Profit/Loss
     */
    fun calculate(initialValue: Double, finalValue: Double, years: Double): Triple<Double, Double, Double> {
        val cagr = ((finalValue / initialValue).pow(1 / years) - 1) * 100
        val totalReturn = ((finalValue - initialValue) / initialValue) * 100
        return Triple(cagr, totalReturn, finalValue - initialValue)
    }
}
