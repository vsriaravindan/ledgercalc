package com.example.calchub.domain.logic.other
/**
 * Calculator logic for Salary (Net Pay).
 * Calculates the in-hand salary after standard deductions.
 */
object SalaryCalculator {
    /**
     * Calculates Gross, Deductions, and Net Salary.
     *
     * @param basicSalary Basic Pay component of salary.
     * @param hra House Rent Allowance.
     * @param otherAllowances Special and other allowances.
     * @param pf Provident Fund deduction.
     * @param professionalTax Professional Tax deduction (default 200).
     * @return A [Triple] containing:
     *  - First: Gross Salary (Basic + HRA + Allowances)
     *  - Second: Total Deductions (PF + PT)
     *  - Third: Net Salary (In-hand)
     */
    fun calculate(basicSalary: Double, hra: Double, otherAllowances: Double, pf: Double, professionalTax: Double = 200.0): Triple<Double, Double, Double> {
        val grossSalary = basicSalary + hra + otherAllowances
        val deductions = pf + professionalTax
        val netSalary = grossSalary - deductions
        return Triple(grossSalary, deductions, netSalary)
    }
}
