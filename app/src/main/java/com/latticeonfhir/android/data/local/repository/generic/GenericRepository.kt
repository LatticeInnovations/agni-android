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
    suspend fun updateRelationFhirId()

    suspend fun insertPrescription(prescriptionResponse: PrescriptionResponse): Long
    suspend fun updatePrescriptionFhirId()

    @Deprecated("This method was deprecated use above methods to store POST Generic Entity")
    suspend fun insertOrUpdatePostEntity(patientId: String, entity: Any, typeEnum: GenericTypeEnum, replaceEntireRow: Boolean = false, uuid: String = UUIDBuilder.generateUUID()): Long

    suspend fun insertOrUpdatePatchEntity(patientFhirId: String,map: Map<String,Any>, typeEnum: GenericTypeEnum, uuid: String = UUIDBuilder.generateUUID()): Long
}