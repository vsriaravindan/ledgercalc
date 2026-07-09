package com.example.calchub.domain.logic

import com.example.calchub.domain.logic.CalculatorLogic.calculateEMI
import com.example.calchub.domain.logic.investment.EpfCalculator
import com.example.calchub.domain.logic.investment.FdCalculator
import com.example.calchub.domain.logic.investment.LumpsumCalculator
import com.example.calchub.domain.logic.investment.NscCalculator
import com.example.calchub.domain.logic.investment.PpfCalculator
import com.example.calchub.domain.logic.investment.RdCalculator
import com.example.calchub.domain.logic.investment.SipCalculator
import com.example.calchub.domain.logic.investment.SsyCalculator
import com.example.calchub.domain.logic.investment.SwpCalculator
import com.example.calchub.domain.logic.loan.EmiCalculator
import com.example.calchub.domain.logic.loan.FlatVsReducingCalculator
import com.example.calchub.domain.logic.other.CompoundInterestCalculator
import com.example.calchub.domain.logic.other.InflationCalculator
import com.example.calchub.domain.logic.other.PostOfficeMisCalculator
import com.example.calchub.domain.logic.other.SalaryCalculator
import com.example.calchub.domain.logic.other.ScssCalculator
import com.example.calchub.domain.logic.other.SimpleInterestCalculator
import com.example.calchub.domain.logic.retirement.ApyCalculator
import com.example.calchub.domain.logic.retirement.GratuityCalculator
import com.example.calchub.domain.logic.retirement.NpsCalculator
import com.example.calchub.domain.logic.retirement.RetirementCalculator
import com.example.calchub.domain.logic.tax.GstCalculator
import com.example.calchub.domain.logic.tax.HraCalculator
import com.example.calchub.domain.logic.tax.IncomeTaxCalculator
import com.example.calchub.domain.logic.tax.TdsCalculator
import com.example.calchub.domain.logic.trading.BrokerageCalculator
import com.example.calchub.domain.logic.trading.CagrCalculator
import com.example.calchub.domain.logic.trading.MarginCalculator
import com.example.calchub.domain.logic.trading.StockAverageCalculator
import com.example.calchub.domain.logic.trading.XirrCalculator

/**
 * Main Calculator Logic - Delegates to specialized calculator objects
 * This maintains backward compatibility while organizing code better
 */
object CalculatorLogic {

    // Investment Calculators

    /**
     * Calculates Systematic Investment Plan (SIP) returns.
     * @param monthlyInvestment Amount invested per month.
     * @param expectedReturnRate Expected annual return rate in percentage.
     * @param timePeriodYears Duration of investment in years.
     * @return Triple of (Invested Amount, Returns, Total Value).
     */
    fun calculateSIP(monthlyInvestment: Double, expectedReturnRate: Double, timePeriodYears: Double) =
        SipCalculator.calculate(monthlyInvestment, expectedReturnRate, timePeriodYears)

    /**
     * Calculates Lumpsum investment returns.
     * @param totalInvestment Initial lump sum amount.
     * @param expectedReturnRate Expected annual return rate in percentage.
     * @param timePeriodYears Duration of investment in years.
     * @return Triple of (Invested Amount, Returns, Total Value).
     */
    fun calculateLumpsum(totalInvestment: Double, expectedReturnRate: Double, timePeriodYears: Double) =
        LumpsumCalculator.calculate(totalInvestment, expectedReturnRate, timePeriodYears)

    /**
     * Calculates Fixed Deposit (FD) returns.
     * @param totalInvestment Principal amount.
     * @param rateOfInterest Annual interest rate in percentage.
     * @param timePeriodYears Duration in years.
     * @param compoundingFrequency Times interest is compounded per year (default: 4 for Quarterly).
     * @return Triple of (Principal, Interest Earned, Maturity Amount).
     */
    fun calculateFD(totalInvestment: Double, rateOfInterest: Double, timePeriodYears: Double, compoundingFrequency: Int = 4) =
        FdCalculator.calculate(totalInvestment, rateOfInterest, timePeriodYears, compoundingFrequency)

    /**
     * Calculates Public Provident Fund (PPF) returns.
     * @param yearlyInvestment Amount invested per year.
     * @param interestRate Annual interest rate (default: current PPF rate).
     * @param timePeriodYears Duration in years (default: 15).
     * @return Triple of (Total Invested, Interest Earned, Maturity Amount).
     */
    fun calculatePPF(yearlyInvestment: Double, interestRate: Double, timePeriodYears: Double = 15.0) =
        PpfCalculator.calculate(yearlyInvestment, interestRate, timePeriodYears)

