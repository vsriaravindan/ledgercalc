package com.example.calchub.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calchub.ui.screens.MainScreen
import com.example.calchub.ui.screens.apy.ApyCalculatorScreen
import com.example.calchub.ui.screens.brokerage.BrokerageCalculatorScreen
import com.example.calchub.ui.screens.cagr.CagrCalculatorScreen
import com.example.calchub.ui.screens.car_loan_emi.CarLoanEmiCalculatorScreen
import com.example.calchub.ui.screens.compound_interest.CompoundInterestCalculatorScreen
import com.example.calchub.ui.screens.emi.EmiCalculatorScreen
import com.example.calchub.ui.screens.epf.EpfCalculatorScreen
import com.example.calchub.ui.screens.fd.FdCalculatorScreen
import com.example.calchub.ui.screens.flat_vs_reducing.FlatVsReducingCalculatorScreen
import com.example.calchub.ui.screens.gratuity.GratuityCalculatorScreen
import com.example.calchub.ui.screens.gst.GstCalculatorScreen
import com.example.calchub.ui.screens.home_loan_emi.HomeLoanEmiCalculatorScreen
import com.example.calchub.ui.screens.hra.HraCalculatorScreen
import com.example.calchub.ui.screens.income_tax.IncomeTaxCalculatorScreen
import com.example.calchub.ui.screens.inflation.InflationCalculatorScreen
import com.example.calchub.ui.screens.lumpsum.LumpsumCalculatorScreen
import com.example.calchub.ui.screens.margin.MarginCalculatorScreen
import com.example.calchub.ui.screens.mf_returns.MfReturnsCalculatorScreen
import com.example.calchub.ui.screens.nps.NpsCalculatorScreen
import com.example.calchub.ui.screens.nsc.NscCalculatorScreen
import com.example.calchub.ui.screens.post_office_mis.PostOfficeMisCalculatorScreen
import com.example.calchub.ui.screens.ppf.PpfCalculatorScreen
import com.example.calchub.ui.screens.rd.RdCalculatorScreen
import com.example.calchub.ui.screens.retirement.RetirementCalculatorScreen
import com.example.calchub.ui.screens.salary.SalaryCalculatorScreen
import com.example.calchub.ui.screens.scss.ScssCalculatorScreen
import com.example.calchub.ui.screens.simple_interest.SimpleInterestCalculatorScreen
import com.example.calchub.ui.screens.sip.SipCalculatorScreen
import com.example.calchub.ui.screens.ssy.SsyCalculatorScreen
import com.example.calchub.ui.screens.step_up_sip.StepUpSipCalculatorScreen
import com.example.calchub.ui.screens.stock_average.StockAverageCalculatorScreen
import com.example.calchub.ui.screens.swp.SwpCalculatorScreen
import com.example.calchub.ui.screens.tds.TdsCalculatorScreen
import com.example.calchub.ui.screens.xirr.XirrCalculatorScreen

/**
 * Main Navigation Host for the application.
 * Defines the navigation graph and routes for all screens.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(onCalculatorClick = { route ->
                navController.navigate(route)
            })
        }
        
        // Placeholder routes for all calculators
        // In a real app, we would map these to specific screens
        // For now, we'll just show a placeholder for any calculator route
        // We can't dynamically create composables in a loop easily inside NavHost due to lambda capturing,
        // but we can define a generic route or individual ones.
        // Let's define a generic one for now to test navigation.
        
        // Actually, let's just define the SIP one as a test, and others as placeholders
        composable("sip") {
             SipCalculatorScreen(onBackClick = { navController.popBackStack() })
        }
        
        // Generic placeholder for others (this won't work as catch-all without a parameter, 
        // so we'd need to define each one or use a route with argument like "calculator/{type}")
        // For simplicity in this step, let's just add a few common ones.
        
        composable("emi") { 
            EmiCalculatorScreen(onBackClick = { navController.popBackStack() })
        }
        composable("fd") { 
            FdCalculatorScreen(onBackClick = { navController.popBackStack() })
        }
        composable("ppf") { 
            PpfCalculatorScreen(onBackClick = { navController.popBackStack() })
        }
        composable("lumpsum") { 
            LumpsumCalculatorScreen(onBackClick = { navController.popBackStack() })
        }
        composable("swp") { 
            SwpCalculatorScreen(onBackClick = { navController.popBackStack() })
        }
        composable("rd") { 
            RdCalculatorScreen(onBackClick = { navController.popBackStack() })
        }
        composable("nsc") { 
            NscCalculatorScreen(onBackClick = { navController.popBackStack() })
        }
        composable("epf") { 
            EpfCalculatorScreen(onBackClick = { navController.popBackStack() })
        }
        composable("ssy") { 
            SsyCalculatorScreen(onBackClick = { navController.popBackStack() })
        }
        
        // All remaining calculators with actual screens
        composable("nps") { NpsCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("mf_returns") { MfReturnsCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("compound_interest") { CompoundInterestCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("simple_interest") { SimpleInterestCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("step_up_sip") { StepUpSipCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("retirement") { RetirementCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("income_tax") { IncomeTaxCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("hra") { HraCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("gratuity") { GratuityCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("apy") { ApyCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("cagr") { CagrCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("gst") { GstCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("tds") { TdsCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("salary") { SalaryCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("inflation") { InflationCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("post_office_mis") { PostOfficeMisCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("scss") { ScssCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("stock_average") { StockAverageCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("xirr") { XirrCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("brokerage") { BrokerageCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("margin") { MarginCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("flat_vs_reducing") { FlatVsReducingCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("home_loan_emi") { HomeLoanEmiCalculatorScreen(onBackClick = { navController.popBackStack() }) }
        composable("car_loan_emi") { CarLoanEmiCalculatorScreen(onBackClick = { navController.popBackStack() }) }
    }
}
