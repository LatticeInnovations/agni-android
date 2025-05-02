package com.latticeonfhir.features.auth.navigation

const val authRoute = "auth"

sealed class Screen(val route: String) {
    data object PhoneEmailScreen : Screen("$authRoute/phone_email_screen")
    data object SignUpPhoneEmailScreen : Screen("$authRoute/sign_up_phone_email_screen")
    data object OtpScreen : Screen("$authRoute/otp_screen")
    data object SignUpScreen : Screen("$authRoute/sign_up_screen")
}