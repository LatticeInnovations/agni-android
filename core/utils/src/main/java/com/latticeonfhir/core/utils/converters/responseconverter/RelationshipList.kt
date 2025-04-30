package com.latticeonfhir.core.utils.converters.responseconverter

object RelationshipList {
    fun getRelationshipList(gender: String): List<String> {
        return when (gender) {
            "male" -> listOf(
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

            "female" -> listOf(
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

            else -> listOf(
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
}