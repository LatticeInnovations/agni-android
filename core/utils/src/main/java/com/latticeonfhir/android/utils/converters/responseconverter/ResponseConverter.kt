package com.latticeonfhir.core.utils.converters.responseconverter

import com.latticeonfhir.android.data.local.model.labtest.LabTestLocal
import com.latticeonfhir.android.data.local.model.labtest.LabTestPhotoResponseLocal
import com.latticeonfhir.android.data.local.model.prescription.MedicationLocal
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionPhotoResponseLocal
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionResponseLocal
import com.latticeonfhir.android.data.local.model.relation.Relation
import com.latticeonfhir.android.data.local.model.symdiag.SymptomsAndDiagnosisData
import com.latticeonfhir.android.data.local.model.vital.VitalLocal
import com.latticeonfhir.android.data.server.model.cvd.CVDResponse
import com.latticeonfhir.android.data.server.model.dispense.response.DispenseData
import com.latticeonfhir.android.data.server.model.dispense.response.MedicineDispenseResponse
import com.latticeonfhir.android.data.server.model.labormed.labtest.DiagnosticReport
import com.latticeonfhir.android.data.server.model.labormed.labtest.LabTestResponse
import com.latticeonfhir.android.data.server.model.labormed.medicalrecord.MedicalRecord
import com.latticeonfhir.android.data.server.model.labormed.medicalrecord.MedicalRecordResponse
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientLastUpdatedResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicationResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.Strength
import com.latticeonfhir.android.data.server.model.prescription.photo.File
import com.latticeonfhir.android.data.server.model.prescription.photo.PrescriptionPhotoResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.data.server.model.relatedperson.Relationship
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.TimeConverter.convertStringToDate
import com.latticeonfhir.android.utils.converters.TimeConverter.toPatientDate
import com.latticeonfhir.android.utils.converters.TimeConverter.toTimeInMilli
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.core.data.server.model.prescription.medication.MedicineTimeResponse
import com.latticeonfhir.core.data.server.model.scheduleandappointment.Slot
import com.latticeonfhir.core.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.core.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisItem
import com.latticeonfhir.core.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisResponse
import com.latticeonfhir.core.data.server.model.symptomsanddiagnosis.SymptomsItem
import com.latticeonfhir.core.data.server.model.vitals.VitalResponse
import com.latticeonfhir.core.database.dao.AppointmentDao
import com.latticeonfhir.core.database.dao.MedicationDao
import com.latticeonfhir.core.database.dao.PatientDao
import com.latticeonfhir.core.database.dao.PrescriptionDao
import com.latticeonfhir.core.database.dao.ScheduleDao
import com.latticeonfhir.core.database.entities.appointment.AppointmentEntity
import com.latticeonfhir.core.database.entities.cvd.CVDEntity
import com.latticeonfhir.core.database.entities.dispense.DispenseDataEntity
import com.latticeonfhir.core.database.entities.dispense.DispensePrescriptionEntity
import com.latticeonfhir.core.database.entities.dispense.MedicineDispenseListEntity
import com.latticeonfhir.core.database.entities.generic.GenericEntity
import com.latticeonfhir.core.database.entities.labtestandmedrecord.LabTestAndMedEntity
import com.latticeonfhir.core.database.entities.labtestandmedrecord.photo.LabTestAndFileEntity
import com.latticeonfhir.core.database.entities.labtestandmedrecord.photo.LabTestAndMedPhotoEntity
import com.latticeonfhir.core.database.entities.medication.MedicationEntity
import com.latticeonfhir.core.database.entities.medication.MedicationStrengthRelation
import com.latticeonfhir.core.database.entities.medication.MedicineTimingEntity
import com.latticeonfhir.core.database.entities.medication.StrengthEntity
import com.latticeonfhir.core.database.entities.patient.IdentifierEntity
import com.latticeonfhir.core.database.entities.patient.PatientAndIdentifierEntity
import com.latticeonfhir.core.database.entities.patient.PatientEntity
import com.latticeonfhir.core.database.entities.patient.PatientLastUpdatedEntity
import com.latticeonfhir.core.database.entities.patient.PermanentAddressEntity
import com.latticeonfhir.core.database.entities.prescription.PrescriptionAndMedicineRelation
import com.latticeonfhir.core.database.entities.prescription.PrescriptionDirectionsEntity
import com.latticeonfhir.core.database.entities.prescription.PrescriptionEntity
import com.latticeonfhir.core.database.entities.prescription.photo.PrescriptionAndFileEntity
import com.latticeonfhir.core.database.entities.prescription.photo.PrescriptionPhotoEntity
import com.latticeonfhir.core.database.entities.relation.RelationEntity
import com.latticeonfhir.core.database.entities.schedule.ScheduleEntity
import com.latticeonfhir.core.database.entities.symptomsanddiagnosis.DiagnosisEntity
import com.latticeonfhir.core.database.entities.symptomsanddiagnosis.SymptomAndDiagnosisEntity
import com.latticeonfhir.core.database.entities.symptomsanddiagnosis.SymptomsAndDiagnosisLocal
import com.latticeonfhir.core.database.entities.symptomsanddiagnosis.SymptomsEntity
import com.latticeonfhir.core.database.entities.vitals.VitalEntity
import com.latticeonfhir.core.database.views.PrescriptionDirectionAndMedicineView
import com.latticeonfhir.core.model.enums.PhotoDeleteEnum
import com.latticeonfhir.core.model.enums.PrescriptionType
import com.latticeonfhir.core.model.enums.RelationEnum
import com.latticeonfhir.core.model.local.appointment.AppointmentResponseLocal
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.core.utils.converters.responseconverter.RelationConverter.getInverseRelation
import java.util.Date
import java.util.UUID

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

 suspend fun Relationship.toRelationEntity(
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
    com.latticeonfhir.android.utils.converters.responsemapper.ApiResponseConverter.convert(
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

 fun <T> List<T>.toNoBracketAndNoSpaceString(): String {
    return this.toString().replace("[", "").replace("]", "").replace(" ", "")
}

 suspend fun PrescriptionResponse.toPrescriptionEntity(
    patientDao: PatientDao,
): PrescriptionEntity {
    return PrescriptionEntity(
        id = prescriptionId,
        prescriptionDate = generatedOn,
        patientId = patientDao.getPatientIdByFhirId(patientFhirId)!!,
        appointmentId = appointmentUuid,
        patientFhirId = patientFhirId,
        prescriptionFhirId = prescriptionFhirId,
        prescriptionType = PrescriptionType.FORM.type
    )
}


 suspend fun PrescriptionPhotoResponse.toPrescriptionEntity(
    patientDao: PatientDao,
): PrescriptionEntity {
    return PrescriptionEntity(
        id = prescriptionId,
        prescriptionDate = generatedOn,
        patientId = patientDao.getPatientIdByFhirId(patientFhirId)!!,
        appointmentId = appointmentUuid,
        patientFhirId = patientFhirId,
        prescriptionFhirId = prescriptionFhirId,
        prescriptionType = PrescriptionType.PHOTO.type
    )
}


 fun PrescriptionResponseLocal.toPrescriptionEntity(): PrescriptionEntity {
    return PrescriptionEntity(
        id = prescriptionId,
        prescriptionDate = generatedOn,
        patientId = patientId,
        appointmentId = appointmentId,
        patientFhirId = patientFhirId,
        prescriptionFhirId = null,
        prescriptionType = PrescriptionType.FORM.type
    )
}

 fun PrescriptionPhotoResponseLocal.toPrescriptionEntity(): PrescriptionEntity {
    return PrescriptionEntity(
        id = prescriptionId,
        prescriptionDate = generatedOn,
        patientId = patientId,
        appointmentId = appointmentId,
        patientFhirId = patientFhirId,
        prescriptionFhirId = null,
        prescriptionType = PrescriptionType.PHOTO.type
    )
}

 suspend fun PrescriptionResponse.toListOfPrescriptionDirectionsEntity(medicationDao: MedicationDao): List<PrescriptionDirectionsEntity> {
    return prescription.map { medication ->
        PrescriptionDirectionsEntity(
            id = medication.medReqUuid,
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
            medReqFhirId = medication.medReqFhirId
        )
    }
}


 fun PrescriptionPhotoResponse.toListOfPrescriptionPhotoEntity(): List<PrescriptionPhotoEntity> {
    return prescription.map { prescriptionItem ->
        PrescriptionPhotoEntity(
            id = prescriptionItem.documentUuid,
            fileName = prescriptionItem.filename,
            prescriptionId = prescriptionId,
            note = prescriptionItem.note,
            documentFhirId = prescriptionItem.documentFhirId
        )
    }
}

 fun PrescriptionResponseLocal.toListOfPrescriptionDirectionsEntity(): List<PrescriptionDirectionsEntity> {
    return prescription.map { medication ->
        PrescriptionDirectionsEntity(
            id = medication.medReqUuid,
            medFhirId = medication.medFhirId,
            qtyPerDose = medication.qtyPerDose,
            frequency = medication.frequency,
            timing = medication.timing,
            duration = medication.duration,
            qtyPrescribed = medication.qtyPrescribed,
            note = medication.note,
            prescriptionId = prescriptionId,
            medReqFhirId = medication.medReqFhirId
        )
    }
}

 fun PrescriptionPhotoResponseLocal.toListOfPrescriptionPhotoEntity(): List<PrescriptionPhotoEntity> {
    return prescription.map { prescriptionItem ->
        PrescriptionPhotoEntity(
            id = prescriptionItem.documentUuid,
            fileName = prescriptionItem.filename,
            prescriptionId = prescriptionId,
            note = prescriptionItem.note,
            documentFhirId = prescriptionItem.documentFhirId
        )
    }
}

 fun List<MedicineTimeResponse>.toListOfMedicineDirectionsEntity(): List<MedicineTimingEntity> {
    return map { medicineTimeResponse ->
        MedicineTimingEntity(
            medicalDosage = medicineTimeResponse.medInstructionVal,
            medicalDosageId = medicineTimeResponse.medInstructionCode
        )
    }
}

 fun ScheduleResponse.toScheduleEntity(): ScheduleEntity {
    return ScheduleEntity(
        id = uuid,
        scheduleFhirId = scheduleId,
        startTime = planningHorizon.start,
        endTime = planningHorizon.end,
        bookedSlots = bookedSlots!!,
        orgId = orgId
    )
}

 fun ScheduleEntity.toScheduleResponse(): ScheduleResponse {
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
suspend fun AppointmentResponse.toAppointmentEntity(
    patientDao: PatientDao,
    scheduleDao: ScheduleDao
): AppointmentEntity {
    return AppointmentEntity(
        id = uuid,
        appointmentFhirId = appointmentId,
        createdOn = createdOn,
        patientId = patientDao.getPatientIdByFhirId(patientFhirId)!!,
        scheduleId = scheduleDao.getScheduleStartTimeByFhirId(scheduleId)!!,
        orgId = orgId,
        status = status,
        startTime = slot.start,
        endTime = slot.end,
        appointmentType = appointmentType,
        inProgressTime = inProgressTime
    )
}

 suspend fun AppointmentEntity.toAppointmentResponse(
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
        status = status,
        appointmentType = appointmentType,
        inProgressTime = inProgressTime
    )
}

 fun AppointmentEntity.toAppointmentResponseLocal(): AppointmentResponseLocal {
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
        status = status,
        appointmentType = appointmentType,
        inProgressTime = inProgressTime
    )
}

// Appointment Response from Local
 fun AppointmentResponseLocal.toAppointmentEntity(): AppointmentEntity {
    return AppointmentEntity(
        id = uuid,
        appointmentFhirId = appointmentId,
        createdOn = createdOn,
        patientId = patientId,
        scheduleId = scheduleId,
        orgId = orgId,
        status = status,
        startTime = slot.start,
        endTime = slot.end,
        appointmentType = appointmentType,
        inProgressTime = inProgressTime
    )
}

 fun PrescriptionAndMedicineRelation.toPrescriptionResponseLocal(): PrescriptionResponseLocal {
    return PrescriptionResponseLocal(
        patientId = prescriptionEntity.patientId,
        patientFhirId = prescriptionEntity.patientFhirId,
        appointmentId = prescriptionEntity.appointmentId,
        generatedOn = prescriptionEntity.prescriptionDate,
        prescriptionId = prescriptionEntity.id,
        prescription = prescriptionDirectionAndMedicineView.map { prescriptionDirectionAndMedicineView -> prescriptionDirectionAndMedicineView.toMedicationLocal() }
    )
}

 fun PrescriptionDirectionAndMedicineView.toMedicationLocal(): MedicationLocal {
    return MedicationLocal(
        doseForm = medicationEntity.doseForm,
        duration = prescriptionDirectionsEntity.duration,
        frequency = prescriptionDirectionsEntity.frequency,
        medFhirId = medicationEntity.medFhirId,
        note = prescriptionDirectionsEntity.note,
        qtyPerDose = prescriptionDirectionsEntity.qtyPerDose,
        qtyPrescribed = prescriptionDirectionsEntity.qtyPrescribed,
        timing = prescriptionDirectionsEntity.timing,
        medReqFhirId = prescriptionDirectionsEntity.medReqFhirId,
        medReqUuid = prescriptionDirectionsEntity.id,
        medName = medicationEntity.medName,
        medUnit = medicationEntity.medUnit
    )
}

 fun PatientLastUpdatedEntity.toPatientLastUpdatedResponse(): PatientLastUpdatedResponse {
    return PatientLastUpdatedResponse(
        uuid = patientId,
        timestamp = lastUpdated
    )
}

 fun PatientLastUpdatedResponse.toPatientLastUpdatedEntity(): PatientLastUpdatedEntity {
    return PatientLastUpdatedEntity(
        patientId = uuid,
        lastUpdated = timestamp
    )
}

 suspend fun PrescriptionAndFileEntity.toPrescriptionPhotoResponse(
    appointmentDao: AppointmentDao
): PrescriptionPhotoResponse {
    return PrescriptionPhotoResponse(
        patientFhirId = prescriptionEntity.patientFhirId ?: prescriptionEntity.patientId,
        appointmentId = appointmentDao.getFhirIdByAppointmentId(prescriptionEntity.appointmentId)
            ?: prescriptionEntity.appointmentId,
        generatedOn = prescriptionEntity.prescriptionDate,
        prescriptionId = prescriptionEntity.id,
        prescription = prescriptionPhotoEntity.map { prescriptionPhotoEntity ->
            File(
                documentUuid = prescriptionPhotoEntity.id,
                documentFhirId = prescriptionPhotoEntity.documentFhirId,
                filename = prescriptionPhotoEntity.fileName,
                note = prescriptionPhotoEntity.note ?: ""
            )
        },
        appointmentUuid = prescriptionEntity.appointmentId,
        prescriptionFhirId = prescriptionEntity.prescriptionFhirId,
        status = null
    )
}


 fun PrescriptionAndFileEntity.toPrescriptionPhotoResponseLocal(): PrescriptionPhotoResponseLocal {
    return PrescriptionPhotoResponseLocal(
        patientId = prescriptionEntity.patientId,
        patientFhirId = prescriptionEntity.patientFhirId,
        appointmentId = prescriptionEntity.appointmentId,
        generatedOn = prescriptionEntity.prescriptionDate,
        prescriptionId = prescriptionEntity.id,
        prescription = prescriptionPhotoEntity.map { it.toFile() },
        prescriptionFhirId = prescriptionEntity.prescriptionFhirId
    )
}

private fun PrescriptionPhotoEntity.toFile(): File {
    return File(
        documentUuid = id,
        documentFhirId = documentFhirId,
        filename = fileName,
        note = note ?: ""
    )
}

 fun PrescriptionAndFileEntity.toFilesList(): List<File> {
    return prescriptionPhotoEntity.map {
        File(
            documentUuid = it.id,
            documentFhirId = it.documentFhirId,
            filename = it.fileName,
            note = it.note ?: ""
        )
    }
}

 fun CVDResponse.toCVDEntity(): CVDEntity {
    return CVDEntity(
        cvdFhirId = cvdFhirId,
        cvdUuid = cvdUuid,
        appointmentId = appointmentId,
        patientId = patientId,
        bmi = bmi,
        bpDiastolic = bpDiastolic,
        bpSystolic = bpSystolic,
        cholesterol = cholesterol,
        cholesterolUnit = cholesterolUnit,
        diabetic = diabetic,
        heightCm = heightCm,
        createdOn = createdOn,
        heightInch = heightInch,
        heightFt = heightFt,
        risk = risk,
        practitionerName = practitionerName,
        smoker = smoker,
        weight = weight
    )
}


 suspend fun CVDResponse.toCVDEntity(
    patientDao: PatientDao,
    appointmentDao: AppointmentDao
): CVDEntity {
    return CVDEntity(
        cvdFhirId = cvdFhirId,
        cvdUuid = cvdUuid,
        appointmentId = appointmentDao.getAppointmentIdByFhirId(appointmentId),
        patientId = patientDao.getPatientIdByFhirId(patientId)!!,
        bmi = bmi,
        bpDiastolic = bpDiastolic,
        bpSystolic = bpSystolic,
        cholesterol = cholesterol,
        cholesterolUnit = cholesterolUnit,
        diabetic = diabetic,
        heightCm = heightCm,
        createdOn = createdOn,
        heightInch = heightInch,
        heightFt = heightFt,
        risk = risk,
        practitionerName = practitionerName,
        smoker = smoker,
        weight = weight
    )
}

 fun CVDEntity.toCVDResponse(): CVDResponse {
    return CVDResponse(
        cvdFhirId = cvdFhirId,
        cvdUuid = cvdUuid,
        appointmentId = appointmentId,
        patientId = patientId,
        bmi = bmi,
        bpDiastolic = bpDiastolic,
        bpSystolic = bpSystolic,
        cholesterol = cholesterol,
        cholesterolUnit = cholesterolUnit,
        diabetic = diabetic,
        heightCm = heightCm,
        createdOn = createdOn,
        heightInch = heightInch,
        heightFt = heightFt,
        risk = risk,
        practitionerName = practitionerName,
        smoker = smoker,
        weight = weight
    )
}

 fun VitalEntity.toVitalLocal(): VitalLocal {
    return VitalLocal(
        vitalUuid = vitalUuid,
        fhirId = fhirId,
        patientId = patientId,
        appointmentId = appointmentId,
        bloodGlucose = bloodGlucose,
        bloodGlucoseType = bloodGlucoseType,
        bloodGlucoseUnit = bloodGlucoseUnit,
        bpDiastolic = bpDiastolic,
        bpSystolic = bpSystolic,
        createdOn = createdOn,
        eyeTestType = eyeTestType,
        heartRate = heartRate,
        heightCm = heightCm,
        heightFt = heightFt,
        heightInch = heightInch,
        leftEye = leftEye,
        respRate = respRate,
        rightEye = rightEye,
        spo2 = spo2,
        temp = temp,
        tempUnit = tempUnit,
        weight = weight,
        practitionerName = practitionerName,
        cholesterol = cholesterol,
        cholesterolUnit = cholesterolUnit
    )
}

 fun VitalLocal.toVitalEntity(): VitalEntity {
    return VitalEntity(
        vitalUuid = vitalUuid,
        fhirId = fhirId,
        patientId = patientId,
        appointmentId = appointmentId,
        bloodGlucose = bloodGlucose,
        bloodGlucoseType = bloodGlucoseType,
        bloodGlucoseUnit = bloodGlucoseUnit,
        bpDiastolic = bpDiastolic,
        bpSystolic = bpSystolic,
        createdOn = createdOn,
        eyeTestType = eyeTestType,
        heartRate = heartRate,
        heightCm = heightCm,
        heightFt = heightFt,
        heightInch = heightInch,
        leftEye = leftEye,
        respRate = respRate,
        rightEye = rightEye,
        spo2 = spo2,
        temp = temp,
        tempUnit = tempUnit,
        weight = weight,
        practitionerName = practitionerName,
        cholesterol = cholesterol,
        cholesterolUnit = cholesterolUnit
    )
}

 suspend fun VitalResponse.toVitalEntity(
    patientDao: PatientDao,
    appointmentDao: AppointmentDao
): VitalEntity {
    return VitalEntity(
        vitalUuid = vitalUuid,
        fhirId = vitalFhirId,
        patientId = patientDao.getPatientIdByFhirId(patientId!!),
        appointmentId = appointmentDao.getAppointmentIdByFhirId(appointmentId),
        bloodGlucose = bloodGlucose,
        bloodGlucoseType = bloodGlucoseType,
        bloodGlucoseUnit = bloodGlucoseUnit,
        bpDiastolic = bpDiastolic,
        bpSystolic = bpSystolic,
        createdOn = createdOn.convertStringToDate(),
        eyeTestType = eyeTestType,
        heartRate = heartRate,
        heightCm = heightCm,
        heightFt = heightFt,
        heightInch = heightInch,
        leftEye = leftEye?.trim()?.toInt(),
        respRate = respRate,
        rightEye = rightEye?.trim()?.toInt(),
        spo2 = spo2,
        temp = temp,
        tempUnit = tempUnit,
        weight = weight,
        practitionerName = practitionerName,
        cholesterol = cholesterol,
        cholesterolUnit = cholesterolUnit
    )
}


 fun SymptomsItem.toSymptomsEntity(): SymptomsEntity {
    return SymptomsEntity(id = UUID.randomUUID().toString(), code = code, display = display,
        type = type,
        gender = gender
    )
}

 fun SymptomsAndDiagnosisItem.toDiagnosisEntity(): DiagnosisEntity {
    return DiagnosisEntity(id = UUID.randomUUID().toString(), code = code, display = display)
}

 fun SymptomsEntity.toSymptoms(): SymptomsItem {
    return SymptomsItem(code = code, display = display, type = type, gender = gender)
}

 fun DiagnosisEntity.toDiagnosis(): SymptomsAndDiagnosisItem {
    return SymptomsAndDiagnosisItem(code = code, display = display)
}

 fun SymptomsAndDiagnosisLocal.toSymptomsAndDiagnosisEntity(): SymptomAndDiagnosisEntity {
    return SymptomAndDiagnosisEntity(
        symDiagUuid = symDiagUuid,
        appointmentId = appointmentId, fhirId = symDiagFhirId,
        createdOn = createdOn,
        diagnosis = diagnosis,
        symptoms = symptoms,
        practitionerName = practitionerName!!,
        patientId = patientId!!
    )
}

 fun SymptomAndDiagnosisEntity.toSymptomsAndDiagnosisLocal(): SymptomsAndDiagnosisLocal {
    return SymptomsAndDiagnosisLocal(
        symDiagUuid = symDiagUuid,
        appointmentId = appointmentId, symDiagFhirId = fhirId,
        createdOn = createdOn,
        diagnosis = diagnosis,
        symptoms = symptoms,
        practitionerName = practitionerName,
        patientId = patientId
    )
}


 suspend fun SymptomsAndDiagnosisResponse.toSymptomsAndDiagnosisEntity(
    studentDao: PatientDao,
    appointmentDao: AppointmentDao
): SymptomAndDiagnosisEntity {
    return SymptomAndDiagnosisEntity(
        symDiagUuid = symDiagUuid,
        appointmentId = appointmentDao.getAppointmentIdByFhirId(appointmentId),
        fhirId = symDiagFhirId,
        createdOn = createdOn.convertStringToDate(),
        diagnosis = diagnosis,
        symptoms = symptoms,
        practitionerName = practitionerName,
        patientId = studentDao.getPatientIdByFhirId(patientId)!!
    )
}
 fun SymptomsAndDiagnosisLocal.toSymDiagData(): SymptomsAndDiagnosisData {
    return SymptomsAndDiagnosisData(
        symDiagUuid = symDiagUuid,
        appointmentId = appointmentId,
        createdOn = createdOn,
        diagnosis = diagnosis.map { it.code },
        symptoms = symptoms.map { it.code },
        patientId = patientId
    )
}

 fun LabTestAndFileEntity.toFilesList(): List<File> {
    return labTestAndMedPhotoEntity.map {
        File(
            filename = it.fileName, note = it.note ?: "", documentFhirId = "", documentUuid = ""
        )
    }
}


 suspend fun LabTestAndFileEntity.toLabTestPhotoResponseLocal(
    appointmentDao: AppointmentDao
): LabTestPhotoResponseLocal {
    return LabTestPhotoResponseLocal(
        labTestId = labTestAndMedEntity.id,
        appointmentId = appointmentDao.getFhirIdByAppointmentId(labTestAndMedEntity.appointmentId)
            ?: labTestAndMedEntity.appointmentId,
        patientId = labTestAndMedEntity.patientId,
        labTestFhirId = labTestAndMedEntity.labTestFhirId,
        createdOn = labTestAndMedEntity.createdOn,
        labTests = labTestAndMedPhotoEntity.map { labTestAndMedPhotoEntity ->
            File(
                labTestAndMedPhotoEntity.id,
                labTestAndMedPhotoEntity.fhirId,
                labTestAndMedPhotoEntity.fileName,
                labTestAndMedPhotoEntity.note ?: ""
            )
        })
}

 suspend fun DiagnosticReport.toLabTestPhotoResponseLocal(
    labTestResponse: LabTestResponse,
    appointmentDao: AppointmentDao,
    studentDao: PatientDao
): LabTestLocal {
    return LabTestLocal(
        labTestId = diagnosticUuid,
        appointmentId = appointmentDao.getAppointmentIdByFhirId(labTestResponse.appointmentId),
        patientId = studentDao.getPatientIdByFhirId(labTestResponse.patientId)!!,
        labTestFhirId = diagnosticReportFhirId,
        createdOn = createdOn.convertStringToDate()
    )
}

 suspend fun MedicalRecord.toMedRecordPhotoResponseLocal(
    medicalRecordResponse: MedicalRecordResponse,
    appointmentDao: AppointmentDao,
    studentDao: PatientDao
): LabTestLocal {
    return LabTestLocal(
        labTestId = medicalReportUuid,
        appointmentId = appointmentDao.getAppointmentIdByFhirId(medicalRecordResponse.appointmentId),
        patientId = studentDao.getPatientIdByFhirId(medicalRecordResponse.patientId)!!,
        labTestFhirId = medicalRecordFhirId,
        createdOn = createdOn.convertStringToDate()
    )
}

 fun LabTestResponse.toListOfLabTestPhotoEntity(
): List<LabTestAndMedPhotoEntity> {
    val list: MutableList<LabTestAndMedPhotoEntity> = mutableListOf()
    val fileNameSet: MutableSet<String> = mutableSetOf()

    diagnosticReport.filter { it.status == PhotoDeleteEnum.SAVED.value }.map { diagnosticReport ->
        diagnosticReport.documents.map {
            if (!fileNameSet.contains(it.filename)) {

                list.add(
                    LabTestAndMedPhotoEntity(
                        id = it.labDocumentUuid,
                        labTestId = diagnosticReport.diagnosticUuid,
                        fileName = it.filename, note = it.note, fhirId = it.labDocumentfhirId
                    )
                )
                fileNameSet.add(it.filename)

            }
        }
    }
    return list
}

 fun MedicalRecordResponse.toListOfLabTestAndMedPhotoEntity(
): List<LabTestAndMedPhotoEntity> {
    val list: MutableList<LabTestAndMedPhotoEntity> = mutableListOf()
    val fileNameSet: MutableSet<String> = mutableSetOf() // Set to track unique file names

    medicalRecord.filter { it.status == PhotoDeleteEnum.SAVED.value }.map { diagnosticReport ->
        diagnosticReport.documents.map {
            if (!fileNameSet.contains(it.filename)) { // Check if fileName is not already in the set
                list.add(
                    LabTestAndMedPhotoEntity(
                        id = it.medicalDocumentUuid,
                        labTestId = diagnosticReport.medicalReportUuid,
                        fileName = it.filename, note = it.note, fhirId = it.medicalDocumentfhirId
                    )
                )
                fileNameSet.add(it.filename) // Add the fileName to the set
            }
        }
    }
    return list
}

 fun LabTestPhotoResponseLocal.toLabTestAndMedEntity(type: String): LabTestAndMedEntity {
    return LabTestAndMedEntity(
        id = labTestId,
        appointmentId = appointmentId,
        labTestFhirId = labTestFhirId,
        patientId = patientId,
        createdOn = createdOn,
        type = type

    )
}
 fun LabTestPhotoResponseLocal.toListOfLabTestPhotoEntity(): List<LabTestAndMedPhotoEntity> {
    return labTests.map { labTestItem ->
        LabTestAndMedPhotoEntity(
            id = labTestItem.documentUuid,
            fileName = labTestItem.filename,
            labTestId = labTestId, note = labTestItem.note, fhirId = labTestItem.documentFhirId
        )
    }
}
 fun LabTestAndMedEntity.toLabTestLocal(): LabTestLocal {
    return LabTestLocal(
        labTestId = id,
        appointmentId = appointmentId,
        patientId = patientId,
        labTestFhirId = labTestFhirId,
        createdOn = createdOn

    )
}
 fun LabTestLocal.toLabTestEntity(type: String): LabTestAndMedEntity {
    return LabTestAndMedEntity(
        id = labTestId,
        appointmentId = appointmentId,
        patientId = patientId,
        labTestFhirId = labTestFhirId,
        createdOn = createdOn,
        type = type

    )
}

 fun List<MedicationResponse>.toListOfMedicationEntity(): List<MedicationEntity> {
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
            medNumeratorVal = medication.medNumeratorVal,
            isOTC = medication.isOTC
        )
    }
}

 fun MedicationResponse.toListOfStrengthEntity(): List<StrengthEntity> {
    return this.strength.map { strength ->
        StrengthEntity(
            id = UUIDBuilder.generateUUID(),
            medFhirId = this.medFhirId,
            medName = strength.medName,
            unitMeasureValue = strength.unitMeasureValue,
            medMeasureCode = strength.medMeasureCode
        )
    }
}

 fun MedicationStrengthRelation.toMedicationResponse(): MedicationResponse {
    return MedicationResponse(
        medFhirId = this.medicationEntity.medFhirId,
        medCode = this.medicationEntity.medCodeName,
        medName = this.medicationEntity.medName,
        doseForm = this.medicationEntity.doseForm,
        doseFormCode = this.medicationEntity.doseFormCode,
        activeIngredient = this.medicationEntity.activeIngredient,
        activeIngredientCode = this.medicationEntity.activeIngredientCode,
        medUnit = this.medicationEntity.medUnit,
        medNumeratorVal = this.medicationEntity.medNumeratorVal,
        isOTC = this.medicationEntity.isOTC,
        strength = this.strength.map {
            Strength(
                medMeasureCode = it.medMeasureCode,
                medName = it.medName,
                unitMeasureValue = it.unitMeasureValue
            )
        }
    )
}

 suspend fun MedicineDispenseResponse.toDispensePrescriptionEntity(
    patientDao: PatientDao,
    prescriptionDao: PrescriptionDao
): DispensePrescriptionEntity {
    return DispensePrescriptionEntity(
        patientId = patientDao.getPatientIdByFhirId(this.patientId)!!,
        prescriptionId = prescriptionDao.getPrescriptionIdByFhirId(this.prescriptionFhirId),
        status = this.status
    )
}

 suspend fun DispenseData.toListOfDispenseDataEntity(
    patientDao: PatientDao,
    prescriptionDao: PrescriptionDao,
    appointmentDao: AppointmentDao,
    prescriptionFhirId: String?
): DispenseDataEntity {
    return DispenseDataEntity(
        dispenseId = this.dispenseId,
        dispenseFhirId = this.dispenseFhirId,
        generatedOn = this.generatedOn,
        note = this.note,
        patientId = patientDao.getPatientIdByFhirId(this.patientId)!!,
        prescriptionId = if (prescriptionFhirId.isNullOrBlank()) null else prescriptionDao.getPrescriptionIdByFhirId(
            prescriptionFhirId
        ),
        appointmentId = if (this.appointmentId.isNullOrBlank()) null else appointmentDao.getAppointmentIdByFhirId(
            this.appointmentId!!
        )
    )
}

 suspend fun DispenseData.toListOfMedicineDispenseListEntity(
    patientDao: PatientDao
): List<MedicineDispenseListEntity> {
    return this.medicineDispensedList.map {
        MedicineDispenseListEntity(
            medDispenseUuid = it.medDispenseUuid,
            medDispenseFhirId = it.medDispenseFhirId,
            dispenseId = this.dispenseId,
            patientId = patientDao.getPatientIdByFhirId(it.patientId)!!,
            category = it.category,
            qtyDispensed = it.qtyDispensed,
            qtyPrescribed = it.prescriptionData?.qtyPrescribed ?: it.qtyDispensed,
            date = it.date,
            isModified = it.isModified,
            modificationType = it.modificationType,
            medNote = it.medNote,
            dispensedMedFhirId = it.dispensedMedication.medFhirId,
            prescribedMedFhirId = it.prescriptionData?.medFhirId ?: it.medFhirId,
            prescribedMedReqId = it.prescriptionData?.medReqFhirId
        )
    }
}
