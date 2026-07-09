package com.example.calchub.domain.logic.other
import kotlin.math.pow
/**
 * Calculator logic for Inflation.
 * Calculates the future value of an amount or cost based on the inflation rate.
 */
object InflationCalculator {
    /**
     * Calculates the future cost due to inflation.
     *
     * @param currentPrice The current price or cost.
     * @param inflationRate The annual inflation rate in percent.
     * @param years The number of years in the future.
     * @return A [Triple] containing:
     *  - First: Current Price
     *  - Second: Increase in Price (Inflation Effect)
     *  - Third: Future Price
     */
    fun calculate(currentPrice: Double, inflationRate: Double, years: Double): Triple<Double, Double, Double> {
        val futurePrice = currentPrice * (1 + inflationRate / 100).pow(years)
        val increase = futurePrice - currentPrice
        return Triple(currentPrice, increase, futurePrice)
    }
}
