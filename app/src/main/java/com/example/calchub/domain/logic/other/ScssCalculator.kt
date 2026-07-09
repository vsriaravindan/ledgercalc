package com.example.calchub.domain.logic.other
/**
 * Calculator logic for Senior Citizens Savings Scheme (SCSS).
 * SCSS is a government-backed retirement benefits program.
 */
object ScssCalculator {
    /**
     * Calculates quarterly interest and total maturity amount for SCSS.
     *
     * @param investment The total amount invested.
     * @param interestRate The annual interest rate in percent (default is 8.2%).
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Total Interest Earned over 5 years
     *  - Third: Total Amount (Principal + Interest)
     */
    fun calculate(investment: Double, interestRate: Double = 8.2): Triple<Double, Double, Double> {
        val tenure = 5.0
        val quarterlyInterest = (investment * interestRate / 100) / 4
        val yearlyInterest = quarterlyInterest * 4
        val totalInterest = yearlyInterest * tenure
        return Triple(investment, totalInterest, investment + totalInterest)
    }
}
