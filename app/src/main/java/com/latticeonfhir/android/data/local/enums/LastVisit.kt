package com.latticeonfhir.core.data.local.enums

enum class LastVisit(val label: String) {
    NOT_APPLICABLE("Not Applicable"),
    LAST_WEEK("Last week"),
    LAST_MONTH("Last month"),
    LAST_THREE_MONTHS("Last 3 months"),
    LAST_YEAR("Last year");

    companion object {
        fun getLastVisitList(): List<String> = entries.map { it.label }
    }
}