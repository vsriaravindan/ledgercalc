package com.example.calchub.ui.screens.step_up_sip

import androidx.compose.runtime.Composable
import com.example.calchub.ui.screens.sip.SipCalculatorScreen

/**
 * Screen for Step-up SIP calculation.
 * Currently reusing [SipCalculatorScreen], will be enhanced for step-up logic.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@Composable
fun StepUpSipCalculatorScreen(onBackClick: () -> Unit) {
    SipCalculatorScreen(onBackClick)
}
