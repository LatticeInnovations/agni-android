package com.latticeonfhir.android.ui.vitalsscreen.enums

enum class VitalsEyeEnum(val number: Int, val value: String) {

    NORMAL_VISION(1, "Normal Vision (6/6 or 20/20)"),
    MILD_IMPAIRMENT(2, "Mild Impairment (6/9 or 20/30)"),
    MODERATE_IMPAIRMENT(3, "Moderate Impairment (6/12 or 20/40)"),
    SIGNIFICANT_IMPAIRMENT(4, "Significant Impairment (6/18 or 20/60)"),
    SEVERE_IMPAIRMENT(5, "Severe Impairment (6/24 or 20/80)"),
    VERY_SEVERE_IMPAIRMENT(6, "Very Severe Impairment (6/36 or 20/120)"),
    LEGAL_BLINDNESS(7, "Legal Blindness (6/60 or 20/200)");
}