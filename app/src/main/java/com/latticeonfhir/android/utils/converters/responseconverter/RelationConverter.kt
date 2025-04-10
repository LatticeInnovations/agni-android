package com.latticeonfhir.android.utils.converters.responseconverter

import android.content.Context
import com.latticeonfhir.core.R
import com.latticeonfhir.core.data.local.enums.GenderEnum
import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.core.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.entities.relation.RelationEntity
import com.latticeonfhir.android.utils.converters.responseconverter.RelationConverter.RelationMapping.femaleToFemale
import com.latticeonfhir.core.utils.converters.responseconverter.RelationConverter.RelationMapping.femaleToMale
import com.latticeonfhir.core.utils.converters.responseconverter.RelationConverter.RelationMapping.femaleToOther
import com.latticeonfhir.core.utils.converters.responseconverter.RelationConverter.RelationMapping.femaleToUnknown
import com.latticeonfhir.core.utils.converters.responseconverter.RelationConverter.RelationMapping.maleToFemale
import com.latticeonfhir.core.utils.converters.responseconverter.RelationConverter.RelationMapping.maleToMale
import com.latticeonfhir.core.utils.converters.responseconverter.RelationConverter.RelationMapping.maleToOther
import com.latticeonfhir.core.utils.converters.responseconverter.RelationConverter.RelationMapping.maleToUnknown
import com.latticeonfhir.core.utils.converters.responseconverter.RelationConverter.RelationMapping.otherToFemale
import com.latticeonfhir.core.utils.converters.responseconverter.RelationConverter.RelationMapping.otherToMale
import com.latticeonfhir.core.utils.converters.responseconverter.RelationConverter.RelationMapping.otherToOther
import com.latticeonfhir.android.utils.converters.responseconverter.RelationConverter.RelationMapping.otherToUnknown
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object RelationConverter {

    internal fun getInverseRelation(
        relationEntity: RelationEntity,
        patientDao: PatientDao,
        relationFetched: (RelationEnum) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val fromGender =
                patientDao.getPatientDataById(relationEntity.fromId)[0].patientEntity.gender
            val toGender =
                patientDao.getPatientDataById(relationEntity.toId)[0].patientEntity.gender
            relationFetched(
                when (GenderEnum.fromString(fromGender)) {
                    GenderEnum.MALE -> {
                        fromIsMale(toGender, relationEntity)
                    }

                    GenderEnum.FEMALE -> {
                        fromIsFemale(toGender, relationEntity)
                    }

                    GenderEnum.OTHER -> {
                        fromIsOther(toGender, relationEntity)
                    }

                    GenderEnum.UNKNOWN -> {
                        fromIsUnknown()
                    }
                }
            )
        }
    }

    internal fun getRelationEnumFromString(relation: String): String {
        return when (relation) {
            "Father" -> RelationEnum.FATHER.value
            "Mother" -> RelationEnum.MOTHER.value
            "Grand Father" -> RelationEnum.GRAND_FATHER.value
            "Grand Mother" -> RelationEnum.GRAND_MOTHER.value
            "Brother" -> RelationEnum.BROTHER.value
            "Sister" -> RelationEnum.SISTER.value
            "Wife" -> RelationEnum.WIFE.value
            "Son" -> RelationEnum.SON.value
            "Daughter" -> RelationEnum.DAUGHTER.value
            "Grand Son" -> RelationEnum.GRAND_SON.value
            "Grand Daughter" -> RelationEnum.GRAND_DAUGHTER.value
            "Uncle" -> RelationEnum.UNCLE.value
            "Aunty" -> RelationEnum.AUNTY.value
            "Brother-in-law" -> RelationEnum.BROTHER_IN_LAW.value
            "Sister-in-law" -> RelationEnum.SISTER_IN_LAW.value
            "Father-in-law" -> RelationEnum.FATHER_IN_LAW.value
            "Mother-in-law" -> RelationEnum.MOTHER_IN_LAW.value
            "Son-in-law" -> RelationEnum.SON_IN_LAW.value
            "Nephew" -> RelationEnum.NEPHEW.value
            "Husband" -> RelationEnum.HUSBAND.value
            "Child" -> RelationEnum.CHILD.value
            "Grand Child" -> RelationEnum.GRAND_CHILD.value
            "Sibling" -> RelationEnum.SIBLING.value
            "Spouse" -> RelationEnum.SPOUSE.value
            "Parent" -> RelationEnum.PARENT.value
            "Grand Parent" -> RelationEnum.GRAND_PARENT.value
            "Niece" -> RelationEnum.NIECE.value
            "In-Law" -> RelationEnum.IN_LAW.value
            "Daughter-in-law" -> RelationEnum.DAUGHTER_IN_LAW.value
            else -> RelationEnum.UNKNOWN.value
        }
    }

    internal fun getRelationFromRelationEnum(context: Context, relationEnum: RelationEnum): String {
        return context.resources.getStringArray(R.array.relation)[relationEnum.number]
    }

    private fun fromIsMale(toGender: String, relationEntity: RelationEntity): RelationEnum {
        return when (GenderEnum.fromString(toGender)) {
            GenderEnum.MALE -> {
                maleToMale(relationEntity)
            }

            GenderEnum.FEMALE -> {
                maleToFemale(relationEntity)
            }

            GenderEnum.OTHER -> {
                maleToOther(relationEntity)
            }

            GenderEnum.UNKNOWN -> {
                maleToUnknown()
            }
        }
    }

    private fun fromIsFemale(toGender: String, relationEntity: RelationEntity): RelationEnum {
        return when (GenderEnum.fromString(toGender)) {
            GenderEnum.MALE -> {
                femaleToMale(relationEntity)
            }

            GenderEnum.FEMALE -> {
                femaleToFemale(relationEntity)
            }

            GenderEnum.OTHER -> {
                femaleToOther(relationEntity)
            }

            GenderEnum.UNKNOWN -> {
                femaleToUnknown()
            }
        }
    }

    private fun fromIsOther(toGender: String, relationEntity: RelationEntity): RelationEnum {
        return when (GenderEnum.fromString(toGender)) {
            GenderEnum.MALE -> {
                otherToMale(relationEntity)
            }

            GenderEnum.FEMALE -> {
                otherToFemale(relationEntity)
            }

            GenderEnum.OTHER -> {
                otherToOther(relationEntity)
            }

            GenderEnum.UNKNOWN -> {
                otherToUnknown()
            }
        }
    }

    private fun fromIsUnknown(): RelationEnum {
        return RelationEnum.UNKNOWN
    }

    object RelationMapping {
        internal fun maleToMale(relationEntity: RelationEntity): RelationEnum {
            return when (relationEntity.relation) {
                RelationEnum.FATHER -> RelationEnum.SON
                RelationEnum.GRAND_FATHER -> RelationEnum.GRAND_SON
                RelationEnum.BROTHER -> RelationEnum.BROTHER
                RelationEnum.SON -> RelationEnum.FATHER
                RelationEnum.GRAND_SON -> RelationEnum.GRAND_FATHER
                RelationEnum.UNCLE -> RelationEnum.NEPHEW
                RelationEnum.BROTHER_IN_LAW -> RelationEnum.BROTHER_IN_LAW
                RelationEnum.FATHER_IN_LAW -> RelationEnum.SON_IN_LAW
                RelationEnum.HUSBAND -> RelationEnum.HUSBAND
                RelationEnum.SON_IN_LAW -> RelationEnum.FATHER_IN_LAW
                RelationEnum.NEPHEW -> RelationEnum.UNCLE
                else -> {
                    RelationEnum.UNKNOWN
                }
            }
        }

        internal fun maleToFemale(relationEntity: RelationEntity): RelationEnum {
            return when (relationEntity.relation) {
                RelationEnum.FATHER -> RelationEnum.DAUGHTER
                RelationEnum.GRAND_FATHER -> RelationEnum.GRAND_DAUGHTER
                RelationEnum.BROTHER -> RelationEnum.SISTER
                RelationEnum.SON -> RelationEnum.MOTHER
                RelationEnum.GRAND_SON -> RelationEnum.GRAND_MOTHER
                RelationEnum.UNCLE -> RelationEnum.NIECE
                RelationEnum.HUSBAND -> RelationEnum.WIFE
                RelationEnum.SON_IN_LAW -> RelationEnum.MOTHER_IN_LAW
                RelationEnum.NEPHEW -> RelationEnum.AUNTY
                RelationEnum.BROTHER_IN_LAW -> RelationEnum.SISTER_IN_LAW
                RelationEnum.FATHER_IN_LAW -> RelationEnum.DAUGHTER_IN_LAW
                else -> {
                    RelationEnum.UNKNOWN
                }
            }
        }

        internal fun maleToOther(relationEntity: RelationEntity): RelationEnum {
            return when (relationEntity.relation) {
                RelationEnum.FATHER -> RelationEnum.CHILD
                RelationEnum.GRAND_FATHER -> RelationEnum.GRAND_CHILD
                RelationEnum.BROTHER -> RelationEnum.SIBLING
                RelationEnum.SON -> RelationEnum.PARENT
                RelationEnum.GRAND_SON -> RelationEnum.GRAND_CHILD
                RelationEnum.UNCLE -> RelationEnum.NIECE_NEPHEW
                RelationEnum.BROTHER_IN_LAW -> RelationEnum.IN_LAW
                RelationEnum.FATHER_IN_LAW -> RelationEnum.IN_LAW
                RelationEnum.HUSBAND -> RelationEnum.SPOUSE
                RelationEnum.SON_IN_LAW -> RelationEnum.IN_LAW
                RelationEnum.NEPHEW -> RelationEnum.GUARDIAN
                else -> {
                    RelationEnum.UNKNOWN
                }
            }
        }

        internal fun maleToUnknown(): RelationEnum {
            return RelationEnum.UNKNOWN
        }

        internal fun femaleToMale(relationEntity: RelationEntity): RelationEnum {
            return when (relationEntity.relation) {
                RelationEnum.MOTHER -> RelationEnum.SON
                RelationEnum.GRAND_MOTHER -> RelationEnum.GRAND_SON
                RelationEnum.SISTER -> RelationEnum.BROTHER
                RelationEnum.DAUGHTER -> RelationEnum.FATHER
                RelationEnum.GRAND_DAUGHTER -> RelationEnum.GRAND_FATHER
                RelationEnum.AUNTY -> RelationEnum.NEPHEW
                RelationEnum.SISTER_IN_LAW -> RelationEnum.BROTHER_IN_LAW
                RelationEnum.MOTHER_IN_LAW -> RelationEnum.SON_IN_LAW
                RelationEnum.DAUGHTER_IN_LAW -> RelationEnum.FATHER_IN_LAW
                RelationEnum.NIECE -> RelationEnum.UNCLE
                RelationEnum.WIFE -> RelationEnum.HUSBAND
                else -> {
                    RelationEnum.UNKNOWN
                }
            }
        }

        internal fun femaleToFemale(relationEntity: RelationEntity): RelationEnum {
            return when (relationEntity.relation) {
                RelationEnum.MOTHER -> RelationEnum.DAUGHTER
                RelationEnum.GRAND_MOTHER -> RelationEnum.GRAND_DAUGHTER
                RelationEnum.SISTER -> RelationEnum.SISTER
                RelationEnum.DAUGHTER -> RelationEnum.MOTHER
                RelationEnum.GRAND_DAUGHTER -> RelationEnum.GRAND_MOTHER
                RelationEnum.AUNTY -> RelationEnum.NIECE
                RelationEnum.SISTER_IN_LAW -> RelationEnum.SISTER_IN_LAW
                RelationEnum.MOTHER_IN_LAW -> RelationEnum.DAUGHTER_IN_LAW
                RelationEnum.DAUGHTER_IN_LAW -> RelationEnum.MOTHER_IN_LAW
                RelationEnum.NIECE -> RelationEnum.AUNTY
                RelationEnum.WIFE -> RelationEnum.WIFE
                else -> {
                    RelationEnum.UNKNOWN
                }
            }
        }

        internal fun femaleToOther(relationEntity: RelationEntity): RelationEnum {
            return when (relationEntity.relation) {
                RelationEnum.MOTHER -> RelationEnum.CHILD
                RelationEnum.GRAND_MOTHER -> RelationEnum.GRAND_CHILD
                RelationEnum.SISTER -> RelationEnum.SIBLING
                RelationEnum.DAUGHTER -> RelationEnum.PARENT
                RelationEnum.GRAND_DAUGHTER -> RelationEnum.GRAND_CHILD
                RelationEnum.AUNTY -> RelationEnum.NIECE_NEPHEW
                RelationEnum.SISTER_IN_LAW -> RelationEnum.IN_LAW
                RelationEnum.MOTHER_IN_LAW -> RelationEnum.IN_LAW
                RelationEnum.DAUGHTER_IN_LAW -> RelationEnum.IN_LAW
                RelationEnum.WIFE -> RelationEnum.SPOUSE
                RelationEnum.NIECE -> RelationEnum.GUARDIAN
                else -> {
                    RelationEnum.UNKNOWN
                }
            }
        }

        internal fun femaleToUnknown(): RelationEnum {
            return RelationEnum.UNKNOWN
        }

        internal fun otherToMale(relationEntity: RelationEntity): RelationEnum {
            return when (relationEntity.relation) {
                RelationEnum.PARENT -> RelationEnum.SON
                RelationEnum.GRAND_PARENT -> RelationEnum.GRAND_SON
                RelationEnum.SIBLING -> RelationEnum.BROTHER
                RelationEnum.CHILD -> RelationEnum.FATHER
                RelationEnum.GRAND_CHILD -> RelationEnum.GRAND_FATHER
                RelationEnum.IN_LAW -> RelationEnum.SON_IN_LAW
                RelationEnum.GUARDIAN -> RelationEnum.NEPHEW
                else -> {
                    RelationEnum.UNKNOWN
                }
            }
        }

        internal fun otherToFemale(relationEntity: RelationEntity): RelationEnum {
            return when (relationEntity.relation) {
                RelationEnum.PARENT -> RelationEnum.DAUGHTER
                RelationEnum.GRAND_PARENT -> RelationEnum.GRAND_DAUGHTER
                RelationEnum.SIBLING -> RelationEnum.SISTER
                RelationEnum.CHILD -> RelationEnum.MOTHER
                RelationEnum.GRAND_CHILD -> RelationEnum.GRAND_MOTHER
                RelationEnum.IN_LAW -> RelationEnum.DAUGHTER_IN_LAW
                RelationEnum.GUARDIAN -> RelationEnum.NIECE
                else -> {
                    RelationEnum.UNKNOWN
                }
            }
        }

        internal fun otherToOther(relationEntity: RelationEntity): RelationEnum {
            return when (relationEntity.relation) {
                RelationEnum.PARENT -> RelationEnum.CHILD
                RelationEnum.GRAND_PARENT -> RelationEnum.GRAND_CHILD
                RelationEnum.SIBLING -> RelationEnum.SIBLING
                RelationEnum.CHILD -> RelationEnum.PARENT
                RelationEnum.GRAND_CHILD -> RelationEnum.GRAND_CHILD
                RelationEnum.IN_LAW -> RelationEnum.IN_LAW
                RelationEnum.GUARDIAN -> RelationEnum.NIECE_NEPHEW
                else -> {
                    RelationEnum.UNKNOWN
                }
            }
        }

        internal fun otherToUnknown(): RelationEnum {
            return RelationEnum.UNKNOWN
        }
    }
}