package com.heartcare.agni.data.local.model.labtest

import androidx.annotation.Keep
import com.heartcare.agni.data.server.model.prescription.photo.File
import java.util.Date

@Keep
data class LabTestPhotoResponseLocal(
    val labTestId: String,
    val appointmentId: String,
    val patientId: String,
    val labTestFhirId: String? = null,
    val createdOn: Date,
    val labTests: List<File>
)