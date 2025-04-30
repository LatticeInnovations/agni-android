package com.latticeonfhir.core.sms

object OtpRegex {
    val otpPattern = Regex("(|^)\\d{6}")
}