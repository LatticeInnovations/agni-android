package com.heartcare.agni.data.local.enums

enum class AppointmentStatusEnum(val value: String, val label: String) {
    ARRIVED("arrived", "Arrived"),
    WALK_IN("walkin", "Walk-in"),
    SCHEDULED("scheduled", "Scheduled"),
    CANCELLED("cancelled", "Cancelled"),
    IN_PROGRESS("in-progress", "In-progress"),
    COMPLETED("completed", "Completed"),
    NO_SHOW("noshow", "No-show");

    companion object {
        fun fromLabel(label: String) = AppointmentStatusEnum.values().first { it.label == label }
        fun fromValue(value: String) = AppointmentStatusEnum.values().first { it.value == value }
    }
}