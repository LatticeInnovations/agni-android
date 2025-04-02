package com.latticeonfhir.android.auth.utils.regex

object EmailRegex {
    val emailPattern = Regex("[a-z0-9.]+@[a-z]+\\.[a-z]{2,3}")
}