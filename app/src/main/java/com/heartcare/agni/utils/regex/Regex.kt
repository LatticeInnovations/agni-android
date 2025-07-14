package com.heartcare.agni.utils.regex

import kotlin.text.Regex

object RegexPatterns {
    val atLeastOneAlphaAndNumber = Regex("^(?=.*[A-Za-z])(?=.*\\d).+$")
    val passwordRegex = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,15}$")
}