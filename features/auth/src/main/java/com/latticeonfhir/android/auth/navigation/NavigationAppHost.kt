package com.latticeonfhir.core.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.latticeonfhir.core.auth.ui.login.OtpScreen
import com.latticeonfhir.core.auth.ui.login.PhoneEmailScreen
import com.latticeonfhir.android.auth.ui.signup.SignUpPhoneEmailScreen
import com.latticeonfhir.core.auth.ui.signup.SignUpScreen


fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    onAuthSuccess: () -> Unit
) {
    navigation(startDestination = Screen.PhoneEmailScreen.route, route = authRoute) {
        composable(Screen.PhoneEmailScreen.route) { PhoneEmailScreen(navController) }
        composable(Screen.SignUpPhoneEmailScreen.route) { SignUpPhoneEmailScreen(navController) }
        composable(Screen.SignUpScreen.route) { SignUpScreen(navController = navController, onAuthSuccess) }
        composable(Screen.OtpScreen.route) { OtpScreen(navController, onAuthSuccess) }
    }
}