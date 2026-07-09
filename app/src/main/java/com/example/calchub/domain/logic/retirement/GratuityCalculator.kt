package com.example.calchub.domain.logic.retirement
/**
 * Calculator logic for Gratuity.
 * Gratuity is a lump sum amount paid by an employer to an employee for services rendered.
 */
object GratuityCalculator {
    /**
     * Calculates the gratuity amount and taxable/tax-free components.
     *
     * @param lastSalary The last drawn salary (Basic + DA).
     * @param yearsOfService The number of years of service.
     * @return A [Triple] containing:
     *  - First: Total Gratuity Amount
     *  - Second: Tax Exempt Gratuity
     *  - Third: Taxable Gratuity
     */
    fun calculate(lastSalary: Double, yearsOfService: Double): Triple<Double, Double, Double> {
        val gratuity = (lastSalary * yearsOfService * 15) / 26
        val taxFreeLimit = 2000000.0
        val taxableAmount = (gratuity - taxFreeLimit).coerceAtLeast(0.0)
        return Triple(gratuity, taxFreeLimit.coerceAtMost(gratuity), taxableAmount)
    }
}
