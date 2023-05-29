package com.latticeonfhir.android.data.server.repository.sync

import com.google.gson.internal.LinkedTreeMap
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.IdentifierCodeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.dao.MedicationDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.dao.PrescriptionDao
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.data.local.roomdb.entities.patient.IdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity
import com.latticeonfhir.android.data.local.roomdb.entities.relation.RelationEntity
import com.latticeonfhir.android.data.server.api.PatientApiService
import com.latticeonfhir.android.data.server.api.PrescriptionApiService
import com.latticeonfhir.android.data.server.constants.ConstantValues.COUNT_VALUE
import com.latticeonfhir.android.data.server.constants.ConstantValues.DEFAULT_MAX_COUNT_VALUE
import com.latticeonfhir.android.data.server.constants.EndPoints.MEDICATION_REQUEST
import com.latticeonfhir.android.data.server.constants.EndPoints.PATIENT
import com.latticeonfhir.android.data.server.constants.EndPoints.RELATED_PERSON
import com.latticeonfhir.android.data.server.constants.QueryParameters.COUNT
import com.latticeonfhir.android.data.server.constants.QueryParameters.ID
import com.latticeonfhir.android.data.server.constants.QueryParameters.LAST_UPDATED
import com.latticeonfhir.android.data.server.constants.QueryParameters.OFFSET
import com.latticeonfhir.android.data.server.constants.QueryParameters.PATIENT_ID
import com.latticeonfhir.android.data.server.constants.QueryParameters.SORT
import com.latticeonfhir.android.data.server.model.create.CreateResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicationResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicineTimeResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.fromJson
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.mapToObject
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeStampDate
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfId
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfMedicationEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfMedicineDirectionsEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfPrescriptionDirectionsEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toNoBracketAndNoSpaceString
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPrescriptionEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toRelationEntity
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiContinueResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiResponseConverter
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val patientApiService: PatientApiService,
    private val prescriptionApiService: PrescriptionApiService,
    private val patientDao: PatientDao,
    private val genericDao: GenericDao,
    private val preferenceRepository: PreferenceRepository,
    private val relationDao: RelationDao,
    private val medicationDao: MedicationDao,
    private val prescriptionDao: PrescriptionDao
) : SyncRepository {

    private val identifierList = mutableListOf<IdentifierEntity>()

    override suspend fun getAndInsertListPatientData(offset: Int): ResponseMapper<List<PatientResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncPatient() != 0L) map[LAST_UPDATED] =
            preferenceRepository.getLastSyncPatient().toTimeStampDate()

        ApiResponseConverter.convert(
            patientApiService.getListData(
                PATIENT,
                map
            ),
            true
        ).run {
            return when (this) {
                is ApiContinueResponse -> {
                    //Insert Patient Data
                    patientDao.insertPatientData(*body.map { it.toPatientEntity() }.toTypedArray())

                    val listOfGenericEntity = mutableListOf<GenericEntity>()

                    body.map { patientResponse ->
                        listOfGenericEntity.add(
                            GenericEntity(
                                id = UUID.randomUUID().toString(),
                                patientId = patientResponse.id,
                                payload = patientResponse.fhirId!!,
                                type = GenericTypeEnum.FHIR_IDS,
                                syncType = SyncType.POST
                            )
                        )
                        patientResponse.toListOfIdentifierEntity()?.let { listOfIdentifiers ->
                            identifierList.addAll(listOfIdentifiers)
                        }
                    }

                    genericDao.insertGenericEntity(
                        *listOfGenericEntity.toTypedArray()
                    )

                    //Insert Patient Data
                    patientDao.insertIdentifiers(*identifierList.filter { it.identifierCode != IdentifierCodeEnum.MEDICAL_RECORD.value }
                        .toTypedArray())

                    //Call for next batch data
                    getAndInsertListPatientData(offset + COUNT_VALUE)
                }

                is ApiEndResponse -> {
                    //Set Last Update Time
                    preferenceRepository.setLastSyncPatient(Date().time)

                    //Insert Patient Data
                    patientDao.insertPatientData(*body.map { it.toPatientEntity() }.toTypedArray())

                    val listOfGenericEntity = mutableListOf<GenericEntity>()

                    body.map { patientResponse ->
                        listOfGenericEntity.add(
                            GenericEntity(
                                id = UUID.randomUUID().toString(),
                                patientId = patientResponse.id,
                                payload = patientResponse.fhirId!!,
                                type = GenericTypeEnum.FHIR_IDS,
                                syncType = SyncType.POST
                            )
                        )
                        patientResponse.toListOfIdentifierEntity()?.let { listOfIdentifiers ->
                            identifierList.addAll(listOfIdentifiers)
                        }
                    }

                    genericDao.insertGenericEntity(
                        *listOfGenericEntity.toTypedArray()
                    )

                    //Insert Patient Identifiers
                    patientDao.insertIdentifiers(*identifierList.filter { it.identifierCode != IdentifierCodeEnum.MEDICAL_RECORD.value }
                        .toTypedArray())

                    this
                }

                else -> this
            }
        }
    }

    override suspend fun getAndInsertPatientDataById(id: String): ResponseMapper<List<PatientResponse>> {
        ApiResponseConverter.convert(
            patientApiService.getListData(
                PATIENT,
                mapOf(Pair(ID, id))
            )
        ).run {
            return when (this) {
                is ApiEndResponse -> {
                    //Insert Patient Data
                    patientDao.insertPatientData(*body.map { it.toPatientEntity() }.toTypedArray())

                    body.map { patientResponse ->
                        patientResponse.toListOfIdentifierEntity()?.let { listOfIdentifiers ->
                            identifierList.addAll(listOfIdentifiers)
                        }
                    }

                    //Insert Patient Identifiers
                    patientDao.insertIdentifiers(*identifierList.filter { it.identifierCode != IdentifierCodeEnum.MEDICAL_RECORD.value }
                        .toTypedArray())

                    this
                }

                else -> this
            }
        }
    }

    override suspend fun getAndInsertRelation(): ResponseMapper<List<RelatedPersonResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            GenericTypeEnum.FHIR_IDS,
            SyncType.POST,
            COUNT_VALUE
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                val map = mutableMapOf<String, String>()
                map[PATIENT_ID] =
                    listOfGenericEntity.map { it.payload }.toNoBracketAndNoSpaceString()
                map[COUNT] = DEFAULT_MAX_COUNT_VALUE.toString()
                ApiResponseConverter.convert(
                    patientApiService.getRelationData(
                        RELATED_PERSON,
                        map
                    )
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            val relationEntity = mutableListOf<RelationEntity>()
                            body.map { relatedPersonResponse ->
                                if (relatedPersonResponse.relationship.isNotEmpty()) {
                                    relatedPersonResponse.relationship.map { relationship ->
                                        relationEntity.add(
                                            relationship.toRelationEntity(
                                                relatedPersonResponse.id,
                                                patientDao,
                                                patientApiService
                                            )
                                        )
                                    }
                                }
                            }
                            if (relationEntity.isNotEmpty()) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    relationDao.insertRelation(
                                        *relationEntity.toTypedArray()
                                    )
                                }
                            }
                            genericDao.deleteSyncPayload(listOfGenericEntity.toListOfId()).run {
                                getAndInsertRelation()
                            }
                        }

                        else -> {
                            this
                        }
                    }
                }
            }
        }
    }

    override suspend fun getAndInsertPrescription(offset: Int): ResponseMapper<List<PrescriptionResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        if (preferenceRepository.getLastSyncPrescription() != 0L) map[LAST_UPDATED] = preferenceRepository.getLastSyncPrescription().toTimeStampDate()

        return ApiResponseConverter.convert(prescriptionApiService.getPastPrescription(map),).run {
            when (this) {

                is ApiContinueResponse -> {
                    prescriptionDao.insertPrescription(
                        *body.map { prescriptionResponse -> prescriptionResponse.toPrescriptionEntity() }.toTypedArray()
                    )

                    val medicineDirections = mutableListOf<PrescriptionDirectionsEntity>()
                    body.forEach { prescriptionResponse ->
                        medicineDirections.addAll(prescriptionResponse.toListOfPrescriptionDirectionsEntity())
                    }
                    prescriptionDao.insertPrescriptionMedicines(
                        *medicineDirections.toTypedArray()
                    )
                    getAndInsertPrescription(offset + COUNT_VALUE)
                }

                is ApiEndResponse -> {
                    prescriptionDao.insertPrescription(
                        *body.map { prescriptionResponse -> prescriptionResponse.toPrescriptionEntity() }.toTypedArray()
                    )
                    val medicineDirections = mutableListOf<PrescriptionDirectionsEntity>()
                    body.forEach { prescriptionResponse ->
                        medicineDirections.addAll(prescriptionResponse.toListOfPrescriptionDirectionsEntity())
                    }
                    prescriptionDao.insertPrescriptionMedicines(
                        *medicineDirections.toTypedArray()
                    )
                    this
                }

                else -> this
            }
        }
    }

    override suspend fun getAndInsertMedication(offset: Int): ResponseMapper<List<MedicationResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()

        return ApiResponseConverter.convert(
            prescriptionApiService.getAllMedications(map),
            true
        ).run {
            when (this) {
                is ApiContinueResponse -> {
                    medicationDao.insertMedication(
                        *body.toListOfMedicationEntity().toTypedArray()
                    )
                    getAndInsertMedication(offset + COUNT_VALUE)
                }

                is ApiEndResponse -> {
                    medicationDao.insertMedication(
                        *body.toListOfMedicationEntity().toTypedArray()
                    )
                    this
                }

                else -> this
            }
        }
    }

    override suspend fun getMedicineTime(): ResponseMapper<List<MedicineTimeResponse>> {
        return ApiResponseConverter.convert(
            prescriptionApiService.getMedicineTime()
        ).run {
            when (this) {
                is ApiEndResponse -> {
                    medicationDao.insertMedicineDosageInstructions(
                        *body.toListOfMedicineDirectionsEntity().toTypedArray()
                    )
                }

                else -> {}
            }
            this
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
                            .mapToObject(PatientResponse::class.java) as Any
                    }
                )
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        body.map { createResponse ->
                            patientDao.updateFhirId(createResponse.id!!, createResponse.fhirId!!)
                        }
                        genericDao.deleteSyncPayload(listOfGenericEntity.toListOfId())
                            .let { deletedRows ->
                                if (deletedRows > 0) sendPersonPostData()
                                else this
                            }
                    }

                    else -> this
                }
            }
        }
    }

    override suspend fun sendRelatedPersonPostData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.RELATION,
            syncType = SyncType.POST
        ).let { listOfRelatedEntity ->
            if (listOfRelatedEntity.isEmpty()) ApiEmptyResponse()
            else ApiResponseConverter.convert(
                patientApiService.createData(
                    RELATED_PERSON,
                    listOfRelatedEntity.map {
                        it.payload.fromJson<LinkedTreeMap<*, *>>()
                            .mapToObject(RelatedPersonResponse::class.java) as Any
                    }
                )
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
                ApiResponseConverter.convert(
                    prescriptionApiService.postPrescriptionRelatedData(
                        MEDICATION_REQUEST,
                        listOfGenericEntity.map { it.payload.fromJson<PrescriptionResponse>() }
                    )
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            genericDao.deleteSyncPayload(listOfGenericEntity.toListOfId())
                                .let { deletedRows ->
                                    if (deletedRows > 0) sendPrescriptionPostData() else this
                                }
                        }

                        else -> this
                    }
                }
            }
        }
    }

    override suspend fun sendPersonPatchData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.PATIENT,
            syncType = SyncType.PATCH
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
            else {
                ApiResponseConverter.convert(
                    patientApiService.patchListOfChanges(
                        PATIENT,
                        listOfGenericEntity.map { it.payload.fromJson() }
                    )
                ).run {
                    when (this) {
                        is ApiContinueResponse -> {
                            genericDao.deleteSyncPayload(listOfGenericEntity.toListOfId()).also {
                                if (it > 0) sendPersonPatchData()
                            }
                        }

                        is ApiEndResponse -> {
                            genericDao.deleteSyncPayload(listOfGenericEntity.toListOfId()).also {
                                if (it > 0) sendPersonPatchData()
                            }
                        }

                        else -> {}
                    }
                    this
                }
            }
        }
    }

    override suspend fun sendRelatedPersonPatchData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(GenericTypeEnum.RELATION, SyncType.PATCH)
            .let { lisOfGenericEntity ->
                if (lisOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        patientApiService.patchListOfChanges(
                            RELATED_PERSON,
                            lisOfGenericEntity.map { it.payload.fromJson() }
                        )
                    ).run {
                        when (this) {
                            is ApiContinueResponse -> {
                                genericDao.deleteSyncPayload(lisOfGenericEntity.toListOfId()).also {
                                    if (it > 0) sendRelatedPersonPatchData()
                                }
                            }

                            is ApiEndResponse -> {
                                genericDao.deleteSyncPayload(lisOfGenericEntity.toListOfId()).also {
                                    if (it > 0) sendRelatedPersonPatchData()
                                }
                            }

                            else -> {}
                        }
                        this
                    }
                }
            }
    }
}