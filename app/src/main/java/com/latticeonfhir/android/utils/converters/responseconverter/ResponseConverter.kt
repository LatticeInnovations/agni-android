package com.latticeonfhir.android.utils.converters.responseconverter

import com.latticeonfhir.android.data.local.enums.PrescriptionType
import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.model.prescription.MedicationLocal
import com.latticeonfhir.android.data.local.model.labtest.LabTestLocal
import com.latticeonfhir.android.data.local.model.labtest.LabTestPhotoResponseLocal
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionPhotoResponseLocal
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionResponseLocal
import com.latticeonfhir.android.data.local.model.relation.Relation
import com.latticeonfhir.android.data.local.model.symdiag.SymptomsAndDiagnosisData
import com.latticeonfhir.android.data.local.model.vital.VitalLocal
import com.latticeonfhir.android.data.local.roomdb.dao.AppointmentDao
import com.latticeonfhir.android.data.local.roomdb.dao.MedicationDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.dao.ScheduleDao
import com.latticeonfhir.android.data.local.roomdb.entities.appointment.AppointmentEntity
import com.latticeonfhir.android.data.local.roomdb.entities.cvd.CVDEntity
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.data.local.roomdb.entities.labtestandmedrecord.LabTestAndMedEntity
import com.latticeonfhir.android.data.local.roomdb.entities.labtestandmedrecord.photo.LabTestAndFileEntity
import com.latticeonfhir.android.data.local.roomdb.entities.labtestandmedrecord.photo.LabTestAndMedPhotoEntity
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
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.photo.PrescriptionAndFileEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.photo.PrescriptionPhotoEntity
import com.latticeonfhir.android.data.local.roomdb.entities.relation.RelationEntity
import com.latticeonfhir.android.data.local.roomdb.entities.schedule.ScheduleEntity
import com.latticeonfhir.android.data.local.roomdb.entities.symptomsanddiagnosis.DiagnosisEntity
import com.latticeonfhir.android.data.local.roomdb.entities.symptomsanddiagnosis.SymptomAndDiagnosisEntity
import com.latticeonfhir.android.data.local.roomdb.entities.symptomsanddiagnosis.SymptomsAndDiagnosisLocal
import com.latticeonfhir.android.data.local.roomdb.entities.symptomsanddiagnosis.SymptomsEntity
import com.latticeonfhir.android.data.local.roomdb.entities.vitals.VitalEntity
import com.latticeonfhir.android.data.local.roomdb.views.PrescriptionDirectionAndMedicineView
import com.latticeonfhir.android.data.server.api.PatientApiService
import com.latticeonfhir.android.data.server.constants.EndPoints.PATIENT
import com.latticeonfhir.android.data.server.constants.QueryParameters
import com.latticeonfhir.android.data.server.model.cvd.CVDResponse
import com.latticeonfhir.android.data.server.model.labormed.labtest.DiagnosticReport
import com.latticeonfhir.android.data.server.model.labormed.labtest.LabTestPhotoResponse
import com.latticeonfhir.android.data.server.model.labormed.labtest.LabTestResponse
import com.latticeonfhir.android.data.server.model.labormed.medicalrecord.MedicalRecord
import com.latticeonfhir.android.data.server.model.labormed.medicalrecord.MedicalRecordResponse
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientLastUpdatedResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicationResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicineTimeResponse
import com.latticeonfhir.android.data.server.model.prescription.photo.File
import com.latticeonfhir.android.data.server.model.prescription.photo.PrescriptionPhotoResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.data.server.model.relatedperson.Relationship
import com.latticeonfhir.android.data.server.model.scheduleandappointment.Slot
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.android.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisItem
import com.latticeonfhir.android.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisResponse
import com.latticeonfhir.android.data.server.model.symptomsanddiagnosis.SymptomsItem
import com.latticeonfhir.android.data.server.model.vitals.VitalResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.RelationConverter.getInverseRelation
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.convertStringToDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiResponseConverter
import timber.log.Timber
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
        prescriptionFhirId = prescriptionFhirId,
        prescriptionType = PrescriptionType.FORM.type
    )
}


