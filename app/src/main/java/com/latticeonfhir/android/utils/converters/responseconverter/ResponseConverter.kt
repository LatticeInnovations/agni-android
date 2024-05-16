package com.latticeonfhir.android.utils.converters.responseconverter

import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionResponseLocal
import com.latticeonfhir.android.data.local.model.relation.Relation
import com.latticeonfhir.android.data.local.roomdb.dao.MedicationDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.dao.ScheduleDao
import com.latticeonfhir.android.data.local.roomdb.entities.appointment.AppointmentEntity
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationEntity
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicineTimingEntity
import com.latticeonfhir.android.data.local.roomdb.entities.patient.IdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientEntity
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientLastUpdatedEntity
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PermanentAddressEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionEntity
import com.latticeonfhir.android.data.local.roomdb.entities.relation.RelationEntity
import com.latticeonfhir.android.data.local.roomdb.entities.schedule.ScheduleEntity
import com.latticeonfhir.android.data.local.roomdb.views.PrescriptionDirectionAndMedicineView
import com.latticeonfhir.android.data.server.api.PatientApiService
import com.latticeonfhir.android.data.server.constants.EndPoints.PATIENT
import com.latticeonfhir.android.data.server.constants.QueryParameters
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientLastUpdatedResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicationResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicineTimeResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.Medication
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.data.server.model.relatedperson.Relationship
import com.latticeonfhir.android.data.server.model.scheduleandappointment.Slot
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.RelationConverter.getInverseRelation
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiResponseConverter
import java.util.Date

fun PatientResponse.toPatientEntity(): PatientEntity {
    return PatientEntity(
        id = id,
        firstName = firstName,
        middleName = middleName,
        lastName = lastName,
        active = active,
        gender = gender,
        birthDate = birthDate.toTimeInMilli(),
        mobileNumber = mobileNumber,
        email = email,
        permanentAddress = permanentAddress.toPermanentAddressEntity(),
        fhirId = fhirId
    )
}

fun PatientAddressResponse.toPermanentAddressEntity(): PermanentAddressEntity {
    return PermanentAddressEntity(
        addressLine1 = addressLine1,
        city = city,
        district = district,
        state = state,
        postalCode = postalCode,
        country = country,
        addressLine2 = addressLine2
    )
}

fun PatientIdentifier.toIdentifierEntity(patientId: String): IdentifierEntity {
    return IdentifierEntity(
        identifierNumber = identifierNumber,
        identifierType = identifierType,
        identifierCode = code,
        patientId = patientId
    )
}

fun PatientResponse.toListOfIdentifierEntity(): List<IdentifierEntity> {
    return this.identifier.map {
        it.toIdentifierEntity(this.id)
    }
}

fun PatientAndIdentifierEntity.toPatientResponse(): PatientResponse {
    return PatientResponse(
        id = patientEntity.id,
        firstName = patientEntity.firstName,
        middleName = patientEntity.middleName,
        lastName = patientEntity.lastName,
        identifier = identifiers.map { it.toPatientIdentifier() },
        active = patientEntity.active,
        gender = patientEntity.gender,
        birthDate = Date(patientEntity.birthDate).time.toPatientDate(),
        mobileNumber = patientEntity.mobileNumber,
        email = patientEntity.email,
        permanentAddress = patientEntity.permanentAddress.toPatientAddressResponse(),
        fhirId = patientEntity.fhirId
    )
}

fun PatientResponse.toPatientAndIdentifierEntityResponse(): PatientAndIdentifierEntity {
    return PatientAndIdentifierEntity(
        patientEntity = toPatientEntity(),
        identifiers = toListOfIdentifierEntity()
    )
}

fun IdentifierEntity.toPatientIdentifier(): PatientIdentifier {
    return PatientIdentifier(
        identifierType = identifierType, identifierNumber = identifierNumber, code = identifierCode
    )
}

fun PermanentAddressEntity.toPatientAddressResponse(): PatientAddressResponse {
    return PatientAddressResponse(
        addressLine1 = addressLine1,
        city = city,
        district = district,
        state = state,
        postalCode = postalCode,
        country = country,
        addressLine2 = addressLine2
    )
}

fun RelationEntity.toReverseRelation(
    patientDao: PatientDao,
    inverseRelationEntity: (RelationEntity) -> Unit
) {
    getInverseRelation(this, patientDao) { relationEnum ->
        inverseRelationEntity(
            RelationEntity(
                id = UUIDBuilder.generateUUID(),
                toId = fromId,
                fromId = toId,
                relation = relationEnum
            )
        )
    }
}

fun List<GenericEntity>.toListOfId(): List<String> {
    return this.map { it.id }
}

