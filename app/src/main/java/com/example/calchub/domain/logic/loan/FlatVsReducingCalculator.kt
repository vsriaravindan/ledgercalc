package com.example.calchub.domain.logic.loan
import kotlin.math.pow
/**
 * Calculator logic for comparing Flat Rate vs Reducing Balance Rate loans.
 * Helps users understand the true cost difference between these two interest calculation methods.
 */
object FlatVsReducingCalculator {
    /**
     * Calculates and compares EMI for Flat Rate and Reducing Balance Rate.
     *
     * @param loanAmount The principal loan amount.
     * @param flatRate The quoted flat interest rate in percent.
     * @param tenureYears The loan tenure in years.
     * @return A [Triple] containing:
     *  - First: EMI as per Flat Rate method
     *  - Second: EMI as per Reducing Balance method (approximation based on equivalent effective rate)
     *  - Third: Difference in EMI (Flat - Reducing)
     */
    fun calculate(loanAmount: Double, flatRate: Double, tenureYears: Double): Triple<Double, Double, Double> {
        val totalInterestFlat = loanAmount * (flatRate / 100) * tenureYears
        val totalAmountFlat = loanAmount + totalInterestFlat
        val emiFlat = totalAmountFlat / (tenureYears * 12)
        val reducingRate = flatRate * 1.8
        val r = reducingRate / 12 / 100
        val n = tenureYears * 12
        val emiReducing = (loanAmount * r * (1 + r).pow(n)) / ((1 + r).pow(n) - 1)
        return Triple(emiFlat, emiReducing, emiFlat - emiReducing)
    }
}
