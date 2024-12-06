package com.latticeonfhir.android.data.local.repository.labtest

import com.latticeonfhir.android.data.local.model.labtest.LabTestLocal
import com.latticeonfhir.android.data.local.model.labtest.LabTestPhotoResponseLocal
import com.latticeonfhir.android.data.server.model.prescription.photo.File

interface LabTestRepository {

    suspend fun insertPhotoLabTestAndMed(local: LabTestPhotoResponseLocal, type: String): Long
    suspend fun getLastLabTestAndMed(appointmentId: String): LabTestLocal
    suspend fun getLastPhotoLabAndMedTest(patientId: String, photoviewType: String): List<File>
    suspend fun getLabTestAndMedPhotoByAppointmentId(
        patientId: String,
        photoviewType: String
    ): List<LabTestPhotoResponseLocal>

    suspend fun getLabTestAndPhotoByDate(
        patientId: String,
        photoviewType: String,
        startDate: Long,
        endDate: Long
    ): LabTestPhotoResponseLocal

    suspend fun insertLabTestAndPhotos(
        labTestPhotoResponseLocal: LabTestPhotoResponseLocal,
        type: String
    ): Long

    suspend fun deleteLabTestAndPhotos(
        labTestPhotoResponseLocal: LabTestPhotoResponseLocal,
        type: String
    ): Int
}