package com.heartcare.agni.utils.converters.responseconverter

object SymDiagConverter {
    fun String.splitString(): Pair<String, String> {
        val indexOfComma = this.indexOf(",")

        return if (indexOfComma != -1) {
            val code = this.substring(0, indexOfComma).trim()
            val display = this.substring(indexOfComma + 1).trim()
            Pair(code, display)
        } else {
            "" to ""
        }
    }
}