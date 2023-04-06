package com.latticeonfhir.android.utils.relation

import com.latticeonfhir.android.data.local.enums.GenderEnum
import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.entities.RelationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Relation {

    internal fun getInverseRelation(
        relationEntity: RelationEntity,
        patientDao: PatientDao,
        relationFetched: (RelationEnum) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val fromGender = patientDao.getPatientDataById(relationEntity.fromId).patientEntity.gender
            val toGender = patientDao.getPatientDataById(relationEntity.toId).patientEntity.gender
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
}