    /**
     * Calculates Recurring Deposit (RD) returns.
     * @param monthlyDeposit Amount deposited per month.
     * @param interestRate Annual interest rate.
     * @param timePeriodYears Duration in years.
     * @return Triple of (Total Invested, Interest Earned, Maturity Amount).
     */
    fun calculateRD(monthlyDeposit: Double, interestRate: Double, timePeriodYears: Double) =
        RdCalculator.calculate(monthlyDeposit, interestRate, timePeriodYears)

    /**
     * Calculates National Savings Certificate (NSC) returns.
     * @param investment Principal investment amount.
     * @param interestRate Annual interest rate (default: 7.7).
     * @return Triple of (Invested, Interest, Total).
     */
    fun calculateNSC(investment: Double, interestRate: Double = 7.7) =
        NscCalculator.calculate(investment, interestRate)

    /**
     * Calculates Employee Provident Fund (EPF) returns.
     * @param monthlyContribution Monthly contribution amount.
     * @param interestRate Annual interest rate (default: 8.25).
     * @param timePeriodYears Duration in years.
     * @return Triple of (Total Contribution, Interest, Maturity Amount).
     */
    fun calculateEPF(monthlyContribution: Double, interestRate: Double = 8.25, timePeriodYears: Double) =
        EpfCalculator.calculate(monthlyContribution, interestRate, timePeriodYears)

    /**
     * Calculates Sukanya Samriddhi Yojana (SSY) returns.
     * @param yearlyDeposit Annual deposit amount.
     * @param interestRate Annual interest rate (default: 8.2).
     * @param timePeriodYears Duration in years (default: 15).
     * @return Triple of (Total Invested, Interest, Maturity Amount).
     */
    fun calculateSSY(yearlyDeposit: Double, interestRate: Double = 8.2, timePeriodYears: Double = 15.0) =
        SsyCalculator.calculate(yearlyDeposit, interestRate, timePeriodYears)

    /**
     * Calculates Systematic Withdrawal Plan (SWP).
     * @param totalInvestment Initial lump sum investment.
     * @param withdrawalPerMonth Monthly withdrawal amount.
     * @param expectedReturnRate Expected annual return rate.
     * @param timePeriodYears Duration in years.
     * @return Triple of (Initial Investment, Total Withdrawn, Final Balance).
     */
    fun calculateSWP(totalInvestment: Double, withdrawalPerMonth: Double, expectedReturnRate: Double, timePeriodYears: Double) =
        SwpCalculator.calculate(totalInvestment, withdrawalPerMonth, expectedReturnRate, timePeriodYears)

    // Loan Calculators

    /**
     * Calculates Equated Monthly Installment (EMI).
     * @param loanAmount Total loan amount.
     * @param interestRate Annual interest rate.
     * @param tenureYears Loan tenure in years.
     * @return Triple of (EMI Amount, Total Interest, Total Payment).
     */
    fun calculateEMI(loanAmount: Double, interestRate: Double, tenureYears: Double) =
        EmiCalculator.calculate(loanAmount, interestRate, tenureYears)

    /**
     * Calculates Home Loan EMI.
     * Alias for [calculateEMI].
     */
    fun calculateHomeLoanEMI(loanAmount: Double, interestRate: Double, tenureYears: Double) =
        EmiCalculator.calculate(loanAmount, interestRate, tenureYears)

    /**
     * Calculates Car Loan EMI.
     * Alias for [calculateEMI].
     */
    fun calculateCarLoanEMI(loanAmount: Double, interestRate: Double, tenureYears: Double) =
        EmiCalculator.calculate(loanAmount, interestRate, tenureYears)

    /**
     * Compares Flat Rate vs Reducing Balance interest.
     * @param loanAmount Loan amount.
     * @param flatRate Flat interest rate.
     * @param tenureYears Loan tenure.
     * @return Pair of EMIs (Flat Rate EMI, Reducing Balance EMI).
     */
    fun calculateFlatVsReducing(loanAmount: Double, flatRate: Double, tenureYears: Double) =
        FlatVsReducingCalculator.calculate(loanAmount, flatRate, tenureYears)

    // Retirement Calculators

