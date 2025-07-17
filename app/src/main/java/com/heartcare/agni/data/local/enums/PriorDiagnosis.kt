package com.heartcare.agni.data.local.enums

enum class PriorDiagnosis(val display: String) {
    HYPERTENSION("Hypertension"),
    HEART_DISEASE("Heart attack / Angina / Other heart disease"),
    TIA("Transient ischaemic attack (TIA)"),
    DIABETES("Diabetes"),
    HYPERCHOLESTEROLAEMIA("Hypercholesterolaemia"),
    COVID_19("COVID-19"),
    CANCER("Cancer"),
    ASTHMA("Asthma"),
    COPD("Chronic obstructive pulmonary disease (COPD)"),
    CHRONIC_KIDNEY_DISEASE("Chronic kidney disease"),
    TUBERCULOSIS("Tuberculosis"),
    AIDS_OR_HIV("AIDS or HIV-positive status"),
    OTHERS("Others");

    companion object {
        fun getListOfPriorDx(): List<String> = entries.map { it.display }
    }
}