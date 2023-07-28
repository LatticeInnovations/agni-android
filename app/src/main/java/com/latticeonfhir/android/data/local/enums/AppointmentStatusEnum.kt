package com.latticeonfhir.android.data.local.enums

enum class AppointmentStatusEnum(val value: String) {
    ARRIVED("arrived"),
    WALK_IN("walkin"),
    SCHEDULED("scheduled"),
    CANCELLED("cancelled"),
    IN_PROGRESS("In-progress"),
    COMPLETED("completed"),
    NO_SHOW("noshow")
}