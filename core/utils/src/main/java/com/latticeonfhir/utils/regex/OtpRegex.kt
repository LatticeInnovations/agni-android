package com.latticeonfhir.core.utils.regex

object OtpRegex {
    val otpPattern = Regex("(|^)\\d{6}")
}