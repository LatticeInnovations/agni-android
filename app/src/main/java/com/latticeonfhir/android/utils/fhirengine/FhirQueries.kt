package com.latticeonfhir.android.utils.fhirengine

import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.SearchResult
import com.google.android.fhir.logicalId
import com.google.android.fhir.search.include
import com.google.android.fhir.search.search
import com.latticeonfhir.android.data.local.enums.AppointmentStatusFhir
import com.latticeonfhir.android.data.local.enums.MedicationTimingEnum
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.ENCOUNTER_SYSTEM
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.GROUP_IDENTIFIER
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.LATTICE
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.LATTICE_SYSTEM
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.SCT_URL
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import org.hl7.fhir.r4.model.Annotation
import org.hl7.fhir.r4.model.Appointment
import org.hl7.fhir.r4.model.Appointment.AppointmentStatus
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Dosage
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Encounter.EncounterStatus
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.Medication
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Person
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.RelatedPerson
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.Schedule
import org.hl7.fhir.r4.model.Slot
import org.hl7.fhir.r4.model.Timing
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
    ): List<SearchResult<Encounter>> {
        return fhirEngine.search<Encounter> {
            filter(
                Encounter.SUBJECT, {
                    value = "${ResourceType.Patient.name}/$patientId"
                }
            )
            filter(
                Encounter.STATUS, {
                    value = of(EncounterStatus.PLANNED.toCode())
                }
            )
            include(ResourceType.Appointment, Encounter.APPOINTMENT) {
                filter(
                    Appointment.STATUS, {
                        value = of(AppointmentStatus.PROPOSED.toCode())
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
    ): List<SearchResult<Encounter>> {
        return fhirEngine.search<Encounter> {
            filter(
                Encounter.SUBJECT, {
                    value = "${ResourceType.Patient.name}/$patientId"
                }
            )
            filter(
                Encounter.STATUS, {
                    value = of(EncounterStatus.FINISHED.toCode())
                }
            )
            include(ResourceType.Appointment, Encounter.APPOINTMENT) {
                filter(
                    Appointment.STATUS, {
                        value = of(AppointmentStatus.ARRIVED.toCode())
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

    suspend fun getAppointmentToday(
        fhirEngine: FhirEngine,
        patientId: String
    ): List<SearchResult<Encounter>> {
        return fhirEngine.search<Encounter> {
            filter(
                Encounter.SUBJECT, {
                    value = "${ResourceType.Patient.name}/$patientId"
                }
            )
            include(ResourceType.Appointment, Encounter.APPOINTMENT) {
                filter(
                    Appointment.STATUS, {
                        value = of(AppointmentStatus.ARRIVED.toCode())
                    }, {
                        value = of(AppointmentStatus.NOSHOW.toCode())
                    }
                )
                filter(
                    Appointment.DATE, {
                        prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
                        value = of(DateTimeType(Date(Date().toEndOfDay())))
                    }
                )
                filter(
                    Appointment.DATE, {
                        prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
                        value = of(DateTimeType(Date(Date().toTodayStartDate())))
                    }
                )
            }
        }
    }

    suspend fun getTotalNumberOfAppointmentsToday(
        fhirEngine: FhirEngine
    ): Int {
        return fhirEngine.search<Appointment> {
            filter(
                Appointment.STATUS, {
                    value = of(AppointmentStatus.ARRIVED.toCode())
                }, {
                    value = of(AppointmentStatus.NOSHOW.toCode())
                }, {
                    value = of(AppointmentStatus.PROPOSED.toCode())
                }
            )
            filter(
                Appointment.DATE, {
                    prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
                    value = of(DateTimeType(Date(Date().toEndOfDay())))
                }
            )
            filter(
                Appointment.DATE, {
                    prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
                    value = of(DateTimeType(Date(Date().toTodayStartDate())))
                }
            )
        }.size
    }

    suspend fun getTodayScheduledAppointmentOfPatient(
        fhirEngine: FhirEngine,
        patientId: String,
        startTime: Date,
        endTime: Date
    ): Appointment? {
        fhirEngine.search<Encounter> {
            filter(
                Encounter.SUBJECT, {
                    value = "${ResourceType.Patient.name}/$patientId"
                }
            )
            filter(
                Encounter.STATUS, {
                    value = of(EncounterStatus.PLANNED.toCode())
                }
            )
            include(ResourceType.Appointment, Encounter.APPOINTMENT) {
                filter(
                    Appointment.STATUS, {
                        value = of(AppointmentStatus.PROPOSED.toCode())
                    }
                )
                filter(
                    Appointment.APPOINTMENT_TYPE, {
                        value = of(AppointmentStatusFhir.SCHEDULE.type)
                    }
                )

                filter(
                    Appointment.DATE, {
                        prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
                        value = of(DateTimeType(endTime))
                    }
                )
                filter(
                    Appointment.DATE, {
                        prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
                        value = of(DateTimeType(startTime))
                    }
                )
            }
        }.forEach { searchResult ->
            searchResult.included?.get(Encounter.APPOINTMENT.paramName)
                ?.forEach { appointment ->
                    if ((appointment as Appointment).start > startTime && appointment.start < endTime)
                        return appointment
                }
        }
        return null
    }

    suspend fun getScheduleByTime(
        fhirEngine: FhirEngine,
        startTime: Date,
        endTime: Date
    ): Schedule? {
        fhirEngine.search<Schedule> {
            filter(
                Schedule.DATE, {
                    prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
                    value = of(DateTimeType(startTime))
                }
            )
            filter(
                Schedule.DATE, {
                    prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
                    value = of(DateTimeType(endTime))
                }
            )
        }.forEach { result ->
            return result.resource
        }
        return null
    }

    suspend fun createScheduleResource(
        fhirEngine: FhirEngine,
        scheduleId: String,
        locationId: String,
        startTime: Date,
        endTime: Date
    ) {
        fhirEngine.create(
            Schedule().apply {
                id = scheduleId
                identifier.add(
                    Identifier().apply {
                        type = CodeableConcept(
                            Coding(
                                LATTICE_SYSTEM,
                                "U",
                                ""
                            )
                        )
                        system = LATTICE
                        value = scheduleId
                    }
                )
                active = true
                actor.add(
                    Reference("${ResourceType.Location.name}/$locationId")
                )
                planningHorizon.start = startTime
                planningHorizon.end = endTime
            }
        )
    }

    private fun createSlotResource(
        slotId: String,
        scheduleId: String,
        startTime: Date,
        endTime: Date
    ): Slot {
        return Slot().apply {
            id = slotId
            schedule.reference = "${ResourceType.Schedule.name}/$scheduleId"
            status = Slot.SlotStatus.FREE
            start = startTime
            end = endTime
        }
    }

    private fun createAppointmentResource(
        patientId: String,
        locationId: String,
        appointmentId: String,
        appointmentStatus: AppointmentStatus,
        typeOfAppointment: String,
        startTime: Date,
        slotId: String
    ): Appointment {
        return Appointment().apply {
            id = appointmentId
            identifier.add(
                Identifier().apply {
                    type = CodeableConcept(
                        Coding(
                            LATTICE_SYSTEM,
                            "U",
                            ""
                        )
                    )
                    system = LATTICE
                    value = appointmentId
                }
            )
            status = appointmentStatus
            appointmentType = CodeableConcept(
                Coding(
                    "http://snomed.info/sct",
                    typeOfAppointment,
                    ""
                )
            )
            start = startTime
            slot.add(
                Reference("${ResourceType.Slot.name}/$slotId")
            )
            created = Date()
            participant.addAll(
                listOf(
                    Appointment.AppointmentParticipantComponent()
                        .setActor(Reference("${ResourceType.Patient.name}/$patientId")),
                    Appointment.AppointmentParticipantComponent()
                        .setActor(Reference("${ResourceType.Location.name}/$locationId"))
                )
            )
        }
    }

    private fun createEncounterResource(
        patientId: String,
        encounterId: String,
        appointmentId: String
    ): Encounter {
        return Encounter().apply {
            id = encounterId
            identifier.add(
                Identifier().apply {
                    system = ENCOUNTER_SYSTEM
                    value = encounterId
                }
            )
            status = EncounterStatus.PLANNED
            subject.reference = "${ResourceType.Patient.name}/$patientId"
            appointment.add(
                Reference("${ResourceType.Appointment.name}/$appointmentId")
            )
        }
    }

    suspend fun getNumberOfAppointmentsByScheduleId(
        fhirEngine: FhirEngine,
        scheduleId: String
    ): Int {
        var numberOfAppointments = 0
        fhirEngine.search<Slot> {
            filter(
                Slot.SCHEDULE, {
                    value = "${ResourceType.Schedule.name}/$scheduleId"
                }
            )
        }.forEach { slot ->
            fhirEngine.search<Appointment> {
                filter(
                    Appointment.SLOT, {
                        value = "${ResourceType.Slot.name}/${slot.resource.logicalId}"
                    }
                )
            }.forEach { _ ->
                numberOfAppointments++
            }
        }
        return numberOfAppointments
    }

    suspend fun createNewAppointment(
        fhirEngine: FhirEngine,
        patientId: String,
        locationId: String,
        scheduleStartTime: Date,
        scheduleEndTime: Date,
        slotStartTime: Date,
        slotEndTime: Date,
        appointmentStatus: AppointmentStatus,
        typeOfAppointment: String
    ) {
        var scheduleId = UUIDBuilder.generateUUID()
        val scheduleResource = getScheduleByTime(
            fhirEngine,
            scheduleStartTime,
            scheduleEndTime
        )
        if (scheduleResource != null) {
            scheduleId = scheduleResource.logicalId
        } else {
            // create a schedule
            createScheduleResource(
                fhirEngine,
                scheduleId,
                locationId,
                scheduleStartTime,
                scheduleEndTime
            )
        }
        val slotId = UUIDBuilder.generateUUID()
        val appointmentId = UUIDBuilder.generateUUID()
        fhirEngine.create(
            createSlotResource(
                slotId = slotId,
                scheduleId = scheduleId,
                startTime = slotStartTime,
                endTime = slotEndTime
            ),
            createAppointmentResource(
                patientId = patientId,
                locationId = locationId,
                appointmentId = appointmentId,
                appointmentStatus = appointmentStatus,
                typeOfAppointment = typeOfAppointment,
                startTime = slotStartTime,
                slotId = slotId
            ),
            createEncounterResource(
                patientId = patientId,
                encounterId = UUIDBuilder.generateUUID(),
                appointmentId = appointmentId
            )
        )
    }

    suspend fun getAllAppointmentByDate(
        fhirEngine: FhirEngine,
        date: Date
    ): List<SearchResult<Encounter>> {
        return fhirEngine.search<Encounter> {
            include(ResourceType.Patient, Encounter.SUBJECT)
            include(ResourceType.Appointment, Encounter.APPOINTMENT) {
                filter(
                    Appointment.DATE, {
                        prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
                        value = of(DateTimeType(Date(date.toEndOfDay())))
                    }
                )
                filter(
                    Appointment.DATE, {
                        prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
                        value = of(DateTimeType(Date(date.toTodayStartDate())))
                    }
                )
            }
        }.filter { searchResult ->
            !(searchResult.included?.get(Encounter.SUBJECT.paramName).isNullOrEmpty() ||
                    searchResult.included?.get(Encounter.APPOINTMENT.paramName).isNullOrEmpty())
        }
    }

    suspend fun getAppointments(
        fhirEngine: FhirEngine,
        date: Date,
        encounterStatus: EncounterStatus,
        appointmentStatus: AppointmentStatus
    ): List<SearchResult<Encounter>> {
        return fhirEngine.search<Encounter> {
            filter(
                Encounter.STATUS, {
                    value = of(encounterStatus.toCode())
                }
            )
            include(ResourceType.Appointment, Encounter.APPOINTMENT) {
                filter(
                    Appointment.STATUS, {
                        value = of(appointmentStatus.toCode())
                    }
                )
                filter(
                    Appointment.DATE, {
                        prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
                        value = of(DateTimeType(date))
                    }
                )
            }
        }.filter { searchResult ->
            !searchResult.included?.get(Encounter.APPOINTMENT.paramName).isNullOrEmpty()
        }
    }

    suspend fun getTodayAppointmentAndEncounterOfPatient(
        fhirEngine: FhirEngine,
        patientId: String
    ): SearchResult<Encounter>? {
        return fhirEngine.search<Encounter> {
            filter(
                Encounter.SUBJECT, {
                    value = "${ResourceType.Patient.name}/$patientId"
                }
            )
            include(ResourceType.Appointment, Encounter.APPOINTMENT) {
                filter(
                    Appointment.DATE, {
                        prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
                        value = of(DateTimeType(Date(Date().toEndOfDay())))
                    }
                )
                filter(
                    Appointment.DATE, {
                        prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
                        value = of(DateTimeType(Date(Date().toTodayStartDate())))
                    }
                )
            }
        }.firstOrNull { searchResult ->
            val appointment =
                searchResult.included?.get(Encounter.APPOINTMENT.paramName)?.get(0) as Appointment?
            (appointment != null) && (appointment.status != AppointmentStatus.CANCELLED)
        }
    }

    suspend fun getMedicationList(
        fhirEngine: FhirEngine
    ): List<Medication> {
        return fhirEngine.search<Medication> { }.map {
            it.resource
        }
    }

    fun createMedicationRequestResource(
        encounterId: String,
        medicationId: String,
        patientId: String,
        notes: String,
        medTiming: String,
        freq: String,
        duration: String,
        qty: String,
        formDisplay: String,
        formCode: String
    ): MedicationRequest {
        val uuid = UUIDBuilder.generateUUID()
        val groupId = Date().time.toString().slice(9 .. 11)+patientId
        return MedicationRequest().apply {
            id = uuid
            identifier.addAll(
                listOf(
                    Identifier().apply {
                        system = ENCOUNTER_SYSTEM
                        value = uuid
                    },
                    Identifier().apply {
                        system = ENCOUNTER_SYSTEM
                        value = encounterId
                    },
                    Identifier().apply {
                        system = SCT_URL
                        value = medicationId
                    }
                )
            )
            intent = MedicationRequest.MedicationRequestIntent.ORDER
            medicationReference.reference = "${ResourceType.Medication.name}/$medicationId"
            subject.reference = "${ResourceType.Patient.name}/$patientId"
            encounter.reference = "${ResourceType.Encounter.name}/$encounterId"
            groupIdentifier = Identifier().apply {
                system = GROUP_IDENTIFIER
                value = groupId
            }
            if (notes.isNotBlank()) {
                note.add(
                    Annotation().apply {
                        text = notes
                    }
                )
            }
            dosageInstruction.add(
                Dosage().apply {
                    if (medTiming.isNotBlank()) {
                        additionalInstruction.add(
                            CodeableConcept(
                                Coding(
                                    "",
                                    MedicationTimingEnum.getCode(medTiming),
                                    medTiming
                                )
                            )
                        )
                    }
                    timing.repeat.apply {
                        boundsDuration.apply {
                            unit = "days"
                            system = "http://unitsofmeasure.org"
                            code = "d"
                        }
                        frequency = freq.toInt()
                        period = duration.toBigDecimal()
                        periodUnit = Timing.UnitsOfTime.D
                    }
                    doseAndRate.add(
                        Dosage.DosageDoseAndRateComponent().apply {
                            doseQuantity.apply {
                                value = qty.toBigDecimal()
                                unit = formDisplay
                                code = formCode
                            }
                        }
                    )
                }
            )
        }
    }
}