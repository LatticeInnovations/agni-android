package com.latticeonfhir.android.data.local.enums

enum class MedicationTimingEnum(val value: String, val code: String) {
    BEFORE_MEAL("Before meal", "307165006"),
    BEFORE_FOOD("Before food", "311500009"),
    AN_HOUR_BEFORE("An hour before food or on an empty stomach", "311502001"),
    HALF_TO_AN_HOUR_BEFORE("Half to one hour before food", "311501008"),
    BEFORE_LUNCH("Before lunch", "3091000175105"),
    BETWEEN_MEALS("Between meals", "309613002"),
    WITH_SNACK("With snack", "1551000175109"),
    DURING_MEALS("During meal","309612007"),
    DURING_FEED("During feed", "225763002"),
    POSTPRANDIAL("Postprandial", "24863003"),
    AFTER_FEED("After feed", "309603005"),
    AFTER_FOOD("After food", "225758001"),
    WITH_OR_AFTER_MEAL("With or after meal", "311503006"),
    AFTER_DINNER("After dinner", "1521000175104"),
    AFTER_LUNCH("After lunch", "1541000175107"),
    DAILY_WITHOUT_BREAKFAST("Daily with breakfast", "1751000175104"),
    DAILY_WITH_DINNER("Daily with dinner", "1771000175105"),
    DAILY_WITH_LUNCH("Daily with lunch", "1761000175102"),
    WITH_BREAKFAST("With breakfast", "769561004"),
    WITH_DINNER("With dinner", "769559008"),
    WITH_LARGEST_MEAL("With largest meal", "769556001"),
    WITH_LUNCH("With lunch", "769560003"),
    WITH_MEALS("With meals", "769557005"),
    WITH_OR_AFTER_FOOD("With or after food", "311504000"),
    WITH_SUPPER("With supper", "769558000"),
    WITHOUT_REGARDS("Without regard to meals", "424616004");

    companion object {
        fun getListOfTiming(): List<String> = MedicationTimingEnum.entries.map { it.value }
        fun getValue(code: String): String = MedicationTimingEnum.entries.first { it.code == code }.value
        fun getCode(value: String): String = MedicationTimingEnum.entries.first { it.value == value }.code
    }
}