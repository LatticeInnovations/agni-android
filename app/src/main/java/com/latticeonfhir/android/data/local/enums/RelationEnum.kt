package com.latticeonfhir.android.data.local.enums

enum class RelationEnum(val number: Int, val value: String) {

    MOTHER(0,"MTH"),
    FATHER(1,"FTH"),
    GRAND_FATHER(2,"GRFTH"),
    GRAND_MOTHER(3,"GRMTH"),
    BROTHER(4,"BRO"),
    SISTER(5,"SIS"),
    WIFE(6,"WIFE"),
    SON(7,"SON"),
    DAUGHTER(8,"DAUC"),
    GRAND_SON(9,"GRNDSON"),
    GRAND_DAUGHTER(10,"GRNDDAU"),
    UNCLE(11,"UNCLE"),
    AUNTY(12,"AUNT"),
    BROTHER_IN_LAW(13,"BROINLAW"),
    SISTER_IN_LAW(14,"SISINLAW"),
    FATHER_IN_LAW(15,"FTHINLAW"),
    MOTHER_IN_LAW(16,"MTHINLAW"),
    NEPHEW(17,"NEPHEW"),
    SON_IN_LAW(18,"SONINLAW"),
    HUSBAND(19,"HUSB"),
    CHILD(20,"CHILD"),
    GRAND_CHILD(21,"GRNDCHILD"),
    SIBLING(22,"SIB"),
    SPOUSE(23,"SPS"),
    PARENT(24,"PRN"),
    GRAND_PARENT(25,"GRPRN"),
    NIECE(26,"NIECE"),
    IN_LAW(27,"INLAW"),
    DAUGHTER_IN_LAW(28,"DAUINLAW"),
    UNKNOWN(29,"U");

    companion object {
        fun fromInt(number: Int) = values().first { it.number == number }
        fun fromString(value: String) = values().first { it.value == value }
    }
}