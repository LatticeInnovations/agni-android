package com.latticeonfhir.android.utils.converters.responseconverter

import android.content.Context
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.enums.GenderEnum
import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.entities.RelationEntity
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
            val fromGender = patientDao.getPatientDataById(relationEntity.fromId)[0].patientEntity.gender
            val toGender = patientDao.getPatientDataById(relationEntity.toId)[0].patientEntity.gender
            relationFetched(when (GenderEnum.fromString(fromGender)) {
                GenderEnum.MALE -> {
                    when (GenderEnum.fromString(toGender)) {
                        GenderEnum.MALE -> {
                            when (relationEntity.relation) {
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
                        GenderEnum.FEMALE -> {
                            when (relationEntity.relation) {
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
                        GenderEnum.OTHER -> {
                            when (relationEntity.relation) {
                                RelationEnum.FATHER -> RelationEnum.CHILD
                                RelationEnum.GRAND_FATHER -> RelationEnum.GRAND_CHILD
                                RelationEnum.BROTHER -> RelationEnum.SIBLING
                                RelationEnum.SON -> RelationEnum.PARENT
                                RelationEnum.GRAND_SON -> RelationEnum.GRAND_CHILD
                                RelationEnum.UNCLE -> RelationEnum.NEPHEW
                                RelationEnum.BROTHER_IN_LAW -> RelationEnum.IN_LAW
                                RelationEnum.FATHER_IN_LAW -> RelationEnum.IN_LAW
                                RelationEnum.HUSBAND -> RelationEnum.SPOUSE
                                RelationEnum.SON_IN_LAW -> RelationEnum.IN_LAW
                                else -> {
                                    RelationEnum.UNKNOWN
                                }
                            }
                        }
                        GenderEnum.UNKNOWN -> {
                            RelationEnum.UNKNOWN
                        }
                    }
                }
                GenderEnum.FEMALE -> {
                    when (GenderEnum.fromString(toGender)) {
                        GenderEnum.MALE -> {
                            when (relationEntity.relation) {
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
                        GenderEnum.FEMALE -> {
                            when (relationEntity.relation) {
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
                        GenderEnum.OTHER -> {
                            when (relationEntity.relation) {
                                RelationEnum.MOTHER -> RelationEnum.CHILD
                                RelationEnum.GRAND_MOTHER -> RelationEnum.GRAND_CHILD
                                RelationEnum.SISTER -> RelationEnum.SIBLING
                                RelationEnum.DAUGHTER -> RelationEnum.PARENT
                                RelationEnum.GRAND_DAUGHTER -> RelationEnum.GRAND_CHILD
                                RelationEnum.AUNTY -> RelationEnum.NEPHEW
                                RelationEnum.SISTER_IN_LAW -> RelationEnum.IN_LAW
                                RelationEnum.MOTHER_IN_LAW -> RelationEnum.IN_LAW
                                RelationEnum.DAUGHTER_IN_LAW -> RelationEnum.IN_LAW
                                RelationEnum.WIFE -> RelationEnum.SPOUSE
                                else -> {
                                    RelationEnum.UNKNOWN
                                }
                            }
                        }
                        GenderEnum.UNKNOWN -> {
                            RelationEnum.UNKNOWN
                        }
                    }
                }
                GenderEnum.OTHER -> {
                    when (GenderEnum.fromString(toGender)) {
                        GenderEnum.MALE -> {
                            when (relationEntity.relation) {
                                RelationEnum.FATHER -> RelationEnum.SON
                                RelationEnum.GRAND_FATHER -> RelationEnum.GRAND_SON
                                RelationEnum.BROTHER -> RelationEnum.BROTHER
                                RelationEnum.SON -> RelationEnum.FATHER
                                RelationEnum.GRAND_SON -> RelationEnum.GRAND_FATHER
                                RelationEnum.UNCLE -> RelationEnum.NEPHEW
                                RelationEnum.BROTHER_IN_LAW -> RelationEnum.BROTHER_IN_LAW
                                RelationEnum.FATHER_IN_LAW -> RelationEnum.SON_IN_LAW
                                else -> {
                                    RelationEnum.UNKNOWN
                                }
                            }
                        }
                        GenderEnum.FEMALE -> {
                            when (relationEntity.relation) {
                                RelationEnum.FATHER -> RelationEnum.DAUGHTER
                                RelationEnum.GRAND_FATHER -> RelationEnum.GRAND_DAUGHTER
                                RelationEnum.BROTHER -> RelationEnum.SISTER
                                RelationEnum.SON -> RelationEnum.MOTHER
                                RelationEnum.GRAND_SON -> RelationEnum.GRAND_MOTHER
                                RelationEnum.UNCLE -> RelationEnum.NIECE
                                RelationEnum.BROTHER_IN_LAW -> RelationEnum.SISTER_IN_LAW
                                RelationEnum.FATHER_IN_LAW -> RelationEnum.DAUGHTER_IN_LAW
                                else -> {
                                    RelationEnum.UNKNOWN
                                }
                            }
                        }
                        GenderEnum.OTHER -> {
                            when (relationEntity.relation) {
                                RelationEnum.FATHER -> RelationEnum.CHILD
                                RelationEnum.GRAND_FATHER -> RelationEnum.GRAND_CHILD
                                RelationEnum.BROTHER -> RelationEnum.SIBLING
                                RelationEnum.SON -> RelationEnum.PARENT
                                RelationEnum.GRAND_SON -> RelationEnum.GRAND_CHILD
                                RelationEnum.UNCLE -> RelationEnum.NEPHEW
                                RelationEnum.BROTHER_IN_LAW -> RelationEnum.IN_LAW
                                RelationEnum.FATHER_IN_LAW -> RelationEnum.IN_LAW
                                else -> {
                                    RelationEnum.UNKNOWN
                                }
                            }
                        }
                        GenderEnum.UNKNOWN -> {
                            RelationEnum.UNKNOWN
                        }
                    }
                }
                GenderEnum.UNKNOWN -> {
                    RelationEnum.UNKNOWN
                }
            })
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
            "Unknown" -> RelationEnum.UNKNOWN.value
            else -> {
                RelationEnum.UNKNOWN.value
            }
        }
    }
        
    internal fun getRelationFromRelationEnum(context: Context, relationEnum: RelationEnum): String {
        return context.resources.getStringArray(R.array.relation)[relationEnum.number]
    }
}