package com.example.calchub.domain.logic.other
/**
 * Calculator logic for Simple Interest.
 * Simple interest is a quick method of calculating the interest charge on a loan.
 */
object SimpleInterestCalculator {
    /**
     * Calculates Simple Interest: SI = (P * R * T) / 100
     *
     * @param principal The principal amount.
     * @param rate The annual interest rate in percent.
     * @param timeYears The time period in years.
     * @return A [Triple] containing:
     *  - First: Principal Amount
     *  - Second: Total Interest Earned
     *  - Third: Total Amount (Principal + Interest)
     */
    fun calculate(principal: Double, rate: Double, timeYears: Double): Triple<Double, Double, Double> {
        val interest = (principal * rate * timeYears) / 100
        val totalAmount = principal + interest
        return Triple(principal, interest, totalAmount)
    }
}
