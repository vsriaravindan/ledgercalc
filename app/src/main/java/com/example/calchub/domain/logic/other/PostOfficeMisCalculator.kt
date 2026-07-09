package com.example.calchub.domain.logic.other
/**
 * Calculator logic for Post Office Monthly Income Scheme (MIS).
 * MIS is a government-backed savings scheme that offers a fixed monthly income.
 */
object PostOfficeMisCalculator {
    /**
     * Calculates the monthly income from MIS.
     *
     * @param investment The total amount invested.
     * @param interestRate The annual interest rate in percent (default is 7.4%).
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Monthly Income
     *  - Third: Total Income over 5 years (Tenure of MIS)
     */
    fun calculate(investment: Double, interestRate: Double = 7.4): Triple<Double, Double, Double> {
        val monthlyIncome = (investment * interestRate / 100) / 12
        val yearlyIncome = monthlyIncome * 12
        val totalIncome5Years = yearlyIncome * 5
        return Triple(investment, monthlyIncome, totalIncome5Years)
    }
}