    /**
     * Calculates National Pension System (NPS) corpus.
     * @param monthlyContribution Monthly investment.
     * @param expectedReturnRate Expected annual return rate.
     * @param currentAge Current age of the investor.
     * @param retirementAge Target retirement age (default: 60).
     * @return Triple of (Total Invested, Interest Earned, Maturity Amount).
     */
    fun calculateNPS(monthlyContribution: Double, expectedReturnRate: Double, currentAge: Double, retirementAge: Double = 60.0) =
        NpsCalculator.calculate(monthlyContribution, expectedReturnRate, currentAge, retirementAge)

    /**
     * Calculates Retirement Corpus and Monthly Savings (SIP) needed.
     * @param currentAge Current age.
     * @param retirementAge Retirement age.
     * @param monthlyExpenses Current monthly expenses.
     * @param inflationRate Expected inflation rate.
     * @param expectedReturn Expected return on investment.
     * @return Triple of (Monthly SIP Needed, Corpus Needed, Future Monthly Expenses).
     */
    fun calculateRetirement(currentAge: Double, retirementAge: Double, monthlyExpenses: Double, inflationRate: Double = 6.0, expectedReturn: Double = 12.0) =
        RetirementCalculator.calculate(currentAge, retirementAge, monthlyExpenses, inflationRate, expectedReturn)

    /**
     * Calculates Atal Pension Yojana (APY).
     * @param currentAge Subscriber's current age.
     * @param pensionAmount Desired monthly pension amount.
     * @return Pair of (Monthly Contribution, Total Contribution).
     */
    fun calculateAPY(currentAge: Double, pensionAmount: Double = 5000.0) =
        ApyCalculator.calculate(currentAge, pensionAmount)

    /**
     * Calculates Gratuity.
     * @param lastSalary Last drawn basic salary + DA.
     * @param yearsOfService Number of years of service.
     * @return Gratuity Amount.
     */
    fun calculateGratuity(lastSalary: Double, yearsOfService: Double) =
        GratuityCalculator.calculate(lastSalary, yearsOfService)

    // Tax Calculators

    /**
     * Calculates Income Tax (Old Regime Simplified).
     * @param annualIncome Total annual income.
     * @param deductions Total deductions (80C, etc.).
     * @return Triple of (Gross Income, Tax Payable, Net Income).
     */
    fun calculateIncomeTax(annualIncome: Double, deductions: Double = 0.0) =
        IncomeTaxCalculator.calculate(annualIncome, deductions)

    /**
     * Calculates House Rent Allowance (HRA) Exemption.
     * @param basicSalary Basic Salary.
     * @param hraReceived HRA Received from employer.
     * @param rentPaid Annual rent paid.
     * @param isMetroCity Whether living in a metro city.
     * @return Triple of (HRA Received, Exempt Amount, Taxable HRA).
     */
    fun calculateHRA(basicSalary: Double, hraReceived: Double, rentPaid: Double, isMetroCity: Boolean) =
        HraCalculator.calculate(basicSalary, hraReceived, rentPaid, isMetroCity)

    /**
     * Calculates Goods and Services Tax (GST).
     * @param amount Base or Total amount.
     * @param gstRate GST Rate percentage.
     * @param isInclusive Whether the amount is inclusive of GST.
     * @return Triple of (Base Amount, GST Amount, Total Amount).
     */
    fun calculateGST(amount: Double, gstRate: Double = 18.0, isInclusive: Boolean = false) =
        GstCalculator.calculate(amount, gstRate, isInclusive)

    /**
     * Calculates Tax Deducted at Source (TDS).
     * @param amount Gross amount.
     * @param tdsRate TDS rate percentage.
     * @return Triple of (Gross Amount, TDS Deducted, Net Amount).
     */
    fun calculateTDS(amount: Double, tdsRate: Double = 10.0) =
        TdsCalculator.calculate(amount, tdsRate)

    // Trading Calculators

    /**
     * Calculates Compound Annual Growth Rate (CAGR).
     * @param initialValue Initial investment value.
     * @param finalValue Final investment value.
     * @param years Duration in years.
     * @return Triple of (CAGR %, Total Return, Absolute Return).
     */
    fun calculateCAGR(initialValue: Double, finalValue: Double, years: Double) =
        CagrCalculator.calculate(initialValue, finalValue, years)

