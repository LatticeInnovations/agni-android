package com.latticeonfhir.android.utils.fhirengine

import com.google.android.fhir.FhirEngine
import com.google.android.fhir.SearchResult
import com.google.android.fhir.search.include
import com.google.android.fhir.search.search
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Person
import org.hl7.fhir.r4.model.RelatedPerson
import org.hl7.fhir.r4.model.ResourceType

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

    suspend fun getPersonResource(
        fhirEngine: FhirEngine,
        patientId: String
    ): Person {
        return fhirEngine.search<Person> {
            filter(
                Person.LINK, {
                    value = "Patient/$patientId"
                }
            )
        }[0].resource
    }

    suspend fun getRelatedPerson(
        fhirEngine: FhirEngine,
        relatedPersonId: String
    ): List<SearchResult<RelatedPerson>> {
        return fhirEngine.search<RelatedPerson> {
            filter(
                RelatedPerson.RES_ID, {
                    value = of(relatedPersonId)
                }
            )
            include(ResourceType.Patient, RelatedPerson.PATIENT)
        }
    }
}