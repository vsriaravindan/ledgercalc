package com.example.calchub.domain.logic.retirement
import kotlin.math.pow
/**
 * Calculator logic for Retirement Planning.
 * Calculates the corpus required for retirement and the SIP needed to achieve it.
 */
object RetirementCalculator {
    /**
     * Calculates the monthly SIP required to build the target retirement corpus.
     *
     * @param currentAge Current age.
     * @param retirementAge Desired retirement age.
     * @param monthlyExpenses Current monthly expenses.
     * @param inflationRate Expected annual inflation rate (default 6%).
     * @param expectedReturn Expected annual return on investment (default 12%).
     * @return A [Triple] containing:
     *  - First: Monthly SIP Amount Required
     *  - Second: Total Corpus Needed at Retirement
     *  - Third: Future Monthly Expenses at Retirement
     */
    fun calculate(currentAge: Double, retirementAge: Double, monthlyExpenses: Double, inflationRate: Double = 6.0, expectedReturn: Double = 12.0): Triple<Double, Double, Double> {
        val yearsToRetirement = retirementAge - currentAge
        val yearsInRetirement = 25.0
        val futureExpenses = monthlyExpenses * (1 + inflationRate / 100).pow(yearsToRetirement)
        val corpusNeeded = futureExpenses * 12 * yearsInRetirement
        val i = expectedReturn / 12 / 100
        val n = yearsToRetirement * 12
        val monthlySIP = corpusNeeded / (((1 + i).pow(n) - 1) / i * (1 + i))
        return Triple(monthlySIP, corpusNeeded, futureExpenses * 12)
    }
}
