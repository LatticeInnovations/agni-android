package com.latticeonfhir.android.data.server.repository.sync

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.IdentifierCodeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
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
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.data.local.roomdb.entities.patient.IdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.photo.PrescriptionPhotoEntity
import com.latticeonfhir.android.data.local.roomdb.entities.relation.RelationEntity
import com.latticeonfhir.android.data.server.api.PatientApiService
import com.latticeonfhir.android.data.server.model.create.CreateResponse
import com.latticeonfhir.android.data.server.model.cvd.CVDResponse
import com.latticeonfhir.android.data.server.model.patient.PatientLastUpdatedResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicationResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicineTimeResponse
import com.latticeonfhir.android.data.server.model.prescription.photo.PrescriptionPhotoResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.android.data.server.model.vitals.VitalResponse
import com.latticeonfhir.android.utils.constants.ErrorConstants
import com.latticeonfhir.android.utils.converters.responseconverter.toAppointmentEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toCVDEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfId
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfMedicationEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfMedicineDirectionsEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfPrescriptionDirectionsEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfPrescriptionPhotoEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientLastUpdatedEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPrescriptionEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toRelationEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toScheduleEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toVitalEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

open class SyncRepositoryDatabaseTransactions(
    private val patientApiService: PatientApiService,
    private val patientDao: PatientDao,
    private val genericDao: GenericDao,
    private val relationDao: RelationDao,
    private val medicationDao: MedicationDao,
    private val prescriptionDao: PrescriptionDao,
    private val scheduleDao: ScheduleDao,
    private val appointmentDao: AppointmentDao,
    private val patientLastUpdatedDao: PatientLastUpdatedDao,
    private val cvdDao: CVDDao,
    private val vitalDao: VitalDao
) {

    protected suspend fun insertPatient(body: List<PatientResponse>) {
        //Insert Patient Data
        patientDao.insertPatientData(*body.map { it.toPatientEntity() }.toTypedArray())

        val listOfGenericEntity = mutableListOf<GenericEntity>()
        val identifierList = mutableListOf<IdentifierEntity>()

        body.map { patientResponse ->
            listOfGenericEntity.addAll(
                listOf(
                    GenericEntity(
                        id = UUID.randomUUID().toString(),
                        patientId = patientResponse.id,
                        payload = patientResponse.fhirId!!,
                        type = GenericTypeEnum.FHIR_IDS,
                        syncType = SyncType.POST
                    ),
                    GenericEntity(
                        id = UUID.randomUUID().toString(),
                        patientId = patientResponse.id,
                        payload = patientResponse.fhirId,
                        type = GenericTypeEnum.FHIR_IDS_PRESCRIPTION,
                        syncType = SyncType.POST
                    ),
                    GenericEntity(
                        id = UUID.randomUUID().toString(),
                        patientId = patientResponse.id,
                        payload = patientResponse.fhirId,
                        type = GenericTypeEnum.FHIR_IDS_PRESCRIPTION_PHOTO,
                        syncType = SyncType.POST
                    )
                )
            )
            patientResponse.toListOfIdentifierEntity().let { listOfIdentifiers ->
                identifierList.addAll(listOfIdentifiers)
            }
        }

        genericDao.insertGenericEntity(
            *listOfGenericEntity.toTypedArray()
        )

        //Insert Identifer Data
        patientDao.insertIdentifiers(*identifierList.filter { it.identifierCode != IdentifierCodeEnum.MEDICAL_RECORD.value }
            .toTypedArray())
    }

    protected suspend fun insertRelations(body: List<RelatedPersonResponse>) {
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
    }

    protected suspend fun insertFormPrescriptions(body: List<PrescriptionResponse>) {
        prescriptionDao.insertPrescription(*body.map { prescriptionResponse ->
            prescriptionResponse.toPrescriptionEntity(
                patientDao
            )
        }.toTypedArray())
        val medicineDirections = mutableListOf<PrescriptionDirectionsEntity>()
        body.forEach { prescriptionResponse ->
            medicineDirections.addAll(
                prescriptionResponse.toListOfPrescriptionDirectionsEntity(
                    medicationDao
                )
            )
        }
        prescriptionDao.insertPrescriptionMedicines(
            *medicineDirections.toTypedArray()
        )
    }

    protected suspend fun insertPhotoPrescriptions(body: List<PrescriptionPhotoResponse>) {
        prescriptionDao.insertPrescription(*body.map { prescriptionResponse ->
            prescriptionResponse.toPrescriptionEntity(
                patientDao
            )
        }.toTypedArray())
        val prescriptionPhotos = mutableListOf<PrescriptionPhotoEntity>()
        body.forEach { prescriptionResponse ->
            prescriptionPhotos.addAll(
                prescriptionResponse.toListOfPrescriptionPhotoEntity()
            )
        }
        prescriptionDao.insertPrescriptionPhotos(
            *prescriptionPhotos.toTypedArray()
        )
        val listOfGenericEntity = mutableListOf<GenericEntity>()
        body.map { prescriptionPhotoResponse ->
            prescriptionPhotoResponse.prescription.map {
                it.filename
            }.forEach { fileName ->
                listOfGenericEntity.add(
                    GenericEntity(
                        id = UUID.randomUUID().toString(),
                        patientId = prescriptionPhotoResponse.prescriptionId,
                        payload = fileName,
                        type = GenericTypeEnum.PRESCRIPTION_PHOTO,
                        syncType = SyncType.POST
                    )
                )
            }
        }
        genericDao.insertGenericEntity(
            *listOfGenericEntity.toTypedArray()
        )
    }

    protected suspend fun insertMedication(body: List<MedicationResponse>) {
        medicationDao.insertMedication(
            *body.toListOfMedicationEntity().toTypedArray()
        )
    }

    protected suspend fun insertMedicationTiming(body: List<MedicineTimeResponse>) {
        medicationDao.insertMedicineDosageInstructions(
            *body.toListOfMedicineDirectionsEntity().toTypedArray()
        )
    }

    protected suspend fun insertSchedule(body: List<ScheduleResponse>) {
        scheduleDao.insertScheduleEntity(*body.map { scheduleResponse ->
            scheduleResponse.toScheduleEntity()
        }.toTypedArray())
    }

    protected suspend fun insertAppointment(body: List<AppointmentResponse>) {
        appointmentDao.insertAppointmentEntity(*body.map { appointmentResponse ->
            appointmentResponse.toAppointmentEntity(patientDao, scheduleDao)
        }.toTypedArray())
    }

    protected suspend fun insertCVD(body: List<CVDResponse>) {
        cvdDao.insertCVDRecord(*body.map { cvdResponse ->
            cvdResponse.toCVDEntity(patientDao, appointmentDao)
        }.toTypedArray())
    }

    protected suspend fun insertVital(body: List<VitalResponse>) {
        //Insert Vital Data
        vitalDao.insertVital(*body.map { it.toVitalEntity(patientDao, appointmentDao) }
            .toTypedArray())

        val listOfGenericEntity = mutableListOf<GenericEntity>()

        genericDao.insertGenericEntity(
            *listOfGenericEntity.toTypedArray()
        )

    }

    protected suspend fun insertPatientFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>
    ): Int {
        body.map { createResponse ->
            patientDao.updateFhirId(createResponse.id!!, createResponse.fhirId!!)
        }
        return deleteGenericEntityData(listOfGenericEntities)
    }

    protected suspend fun insertPhotoPrescriptionFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            if (createResponse.error == null) {
                prescriptionDao.updatePrescriptionFhirId(
                    createResponse.id!!, createResponse.fhirId!!
                )
            } else {
                idsToDelete.remove(createResponse.id)
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertPrescriptionAndMedicationRequestFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            if (createResponse.error == null) {
                prescriptionDao.updatePrescriptionFhirId(
                    createResponse.id!!, createResponse.fhirId!!
                )
                createResponse.prescription!!.forEach { prescriptionResponse ->
                    prescriptionDao.updateMedReqFhirId(
                        prescriptionResponse.medReqUuid,
                        prescriptionResponse.medReqFhirId
                    )
                }
            } else {
                idsToDelete.remove(createResponse.id)
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertScheduleFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null, ErrorConstants.SCHEDULE_EXISTS -> {
                    scheduleDao.updateScheduleFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }

                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertAppointmentFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            if (createResponse.error == null) {
                appointmentDao.updateAppointmentFhirId(
                    createResponse.id!!, createResponse.fhirId!!
                )
            } else {
                idsToDelete.remove(createResponse.id)
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }


    protected suspend fun insertPatientLastUpdated(body: List<PatientLastUpdatedResponse>) {
        //Insert Patient Last Updated Data
        patientLastUpdatedDao.insertPatientLastUpdatedData(*body.map { it.toPatientLastUpdatedEntity() }
            .toTypedArray())
    }

    protected suspend fun insertCVDFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            if (createResponse.error == null) {
                cvdDao.updateCVDFhirId(
                    createResponse.id!!, createResponse.fhirId!!
                )
            } else {
                idsToDelete.remove(createResponse.id)
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }
    protected suspend fun insertVitalFhirId(
        listOfGenericEntities: List<GenericEntity>, body: List<CreateResponse>
    ): Int {
        body.map { createResponse ->
            vitalDao.updateVitalFhirId(createResponse.id!!, createResponse.fhirId!!)
        }
        return deleteGenericEntityData(listOfGenericEntities)
    }

    private suspend fun deleteGenericEntityByListOfIds(idsToDelete: List<String>): Int {
        return genericDao.deleteSyncPayload(idsToDelete)
    }

    protected suspend fun deleteGenericEntityData(listOfGenericEntities: List<GenericEntity>): Int {
        return genericDao.deleteSyncPayload(listOfGenericEntities.toListOfId())
    }
}