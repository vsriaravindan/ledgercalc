package com.example.calchub.domain.logic.other
import kotlin.math.pow
/**
 * Calculator logic for Compound Interest.
 * Compound interest is the interest on a loan or deposit calculated based on both the initial principal and the accumulated interest from previous periods.
 */
object CompoundInterestCalculator {
    /**
     * Calculates Compound Interest.
     *
     * @param principal The initial principal amount.
     * @param rate The annual interest rate in percent.
     * @param timeYears The time period in years.
     * @param compoundingFrequency The number of times interest is compounded per year (default is 1 for annually).
     * @return A [Triple] containing:
     *  - First: Principal Amount
     *  - Second: Total Interest Earned
     *  - Third: Total Amount (Principal + Interest)
     */
    fun calculate(principal: Double, rate: Double, timeYears: Double, compoundingFrequency: Int = 1): Triple<Double, Double, Double> {
        val r = rate / 100
        val n = compoundingFrequency
        val t = timeYears
        val totalValue = principal * (1 + r / n).pow(n * t)
        val interest = totalValue - principal
        return Triple(principal, interest, totalValue)
    }
}
