package com.heartcare.agni.utils.regex

import kotlin.text.Regex

object RegexPatterns {
    val atLeastOneAlphaAndNumber = Regex("^(?=.*[A-Za-z])(?=.*\\d).+$")
}