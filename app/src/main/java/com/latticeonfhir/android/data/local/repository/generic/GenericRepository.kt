package com.latticeonfhir.android.data.local.repository.generic

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder

interface GenericRepository {

    suspend fun insertPatient(patientResponse: PatientResponse): Long

    suspend fun insertRelation(patientId: String, relatedPersonResponse: RelatedPersonResponse): Long
    suspend fun updateRelationFhirId(relationGenericEntity: GenericEntity, relatedPersonResponse: RelatedPersonResponse): Long

    suspend fun insertPrescription(patientId: String, prescriptionResponse: PrescriptionResponse): Long
    suspend fun updatePrescriptionFhirId(prescriptionGenericEntity: GenericEntity, prescriptionResponse: PrescriptionResponse): Long

    suspend fun insertOrUpdatePostEntity(patientId: String, entity: Any, typeEnum: GenericTypeEnum, replaceEntireRow: Boolean = false, uuid: String = UUIDBuilder.generateUUID()): Long
    suspend fun insertOrUpdatePatchEntity(patientFhirId: String,map: Map<String,Any>, typeEnum: GenericTypeEnum, uuid: String = UUIDBuilder.generateUUID()): Long

    suspend fun getNonSyncedPostRelations(): List<GenericEntity>
    suspend fun getNonSyncedPostPrescriptions(): List<GenericEntity>
}