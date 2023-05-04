package com.latticeonfhir.android.utils.converters.responseconverter

object RelationshipList {
    internal fun getRelationshipList(gender: String): List<String>{
        if (gender == "male") return listOf(
            "Son",
            "Father",
            "Grand Father",
            "Brother",
            "Grand Son",
            "Uncle",
            "Brother-in-law",
            "Father-in-law",
            "Son-in-law",
            "Nephew",
            "Husband"
        )
        else if (gender == "female") return listOf(
            "Daughter",
            "Mother",
            "Grand Mother",
            "Sister",
            "Grand Daughter",
            "Aunty",
            "Sister-in-law",
            "Mother-in-law",
            "Daughter-in-law",
            "Niece",
            "Wife"
        )
        else return listOf(
            "Child",
            "Parent",
            "Grand Parent",
            "Sibling",
            "Grand Child",
            "In-Law",
            "Spouse"
        )
    }
}