    /**
     * Calculates Brokerage and Taxes for stock trading.
     * @param tradeValue Total value of the trade.
     * @param brokerageRate Brokerage rate percentage.
     * @param isIntraday Whether the trade is intraday.
     * @return Triple of (Brokerage, STT, Net Value).
     */
    fun calculateBrokerage(tradeValue: Double, brokerageRate: Double = 0.03, isIntraday: Boolean = false) =
        BrokerageCalculator.calculate(tradeValue, brokerageRate, isIntraday)

    /**
     * Calculates Margin required for trading.
     * @param quantity Number of shares.
     * @param price Price per share.
     * @param leverage Leverage multiplier (e.g., 5x).
     * @return Triple of (Margin Required, Total Exposure, Total Value).
     */
    fun calculateMargin(quantity: Double, price: Double, leverage: Double = 5.0) =
        MarginCalculator.calculate(quantity, price, leverage)

    /**
     * Calculates Stock Average Price.
     * @param firstQuantity Quantity of first purchase.
     * @param firstPrice Price of first purchase.
     * @param secondQuantity Quantity of second purchase.
     * @param secondPrice Price of second purchase.
     * @return Triple of (Total Quantity, Average Price, Total Investment).
     */
    fun calculateStockAverage(firstQuantity: Double, firstPrice: Double, secondQuantity: Double, secondPrice: Double) =
        StockAverageCalculator.calculate(firstQuantity, firstPrice, secondQuantity, secondPrice)

    /**
     * Calculates Extended Internal Rate of Return (XIRR).
     * @param investments List of periodic investments.
     * @param returns Current market value.
     * @return Triple of (Total Invested, Profit, XIRR %).
     */
    fun calculateXIRR(investments: List<Double>, returns: Double) =
        XirrCalculator.calculate(investments, returns)

    // Other Calculators

    /**
     * Calculates Simple Interest.
     * @param principal Principal amount.
     * @param rate Annual interest rate.
     * @param timeYears Duration in years.
     * @return Triple of (Principal, Interest, Total Amount).
     */
    fun calculateSimpleInterest(principal: Double, rate: Double, timeYears: Double) =
        SimpleInterestCalculator.calculate(principal, rate, timeYears)

    /**
     * Calculates Compound Interest.
     * @param principal Principal amount.
     * @param rate Annual interest rate.
     * @param timeYears Duration in years.
     * @param compoundingFrequency Compounding frequency per year.
     * @return Triple of (Principal, Interest, Total Amount).
     */
    fun calculateCompoundInterest(principal: Double, rate: Double, timeYears: Double, compoundingFrequency: Int = 1) =
        CompoundInterestCalculator.calculate(principal, rate, timeYears, compoundingFrequency)

    /**
     * Calculates In-hand Salary breakdown.
     * @param basicSalary Basic Salary.
     * @param hra HRA Allowance.
     * @param otherAllowances Other allowances.
     * @param pf PF Deduction.
     * @param professionalTax Professional Tax (default: 200).
     * @return Triple of (Gross Salary, Total Deductions, Net Salary).
     */
    fun calculateSalary(basicSalary: Double, hra: Double, otherAllowances: Double, pf: Double, professionalTax: Double = 200.0) =
        SalaryCalculator.calculate(basicSalary, hra, otherAllowances, pf, professionalTax)

    /**
     * Calculates Inflation impact.
     * @param currentPrice Current cost of an item.
     * @param inflationRate Annual inflation rate.
     * @param years Duration in years.
     * @return Triple of (Current Price, Cost Increase, Future Price).
     */
    fun calculateInflation(currentPrice: Double, inflationRate: Double, years: Double) =
        InflationCalculator.calculate(currentPrice, inflationRate, years)

    /**
     * Calculates Post Office Monthly Income Scheme (MIS) returns.
     * @param investment Lump sum investment.
     * @param interestRate Annual interest rate (default: 7.4).
     * @return Triple of (Investment, Monthly Income, Total Income).
     */
    fun calculatePostOfficeMIS(investment: Double, interestRate: Double = 7.4) =
        PostOfficeMisCalculator.calculate(investment, interestRate)

    /**
     * Calculates Senior Citizen Savings Scheme (SCSS) returns.
     * @param investment Lump sum investment.
     * @param interestRate Annual interest rate (default: 8.2).
     * @return Triple of (Investment, Total Interest, Maturity Value).
     */
    fun calculateSCSS(investment: Double, interestRate: Double = 8.2) =
        ScssCalculator.calculate(investment, interestRate)
}