fun Relation.toRelationEntity(): RelationEntity {
    return RelationEntity(
        id = UUIDBuilder.generateUUID(),
        fromId = patientId,
        toId = relativeId,
        relation = RelationEnum.fromString(relation)
    )
}

fun RelationEntity.toRelation(): Relation {
    return Relation(
        patientId = fromId, relativeId = toId, relation = relation.value
    )
}

internal suspend fun Relationship.toRelationEntity(
    fromFhirId: String,
    patientDao: PatientDao,
    patientApiService: PatientApiService
): RelationEntity {
    return RelationEntity(
        id = UUIDBuilder.generateUUID(),
        fromId = patientDao.getPatientIdByFhirId(fromFhirId)!!,
        toId = patientDao.getPatientIdByFhirId(relativeId) ?: getRelativeId(
            fromFhirId,
            patientApiService
        ),
        relation = RelationEnum.fromString(patientIs)
    )
}

private suspend fun getRelativeId(
    relativeFhirId: String,
    patientApiService: PatientApiService
): String {
    var relativeId = ""
    ApiResponseConverter.convert(
        patientApiService.getListData(
            PATIENT,
            mapOf(Pair(QueryParameters.ID, relativeFhirId))
        )
    ).apply {
        if (this is ApiEndResponse) {
            body.map {
                relativeId = it.id
            }
        }
    }
    return relativeId
}

internal fun <T> List<T>.toNoBracketAndNoSpaceString(): String {
    return this.toString().replace("[", "").replace("]", "").replace(" ", "")
}

internal suspend fun PrescriptionResponse.toPrescriptionEntity(
    patientDao: PatientDao,
): PrescriptionEntity {
    return PrescriptionEntity(
        id = prescriptionId,
        prescriptionDate = generatedOn,
        patientId = patientDao.getPatientIdByFhirId(patientFhirId)!!,
        appointmentId = appointmentUuid,
        patientFhirId = patientFhirId,
        prescriptionFhirId = prescriptionFhirId
    )
}


internal fun PrescriptionResponseLocal.toPrescriptionEntity(): PrescriptionEntity {
    return PrescriptionEntity(
        id = prescriptionId,
        prescriptionDate = generatedOn,
        patientId = patientId,
        appointmentId = appointmentId,
        patientFhirId = patientFhirId,
        prescriptionFhirId = null
    )
}

internal suspend fun PrescriptionResponse.toListOfPrescriptionDirectionsEntity(medicationDao: MedicationDao): List<PrescriptionDirectionsEntity> {
    return prescription.map { medication ->
        PrescriptionDirectionsEntity(
            id = medication.medFhirId + prescriptionId,
            medFhirId = medication.medFhirId,
            qtyPerDose = medication.qtyPerDose,
            frequency = medication.frequency,
            timing = medication.timing?.let { timing ->
                medicationDao.getMedicalDosageByMedicalDosageId(
                    timing
                )
            },
            duration = medication.duration,
            qtyPrescribed = medication.qtyPrescribed,
            note = medication.note,
            prescriptionId = prescriptionId,
        )
    }
}

internal fun PrescriptionResponseLocal.toListOfPrescriptionDirectionsEntity(): List<PrescriptionDirectionsEntity> {
    return prescription.map { medication ->
        PrescriptionDirectionsEntity(
            id = medication.medFhirId + prescriptionId,
            medFhirId = medication.medFhirId,
            qtyPerDose = medication.qtyPerDose,
            frequency = medication.frequency,
            timing = medication.timing,
            duration = medication.duration,
            qtyPrescribed = medication.qtyPrescribed,
            note = medication.note,
            prescriptionId = prescriptionId
        )
    }
}

internal fun List<MedicationResponse>.toListOfMedicationEntity(): List<MedicationEntity> {
    return this.map { medication ->
        MedicationEntity(
            medFhirId = medication.medFhirId,
            medCodeName = medication.medCode,
            medName = medication.medName,
            doseForm = medication.doseForm,
            doseFormCode = medication.doseFormCode,
            activeIngredient = medication.activeIngredient,
            activeIngredientCode = medication.activeIngredientCode,
            medUnit = medication.medUnit,
            medNumeratorVal = medication.medNumeratorVal
        )
    }
}

internal fun MedicationEntity.toMedicationResponse(): MedicationResponse {
    return MedicationResponse(
        medFhirId = this.medFhirId,
        medCode = this.medCodeName,
        medName = this.medName,
        doseForm = this.doseForm,
        doseFormCode = this.doseFormCode,
        activeIngredient = this.activeIngredient,
        activeIngredientCode = this.activeIngredientCode,
        medUnit = this.medUnit,
        medNumeratorVal = this.medNumeratorVal
    )
}

