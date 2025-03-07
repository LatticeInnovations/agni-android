package com.latticeonfhir.android.data.server.repository.sync

import com.google.gson.internal.LinkedTreeMap
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.PhotoUploadTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.model.symdiag.SymptomsAndDiagnosisData
import com.latticeonfhir.android.data.local.model.vital.VitalLocal
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.roomdb.dao.AppointmentDao
import com.latticeonfhir.android.data.local.roomdb.dao.CVDDao
import com.latticeonfhir.android.data.local.roomdb.dao.DispenseDao
import com.latticeonfhir.android.data.local.roomdb.dao.FileUploadDao
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.dao.LabTestAndMedDao
import com.latticeonfhir.android.data.local.roomdb.dao.MedicationDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientLastUpdatedDao
import com.latticeonfhir.android.data.local.roomdb.dao.PrescriptionDao
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.dao.ScheduleDao
import com.latticeonfhir.android.data.local.roomdb.dao.SymptomsAndDiagnosisDao
import com.latticeonfhir.android.data.local.roomdb.dao.VitalDao
import com.latticeonfhir.android.data.local.roomdb.dao.vaccincation.ImmunizationDao
import com.latticeonfhir.android.data.local.roomdb.dao.vaccincation.ImmunizationRecommendationDao
import com.latticeonfhir.android.data.local.roomdb.dao.vaccincation.ManufacturerDao
import com.latticeonfhir.android.data.server.api.CVDApiService
import com.latticeonfhir.android.data.server.api.DispenseApiService
import com.latticeonfhir.android.data.server.api.LabTestAndMedRecordService
import com.latticeonfhir.android.data.server.api.PatientApiService
import com.latticeonfhir.android.data.server.api.PrescriptionApiService
import com.latticeonfhir.android.data.server.api.ScheduleAndAppointmentApiService
import com.latticeonfhir.android.data.server.api.SymptomsAndDiagnosisService
import com.latticeonfhir.android.data.server.api.VaccinationApiService
import com.latticeonfhir.android.data.server.api.VitalApiService
import com.latticeonfhir.android.data.server.constants.ConstantValues.COUNT_VALUE
import com.latticeonfhir.android.data.server.constants.ConstantValues.DEFAULT_MAX_COUNT_VALUE
import com.latticeonfhir.android.data.server.constants.EndPoints
import com.latticeonfhir.android.data.server.constants.EndPoints.MEDICATION_DISPENSE
import com.latticeonfhir.android.data.server.constants.EndPoints.MEDICATION_REQUEST
import com.latticeonfhir.android.data.server.constants.EndPoints.PATIENT
import com.latticeonfhir.android.data.server.constants.EndPoints.PATIENT_IMMUNIZATION_RECOMMENDATION
import com.latticeonfhir.android.data.server.constants.EndPoints.PRESCRIPTION_FILE
import com.latticeonfhir.android.data.server.constants.EndPoints.RELATED_PERSON
import com.latticeonfhir.android.data.server.constants.EndPoints.VITAL
import com.latticeonfhir.android.data.server.constants.QueryParameters.COUNT
import com.latticeonfhir.android.data.server.constants.QueryParameters.GREATER_THAN_BUILDER
import com.latticeonfhir.android.data.server.constants.QueryParameters.ID
import com.latticeonfhir.android.data.server.constants.QueryParameters.LAST_UPDATED
import com.latticeonfhir.android.data.server.constants.QueryParameters.OFFSET
import com.latticeonfhir.android.data.server.constants.QueryParameters.ORG_ID
import com.latticeonfhir.android.data.server.constants.QueryParameters.PATIENT_ID
import com.latticeonfhir.android.data.server.constants.QueryParameters.SORT
import com.latticeonfhir.android.data.server.model.create.CreateResponse
import com.latticeonfhir.android.data.server.model.cvd.CVDResponse
import com.latticeonfhir.android.data.server.model.dispense.request.MedicineDispenseRequest
import com.latticeonfhir.android.data.server.model.dispense.response.DispenseData
import com.latticeonfhir.android.data.server.model.dispense.response.MedicineDispenseResponse
import com.latticeonfhir.android.data.server.model.labormed.labtest.LabTestResponse
import com.latticeonfhir.android.data.server.model.labormed.medicalrecord.MedicalRecordResponse
import com.latticeonfhir.android.data.server.model.patient.PatientLastUpdatedResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicationResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicineTimeResponse
import com.latticeonfhir.android.data.server.model.prescription.photo.PrescriptionPhotoResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.android.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisResponse
import com.latticeonfhir.android.data.server.model.vaccination.ImmunizationRecommendationResponse
import com.latticeonfhir.android.data.server.model.vaccination.ImmunizationResponse
import com.latticeonfhir.android.data.server.model.vaccination.ManufacturerResponse
import com.latticeonfhir.android.data.server.model.vitals.VitalResponse
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.fromJson
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.mapToObject
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeStampDate
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfId
import com.latticeonfhir.android.utils.converters.responseconverter.toNoBracketAndNoSpaceString
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiContinueResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiResponseConverter
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import com.latticeonfhir.android.utils.file.DeleteFileManager
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val patientApiService: PatientApiService,
    private val prescriptionApiService: PrescriptionApiService,
    private val scheduleAndAppointmentApiService: ScheduleAndAppointmentApiService,
    private val cvdApiService: CVDApiService,
    private val vitalApiService: VitalApiService,
    private val symptomsAndDiagnosisService: SymptomsAndDiagnosisService,
    private val labTestAndMedRecordService: LabTestAndMedRecordService,
    private val dispenseApiService: DispenseApiService,
    private val vaccinationApiService: VaccinationApiService,
    patientDao: PatientDao,
    private val genericDao: GenericDao,
    private val preferenceRepository: PreferenceRepository,
    deleteFileManager: DeleteFileManager,
    relationDao: RelationDao,
    medicationDao: MedicationDao,
    prescriptionDao: PrescriptionDao,
    scheduleDao: ScheduleDao,
    appointmentDao: AppointmentDao,
    patientLastUpdatedDao: PatientLastUpdatedDao,
    cvdDao: CVDDao,
    vitalDao: VitalDao,
    symptomsAndDiagnosisDao: SymptomsAndDiagnosisDao,
    labTestAndMedDao: LabTestAndMedDao,
    dispenseDao: DispenseDao,
    fileUploadDao: FileUploadDao,
    immunizationRecommendationDao: ImmunizationRecommendationDao,
    immunizationDao: ImmunizationDao,
    manufacturerDao: ManufacturerDao
) : SyncRepository, SyncRepositoryDatabaseTransactions(
    patientApiService,
    patientDao,
    genericDao,
    relationDao,
    medicationDao,
    prescriptionDao,
    scheduleDao,
    appointmentDao,
    patientLastUpdatedDao,
    cvdDao,
    vitalDao,
    symptomsAndDiagnosisDao,
    labTestAndMedDao,
    dispenseDao,
    fileUploadDao,
    deleteFileManager,
    immunizationRecommendationDao,
    immunizationDao,
    manufacturerDao
) {

    override suspend fun getAndInsertListPatientData(
        offset: Int
    ): ResponseMapper<List<PatientResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncPatient() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncPatient().toTimeStampDate()
        )

        ApiResponseConverter.convert(
            patientApiService.getListData(
                PATIENT, map
            ), true
        ).run {
            return when (this) {
                is ApiContinueResponse -> {
                    //Insert Patient
                    insertPatient(body)
                    //Call for next batch data
                    getAndInsertListPatientData(offset + COUNT_VALUE)
                }

                is ApiEndResponse -> {
                    //Set Last Update Time
                    preferenceRepository.setLastSyncPatient(Date().time)
                    //Insert Patient
                    insertPatient(body)
                    this
                }

                else -> this
            }
        }
    }

    override suspend fun getAndInsertPatientDataById(
        id: String
    ): ResponseMapper<List<PatientResponse>> {
        ApiResponseConverter.convert(
            patientApiService.getListData(
                PATIENT, mapOf(Pair(ID, id))
            )
        ).run {
            return when (this) {
                is ApiEndResponse -> {
                    //Insert Patient Data
                    insertPatient(body)
                    this
                }

                else -> this
            }
        }
    }

    override suspend fun getAndInsertRelation(): ResponseMapper<List<RelatedPersonResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            GenericTypeEnum.FHIR_IDS, SyncType.POST, COUNT_VALUE
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                val map = mutableMapOf<String, String>()
                map[PATIENT_ID] =
                    listOfGenericEntity.map { it.payload }.toNoBracketAndNoSpaceString()
                map[COUNT] = DEFAULT_MAX_COUNT_VALUE.toString()
                ApiResponseConverter.convert(patientApiService.getRelationData(RELATED_PERSON, map))
                    .run {
                        when (this) {
                            is ApiEndResponse -> {
                                insertRelations(body)
                                genericDao.deleteSyncPayload(listOfGenericEntity.toListOfId())
                                getAndInsertRelation()
                            }

                            else -> {
                                this
                            }
                        }
                    }
            }
        }
    }


    override suspend fun getAndInsertPhotoPrescription(patientId: String?): ResponseMapper<List<PrescriptionPhotoResponse>> {
        return if (patientId == null) {
            genericDao.getSameTypeGenericEntityPayload(
                GenericTypeEnum.FHIR_IDS_PRESCRIPTION_PHOTO, SyncType.POST, COUNT_VALUE
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    val map = mutableMapOf<String, String>()
                    map[PATIENT_ID] =
                        listOfGenericEntity.map { it.payload }.toNoBracketAndNoSpaceString()
                    map[COUNT] = DEFAULT_MAX_COUNT_VALUE.toString()
                    ApiResponseConverter.convert(prescriptionApiService.getPastPhotoPrescription(map))
                        .run {
                            when (this) {
                                is ApiEndResponse -> {
                                    insertPhotoPrescriptions(body)
                                    genericDao.deleteSyncPayload(listOfGenericEntity.toListOfId())
                                    getAndInsertPhotoPrescription(null)
                                }

                                else -> {
                                    this
                                }
                            }
                        }
                }
            }
        } else {
            ApiResponseConverter.convert(
                prescriptionApiService.getPastPhotoPrescription(
                    mapOf(
                        Pair(PATIENT_ID, patientId)
                    )
                )
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        insertPhotoPrescriptions(body)
                        this
                    }

                    else -> this
                }
            }
        }
    }

    override suspend fun getAndInsertFormPrescription(patientId: String?): ResponseMapper<List<PrescriptionResponse>> {
        return if (patientId == null) {
            genericDao.getSameTypeGenericEntityPayload(
                GenericTypeEnum.FHIR_IDS_PRESCRIPTION, SyncType.POST, COUNT_VALUE
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    val map = mutableMapOf<String, String>()
                    map[PATIENT_ID] =
                        listOfGenericEntity.map { it.payload }.toNoBracketAndNoSpaceString()
                    map[COUNT] = DEFAULT_MAX_COUNT_VALUE.toString()
                    ApiResponseConverter.convert(prescriptionApiService.getPastPrescription(map))
                        .run {
                            when (this) {
                                is ApiEndResponse -> {
                                    insertFormPrescriptions(body)
                                    genericDao.deleteSyncPayload(listOfGenericEntity.toListOfId())
                                    getAndInsertFormPrescription(null)
                                }

                                else -> {
                                    this
                                }
                            }
                        }
                }
            }
        } else {
            ApiResponseConverter.convert(
                prescriptionApiService.getPastPrescription(
                    mapOf(
                        Pair(PATIENT_ID, patientId)
                    )
                )
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        insertFormPrescriptions(body)
                        this
                    }

                    else -> this
                }
            }
        }
    }

    override suspend fun getAndInsertMedication(offset: Int): ResponseMapper<List<MedicationResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        if (preferenceRepository.getLastMedicationSyncDate() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastMedicationSyncDate().toTimeStampDate()
            )

        return ApiResponseConverter.convert(
            prescriptionApiService.getAllMedications(map), true
        ).run {
            when (this) {
                is ApiContinueResponse -> {
                    insertMedication(body)
                    getAndInsertMedication(offset + COUNT_VALUE)
                }

                is ApiEndResponse -> {
                    preferenceRepository.setLastMedicationSyncDate(Date().time)
                    insertMedication(body)
                    this
                }

                else -> this
            }
        }
    }

    override suspend fun getMedicineTime(): ResponseMapper<List<MedicineTimeResponse>> {
        val map = mutableMapOf<String, String>()
        if (preferenceRepository.getLastMedicineDosageInstructionSyncDate() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastMedicineDosageInstructionSyncDate().toTimeStampDate()
            )

        return ApiResponseConverter.convert(
            prescriptionApiService.getMedicineTime(
                map
            )
        ).run {
            when (this) {
                is ApiEndResponse -> {
                    preferenceRepository.setLastMedicineDosageInstructionSyncDate(Date().time)
                    insertMedicationTiming(body)
                    this
                }

                else -> this
            }
        }
    }

    override suspend fun getAndInsertSchedule(offset: Int): ResponseMapper<List<ScheduleResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        map[ORG_ID] = preferenceRepository.getOrganizationFhirId()
        if (preferenceRepository.getLastSyncSchedule() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncSchedule().toTimeStampDate()
        )

        ApiResponseConverter.convert(
            scheduleAndAppointmentApiService.getScheduleList(
                map
            ), true
        ).run {
            return when (this) {
                is ApiContinueResponse -> {
                    //Insert Schedule Data
                    insertSchedule(body)
                    //Call for next batch data
                    getAndInsertSchedule(offset + COUNT_VALUE)
                }

                is ApiEndResponse -> {
                    preferenceRepository.setLastSyncSchedule(Date().time)
                    insertSchedule(body)
                    this
                }

                else -> this
            }
        }
    }

    override suspend fun getAndInsertAppointment(offset: Int): ResponseMapper<List<AppointmentResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        map[ORG_ID] = preferenceRepository.getOrganizationFhirId()
        if (preferenceRepository.getLastSyncAppointment() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncAppointment().toTimeStampDate()
        )

        ApiResponseConverter.convert(
            scheduleAndAppointmentApiService.getAppointmentList(
                map
            ), true
        ).run {
            return when (this) {
                is ApiContinueResponse -> {
                    //Insert Appointment Data
                    insertAppointment(body)
                    //Call for next batch data
                    getAndInsertAppointment(offset + COUNT_VALUE)
                }

                is ApiEndResponse -> {
                    preferenceRepository.setLastSyncAppointment(Date().time)
                    insertAppointment(body)
                    this
                }

                else -> this
            }
        }
    }

    override suspend fun getAndInsertPatientLastUpdatedData(): ResponseMapper<List<PatientLastUpdatedResponse>> {
        ApiResponseConverter.convert(
            patientApiService.getPatientLastUpdatedData()
        ).run {
            return when (this) {
                is ApiEndResponse -> {
                    //Insert Patient Last Updated Data
                    insertPatientLastUpdated(body)
                    this
                }

                else -> this
            }
        }
    }

    override suspend fun getAndInsertCVD(offset: Int): ResponseMapper<List<CVDResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncCVD() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncCVD().toTimeStampDate()
        )

        ApiResponseConverter.convert(
            cvdApiService.getCVD(
                map
            ), true
        ).run {
            return when (this) {
                is ApiContinueResponse -> {
                    //Insert CVD Data
                    insertCVD(body)
                    //Call for next batch data
                    getAndInsertCVD(offset + COUNT_VALUE)
                }

                is ApiEndResponse -> {
                    preferenceRepository.setLastSyncCVD(Date().time)
                    insertCVD(body)
                    this
                }

                else -> this
            }
        }
    }
    override suspend fun getAndInsertListVitalData(offset: Int): ResponseMapper<List<VitalResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncVital() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncVital().toTimeStampDate()
        )

        ApiResponseConverter.convert(
            vitalApiService.getListData(
                VITAL, map
            ), true
        ).run {
            return when (this) {
                is ApiContinueResponse -> {
                    //Insert Patient
                    insertVital(body)
                    //Call for next batch data
                    getAndInsertListVitalData(offset + COUNT_VALUE)
                }

                is ApiEndResponse -> {
                    //Set Last Update Time
                    preferenceRepository.setLastSyncVital(Date().time)
                    //Insert Patient
                    insertVital(body)
                    this
                }

                else -> this
            }
        }
    }
    override suspend fun getAndInsertListSymptomsAndDiagnosisData(offset: Int): ResponseMapper<List<SymptomsAndDiagnosisResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncSymDiag() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncSymDiag().toTimeStampDate()
        )

        ApiResponseConverter.convert(
            symptomsAndDiagnosisService.getListData(
                EndPoints.SYMPTOMS_DIAGNOSIS, map
            ), true
        ).run {
            return when (this) {
                is ApiContinueResponse -> {
                    //Insert Patient
                    insertSymDiag(body)
                    //Call for next batch data
                    getAndInsertListSymptomsAndDiagnosisData(offset + COUNT_VALUE)
                }

                is ApiEndResponse -> {
                    //Set Last Update Time
                    preferenceRepository.setLastSyncSymDiag(Date().time)
                    //Insert Patient
                    insertSymDiag(body)
                    this
                }

                else -> this
            }
        }
    }

    override suspend fun getAndInsertListLabTestData(offset: Int): ResponseMapper<List<LabTestResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncLabTest() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncLabTest().toTimeStampDate()
        )

        ApiResponseConverter.convert(
            labTestAndMedRecordService.getListData(
                EndPoints.LAB_TEST, map
            ), true
        ).run {
            return when (this) {
                is ApiContinueResponse -> {
                    //Insert Patient
                    insertLabTest(body, PhotoUploadTypeEnum.LAB_TEST.value)
                    //Call for next batch data
                    getAndInsertListLabTestData(offset + COUNT_VALUE)
                }

                is ApiEndResponse -> {
                    //Set Last Update Time
                    preferenceRepository.setLastSyncLabTest(Date().time)
                    //Insert Patient
                    insertLabTest(body, PhotoUploadTypeEnum.LAB_TEST.value)
                    this
                }

                else -> this
            }
        }
    }

    override suspend fun getAndInsertListMedicalRecordData(offset: Int): ResponseMapper<List<MedicalRecordResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncMedicalRecord() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncMedicalRecord().toTimeStampDate()
        )

        ApiResponseConverter.convert(
            labTestAndMedRecordService.getListMedicalRecordData(
                EndPoints.MEDICAL_RECORD, map
            ), true
        ).run {
            return when (this) {
                is ApiContinueResponse -> {
                    //Insert Patient
                    insertMedicalRecord(body, PhotoUploadTypeEnum.MEDICAL_RECORD.value)
                    //Call for next batch data
                    getAndInsertListMedicalRecordData(offset + COUNT_VALUE)
                }

                is ApiEndResponse -> {
                    //Set Last Update Time
                    preferenceRepository.setLastSyncMedicalRecord(Date().time)
                    //Insert Patient
                    insertMedicalRecord(body, PhotoUploadTypeEnum.MEDICAL_RECORD.value)
                    this
                }

                else -> this
            }
        }

    }

    override suspend fun getAndInsertDispense(patientId: String?): ResponseMapper<List<MedicineDispenseResponse>> {
        return if (patientId == null) {
            genericDao.getSameTypeGenericEntityPayload(
                GenericTypeEnum.FHIR_IDS_DISPENSE, SyncType.POST, COUNT_VALUE
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) {
                    insertNotDispensedPrescriptions()
                    ApiEmptyResponse()
                }
                else {
                    val map = mutableMapOf<String, String>()
                    map[PATIENT_ID] =
                        listOfGenericEntity.map { it.payload }.toNoBracketAndNoSpaceString()
                    map[COUNT] = DEFAULT_MAX_COUNT_VALUE.toString()
                    ApiResponseConverter.convert(dispenseApiService.getDispenseRecords(map))
                        .run {
                            when (this) {
                                is ApiEndResponse -> {
                                    insertDispense(body)
                                    genericDao.deleteSyncPayload(listOfGenericEntity.toListOfId())
                                    getAndInsertDispense(null)
                                }

                                else -> {
                                    this
                                }
                            }
                        }
                }
            }
        } else {
            ApiResponseConverter.convert(
                dispenseApiService.getDispenseRecords(
                    mapOf(
                        Pair(PATIENT_ID, patientId)
                    )
                )
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        insertDispense(body)
                        insertNotDispensedPrescriptions()
                        this
                    }

                    else -> this
                }
            }
        }
    }

    override suspend fun getAndInsertOTC(patientId: String?): ResponseMapper<List<DispenseData>> {
        return if (patientId == null) {
            genericDao.getSameTypeGenericEntityPayload(
                GenericTypeEnum.FHIR_IDS_OTC, SyncType.POST, COUNT_VALUE
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    val map = mutableMapOf<String, String>()
                    map[PATIENT_ID] =
                        listOfGenericEntity.map { it.payload }.toNoBracketAndNoSpaceString()
                    map[COUNT] = DEFAULT_MAX_COUNT_VALUE.toString()
                    ApiResponseConverter.convert(dispenseApiService.getOTCRecords(map))
                        .run {
                            when (this) {
                                is ApiEndResponse -> {
                                    insertOTC(body)
                                    genericDao.deleteSyncPayload(listOfGenericEntity.toListOfId())
                                    getAndInsertOTC(null)
                                }

                                else -> {
                                    this
                                }
                            }
                        }
                }
            }
        } else {
            ApiResponseConverter.convert(
                dispenseApiService.getOTCRecords(
                    mapOf(
                        Pair(PATIENT_ID, patientId)
                    )
                )
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        insertOTC(body)
                        this
                    }

                    else -> this
                }
            }
        }
    }

    override suspend fun getAndInsertImmunization(): ResponseMapper<Any> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.FHIR_IDS_IMMUNIZATION,
            syncType = SyncType.POST
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                val immunizationRecommendationResponse = getAndInsertImmunizationRecommendation(listOfGenericEntity.map { it.payload })
                val immunizationResponse = getAndInsertImmunization(listOfGenericEntity.map { it.payload })

                if (immunizationRecommendationResponse is ApiEndResponse && immunizationResponse is ApiEndResponse) {
                    insertImmunizationRecommendation(immunizationRecommendationResponse.body)
                    insertImmunization(immunizationResponse.body)
                    genericDao.deleteSyncPayload(listOfGenericEntity.map { it.id })
                    getAndInsertImmunization()
                }
                return immunizationResponse
            }
        }
    }

    private suspend fun getAndInsertImmunizationRecommendation(patientIdList: List<String>): ResponseMapper<List<ImmunizationRecommendationResponse>> {
        val map = mutableMapOf<String, String>()
        map[PATIENT_IMMUNIZATION_RECOMMENDATION] = patientIdList.toNoBracketAndNoSpaceString()
        return ApiResponseConverter.convert(vaccinationApiService.getAllImmunizationRecommendation(map = map))
    }

    private suspend fun getAndInsertImmunization(patientIdList: List<String>): ResponseMapper<List<ImmunizationResponse>> {
        val map = mutableMapOf<String, String>()
        map[PATIENT_ID] = patientIdList.toNoBracketAndNoSpaceString()
        return ApiResponseConverter.convert(vaccinationApiService.getAllImmunization(map = map))
    }

    override suspend fun getAndInsertManufacturer(): ResponseMapper<List<ManufacturerResponse>> {
        val map = mutableMapOf<String, String>()
        if (preferenceRepository.getLastSyncManufacturerRecord() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncManufacturerRecord().toTimeStampDate()
        )
        
        return ApiResponseConverter.convert(vaccinationApiService.getAllManufacturers(map))
            .apply {
                if (this is ApiEndResponse) {
                    insertManufacturer(body)
                    preferenceRepository.setLastSyncManufacturerRecord(Date().time)
                }
            }
    }

    override suspend fun sendPersonPostData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.PATIENT,
            syncType = SyncType.POST
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else ApiResponseConverter.convert(
                patientApiService.createData(
                    PATIENT,
                    listOfGenericEntity.map {
                        it.payload.fromJson<LinkedTreeMap<*, *>>()
                            .mapToObject(PatientResponse::class.java)!!
                    })
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        insertPatientFhirId(
                            listOfGenericEntity,
                            body
                        ).let { deletedRows -> if (deletedRows > 0) sendPersonPostData() else this }
                    }

                    else -> this
                }
            }
        }
    }

    override suspend fun sendRelatedPersonPostData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.RELATION, syncType = SyncType.POST
        ).let { listOfRelatedEntity ->
            if (listOfRelatedEntity.isEmpty()) ApiEmptyResponse()
            else ApiResponseConverter.convert(
                patientApiService.createData(
                    RELATED_PERSON,
                    listOfRelatedEntity.map { relationGenericEntity ->
                        relationGenericEntity.payload.fromJson<LinkedTreeMap<*, *>>()
                            .mapToObject(RelatedPersonResponse::class.java)!!
                    })
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        genericDao.deleteSyncPayload(listOfRelatedEntity.toListOfId())
                            .let { deletedRows ->
                                if (deletedRows > 0) sendRelatedPersonPostData() else this
                            }
                    }

                    else -> this
                }
            }
        }
    }

    override suspend fun sendFormPrescriptionPostData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.PRESCRIPTION,
            syncType = SyncType.POST
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                ApiResponseConverter.convert(
                    prescriptionApiService.postPrescriptionRelatedData(
                        MEDICATION_REQUEST,
                        listOfGenericEntity.map { prescriptionGenericEntity ->
                            prescriptionGenericEntity.payload.fromJson<LinkedTreeMap<*, *>>()
                                .mapToObject(PrescriptionResponse::class.java)!!
                        }
                    )
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            insertPrescriptionAndMedicationRequestFhirId(listOfGenericEntity, body).let { deletedRows ->
                                if (deletedRows > 0) sendFormPrescriptionPostData() else this
                            }
                        }

                        else -> this
                    }
                }
            }
        }
    }

    override suspend fun sendPhotoPrescriptionPostData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.PRESCRIPTION_PHOTO_RESPONSE,
            syncType = SyncType.POST
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                Timber.d("manseeyy prescription post data $listOfGenericEntity")
                ApiResponseConverter.convert(
                    prescriptionApiService.postPrescriptionRelatedData(
                        PRESCRIPTION_FILE,
                        listOfGenericEntity.map { prescriptionGenericEntity ->
                            prescriptionGenericEntity.payload.fromJson<LinkedTreeMap<*, *>>()
                                .mapToObject(PrescriptionPhotoResponse::class.java)!!
                        }
                    )
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            insertPhotoPrescriptionFhirId(listOfGenericEntity, body).let { deletedRows ->
                                if (deletedRows > 0) sendPhotoPrescriptionPostData() else this
                            }
                        }

                        else -> this
                    }
                }
            }
        }
    }

    override suspend fun sendSchedulePostData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.SCHEDULE, syncType = SyncType.POST
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else ApiResponseConverter.convert(
                scheduleAndAppointmentApiService.postScheduleData(listOfGenericEntity.map {
                    it.payload.fromJson<LinkedTreeMap<*, *>>()
                        .mapToObject(ScheduleResponse::class.java)!!
                })
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        insertScheduleFhirId(listOfGenericEntity, body).let { deletedRows ->
                            if (deletedRows > 0) sendSchedulePostData() else this
                        }
                    }

                    else -> this
                }
            }
        }
    }

    override suspend fun sendAppointmentPostData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.APPOINTMENT, syncType = SyncType.POST
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else ApiResponseConverter.convert(
                scheduleAndAppointmentApiService.createAppointment(listOfGenericEntity.map {
                    it.payload.fromJson<LinkedTreeMap<*, *>>()
                        .mapToObject(AppointmentResponse::class.java)!!
                })
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        insertAppointmentFhirId(listOfGenericEntity, body).let { deletedRows ->
                            if (deletedRows > 0) sendAppointmentPostData() else this
                        }
                    }

                    else -> this
                }
            }
        }
    }


    override suspend fun sendPatientLastUpdatePostData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.LAST_UPDATED,
            syncType = SyncType.POST
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else ApiResponseConverter.convert(
                patientApiService.postPatientLastUpdates(
                    listOfGenericEntity.map {
                        it.payload.fromJson<LinkedTreeMap<*, *>>()
                            .mapToObject(PatientLastUpdatedResponse::class.java)!!
                    })
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        val deletedRows = deleteGenericEntityData(listOfGenericEntity)
                        if (deletedRows > 0) sendPatientLastUpdatePostData() else this
                    }

                    else -> this
                }
            }
        }
    }

    override suspend fun sendCVDPostData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.CVD, syncType = SyncType.POST
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else ApiResponseConverter.convert(
                cvdApiService.createCVD(listOfGenericEntity.map {
                    it.payload.fromJson<LinkedTreeMap<*, *>>()
                        .mapToObject(CVDResponse::class.java)!!
                })
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        insertCVDFhirId(listOfGenericEntity, body).let { deletedRows ->
                            if (deletedRows > 0) sendCVDPostData() else this
                        }
                    }

                    else -> this
                }
            }
        }
    }
    override suspend fun sendVitalPostData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.VITAL, syncType = SyncType.POST
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else ApiResponseConverter.convert(
                vitalApiService.createData(VITAL, listOfGenericEntity.map {
                    it.payload.fromJson<LinkedTreeMap<*, *>>().mapToObject(VitalLocal::class.java)!!
                })
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        insertVitalFhirId(
                            listOfGenericEntity, body
                        ).let { deletedRows -> if (deletedRows > 0) sendVitalPostData() else this }
                    }

                    else -> this
                }
            }
        }

    }

    override suspend fun sendSymptomsAndDiagnosisPostData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.SYMPTOMS_DIAGNOSIS, syncType = SyncType.POST
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else ApiResponseConverter.convert(
                symptomsAndDiagnosisService.createData(
                    EndPoints.SYMPTOMS_DIAGNOSIS,
                    listOfGenericEntity.map {
                        it.payload.fromJson<LinkedTreeMap<*, *>>()
                            .mapToObject(SymptomsAndDiagnosisData::class.java)!!
                    })
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        insertSymDiagFhirId(
                            listOfGenericEntity, body
                        ).let { deletedRows -> if (deletedRows > 0) sendSymptomsAndDiagnosisPostData() else this }
                    }

                    else -> this
                }
            }
        }

    }


    override suspend fun sendLabTestPostData(
    ): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.LAB_TEST, syncType = SyncType.POST
        ).let { listOfGenericEntity ->
            Timber.d(
                "Request: ${
                    listOfGenericEntity.map {
                        it.payload.fromJson<MutableMap<String, Any>>()
                    }
                }"
            )
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else ApiResponseConverter.convert(
                labTestAndMedRecordService.createData(EndPoints.LAB_TEST, listOfGenericEntity.map {
                    it.payload.fromJson<MutableMap<String, Any>>()
                })
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        insertLabOrMedFhirId(
                            listOfGenericEntity, body, PhotoUploadTypeEnum.LAB_TEST.value
                        ).let { deletedRows ->
                            if (deletedRows > 0) sendLabTestPostData(
                            ) else this
                        }
                    }

                    else -> this
                }
            }
        }

    }

    override suspend fun sendMedRecordPostData(
    ): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.MEDICAL_RECORD, syncType = SyncType.POST
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else ApiResponseConverter.convert(
                labTestAndMedRecordService.createData(
                    EndPoints.MEDICAL_RECORD,
                    listOfGenericEntity.map {
                        it.payload.fromJson()
                    })
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        insertLabOrMedFhirId(
                            listOfGenericEntity, body, PhotoUploadTypeEnum.MEDICAL_RECORD.value
                        ).let { deletedRows ->
                            if (deletedRows > 0) sendMedRecordPostData(
                            ) else this
                        }
                    }

                    else -> this
                }
            }
        }

    }

    override suspend fun sendDispensePostData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.DISPENSE,
            syncType = SyncType.POST
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                ApiResponseConverter.convert(
                    dispenseApiService.postDispenseData(
                        MEDICATION_DISPENSE,
                        listOfGenericEntity.map { dispenseGenericEntity ->
                            dispenseGenericEntity.payload.fromJson<LinkedTreeMap<*, *>>()
                                .mapToObject(MedicineDispenseRequest::class.java)!!
                        }
                    )
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            insertDispenseFhirId(listOfGenericEntity, body).let { deletedRows ->
                                if (deletedRows > 0) sendDispensePostData() else this
                            }
                        }

                        else -> this
                    }
                }
            }
        }
    }

    override suspend fun sendImmunizationPostData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.IMMUNIZATION,
            syncType = SyncType.POST
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                ApiResponseConverter.convert(
                    vaccinationApiService.postImmunization(
                        listOfGenericEntity.map {
                            it.payload.fromJson<LinkedTreeMap<*, *>>()
                                .mapToObject(ImmunizationResponse::class.java)!!
                        }
                    )
                ).apply {
                    if (this is ApiEndResponse) {
                        insertImmunizationFhirIds(body, listOfGenericEntity)
                            .apply {
                                if (this > 0) sendImmunizationPostData()
                            }
                    }
                }
            }
        }
    }

    override suspend fun sendPersonPatchData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.PATIENT, syncType = SyncType.PATCH
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                ApiResponseConverter.convert(
                    patientApiService.patchListOfChanges(
                        PATIENT,
                        listOfGenericEntity.map { it.payload.fromJson() })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            deleteGenericEntityData(listOfGenericEntity).let {
                                if (it > 0) sendPersonPatchData() else this
                            }
                        }

                        else -> this
                    }
                }
            }
        }
    }

    override suspend fun sendRelatedPersonPatchData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            GenericTypeEnum.RELATION, SyncType.PATCH
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                ApiResponseConverter.convert(
                    patientApiService.patchListOfChanges(
                        RELATED_PERSON,
                        listOfGenericEntity.map { it.payload.fromJson() })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            deleteGenericEntityData(listOfGenericEntity).let {
                                if (it > 0) sendRelatedPersonPatchData() else this
                            }
                        }

                        else -> this
                    }
                }
            }
        }
    }

    override suspend fun sendAppointmentPatchData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.APPOINTMENT, syncType = SyncType.PATCH
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                ApiResponseConverter.convert(
                    scheduleAndAppointmentApiService.patchListOfChanges(
                        listOfGenericEntity.map { it.payload.fromJson() })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            deleteGenericEntityData(listOfGenericEntity).let {
                                if (it > 0) sendAppointmentPatchData() else this
                            }
                        }

                        else -> this
                    }
                }
            }
        }
    }

    override suspend fun sendPrescriptionPhotoPatchData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.PRESCRIPTION_PHOTO_RESPONSE, syncType = SyncType.PATCH
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                ApiResponseConverter.convert(
                    prescriptionApiService.patchListOfChanges(
                        listOfGenericEntity.map { it.payload.fromJson() })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            deleteGenericEntityData(listOfGenericEntity).let {
                                if (it > 0) sendPrescriptionPhotoPatchData() else this
                            }
                        }

                        else -> this
                    }
                }
            }
        }
    }

    override suspend fun sendCVDPatchData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.CVD, syncType = SyncType.PATCH
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                ApiResponseConverter.convert(
                    cvdApiService.patchListOfChanges(
                        listOfGenericEntity.map { it.payload.fromJson() })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            deleteGenericEntityData(listOfGenericEntity).let {
                                if (it > 0) sendCVDPatchData() else this
                            }
                        }

                        else -> this
                    }
                }
            }
        }
    }

    override suspend fun sendLabTestPatchData(
    ): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.LAB_TEST, syncType = SyncType.PATCH
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                ApiResponseConverter.convert(
                    labTestAndMedRecordService.patchListOfChanges(
                        listOfGenericEntity.map { it.payload.fromJson() })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            deleteGenericEntityData(listOfGenericEntity).let {
                                if (it > 0) sendLabTestPatchData(
                                ) else this
                            }
                        }

                        else -> this
                    }
                }
            }
        }

    }

    override suspend fun sendMedRecordPatchData(
    ): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.MEDICAL_RECORD, syncType = SyncType.PATCH
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                ApiResponseConverter.convert(
                    labTestAndMedRecordService.patchListOfChanges(
                        listOfGenericEntity.map { it.payload.fromJson() })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            deleteGenericEntityData(listOfGenericEntity).let {
                                if (it > 0) sendMedRecordPatchData(
                                ) else this
                            }
                        }

                        else -> this
                    }
                }
            }
        }

    }
    override suspend fun sendVitalPatchData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.VITAL, syncType = SyncType.PATCH
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                ApiResponseConverter.convert(
                    vitalApiService.patchListOfChanges(
                        VITAL,
                        listOfGenericEntity.map { it.payload.fromJson() })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            deleteGenericEntityData(listOfGenericEntity).let {
                                if (it > 0) sendVitalPatchData() else this
                            }
                        }

                        else -> this
                    }
                }
            }
        }

    }

    override suspend fun deletePrescriptionPhoto(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.PRESCRIPTION_PHOTO_RESPONSE, syncType = SyncType.DELETE
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                ApiResponseConverter.convert(
                    prescriptionApiService.deletePrescriptionPhoto(
                        listOfGenericEntity.map { it.payload.fromJson() })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            deleteGenericEntityData(listOfGenericEntity).let {
                                if (it > 0) deletePrescriptionPhoto() else this
                            }
                        }

                        else -> this
                    }
                }
            }
        }
    }

    override suspend fun deleteLabTestPhoto(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.LAB_TEST, syncType = SyncType.DELETE
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                ApiResponseConverter.convert(
                    labTestAndMedRecordService.deleteLabOrMedicalRecordPhoto(
                        EndPoints.LAB_TEST,
                        listOfGenericEntity.map { it.payload.fromJson() })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            deleteGenericEntityData(listOfGenericEntity).let {
                                if (it > 0) deleteLabTestPhoto() else this
                            }
                        }

                        else -> this
                    }
                }
            }
        }
    }

    override suspend fun deleteMedTestPhoto(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.MEDICAL_RECORD, syncType = SyncType.DELETE
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                ApiResponseConverter.convert(
                    labTestAndMedRecordService.deleteLabOrMedicalRecordPhoto(
                        EndPoints.MEDICAL_RECORD,
                        listOfGenericEntity.map { it.payload.fromJson() })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            deleteGenericEntityData(listOfGenericEntity).let {
                                if (it > 0) deleteMedTestPhoto() else this
                            }
                        }

                        else -> this
                    }
                }
            }
        }
    }

    override suspend fun sendSymptomsAndDiagnosisPatchData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.SYMPTOMS_DIAGNOSIS, syncType = SyncType.PATCH
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                ApiResponseConverter.convert(
                    symptomsAndDiagnosisService.patchListOfChanges(EndPoints.SYMPTOMS_DIAGNOSIS,
                        listOfGenericEntity.map { it.payload.fromJson() })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            deleteGenericEntityData(listOfGenericEntity).let {
                                if (it > 0) sendSymptomsAndDiagnosisPatchData() else this
                            }
                        }

                        else -> this
                    }
                }
            }
        }

    }

}