package com.latticeonfhir.android.data.server.repository.sync

import com.google.gson.internal.LinkedTreeMap
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.model.vital.VitalLocal
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.roomdb.dao.AppointmentDao
import com.latticeonfhir.android.data.local.roomdb.dao.CVDDao
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.dao.MedicationDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientLastUpdatedDao
import com.latticeonfhir.android.data.local.roomdb.dao.PrescriptionDao
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.dao.ScheduleDao
import com.latticeonfhir.android.data.local.roomdb.dao.VitalDao
import com.latticeonfhir.android.data.server.api.CVDApiService
import com.latticeonfhir.android.data.server.api.PatientApiService
import com.latticeonfhir.android.data.server.api.PrescriptionApiService
import com.latticeonfhir.android.data.server.api.ScheduleAndAppointmentApiService
import com.latticeonfhir.android.data.server.api.VitalApiService
import com.latticeonfhir.android.data.server.constants.ConstantValues.COUNT_VALUE
import com.latticeonfhir.android.data.server.constants.ConstantValues.DEFAULT_MAX_COUNT_VALUE
import com.latticeonfhir.android.data.server.constants.EndPoints
import com.latticeonfhir.android.data.server.constants.EndPoints.MEDICATION_REQUEST
import com.latticeonfhir.android.data.server.constants.EndPoints.PATIENT
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
import com.latticeonfhir.android.data.server.model.patient.PatientLastUpdatedResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicationResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicineTimeResponse
import com.latticeonfhir.android.data.server.model.prescription.photo.PrescriptionPhotoResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
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
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val patientApiService: PatientApiService,
    private val prescriptionApiService: PrescriptionApiService,
    private val scheduleAndAppointmentApiService: ScheduleAndAppointmentApiService,
    private val cvdApiService: CVDApiService,
    private val vitalApiService: VitalApiService,
    patientDao: PatientDao,
    private val genericDao: GenericDao,
    private val preferenceRepository: PreferenceRepository,
    relationDao: RelationDao,
    medicationDao: MedicationDao,
    prescriptionDao: PrescriptionDao,
    scheduleDao: ScheduleDao,
    appointmentDao: AppointmentDao,
    patientLastUpdatedDao: PatientLastUpdatedDao,
    cvdDao: CVDDao,
    vitalDao: VitalDao
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
    vitalDao
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


    override suspend fun getAndInsertPrescription(patientId: String?): ResponseMapper<List<PrescriptionPhotoResponse>> {
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
                                    insertPrescriptions(body)
                                    genericDao.deleteSyncPayload(listOfGenericEntity.toListOfId())
                                    getAndInsertPrescription(null)
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
                        insertPrescriptions(body)
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
                EndPoints.VITAL, map
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

    override suspend fun sendPrescriptionPostData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.PRESCRIPTION,
            syncType = SyncType.POST
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                Timber.d("manseeyy prescription post data $listOfGenericEntity")
                ApiResponseConverter.convert(
                    prescriptionApiService.postPrescriptionRelatedData(
                        MEDICATION_REQUEST,
                        listOfGenericEntity.map { prescriptionGenericEntity ->
                            prescriptionGenericEntity.payload.fromJson<LinkedTreeMap<*, *>>()
                                .mapToObject(PrescriptionPhotoResponse::class.java)!!
                        }
                    )
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            insertPrescriptionFhirId(listOfGenericEntity, body).let { deletedRows ->
                                if (deletedRows > 0) sendPrescriptionPostData() else this
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
            genericTypeEnum = GenericTypeEnum.PRESCRIPTION, syncType = SyncType.PATCH
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

}