internal fun List<MedicineTimeResponse>.toListOfMedicineDirectionsEntity(): List<MedicineTimingEntity> {
    return map { medicineTimeResponse ->
        MedicineTimingEntity(
            medicalDosage = medicineTimeResponse.medInstructionVal,
            medicalDosageId = medicineTimeResponse.medInstructionCode
        )
    }
}

internal fun ScheduleResponse.toScheduleEntity(): ScheduleEntity {
    return ScheduleEntity(
        id = uuid,
        scheduleFhirId = scheduleId,
        startTime = planningHorizon.start,
        endTime = planningHorizon.end,
        bookedSlots = bookedSlots!!,
        orgId = orgId
    )
}

internal fun ScheduleEntity.toScheduleResponse(): ScheduleResponse {
    return ScheduleResponse(
        uuid = id,
        scheduleId = scheduleFhirId,
        bookedSlots = bookedSlots,
        orgId = orgId,
        planningHorizon = Slot(
            start = startTime,
            end = endTime
        )
    )
}

// Appointment Response from Server
internal suspend fun AppointmentResponse.toAppointmentEntity(
    patientDao: PatientDao,
    scheduleDao: ScheduleDao
): AppointmentEntity {
    return AppointmentEntity(
        id = uuid,
        appointmentFhirId = appointmentId,
        createdOn = createdOn,
        patientId = patientDao.getPatientIdByFhirId(patientFhirId!!)!!,
        scheduleId = scheduleDao.getScheduleStartTimeByFhirId(scheduleId)!!,
        orgId = orgId,
        status = status,
        startTime = slot.start,
        endTime = slot.end
    )
}

internal suspend fun AppointmentEntity.toAppointmentResponse(
    scheduleDao: ScheduleDao
): AppointmentResponse {
    return AppointmentResponse(
        uuid = id,
        createdOn = createdOn,
        appointmentId = appointmentFhirId,
        orgId = orgId,
        patientFhirId = patientId,
        scheduleId = scheduleDao.getFhirIdByStartTime(scheduleId)
            ?: scheduleDao.getScheduleByStartTime(scheduleId.time)!!.id,
        slot = Slot(
            start = startTime,
            end = endTime
        ),
        status = status
    )
}

internal fun AppointmentEntity.toAppointmentResponseLocal(): AppointmentResponseLocal {
    return AppointmentResponseLocal(
        uuid = id,
        createdOn = createdOn,
        appointmentId = appointmentFhirId,
        orgId = orgId,
        patientId = patientId,
        scheduleId = scheduleId,
        slot = Slot(
            start = startTime,
            end = endTime
        ),
        status = status
    )
}

// Appointment Response from Local
internal fun AppointmentResponseLocal.toAppointmentEntity(): AppointmentEntity {
    return AppointmentEntity(
        id = uuid,
        appointmentFhirId = appointmentId,
        createdOn = createdOn,
        patientId = patientId,
        scheduleId = scheduleId,
        orgId = orgId,
        status = status,
        startTime = slot.start,
        endTime = slot.end
    )
}

internal fun PrescriptionAndMedicineRelation.toPrescriptionResponseLocal(): PrescriptionResponseLocal {
    return PrescriptionResponseLocal(
        patientId = prescriptionEntity.patientId,
        patientFhirId = prescriptionEntity.patientFhirId,
        appointmentId = prescriptionEntity.appointmentId,
        generatedOn = prescriptionEntity.prescriptionDate,
        prescriptionId = prescriptionEntity.id,
        prescription = prescriptionDirectionAndMedicineView.map { prescriptionDirectionAndMedicineView -> prescriptionDirectionAndMedicineView.toMedication() }
    )
}

internal fun PrescriptionDirectionAndMedicineView.toMedication(): Medication {
    return Medication(
        doseForm = medicationEntity.doseForm,
        duration = prescriptionDirectionsEntity.duration,
        frequency = prescriptionDirectionsEntity.frequency,
        medFhirId = medicationEntity.medFhirId,
        note = prescriptionDirectionsEntity.note,
        qtyPerDose = prescriptionDirectionsEntity.qtyPerDose,
        qtyPrescribed = prescriptionDirectionsEntity.qtyPrescribed,
        timing = prescriptionDirectionsEntity.timing
    )
}

internal fun PatientLastUpdatedEntity.toPatientLastUpdatedResponse() : PatientLastUpdatedResponse {
    return PatientLastUpdatedResponse(
        uuid = patientId,
        timestamp = lastUpdated
    )
}

internal fun PatientLastUpdatedResponse.toPatientLastUpdatedEntity() : PatientLastUpdatedEntity {
    return PatientLastUpdatedEntity(
        patientId = uuid,
        lastUpdated = timestamp
    )
}