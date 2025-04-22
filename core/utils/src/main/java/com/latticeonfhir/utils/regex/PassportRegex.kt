package com.latticeonfhir.utils.regex

object PassportRegex {
    val passportPattern = Regex("^[A-PR-WYa-pr-wy][1-9]\\d\\s?\\d{4}[1-9]$")

}