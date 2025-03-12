package com.latticeonfhir.android.data.server.repository.sync

import com.latticeonfhir.android.data.local.enums.DispenseStatusEnum
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.IdentifierCodeEnum
import com.latticeonfhir.android.data.local.enums.PhotoDeleteEnum
import com.latticeonfhir.android.data.local.enums.PhotoUploadTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
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
import com.latticeonfhir.android.data.local.roomdb.entities.dispense.DispenseDataEntity
import com.latticeonfhir.android.data.local.roomdb.entities.dispense.DispensePrescriptionEntity
import com.latticeonfhir.android.data.local.roomdb.entities.dispense.MedicineDispenseListEntity
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.data.local.roomdb.entities.labtestandmedrecord.photo.LabTestAndMedPhotoEntity
import com.latticeonfhir.android.data.local.roomdb.entities.patient.IdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.photo.PrescriptionPhotoEntity
import com.latticeonfhir.android.data.local.roomdb.entities.relation.RelationEntity
import com.latticeonfhir.android.data.server.api.PatientApiService
import com.latticeonfhir.android.data.server.model.create.CreateResponse
import com.latticeonfhir.android.data.server.model.create.LabDocumentIdResponse
import com.latticeonfhir.android.data.server.model.create.MedDocumentIdResponse
import com.latticeonfhir.android.data.server.model.cvd.CVDResponse
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
import com.latticeonfhir.android.utils.constants.ErrorConstants
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters
import com.latticeonfhir.android.utils.converters.responseconverter.Vaccination.toImmunizationEntity
import com.latticeonfhir.android.utils.converters.responseconverter.Vaccination.toImmunizationRecommendationEntity
import com.latticeonfhir.android.utils.converters.responseconverter.Vaccination.toManufacturerEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toAppointmentEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toCVDEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toDispensePrescriptionEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toLabTestEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toLabTestPhotoResponseLocal
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfDispenseDataEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfId
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfLabTestAndMedPhotoEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfLabTestPhotoEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfMedicationEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfMedicineDirectionsEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfMedicineDispenseListEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfPrescriptionDirectionsEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfPrescriptionPhotoEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toMedRecordPhotoResponseLocal
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientLastUpdatedEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPrescriptionEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toRelationEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toScheduleEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toSymptomsAndDiagnosisEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toVitalEntity
import com.latticeonfhir.android.utils.file.DeleteFileManager
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
    private val vitalDao: VitalDao,
    private val symptomsAndDiagnosisDao: SymptomsAndDiagnosisDao,
    private val labTestAndMedDao: LabTestAndMedDao,
    private val dispenseDao: DispenseDao,
    private val fileUploadDao: FileUploadDao,
    private val deleteFileManager: DeleteFileManager,
    private val immunizationRecommendationDao: ImmunizationRecommendationDao,
    private val immunizationDao: ImmunizationDao,
    private val manufacturerDao: ManufacturerDao
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
                    ),
                    GenericEntity(
                        id = UUID.randomUUID().toString(),
                        patientId = patientResponse.id,
                        payload = patientResponse.fhirId,
                        type = GenericTypeEnum.FHIR_IDS_DISPENSE,
                        syncType = SyncType.POST
                    ),
                    GenericEntity(
                        id = UUID.randomUUID().toString(),
                        patientId = patientResponse.id,
                        payload = patientResponse.fhirId,
                        type = GenericTypeEnum.FHIR_IDS_OTC,
                        syncType = SyncType.POST
                    ),
                    GenericEntity(
                        id = UUID.randomUUID().toString(),
                        patientId = patientResponse.id,
                        payload = patientResponse.fhirId,
                        type = GenericTypeEnum.FHIR_IDS_IMMUNIZATION,
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
        val savedPhotoPrescription = body.filter { it.status == PhotoDeleteEnum.SAVED.value }
        prescriptionDao.insertPrescription(
            *savedPhotoPrescription.map { prescriptionResponse ->
                prescriptionResponse.toPrescriptionEntity(
                    patientDao
                )
            }.toTypedArray()
        )
        val prescriptionPhotos = mutableListOf<PrescriptionPhotoEntity>()
        savedPhotoPrescription.forEach { prescriptionResponse ->
            prescriptionPhotos.addAll(
                prescriptionResponse.toListOfPrescriptionPhotoEntity()
            )
        }
        prescriptionDao.insertPrescriptionPhotos(
            *prescriptionPhotos.toTypedArray()
        )
        val listOfGenericEntity = mutableListOf<GenericEntity>()
        savedPhotoPrescription.map { prescriptionPhotoResponse ->
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

        body.filter { it.status == PhotoDeleteEnum.DELETE.value }
            .map { deletedPhotoPrescription ->
                fileUploadDao.deleteFile(deletedPhotoPrescription.prescription[0].filename)
                deleteFileManager.removeFromInternalStorage(deletedPhotoPrescription.prescription[0].filename)
                prescriptionDao.deletePrescriptionPhoto(deletedPhotoPrescription.toListOfPrescriptionPhotoEntity()[0]).also {
                    prescriptionDao.deletePrescriptionEntity(deletedPhotoPrescription.toPrescriptionEntity(patientDao))
                }
            }
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

    protected suspend fun insertSymDiag(body: List<SymptomsAndDiagnosisResponse>) {
        //Insert Vital Data
        symptomsAndDiagnosisDao.insertSymptomsAndDiagnosis(*body.map {
            it.toSymptomsAndDiagnosisEntity(
                patientDao,
                appointmentDao
            )
        }
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
                createResponse.prescriptionFiles!!.forEach { prescriptionResponse ->
                    prescriptionDao.updateDocumentFhirId(
                        prescriptionResponse.documentUuid,
                        prescriptionResponse.documentfhirId
                    )
                }
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

    protected suspend fun insertSymDiagFhirId(
        listOfGenericEntities: List<GenericEntity>, body: List<CreateResponse>
    ): Int {
        body.map { createResponse ->
            symptomsAndDiagnosisDao.updateSymDiagFhirId(
                createResponse.id!!, createResponse.fhirId!!
            )
        }
        return deleteGenericEntityData(listOfGenericEntities)
    }

    private suspend fun deleteGenericEntityByListOfIds(idsToDelete: List<String>): Int {
        return genericDao.deleteSyncPayload(idsToDelete)
    }

    protected suspend fun deleteGenericEntityData(listOfGenericEntities: List<GenericEntity>): Int {
        return genericDao.deleteSyncPayload(listOfGenericEntities.toListOfId())
    }


    protected suspend fun insertLabTest(body: List<LabTestResponse>, type: String) {
        body.map { labTestResponse ->
            labTestResponse.diagnosticReport.filter { it.status == PhotoDeleteEnum.SAVED.value }
                .map {
                    it.toLabTestPhotoResponseLocal(
                        labTestResponse,
                        appointmentDao,
                        patientDao
                    ).toLabTestEntity(type)
                }.also { labTests ->
                    labTestAndMedDao.insertLabAndMedTest(*labTests.toTypedArray())
                }
        }

        body.map { labTestResponse ->
            labTestResponse.diagnosticReport.filter { it.status == PhotoDeleteEnum.DELETE.value }
                .map {
                    fileUploadDao.deleteFile(it.documents[0].filename)
                    deleteFileManager.removeFromInternalStorage(it.documents[0].filename)
                    labTestAndMedDao.deleteLabTestAndMedPhoto(it.documents[0].filename)
                    labTestAndMedDao.deleteLabTestAndMedEntity(it.diagnosticUuid)
                }
        }

        val labTestAndMedPhotoEntity = mutableListOf<LabTestAndMedPhotoEntity>()
        body.forEach { response ->
            labTestAndMedPhotoEntity.addAll(
                response.toListOfLabTestPhotoEntity()
            )
        }
        labTestAndMedDao.insertLabTestsAndMedPhotos(
            *labTestAndMedPhotoEntity.toTypedArray()
        )
        val listOfGenericEntity = mutableListOf<GenericEntity>()

        body.map { labTestResponse ->
            labTestResponse.diagnosticReport.filter { it.status == PhotoDeleteEnum.SAVED.value }
                .map {
                    it.documents.forEach { fileName ->
                        listOfGenericEntity.add(
                            GenericEntity(
                                id = UUID.randomUUID().toString(),
                                patientId = it.diagnosticReportFhirId,
                                payload = fileName.filename,
                                type = GenericTypeEnum.PHOTO_DOWNLOAD,
                                syncType = SyncType.POST
                            )
                        )

                    }

                }

        }

        genericDao.insertGenericEntity(
            *listOfGenericEntity.toTypedArray()
        )

    }

    protected suspend fun insertMedicalRecord(body: List<MedicalRecordResponse>, type: String) {
        body.map { medicalRecordResponse ->
            medicalRecordResponse.medicalRecord.filter { it.status == PhotoDeleteEnum.SAVED.value }
                .map {
                    it.toMedRecordPhotoResponseLocal(
                        medicalRecordResponse,
                        appointmentDao, patientDao
                    ).toLabTestEntity(type)
                }.also { labTests ->
                    labTestAndMedDao.insertLabAndMedTest(*labTests.toTypedArray())
                }

        }
        body.map { labTestResponse ->
            labTestResponse.medicalRecord.filter { it.status == PhotoDeleteEnum.DELETE.value }
                .map {
                    fileUploadDao.deleteFile(it.documents[0].filename)
                    deleteFileManager.removeFromInternalStorage(it.documents[0].filename)
                    labTestAndMedDao.deleteLabTestAndMedPhoto(it.documents[0].filename)
                    labTestAndMedDao.deleteLabTestAndMedEntity(it.medicalReportUuid)
                }
        }
        val labTestAndMedPhotoEntity = mutableSetOf<LabTestAndMedPhotoEntity>()
        body.forEach { response ->
            labTestAndMedPhotoEntity.addAll(
                response.toListOfLabTestAndMedPhotoEntity()
            )
        }
        labTestAndMedDao.insertLabTestsAndMedPhotos(
            *labTestAndMedPhotoEntity.toTypedArray()
        )
        val listOfGenericEntity = mutableListOf<GenericEntity>()

        body.map { labTestResponse ->
            labTestResponse.medicalRecord.filter { it.status == PhotoDeleteEnum.SAVED.value }
                .map {
                    it.documents.forEach { fileName ->
                        listOfGenericEntity.add(
                            GenericEntity(
                                id = UUID.randomUUID().toString(),
                                patientId = it.medicalRecordFhirId,
                                payload = fileName.filename,
                                type = GenericTypeEnum.PHOTO_DOWNLOAD,
                                syncType = SyncType.POST
                            )
                        )

                    }

                }

        }

        genericDao.insertGenericEntity(
            *listOfGenericEntity.toTypedArray()
        )

    }

    protected suspend fun insertLabOrMedFhirId(
        listOfGenericEntities: List<GenericEntity>, body: List<CreateResponse>, type: String
    ): Int {
        body.map { createResponse ->
            labTestAndMedDao.updateLabTestAndFhirId(
                createResponse.id!!, createResponse.fhirId!!
            )
            if (type == PhotoUploadTypeEnum.LAB_TEST.value) {
                val labDocumentIdResponse =
                    GsonConverters.deserializeList<LabDocumentIdResponse>(createResponse.files)

                labDocumentIdResponse!!.forEach { labTestResponse ->
                    labTestAndMedDao.updateDocumentFhirId(
                        labTestResponse.labDocumentUuid,
                        labTestResponse.labDocumentfhirId
                    )
                }
            } else {
                val medDocumentIdResponse =
                    GsonConverters.deserializeList<MedDocumentIdResponse>(createResponse.files)

                medDocumentIdResponse!!.forEach { medRecordResponse ->
                    labTestAndMedDao.updateDocumentFhirId(
                        medRecordResponse.medicalDocumentUuid,
                        medRecordResponse.medicalDocumentfhirId
                    )
                }
            }
        }
        return deleteGenericEntityData(listOfGenericEntities)
    }


    protected suspend fun insertDispenseFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            if (createResponse.error == null) {
                dispenseDao.updateDispenseFhirId(
                    createResponse.id!!, createResponse.fhirId!!
                )
                createResponse.medicineDispensedList!!.forEach { medicineResponse ->
                    dispenseDao.updateMedicineDispenseFhirId(
                        medicineResponse.medDispenseUuid,
                        medicineResponse.medDispenseFhirId
                    )
                }
            } else {
                idsToDelete.remove(createResponse.id)
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertDispense(body: List<MedicineDispenseResponse>) {
        dispenseDao.insertPrescriptionDispenseData(*body.map { medDispenseResponse ->
            medDispenseResponse.toDispensePrescriptionEntity(
                patientDao,
                prescriptionDao
            )
        }.toTypedArray())

        val dispenseRecords = mutableListOf<DispenseDataEntity>()
        body.forEach { medDispenseResponse ->
            dispenseRecords.addAll(
                medDispenseResponse.dispenseData.map { dispenseData ->
                    dispenseData.toListOfDispenseDataEntity(
                        patientDao,
                        prescriptionDao,
                        appointmentDao,
                        medDispenseResponse.prescriptionFhirId
                    )
                }
            )
        }
        dispenseDao.insertDispenseDataEntity(
            *dispenseRecords.toTypedArray()
        )

        val dispensedMedicationList = mutableListOf<MedicineDispenseListEntity>()
        body.forEach { medDispenseResponse ->
            medDispenseResponse.dispenseData.forEach { dispenseData ->
                dispensedMedicationList.addAll(
                    dispenseData.toListOfMedicineDispenseListEntity(
                        patientDao
                    )
                )
            }
        }
        dispenseDao.insertMedicineDispenseDataList(
            *dispensedMedicationList.toTypedArray()
        )
    }

    protected suspend fun insertNotDispensedPrescriptions() {
        dispenseDao.insertPrescriptionDispenseData(
            *prescriptionDao.getAllFormPrescriptions().filter {
                it.id !in dispenseDao.getAllDispense().map { it.prescriptionId }
            }.map { prescription ->
                DispensePrescriptionEntity(
                    patientId = prescription.patientId,
                    prescriptionId = prescription.id,
                    status = DispenseStatusEnum.NOT_DISPENSED.code
                )
            }.toTypedArray()
        )
    }

    protected suspend fun insertOTC(body: List<DispenseData>) {
        dispenseDao.insertDispenseDataEntity(
            *body.map {
                it.toListOfDispenseDataEntity(
                    patientDao,
                    prescriptionDao,
                    appointmentDao,
                    null
                )
            }
                .toTypedArray()
        )

        val dispensedMedicationList = mutableListOf<MedicineDispenseListEntity>()
        body.forEach { dispenseData ->
            dispensedMedicationList.addAll(
                dispenseData.toListOfMedicineDispenseListEntity(
                    patientDao
                )
            )
        }
        dispenseDao.insertMedicineDispenseDataList(
            *dispensedMedicationList.toTypedArray()
        )
    }

    protected suspend fun insertImmunizationRecommendation(body: List<ImmunizationRecommendationResponse>) {
        immunizationRecommendationDao.insertImmunizationRecommendation(
            *body.map { immunizationRecommendationResponse ->
                patientDao.getPatientIdByFhirId(immunizationRecommendationResponse.patientId)!!.let {
                    immunizationRecommendationResponse.toImmunizationRecommendationEntity(it)
                }
            }.toTypedArray()
        )
    }

    protected suspend fun insertImmunization(body: List<ImmunizationResponse>) {
        immunizationDao.insertImmunization(
            *body.map { immunizationResponse ->
                val patientId = patientDao.getPatientIdByFhirId(immunizationResponse.patientId)!!
                val appointmentId = appointmentDao.getAppointmentIdByFhirId(immunizationResponse.appointmentId)
                immunizationResponse.toImmunizationEntity(patientId, appointmentId)
            }.toTypedArray()
        )

        val listOfGenericEntity = mutableListOf<GenericEntity>()
        body.map { immunizationResponse ->
            immunizationResponse.immunizationFiles?.forEach { file ->
                listOfGenericEntity.add(
                    GenericEntity(
                        id = UUID.randomUUID().toString(),
                        patientId = immunizationResponse.immunizationUuid,
                        payload = file.filename,
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

    protected suspend fun insertManufacturer(body: List<ManufacturerResponse>) {
        manufacturerDao.insertManufacturer(
            *body.map { it.toManufacturerEntity() }.toTypedArray()
        )
    }

    protected suspend fun insertImmunizationFhirIds(body: List<CreateResponse>, listOfGenericEntities: List<GenericEntity>):Int {
        body.forEach { createResponse ->
            immunizationDao.updateFhirId(createResponse.id!!, createResponse.fhirId!!)
        }
        return deleteGenericEntityData(listOfGenericEntities)
    }
}