package com.latticeonfhir.android.data.local.enums

enum class SyncStatusMessageEnum(val message: String) {
    SYNCING_IN_PROGRESS("Syncing"),
    SYNCING_FAILED("Sync failed"),
    SYNCING_COMPLETED("Sync completed"),
    OFFLINE("Offline")
}