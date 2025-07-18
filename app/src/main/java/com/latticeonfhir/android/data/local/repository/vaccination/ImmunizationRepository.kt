package com.latticeonfhir.android.data.local.repository.vaccination

import com.latticeonfhir.android.data.local.model.vaccination.Immunization
import java.util.Date

interface ImmunizationRepository {

    suspend fun insertImmunization(immunization: Immunization): List<Long>
    suspend fun getImmunization(patientId: String): List<Immunization>
    suspend fun getImmunizationByTime(createdOn: Date): Immunization
}