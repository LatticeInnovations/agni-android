package com.latticeonfhir.android.data.local.enums

enum class  AppointmentStatusFhir(
    val uiStatus: String,
    val fhirStatus: String,
    val type: String,
    val encounter: String
) {
    WALK_IN("walkin", "arrived", "walkin", "planned"),
    SCHEDULE("scheduled", "proposed", "routine", "planned"),
    SCHEDULED_ARRIVED("arrived", "arrived", "routine", "planned"),
    SCHEDULED_CANCELLED("cancelled", "cancelled", "routine", "planned"),
    SCHEDULED_NO_SHOW("noshow", "noshow", "routine", "planned"),
    SCHEDULED_PRESCRIBED("in-progress", "arrived", "routine", "in-progress"),
    SCHEDULED_COMPLETED("completed", "arrived", "routine", "finished"),
    WALK_IN_COMPLETED("completed", "arrived", "walkin", "finished"),
    WALK_IN_PRESCRIBED("in-progress", "arrived", "walkin", "in-progress"),
    WALK_IN_CANCELLED("cancelled", "cancelled", "walkin", "planned"),
    WALK_IN_PRESCRIBED_CANCEL("cancelled", "cancelled", "walkin", "in-progress"),
    SCHEDULED_PRESCRIBED_CANCEL("cancelled", "cancelled", "routine", "in-progress"),
    SCHEDULED_PRESCRIBED_NO_SHOW("noshow", "noshow", "routine", "in-progress"),
    WALK_IN_PRESCRIBED_NO_SHOW("noshow", "noshow", "walkin", "in-progress"),
    WALK_IN_COMPLETED_CANCELLED("cancelled", "cancelled", "walkin", "finished"),
    SCHEDULED_COMPLETED_CANCELLED("cancelled", "cancelled", "routine", "finished"),
    WALK_IN_COMPLETED_NO_SHOW("noshow", "noshow", "walkin", "finished"),
    SCHEDULED_COMPLETED_NO_SHOW("noshow", "noshow", "routine", "finished");
}