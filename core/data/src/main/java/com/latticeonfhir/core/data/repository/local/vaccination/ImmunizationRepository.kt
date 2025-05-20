package com.latticeonfhir.core.data.repository.local.vaccination

import com.latticeonfhir.core.model.local.vaccination.Immunization
import java.util.Date

interface ImmunizationRepository {

    suspend fun insertImmunization(immunization: Immunization): List<Long>
    suspend fun getImmunization(patientId: String): List<Immunization>
    suspend fun getImmunizationByTime(createdOn: Date): Immunization
}