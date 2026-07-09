package com.example.calchub.domain.logic.investment

/**
 * Calculator logic for Recurring Deposit (RD).
 * RD is a special kind of term deposit offered by banks which help people with regular incomes to deposit a fixed amount every month into their recurring deposit account and earn interest at the rate applicable to Fixed Deposits.
 */
object RdCalculator {
    /**
     * Calculates the maturity amount and interest earned for a Recurring Deposit.
     *
     * @param monthlyDeposit The amount deposited monthly.
     * @param interestRate The annual interest rate in percent.
     * @param timePeriodYears The duration of the deposit in years.
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Estimated Returns (Interest Earned)
     *  - Third: Total Value (Maturity Amount)
     */
    fun calculate(
        monthlyDeposit: Double,
        interestRate: Double,
        timePeriodYears: Double
    ): Triple<Double, Double, Double> {
        val n = timePeriodYears * 12
        val totalValue = monthlyDeposit * n + (monthlyDeposit * n * (n + 1) / 24) * (interestRate / 100)
        val investedAmount = monthlyDeposit * n
        val estimatedReturns = totalValue - investedAmount
        
        return Triple(investedAmount, estimatedReturns, totalValue)
    }
}
