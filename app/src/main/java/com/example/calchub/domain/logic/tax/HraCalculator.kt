package com.example.calchub.domain.logic.tax
/**
 * Calculator logic for House Rent Allowance (HRA) Exemption.
 * Calculates the HRA exemption amount based on salary, rent paid, and city type.
 */
object HraCalculator {
    /**
     * Calculates HRA Exemption.
     *
     * @param basicSalary Basic Pay component.
     * @param hraReceived Actual HRA received from employer.
     * @param rentPaid Actual rent paid by the employee.
     * @param isMetroCity True if the residence is in a metro city (50% rule), False otherwise (40% rule).
     * @return A [Triple] containing:
     *  - First: Actual HRA Received
     *  - Second: HRA Exemption Amount
     *  - Third: Taxable HRA Amount
     */
    fun calculate(basicSalary: Double, hraReceived: Double, rentPaid: Double, isMetroCity: Boolean): Triple<Double, Double, Double> {
        val metroPercent = if (isMetroCity) 0.5 else 0.4
        val exemption1 = hraReceived
        val exemption2 = rentPaid - (0.1 * basicSalary)
        val exemption3 = metroPercent * basicSalary
        val exemption = minOf(exemption1, exemption2.coerceAtLeast(0.0), exemption3)
        val taxableHRA = hraReceived - exemption
        return Triple(hraReceived, exemption, taxableHRA)
    }
}
