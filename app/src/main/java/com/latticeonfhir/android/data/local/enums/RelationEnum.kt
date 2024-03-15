package com.latticeonfhir.android.data.local.enums

enum class RelationEnum(val number: Int, val value: String, val display: String) {

    MOTHER(0, "MTH", "Mother"),
    FATHER(1, "FTH", "Father"),
    GRAND_FATHER(2, "GRFTH", "Grand Father"),
    GRAND_MOTHER(3, "GRMTH", "Grand Mother"),
    BROTHER(4, "BRO", "Brother"),
    SISTER(5, "SIS", "Sister"),
    WIFE(6, "WIFE", "Wife"),
    SON(7, "SON", "Son"),
    DAUGHTER(8, "DAUC", "Daughter"),
    GRAND_SON(9, "GRNDSON", "Grand Son"),
    GRAND_DAUGHTER(10, "GRNDDAU", "Grand Daughter"),
    UNCLE(11, "UNCLE", "Uncle"),
    AUNTY(12, "AUNT", "Aunty"),
    BROTHER_IN_LAW(13, "BROINLAW", "Brother-in-law"),
    SISTER_IN_LAW(14, "SISINLAW", "Sister-in-law"),
    FATHER_IN_LAW(15, "FTHINLAW", "Father-in-law"),
    MOTHER_IN_LAW(16, "MTHINLAW", "Mother-in-law"),
    NEPHEW(17, "NEPHEW", "Nephew"),
    SON_IN_LAW(18, "SONINLAW", "Son-in-law"),
    HUSBAND(19, "HUSB", "Husband"),
    CHILD(20, "CHILD", "Child"),
    GRAND_CHILD(21, "GRNDCHILD", "Grand Child"),
    SIBLING(22, "SIB", "Sibling"),
    SPOUSE(23, "SPS", "Spouse"),
    PARENT(24, "PRN", "Parent"),
    GRAND_PARENT(25, "GRPRN", "Grand Parent"),
    NIECE(26, "NIECE", "Niece"),
    IN_LAW(27, "INLAW", "In-law"),
    DAUGHTER_IN_LAW(28, "DAUINLAW", "Daughter-in-law"),
    UNKNOWN(29, "U", "Unknown"),
    NIECE_NEPHEW(30, "NIENEPH", "Niece-Nephew"),
    GUARDIAN(31, "GUARD", "Guard");

    companion object {
        fun fromInt(number: Int) = entries.first { it.number == number }
        fun fromString(value: String) = entries.first { it.value == value }
        fun fromDisplay(display: String) = entries.first { it.display == display }
    }
}