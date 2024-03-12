package com.latticeonfhir.android.utils.fhirengine

import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.search
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.Patient

object FhirQueries {
    suspend fun isIdDuplicate(
        fhirEngine: FhirEngine,
        idSystem: String,
        idValue: String
    ): Boolean {
        fhirEngine.search<Patient> {
            filter(
                Patient.IDENTIFIER,
                {
                    value = of(Identifier().apply {
                        system = idSystem
                        value = idValue
                    })
                }
            )
        }.let { results ->
            return results.isNotEmpty()
        }
    }
}