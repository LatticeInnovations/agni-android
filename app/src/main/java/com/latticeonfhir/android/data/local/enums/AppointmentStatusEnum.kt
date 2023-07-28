package com.latticeonfhir.android.data.local.enums

enum class AppointmentStatusEnum(val value: String) {
    Arrived("arrived"),
    WalkIn("walkin"),
    Scheduled("scheduled"),
    Cancelled("cancelled"),
    InProgress("In-progress"),
    Completed("completed"),
    NoShow("noshow")
}