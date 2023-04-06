package com.latticeonfhir.android.data.local.enums

enum class RelationEnum(val number: Int, val value: String) {

    MOTHER(0,"MTH"),
    FATHER(1,"FTH"),
    GRAND_FATHER(2,"GFTH"),
    GRAND_MOTHER(3,"GMTH"),
    BROTHER(4,"BRO"),
    SISTER(5,"SIS"),
    WIFE(6,"WIFE"),
    SON(7,"SON"),
    DAUGHTER(8,"DTR"),
    GRAND_SON(9,"GSON"),
    GRAND_DAUGHTER(10,"GDTR"),
    UNCLE(11,"UCL"),
    AUNTY(12,"ANT"),
    BROTHER_IN_LAW(13,"BROILAW"),
    SISTER_IN_LAW(14,"SISILAW"),
    FATHER_IN_LAW(15,"FTHILAW"),
    MOTHER_IN_LAW(16,"MTHILAW"),
    NEPHEW(17,"NPH"),
    SON_IN_LAW(18,"SONILAW"),
    HUSBAND(19,"HUSBAND"),
    CHILD(20,"CLD"),
    GRAND_CHILD(21,"GCLD"),
    SIBLING(22,"SIB"),
    SPOUSE(23,"SPOUSE"),
    PARENT(24,"PRT"),
    GRAND_PARENT(25,"GPRT"),
    NIECE(26,"NIC"),
    IN_LAW(27,"ILAW"),
    DAUGHTER_IN_LAW(28,"DAUILAW"),
    UNKNOWN(29,"UNK");

    companion object {
        fun fromInt(number: Int) = values().first { it.number == number }
        fun fromString(value: String) = values().first { it.value == value }
    }
}