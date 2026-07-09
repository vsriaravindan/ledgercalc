package com.example.calchub.domain.logic.tax
/**
 * Calculator logic for Income Tax (Old Regime Simplified).
 * Estimates income tax based on slab rates.
 */
object IncomeTaxCalculator {
    /**
     * Calculates Income Tax.
     *
     * @param annualIncome Total annual income.
     * @param deductions Total deductions (e.g., 80C, 80D).
     * @return A [Triple] containing:
     *  - First: Annual Income
     *  - Second: Estimated Tax
     *  - Third: Net Income (Post Tax)
     */
    fun calculate(annualIncome: Double, deductions: Double = 0.0): Triple<Double, Double, Double> {
        val taxableIncome = (annualIncome - deductions).coerceAtLeast(0.0)
        var tax = 0.0
        when {
            taxableIncome <= 250000 -> tax = 0.0
            taxableIncome <= 500000 -> tax = (taxableIncome - 250000) * 0.05
            taxableIncome <= 1000000 -> tax = 12500 + (taxableIncome - 500000) * 0.20
            else -> tax = 12500 + 100000 + (taxableIncome - 1000000) * 0.30
        }
        return Triple(annualIncome, tax, annualIncome - tax)
    }
}
