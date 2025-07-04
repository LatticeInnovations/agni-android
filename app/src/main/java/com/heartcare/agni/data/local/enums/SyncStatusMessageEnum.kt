package com.heartcare.agni.data.local.enums

enum class SyncStatusMessageEnum(val message: String, val display: String) {
    SYNCING_IN_PROGRESS("Syncing", "In-progress"),
    SYNCING_FAILED("Sync failed", "Failed"),
    SYNCING_COMPLETED("Sync completed", "Completed"),
    OFFLINE("Offline", "Offline"),
    NO_INTERNET("No internet, data might be outdated", "No internet")
}