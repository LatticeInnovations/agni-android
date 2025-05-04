package com.latticeonfhir.features.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.features.auth.ui.login.OtpScreen
import com.latticeonfhir.features.auth.ui.login.PhoneEmailScreen
import com.latticeonfhir.features.auth.ui.signup.SignUpPhoneEmailScreen
import com.latticeonfhir.features.auth.ui.signup.SignUpScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavController
) {
    composable(Screen.PhoneEmailScreen.route) { PhoneEmailScreen(navController) }
    composable(Screen.SignUpPhoneEmailScreen.route) { SignUpPhoneEmailScreen(navController) }
    composable(Screen.SignUpScreen.route) { SignUpScreen(navController = navController) }
    composable(Screen.OtpScreen.route) { OtpScreen(navController) }
}