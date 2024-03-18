package com.latticeonfhir.android.utils.fhirengine

import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.SearchResult
import com.google.android.fhir.search.include
import com.google.android.fhir.search.search
import com.latticeonfhir.android.data.local.enums.AppointmentStatusFhir
import org.hl7.fhir.r4.model.Appointment
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Person
import org.hl7.fhir.r4.model.RelatedPerson
import org.hl7.fhir.r4.model.ResourceType
import java.util.Date

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

    suspend fun getScheduledAppointments(
        fhirEngine: FhirEngine,
        patientId: String
    ): List<SearchResult<Encounter>>{
        return fhirEngine.search<Encounter> {
            filter(
                Encounter.SUBJECT, {
                    value = "${ResourceType.Patient.name}/$patientId"
                }
            )
            filter(
                Encounter.STATUS, {
                    value = of(Encounter.EncounterStatus.PLANNED.toCode())
                }
            )
            include(ResourceType.Appointment, Encounter.APPOINTMENT) {
                filter(
                    Appointment.STATUS, {
                        value = of(Appointment.AppointmentStatus.PROPOSED.toCode())
                    }
                )
                filter(
                    Appointment.APPOINTMENT_TYPE, {
                        value = of(AppointmentStatusFhir.SCHEDULE.type)
                    }
                )
                filter(
                    Appointment.DATE, {
                        prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
                        value = of(DateTimeType(Date()))
                    }
                )
            }
        }
    }

    suspend fun getCompletedAppointments(
        fhirEngine: FhirEngine,
        patientId: String
    ): List<SearchResult<Encounter>>{
        return fhirEngine.search<Encounter> {
            filter(
                Encounter.SUBJECT, {
                    value = "${ResourceType.Patient.name}/$patientId"
                }
            )
            filter(
                Encounter.STATUS, {
                    value = of(Encounter.EncounterStatus.FINISHED.toCode())
                }
            )
            include(ResourceType.Appointment, Encounter.APPOINTMENT) {
                filter(
                    Appointment.STATUS, {
                        value = of(Appointment.AppointmentStatus.ARRIVED.toCode())
                    }
                )
                filter(
                    Appointment.DATE, {
                        prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
                        value = of(DateTimeType(Date()))
                    }
                )
            }
        }
    }
}