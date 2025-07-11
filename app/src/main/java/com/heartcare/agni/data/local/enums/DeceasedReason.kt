package com.heartcare.agni.data.local.enums

enum class DeceasedReason(val reason: String) {
    HEART_ATTACK("Heart attack"),
    STROKE("Stroke"),
    HYPERTENSION("Hypertension"),
    DIABETES("Diabetes"),
    CANCER("Cancer"),
    CRD("Chronic respiratory disease"),
    OTHERS("Others");

    companion object {
        fun getDeceasedReasonList(): List<String> = entries.map { it.reason }
    }
}