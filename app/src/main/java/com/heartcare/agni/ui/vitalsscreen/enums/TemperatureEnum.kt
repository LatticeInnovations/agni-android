package com.heartcare.agni.ui.vitalsscreen.enums

enum class TemperatureEnum(val value: String) {

    FAHRENHEIT("F"),
    CELSIUS("C");

    companion object {
        fun fromString(value: String) = TemperatureEnum.entries.first { it.value == value }
    }

}