internal suspend fun PrescriptionPhotoResponse.toPrescriptionEntity(
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


internal fun PrescriptionResponseLocal.toPrescriptionEntity(): PrescriptionEntity {
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

internal fun PrescriptionPhotoResponseLocal.toPrescriptionEntity(): PrescriptionEntity {
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

internal suspend fun PrescriptionResponse.toListOfPrescriptionDirectionsEntity(medicationDao: MedicationDao): List<PrescriptionDirectionsEntity> {
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


internal fun PrescriptionPhotoResponse.toListOfPrescriptionPhotoEntity(): List<PrescriptionPhotoEntity> {
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

internal fun PrescriptionResponseLocal.toListOfPrescriptionDirectionsEntity(): List<PrescriptionDirectionsEntity> {
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

internal fun PrescriptionPhotoResponseLocal.toListOfPrescriptionPhotoEntity(): List<PrescriptionPhotoEntity> {
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
    Timber.d("manseeyy patient $patientFhirId")
    Timber.d("manseeyy appointment $appointmentId")
    Timber.d("manseeyy $appointmentId")
    return AppointmentEntity(
        id = uuid,
        appointmentFhirId = appointmentId,
        createdOn = createdOn,
        patientId = patientDao.getPatientIdByFhirId(patientFhirId)!!,
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
        prescription = prescriptionDirectionAndMedicineView.map { prescriptionDirectionAndMedicineView -> prescriptionDirectionAndMedicineView.toMedicationLocal() }
    )
}

internal fun PrescriptionDirectionAndMedicineView.toMedicationLocal(): MedicationLocal {
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

internal fun PatientLastUpdatedEntity.toPatientLastUpdatedResponse(): PatientLastUpdatedResponse {
    return PatientLastUpdatedResponse(
        uuid = patientId,
        timestamp = lastUpdated
    )
}

internal fun PatientLastUpdatedResponse.toPatientLastUpdatedEntity(): PatientLastUpdatedEntity {
    return PatientLastUpdatedEntity(
        patientId = uuid,
        lastUpdated = timestamp
    )
}

internal suspend fun PrescriptionAndFileEntity.toPrescriptionPhotoResponse(
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
        prescriptionFhirId = prescriptionEntity.prescriptionFhirId
    )
}


internal fun PrescriptionAndFileEntity.toPrescriptionPhotoResponseLocal(): PrescriptionPhotoResponseLocal {
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

internal fun PrescriptionAndFileEntity.toFilesList(): List<File> {
    return prescriptionPhotoEntity.map {
        File(
            documentUuid = it.id,
            documentFhirId = it.documentFhirId,
            filename = it.fileName,
            note = it.note ?: ""
        )
    }
}

internal fun CVDResponse.toCVDEntity(): CVDEntity {
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


internal suspend fun CVDResponse.toCVDEntity(
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

internal fun CVDEntity.toCVDResponse(): CVDResponse {
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

internal fun VitalEntity.toVitalLocal(): VitalLocal {
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

internal fun VitalLocal.toVitalEntity(): VitalEntity {
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

internal suspend fun VitalResponse.toVitalEntity(
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


internal fun SymptomsItem.toSymptomsEntity(): SymptomsEntity {
    return SymptomsEntity(id = UUID.randomUUID().toString(), code = code, display = display,
        type = type,
        gender = gender
    )
}

internal fun SymptomsAndDiagnosisItem.toDiagnosisEntity(): DiagnosisEntity {
    return DiagnosisEntity(id = UUID.randomUUID().toString(), code = code, display = display)
}

internal fun SymptomsEntity.toSymptoms(): SymptomsItem {
    return SymptomsItem(code = code, display = display, type = type, gender = gender)
}

internal fun DiagnosisEntity.toDiagnosis(): SymptomsAndDiagnosisItem {
    return SymptomsAndDiagnosisItem(code = code, display = display)
}

internal fun SymptomsAndDiagnosisLocal.toSymptomsAndDiagnosisEntity(): SymptomAndDiagnosisEntity {
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

internal fun SymptomAndDiagnosisEntity.toSymptomsAndDiagnosisLocal(): SymptomsAndDiagnosisLocal {
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


internal suspend fun SymptomsAndDiagnosisResponse.toSymptomsAndDiagnosisEntity(
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
internal fun SymptomsAndDiagnosisLocal.toSymDiagData(): SymptomsAndDiagnosisData {
    return SymptomsAndDiagnosisData(
        symDiagUuid = symDiagUuid,
        appointmentId = appointmentId,
        createdOn = createdOn,
        diagnosis = diagnosis.map { it.code },
        symptoms = symptoms.map { it.code },
        patientId = patientId
    )
}

internal fun LabTestAndFileEntity.toFilesList(): List<File> {
    return labTestAndMedPhotoEntity.map {
        File(
            filename = it.fileName, note = it.note ?: ""
        )
    }
}


internal suspend fun LabTestAndFileEntity.toLabTestPhotoResponse(
    appointmentDao: AppointmentDao
): LabTestPhotoResponse {
    return LabTestPhotoResponse(
        labTestId = labTestAndMedEntity.id,
        appointmentId = appointmentDao.getFhirIdByAppointmentId(labTestAndMedEntity.appointmentId)
            ?: labTestAndMedEntity.appointmentId,
        patientId = labTestAndMedEntity.patientId,
        labTestFhirId = labTestAndMedEntity.labTestFhirId,
        createdOn = labTestAndMedEntity.createdOn,
        labTests = labTestAndMedPhotoEntity.map { prescriptionPhotoEntity ->
            File(prescriptionPhotoEntity.fileName, prescriptionPhotoEntity.note ?: "")
        })
}

internal suspend fun LabTestAndFileEntity.toLabTestPhotoResponseLocal(
    appointmentDao: AppointmentDao
): LabTestPhotoResponseLocal {
    return LabTestPhotoResponseLocal(
        labTestId = labTestAndMedEntity.id,
        appointmentId = appointmentDao.getFhirIdByAppointmentId(labTestAndMedEntity.appointmentId)
            ?: labTestAndMedEntity.appointmentId,
        patientId = labTestAndMedEntity.patientId,
        labTestFhirId = labTestAndMedEntity.labTestFhirId,
        createdOn = labTestAndMedEntity.createdOn,
        labTests = labTestAndMedPhotoEntity.map { prescriptionPhotoEntity ->
            File(prescriptionPhotoEntity.fileName, prescriptionPhotoEntity.note ?: "")
        })
}

internal suspend fun DiagnosticReport.toLabTestPhotoResponseLocal(
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

internal suspend fun MedicalRecord.toMedRecordPhotoResponseLocal(
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

internal fun LabTestResponse.toListOfLabTestPhotoEntity(
): List<LabTestAndMedPhotoEntity> {
    val list: MutableList<LabTestAndMedPhotoEntity> = mutableListOf()
    val fileNameSet: MutableSet<String> = mutableSetOf()

    diagnosticReport.map { diagnosticReport ->
        diagnosticReport.documents.map {
            if (!fileNameSet.contains(it.filename)) {

                list.add(
                    LabTestAndMedPhotoEntity(
                        id = UUID.randomUUID().toString(),
                        labTestId = diagnosticReport.diagnosticUuid,
                        fileName = it.filename,
                        note = it.note
                    )
                )
                fileNameSet.add(it.filename)

            }
        }
    }
    return list
}

internal fun MedicalRecordResponse.toListOfLabTestAndMedPhotoEntity(
): List<LabTestAndMedPhotoEntity> {
    val list: MutableList<LabTestAndMedPhotoEntity> = mutableListOf()
    val fileNameSet: MutableSet<String> = mutableSetOf() // Set to track unique file names

    medicalRecord.map { diagnosticReport ->
        diagnosticReport.documents.map {
            if (!fileNameSet.contains(it.filename)) { // Check if fileName is not already in the set
                list.add(
                    LabTestAndMedPhotoEntity(
                        id = UUID.randomUUID().toString(),
                        labTestId = diagnosticReport.medicalReportUuid,
                        fileName = it.filename,
                        note = it.note
                    )
                )
                fileNameSet.add(it.filename) // Add the fileName to the set
            }
        }
    }
    return list
}

internal fun LabTestPhotoResponseLocal.toLabTestAndMedEntity(type: String): LabTestAndMedEntity {
    return LabTestAndMedEntity(
        id = labTestId,
        appointmentId = appointmentId,
        labTestFhirId = labTestFhirId,
        patientId = patientId,
        createdOn = createdOn,
        type = type

    )
}
internal fun LabTestPhotoResponseLocal.toListOfLabTestPhotoEntity(): List<LabTestAndMedPhotoEntity> {
    return labTests.map { labTestItem ->
        LabTestAndMedPhotoEntity(
            id = labTestItem.filename + labTestId,
            fileName = labTestItem.filename,
            labTestId = labTestId,
            note = labTestItem.note
        )
    }
}
internal fun LabTestAndMedEntity.toLabTestLocal(): LabTestLocal {
    return LabTestLocal(
        labTestId = id,
        appointmentId = appointmentId,
        patientId = patientId,
        labTestFhirId = labTestFhirId,
        createdOn = createdOn

    )
}
internal fun LabTestLocal.toLabTestEntity(type: String): LabTestAndMedEntity {
    return LabTestAndMedEntity(
        id = labTestId,
        appointmentId = appointmentId,
        patientId = patientId,
        labTestFhirId = labTestFhirId,
        createdOn = createdOn,
        type = type

    )
}